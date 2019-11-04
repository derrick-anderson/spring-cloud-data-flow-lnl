package com.solstice.example;

import com.solstice.example.domain.SaleRecord;
import io.micrometer.core.instrument.Counter;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.integration.annotation.Router;

import java.util.ArrayList;

@EnableBinding(SaleRecordProcessor.class)
public class SaleRecordRouter {

    private PrometheusMeterRegistry meterRegistry;
    private Counter finalized;
    private Counter voided;

    public SaleRecordRouter(PrometheusMeterRegistry registry) {
        this.meterRegistry = registry;
        this.finalized = Counter.builder("kafka.router.finalized").baseUnit("Messages").register(meterRegistry);
        this.voided = Counter.builder("kafka.router.voided").baseUnit("Messages").register(meterRegistry);
    }

    @Router(inputChannel = SaleRecordProcessor.INPUT)
    public ArrayList<String> routeMessage(SaleRecord record) {
        ArrayList<String> channels = new ArrayList<>();
        if(record.finalized && !record.voided){
            System.out.println("Routing Finalized Sale for Sale Id : " + record.saleId);
            finalized.increment();
            channels.add(SaleRecordProcessor.FINAL);
        }else if (record.finalized && record.voided) {
            System.out.println("Routing Voided Sale for Sale Id : " + record.saleId);
            voided.increment();
            channels.add(SaleRecordProcessor.VOID);
        }
        return channels;
    }
}
