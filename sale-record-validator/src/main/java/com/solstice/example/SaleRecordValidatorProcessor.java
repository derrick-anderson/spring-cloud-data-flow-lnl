package com.solstice.example;

import com.solstice.example.domain.SaleRecord;
import io.micrometer.core.instrument.Counter;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.integration.annotation.Filter;

@EnableBinding(Processor.class)
public class SaleRecordValidatorProcessor {

    private PrometheusMeterRegistry meterRegistry;
    private Counter finalized;
    private Counter pending;

    public SaleRecordValidatorProcessor(PrometheusMeterRegistry registry) {
        this.meterRegistry = registry;
        this.finalized = Counter.builder("kafka.validation.finalized").baseUnit("Sale Record").register(meterRegistry);
        this.pending = Counter.builder("kafka.validation.pending").baseUnit("Sale Record").register(meterRegistry);
    }

    @Filter(inputChannel = Processor.INPUT, outputChannel = Processor.OUTPUT)
    public boolean filter(SaleRecord record){
        if(record.finalized) {
            finalized.increment();
            System.out.println("Accepting Record : " + record.saleId);
        } else {
            pending.increment();
            System.out.println("Rejecting Record : " + record.saleId);
        }
        return record.finalized;
    }

}
