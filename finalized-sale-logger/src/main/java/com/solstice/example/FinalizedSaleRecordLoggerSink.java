package com.solstice.example;

import com.solstice.example.domain.SaleRecord;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;

@EnableBinding(Sink.class)
public class FinalizedSaleRecordLoggerSink {

    @StreamListener(Sink.INPUT)
    public void recordSale(SaleRecord sale){
        System.out.println("Received Finalized Sale!!!!");
        System.out.println("Sale Id : " + sale.saleId);
        System.out.println("Total Profit : $" + sale.profit);
    }
}
