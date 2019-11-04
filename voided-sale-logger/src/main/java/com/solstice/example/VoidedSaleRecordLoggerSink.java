package com.solstice.example;

import com.solstice.example.domain.SaleRecord;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;

@EnableBinding(Sink.class)
public class VoidedSaleRecordLoggerSink {

    @StreamListener(Sink.INPUT)
    public void recordSale(SaleRecord sale){
        System.out.println("Received Voided Sale!!!!");
        System.out.println("Sale Id : " + sale.saleId);
        System.out.println("Sale Finalized : " + sale.finalized);
        System.out.println("Sale Voided : " + sale.voided);
        System.out.println("Lost Profit : $" + sale.profit);
    }
}
