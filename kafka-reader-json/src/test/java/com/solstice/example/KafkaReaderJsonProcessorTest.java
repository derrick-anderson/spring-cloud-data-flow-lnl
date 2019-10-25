package com.solstice.example;

import io.micrometer.core.instrument.util.IOUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.cloud.stream.test.binder.MessageCollector;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class KafkaReaderJsonProcessorTest {

	@Autowired
	Processor processor;

	@Autowired
	MessageCollector messageCollector;

	@Test
	public void shouldProcessIncomingMessageAsValid() throws IOException {

		String message = IOUtils.toString(new ClassPathResource("/sampleKafkaMessage.json").getInputStream());

		assertThat(message).isNotNull();
	}
}