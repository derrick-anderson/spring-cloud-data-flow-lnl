package com.solstice.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solstice.example.domain.KafkaJsonData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.cloud.stream.test.binder.MessageCollector;
import org.springframework.messaging.Message;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RunWith(SpringRunner.class)
public class KafkaWriterJsonGeneratorTest {

	@Autowired
	public Processor processor;

	@Autowired
	public MessageCollector messageCollector;

	@Test
	public void generateData() throws InterruptedException, IOException {

		Message message = messageCollector.forChannel(processor.output()).poll(5, TimeUnit.SECONDS);

		assertThat(message.getPayload()).isNotNull();

		System.out.println("Received Message: " + message.getPayload().toString());

		KafkaJsonData thisData = new ObjectMapper().readValue(message.getPayload().toString(), KafkaJsonData.class);

		assertThat(thisData.id).isNotNegative().isNotNull();
		assertThat(thisData.sourceName).isEqualTo("KafkaJsonWriter");
	}
}