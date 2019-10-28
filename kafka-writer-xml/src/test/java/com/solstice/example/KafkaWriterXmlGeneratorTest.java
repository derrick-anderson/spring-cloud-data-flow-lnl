package com.solstice.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.solstice.example.domain.KafkaXMLSource;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.cloud.stream.test.binder.MessageCollector;
import org.springframework.messaging.Message;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RunWith(SpringRunner.class)
public class KafkaWriterXmlGeneratorTest {

    private XmlMapper xmlMapper = new XmlMapper();

    @Autowired
    private Source source;

    @Autowired
    private MessageCollector messageCollector;

    @Autowired
    private PrometheusMeterRegistry meterRegistry;

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void generateXML_shouldCreateValidMessage() throws JsonProcessingException {
        Message collected = messageCollector.forChannel(source.output()).poll();

        assertThat(collected).isNotNull();
        assertThat(collected.getPayload()).isNotNull();

        // Debug Message
        System.out.println(collected.getPayload().toString());

        KafkaXMLSource xmlSource = xmlMapper.readValue(collected.getPayload().toString(), KafkaXMLSource.class);
        assertThat(xmlSource).isNotNull();
        assertThat(xmlSource.id).isPositive().isNotNull();
        assertThat(xmlSource.voided).isNotNull();
        assertThat(xmlSource.finalized).isNotNull();
        assertThat(xmlSource.source).isEqualTo("KAFKA_XML_SOURCE");
        assertThat(xmlSource.profit).isNotNull().isPositive();
    }

    // Dirties Context here and above are necessary to ensure you only capture metrics from this run.
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void recordMetrics_shouldCaptureMetricsAppropriately() throws JsonProcessingException, InterruptedException {
        Message collected = messageCollector.forChannel(source.output()).poll();

        assertThat(collected).isNotNull();
        assertThat(collected.getPayload()).isNotNull();

        // Debug Message
        System.out.println(collected.getPayload().toString());

        KafkaXMLSource xmlSource = xmlMapper.readValue(collected.getPayload().toString(), KafkaXMLSource.class);

        int voided = 0;
        if(xmlSource.voided){ voided = 1; }
        int finalized = 0;
        if(xmlSource.finalized){ finalized = 1; }

        assertThat(meterRegistry.find("kafka.xml.generate.send").counter().count()).isNotNull().isEqualTo(1);
        assertThat(meterRegistry.find("kafka.xml.generate.void").counter().count()).isNotNull().isEqualTo(voided);
        assertThat(meterRegistry.find("kafka.xml.generate.finalized").counter().count()).isNotNull().isEqualTo(finalized);
    }
}