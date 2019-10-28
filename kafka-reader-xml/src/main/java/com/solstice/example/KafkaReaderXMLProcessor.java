package com.solstice.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.solstice.example.domain.SaleRecord;
import com.solstice.example.domain.XMLSource;
import io.micrometer.core.instrument.Counter;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.messaging.handler.annotation.SendTo;

import java.math.BigDecimal;

@EnableBinding(Processor.class)
public class KafkaReaderXMLProcessor {

    private PrometheusMeterRegistry meterRegistry;
    private final String KAFKA_XML_READ = "kafka.xml.read";
    private Counter read;
    private final String KAFKA_XML_VOIDED = "kafka.xml.read.void";
    private Counter voided;
    private final String KAFKA_XML_FINALIZED = "kafka.xml.read.finalized";
    private Counter finalized;

    //Because this is using jackson we have to manually transform the input
    private XmlMapper mapper = new XmlMapper();

    public KafkaReaderXMLProcessor(PrometheusMeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        read = Counter.builder(KAFKA_XML_READ)
                .baseUnit("SaleRecord")
                .description("Kafka XML Sale Record Read Count")
                .register(meterRegistry);
        voided = Counter.builder(KAFKA_XML_VOIDED)
                .baseUnit("SaleRecord")
                .description("Kafka XML Sale Record Voided Count")
                .register(meterRegistry);
        finalized = Counter.builder(KAFKA_XML_FINALIZED)
                .baseUnit("SaleRecord")
                .description("Kafka XML Sale Record Finalized Count")
                .register(meterRegistry);
    }

    @StreamListener(Processor.INPUT)
    @SendTo(Processor.OUTPUT)
    public SaleRecord process(String xmlSource) throws JsonProcessingException {
        // Transform the input and create a sale record to map to
        XMLSource sourceRecord = mapper.readValue(xmlSource, XMLSource.class);
        SaleRecord transformedRecord = new SaleRecord();

        // Map fields
        transformedRecord.saleId = sourceRecord.id;
        transformedRecord.finalized = !sourceRecord.inProcess;
        transformedRecord.voided = !sourceRecord.accepted;
        transformedRecord.profit = new BigDecimal(sourceRecord.profit).divide(new BigDecimal("100.00"));

        //Record Metrics
        recordMetrics(transformedRecord);

        // Return the transformed record
        return transformedRecord;
    }

    public void recordMetrics(SaleRecord record){
        read.increment();
        if(record.voided){ voided.increment();}
        if (record.finalized) { finalized.increment(); }
    }
}
