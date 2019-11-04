package com.solstice.example;

import com.solstice.example.domain.SaleRecord;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.test.binder.MessageCollector;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RunWith(SpringRunner.class)
public class SaleRecordRouterTest {

    @Autowired
    private SaleRecordProcessor processor;

    @Autowired
    private MessageCollector collector;

    @Autowired
    private PrometheusMeterRegistry meterRegistry;

    @Test
    public void route_shouldReturnFinalized_whenGivenNonVoidedFinalizedMessage() throws InterruptedException {

        SaleRecord finalizedRecord = new SaleRecord();
        finalizedRecord.finalized = true;
        finalizedRecord.voided = false;

        processor.input().send(MessageBuilder.withPayload(finalizedRecord).build());

        Message captured = collector.forChannel(processor.finalized()).poll(5, TimeUnit.SECONDS);

        assertThat(captured).isNotNull();
        assertThat(captured.getPayload()).isNotNull();
        assertThat(meterRegistry.find("kafka.router.finalized").counter().count()).isEqualTo(1.0);
    }

    @Test
    public void route_shouldReturnVoided_whenGivenAVoidedFinalizedMessage() throws InterruptedException {
        SaleRecord voidedRecord = new SaleRecord();
        voidedRecord.finalized = true;
        voidedRecord.voided = true;

        processor.input().send(MessageBuilder.withPayload(voidedRecord).build());

        Message captured = collector.forChannel(processor.voided()).poll(5, TimeUnit.SECONDS);

        assertThat(captured).isNotNull();
        assertThat(captured.getPayload()).isNotNull();

        assertThat(meterRegistry.find("kafka.router.voided").counter().count()).isEqualTo(1.0);
    }
}