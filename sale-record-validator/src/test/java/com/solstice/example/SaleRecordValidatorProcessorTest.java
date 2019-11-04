package com.solstice.example;

import com.solstice.example.domain.SaleRecord;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.cloud.stream.test.binder.MessageCollector;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RunWith(SpringRunner.class)
public class SaleRecordValidatorProcessorTest {

    @Autowired
    private Processor processor;

    @Autowired
    private MessageCollector collector;

    @Autowired
    private PrometheusMeterRegistry meterRegistry;

    @Test
    public void filter_shouldReturnTrueAndLog_forFinalizedMessage() {

        SaleRecord test = new SaleRecord();
        test.finalized = true;

        processor.input().send(MessageBuilder.withPayload(test).build());

        Message output = collector.forChannel(processor.output()).poll();

        assertThat(output.getPayload()).isNotNull();

        assertThat(meterRegistry.find("kafka.validation.finalized").counter().count()).isEqualTo(1.0);
    }

    @Test
    public void filter_shouldReturnFalse_forNonFinalizedMessage() throws InterruptedException {
        SaleRecord test = new SaleRecord();
        test.finalized = false;

        processor.input().send(MessageBuilder.withPayload(test).build());

        Message output = collector.forChannel(processor.output()).poll(5, TimeUnit.SECONDS);

        assertThat(output).isNull();

        assertThat(meterRegistry.find("kafka.validation.pending").counter().count()).isEqualTo(1.0);
    }
}