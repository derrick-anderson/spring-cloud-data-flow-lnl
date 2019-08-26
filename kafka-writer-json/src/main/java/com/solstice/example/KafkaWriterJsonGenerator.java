package com.solstice.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solstice.example.domain.KafkaJsonData;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.Instant;
import java.util.Random;

import static java.lang.Math.abs;

@EnableScheduling
@EnableBinding(Processor.class)
public class KafkaWriterJsonGenerator {

	private final SimpleMeterRegistry registry;

	private final Processor channels;

	public KafkaWriterJsonGenerator(SimpleMeterRegistry registry, Processor channels) {
		this.registry = registry;
		this.channels = channels;
	}

	@Scheduled(fixedRateString = "1000")
	public void generateData() throws JsonProcessingException {

		// Make a new object
		KafkaJsonData data = new KafkaJsonData();
		data.id = (Instant.now().getEpochSecond());
		data.valid = (true);
		data.profit = abs(new Random().nextInt());

		// Send to Output Channel
		this.channels.output().send(MessageBuilder.withPayload(new ObjectMapper().writeValueAsString(data)).build());

		// Count the Output
		registry.counter("kafka.write.json").increment();
	}
}
