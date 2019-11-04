package com.solstice.example;

import com.solstice.example.domain.KafkaSource;
import com.solstice.example.domain.SaleRecord;
import io.micrometer.core.instrument.Counter;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.SendTo;

import java.math.BigDecimal;

@EnableBinding(Processor.class)
public class KafkaReaderJsonProcessor {

    private PrometheusMeterRegistry prometheusMeterRegistry;
    private Counter total;
    private Counter voided;
    private Counter finalized;

    public KafkaReaderJsonProcessor(PrometheusMeterRegistry prometheusMeterRegistry){
        this. prometheusMeterRegistry = prometheusMeterRegistry;
        total = Counter.builder("kafka.json.process.total")
                .baseUnit("SaleRecord")
                .description("Number of Sales processed in the Json Message Handler")
                .register(prometheusMeterRegistry);
        voided = Counter.builder("kafka.json.process.voided")
                .baseUnit("SaleRecord")
                .description("Number of Sales processed that were voided in the Json Message Handler")
                .register(prometheusMeterRegistry);
        finalized = Counter.builder("kafka.json.process.finalized")
                .baseUnit("SaleRecord")
                .description("Number of Sales processed that were finalized in the Json Message Handler")
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
        System.out.println("Processing Record with ID: " + source.id.toString() + "\nVoided : " + !source.valid + "\nFinalized: " + !source.weekend);
        return record;
    }

    private void recordMetrics(SaleRecord record){
        total.increment();
        if(record.voided){
            voided.increment();
        }
        if(!record.voided && record.finalized){
            finalized.increment();
        }
    }
}
