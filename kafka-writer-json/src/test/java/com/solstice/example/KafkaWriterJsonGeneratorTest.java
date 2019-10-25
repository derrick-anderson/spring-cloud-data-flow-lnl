package com.solstice.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solstice.example.domain.KafkaJsonData;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.cloud.stream.messaging.Source;
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
	public Source source;

	@Autowired
	public MessageCollector messageCollector;

	@Autowired
	SimpleMeterRegistry registry;

	@Test
	public void generateData() throws InterruptedException, IOException {

		Message message = messageCollector.forChannel(source.output()).poll(5, TimeUnit.SECONDS);

		assertThat(message.getPayload()).isNotNull();

		System.out.println("Received Message: " + message.getPayload().toString());

		KafkaJsonData thisData = new ObjectMapper().readValue(message.getPayload().toString(), KafkaJsonData.class);

		assertThat(thisData.id).isNotNegative().isNotNull();
		assertThat(thisData.sourceName).isEqualTo("KafkaJsonWriter");
	}

	@Test
	public void shouldIncrementRegisteredMetric() throws InterruptedException {

		Counter registerdMetric = registry.find("kafka.write.json.success").counter();

		//Application starts up and finishes executing main, then runs twice more
		Thread.sleep(2000);

		assertThat(registerdMetric.count()).isEqualTo(3.0);
	}
}