package com.solstice.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solstice.example.domain.SaleRecord;
import io.micrometer.core.instrument.util.IOUtils;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.cloud.stream.test.binder.MessageCollector;
import org.springframework.core.io.ClassPathResource;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RunWith(SpringRunner.class)
public class KafkaReaderXMLProcessorTest {

    @Autowired
    private Processor processor;

    @Autowired
    private MessageCollector messageCollector;

    @Autowired
    private PrometheusMeterRegistry meterRegistry;

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void processXmlSource_ShouldConsumeAValidMessage() throws IOException {

        // Create the input message from the input file and send to input channel
        String message = IOUtils.toString(new ClassPathResource("/sampleValidKafkaSaleMessage.xml").getInputStream());
        processor.input().send(MessageBuilder.withPayload(message).build());

        // Collect the output message after transformation
        Message capturedMessage = messageCollector.forChannel(processor.output()).poll();
        assertThat(capturedMessage).isNotNull();

        // Debug
        System.out.println(capturedMessage.getPayload().toString());

        // Transform and Validate
        SaleRecord transformedRecord = new ObjectMapper().readValue(capturedMessage.getPayload().toString(), SaleRecord.class);
        assertThat(transformedRecord).isNotNull();
        assertThat(transformedRecord.saleId).isEqualTo(2061885782503076571L);
        assertThat(transformedRecord.voided).isFalse();
        assertThat(transformedRecord.finalized).isTrue();
        assertThat(transformedRecord.profit).isEqualTo(new BigDecimal("194.3"));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void recordMetrics_shouldTrackMetricsCorrectly() throws IOException {

        // Create the input message from the input file and send to input channel
        String message = IOUtils.toString(new ClassPathResource("/sampleValidKafkaSaleMessage.xml").getInputStream());

        processor.input().send(MessageBuilder.withPayload(message).build());

        assertThat(meterRegistry.find("kafka.xml.process.total").counter().count()).isNotNull().isEqualTo(1.0);
        assertThat(meterRegistry.find("kafka.xml.process.void").counter().count()).isNotNull().isEqualTo(0.0);
        assertThat(meterRegistry.find("kafka.xml.process.finalized").counter().count()).isNotNull().isEqualTo(1.0);

    }
}