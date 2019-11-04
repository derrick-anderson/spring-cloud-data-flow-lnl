package com.solstice.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.solstice.example.domain.KafkaXMLSource;
import io.micrometer.core.instrument.Counter;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Random;

import static java.lang.Math.abs;

@EnableScheduling
@EnableBinding(Source.class)
public class KafkaWriterXmlGenerator {

    private final String KAFKA_XML_GENERATE_SEND = "kafka.xml.generate.send";
    private final String KAFKA_XML_GENERATE_VOID = "kafka.xml.generate.void";
    private final String KAFKA_XML_GENERATE_FINALIZED = "kafka.xml.generate.finalized";

    private XmlMapper xmlMapper = new XmlMapper();
    private Random rng = new Random();
    private PrometheusMeterRegistry meterRegistry;
    private Counter sendCount;
    private Counter voidCount;
    private Counter finalizedCount;

    @Autowired
    private Source source;


    public KafkaWriterXmlGenerator(PrometheusMeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        sendCount = Counter.builder(KAFKA_XML_GENERATE_SEND)
                .baseUnit("SaleRecord")
                .description("XML Sales Records sent to Kafka")
                .register(meterRegistry);
        voidCount = Counter.builder(KAFKA_XML_GENERATE_VOID)
                .baseUnit("SaleRecord")
                .description("XML Sales Records that are voided sent to Kafka")
                .register(meterRegistry);
        finalizedCount = Counter.builder(KAFKA_XML_GENERATE_FINALIZED)
                .baseUnit("SaleRecord")
                .description("XML Sales Records that are finalized sent to Kafka")
                .register(meterRegistry);

    }

    @Scheduled(fixedDelay = 1000L)
    private void generateXML() throws JsonProcessingException {
        // Create a random message
        KafkaXMLSource sourceMessage = new KafkaXMLSource();
        sourceMessage.id = abs(rng.nextLong());
        sourceMessage.finalized = rng.nextBoolean();
        sourceMessage.voided = rng.nextBoolean();
        sourceMessage.profit = abs(rng.nextInt(20000));

        // Record Metrics
        recordMetrics(sourceMessage);

        // Convert Message to XML body and send
        String messageBody = xmlMapper.writeValueAsString(sourceMessage);
        System.out.println("Sending Message : " + messageBody);

        Message outputMessage = MessageBuilder
                .withPayload(messageBody)
                .setHeader(MessageHeaders.CONTENT_TYPE, "text/xml")
                .build();

        this.source.output().send(outputMessage);
    }

    private void recordMetrics(KafkaXMLSource source){
        sendCount.increment();
        if(source.voided) { voidCount.increment(); }
        if(!source.voided && source.finalized) { finalizedCount.increment(); }
    }
}
