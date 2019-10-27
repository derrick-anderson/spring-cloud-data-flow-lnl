package com.solstice.example;

import com.solstice.example.domain.KafkaSource;
import com.solstice.example.domain.SaleRecord;
import io.micrometer.core.instrument.Counter;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.messaging.handler.annotation.SendTo;

import java.math.BigDecimal;

@EnableBinding(Processor.class)
public class KafkaReaderJsonProcessor {

    private PrometheusMeterRegistry prometheusMeterRegistry;
    private Counter processed;
    private Counter voided;

    public KafkaReaderJsonProcessor(PrometheusMeterRegistry prometheusMeterRegistry){
        this. prometheusMeterRegistry = prometheusMeterRegistry;
        processed = Counter.builder("kafka.json.processed")
                .baseUnit("SaleRecord")
                .description("Number of Sales processed in the Json Message Handler")
                .register(prometheusMeterRegistry);
        voided = Counter.builder("kafka.json.voided")
                .baseUnit("SaleRecord")
                .description("Number of Sales processed that were voided in the Json Message Handler")
                .register(prometheusMeterRegistry);
    }

    @StreamListener(Processor.INPUT)
    @SendTo(Processor.OUTPUT)
    public SaleRecord processSourceMessage(KafkaSource source){
        SaleRecord record = new SaleRecord();
        // Utilize the source id as the sale id
        record.saleId = source.id;
        // Only records processed during the week are finalized
        record.finalized = !source.weekend;
        // If a record is marked as not valid, it's voided in the source and we'll route it appropriately
        record.voided = !source.valid;
        // Record the profit which is in pennies in the source system.
        record.profit = new BigDecimal(source.profit).divide(BigDecimal.valueOf(100.00));

        // Return the record and log the handling
        recordMetrics(record);
        return record;
    }

    private void recordMetrics(SaleRecord record){
        processed.increment();
        if(record.voided){
            voided.increment();
        }
    }
}
