package com.solstice.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solstice.example.domain.KafkaJsonData;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.Instant;
import java.util.Random;

import static java.lang.Math.abs;

@EnableScheduling
@EnableBinding(Source.class)
public class KafkaWriterJsonGenerator {

	// Declare our micrometer registry to be used.
	private final PrometheusMeterRegistry registry;

	private final Source channels;

	public KafkaWriterJsonGenerator(PrometheusMeterRegistry registry, Source channels) {
		this.registry = registry;
		this.channels = channels;
	}

	@Scheduled(fixedRateString = "1000")
	public void generateData() {

		// Make a new object
		KafkaJsonData data = new KafkaJsonData();
		data.id = (Instant.now().getEpochSecond());
		data.valid = new Random().nextBoolean();
		data.profit = abs(new Random().nextInt());
		data.weekend = new Random().nextBoolean();

		// Send to Output Channel
		try {
			this.channels.output().send(MessageBuilder.withPayload(new ObjectMapper().writeValueAsString(data)).build());
			// Count the Output
			registry.counter("kafka.write.json.success").increment();
		} catch (JsonProcessingException ex) {
			// Capture the Failure as a count
			registry.counter("kafka.write.json.failure").increment();
		}
	}
}
