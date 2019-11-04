package com.solstice.example;

import com.solstice.example.domain.KafkaJsonData;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.Instant;
import java.util.Random;

import static java.lang.Math.abs;

@EnableBinding(Source.class)
public class KafkaWriterJsonGenerator {

	private Source channels;
	private final String KAFKA_JSON_GENERATE_SEND = "kafka.json.generate.total";
	private final String KAFKA_JSON_GENERATE_VOID = "kafka.json.generate.void";
	private final String KAFKA_JSON_GENERATE_FINALIZED = "kafka.json.generate.finalized";

	// Declare our micrometer registry and counters to be used.
	private PrometheusMeterRegistry registry;
	private Counter sent;
	private Counter finalized;
	private Counter voided;

	public KafkaWriterJsonGenerator(PrometheusMeterRegistry registry, Source channels) {
		this.registry = registry;
		this.registry.config().commonTags(
				"application", "kafka-writer-json",
				"application.name", "kafka-writer-json",
				"application.type", "source"
		);
		this.channels = channels;
		this.sent = Counter.builder(KAFKA_JSON_GENERATE_SEND)
				.baseUnit("SaleRecord")
				.description("Kafka Json Messages Written")
				.register(registry);
		this.finalized = Counter.builder(KAFKA_JSON_GENERATE_FINALIZED)
				.baseUnit("SaleRecord")
				.description("Kafka Json Records Finalized")
				.register(registry);
		this.voided = Counter.builder(KAFKA_JSON_GENERATE_VOID)
				.baseUnit("SaleRecord")
				.description("Kafka Json Records Voided")
				.register(registry);
	}

	@Scheduled(fixedRate = 1000L)
	public void generateData() {

		// Make a new object
		KafkaJsonData data = new KafkaJsonData();
		data.id = (Instant.now().getEpochSecond());
		data.valid = new Random().nextBoolean();
		data.profit = abs(new Random().nextInt());
		data.weekend = new Random().nextBoolean();

		// Register Metrics
		registerMetrics(data);
		System.out.println("Generating Message with id : " + data.id + "\nValid : "+ data.valid + "\nFinalized : " +!data.weekend);

		Message outputMessage = MessageBuilder
                .withPayload(data)
                .setHeader(MessageHeaders.CONTENT_TYPE, "application/json")
                .build();

		this.channels.output().send(outputMessage);
	}

	private void registerMetrics(KafkaJsonData data){
		// Record all creations
		sent.increment();

		// Record any sales generated over the weekend
		if(!data.weekend && data.valid){
			finalized.increment();
		}

		// Record any sales marked as voided by POS system
		if(!data.valid){
			voided.increment();
		}
	}
}
