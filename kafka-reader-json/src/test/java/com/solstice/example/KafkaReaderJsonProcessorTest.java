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
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RunWith(SpringRunner.class)
public class KafkaReaderJsonProcessorTest {

	@Autowired
	Processor processor;

	@Autowired
	MessageCollector messageCollector;

	@Autowired
	PrometheusMeterRegistry prometheusMeterRegistry;

	@Test
	public void processSourceMesssage_shouldMapValidSale() throws IOException {

		// Create the input message from the input file and send to input channel
		String message = IOUtils.toString(new ClassPathResource("/sampleValidKafkaSaleMessage.json").getInputStream());
		processor.input().send(MessageBuilder.withPayload(message).build());

		// Collect the message that is output with the message collector
		Message capturedMessage = messageCollector.forChannel(processor.output()).poll();

		//Debug message
		System.out.println(capturedMessage.getPayload());

		// Assure output is present and of type SaleRecord
		assertThat(capturedMessage).isNotNull();
		SaleRecord capturedSaleRecord = new ObjectMapper().readValue(capturedMessage.getPayload().toString(), SaleRecord.class);;
		assertThat(capturedSaleRecord).isNotNull();
		assertThat(capturedSaleRecord.saleId).isEqualTo(1572210122);
		assertThat(capturedSaleRecord.finalized).isTrue();
		assertThat(capturedSaleRecord.voided).isFalse();
		assertThat(capturedSaleRecord.profit).isEqualTo(new BigDecimal("196.7"));

		// Assert the counters in the registry are correct
		assertThat(prometheusMeterRegistry.find("kafka.json.processed").counter().count()).isEqualTo(1);
		assertThat(prometheusMeterRegistry.find("kafka.json.voided").counter().count()).isEqualTo(0);

	}
}