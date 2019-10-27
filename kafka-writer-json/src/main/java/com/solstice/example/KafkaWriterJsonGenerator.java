package com.solstice.example;

import com.solstice.example.domain.KafkaJsonData;
import io.micrometer.core.instrument.Counter;
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

	private Source channels;
	private final String KAFKA_JSON_GENERATE_SEND = "kafka.json.generate.send";
	private final String KAFKA_JSON_GENERATE_VOID = "kafka.json.generate.void";
	private final String KAFKA_JSON_GENERATE_FINALIZED = "kafka.json.generate.finalized";

	// Declare our micrometer registry and counters to be used.
	private PrometheusMeterRegistry registry;
	private Counter sent;
	private Counter finalized;
	private Counter voided;

	public KafkaWriterJsonGenerator(PrometheusMeterRegistry registry, Source channels) {
		this.registry = registry;
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

	@Scheduled(fixedRateString = "1000")
	public void generateData() {

		// Make a new object
		KafkaJsonData data = new KafkaJsonData();
		data.id = (Instant.now().getEpochSecond());
		data.valid = new Random().nextBoolean();
		data.profit = abs(new Random().nextInt());
		data.weekend = new Random().nextBoolean();

		// Register Metrics
		registerMetrics(data);

		this.channels.output().send(MessageBuilder.withPayload(data).build());
	}

	private void registerMetrics(KafkaJsonData data){
		// Record all creations
		sent.increment();

		// Record any sales generated over the weekend
		if(!data.weekend){
			finalized.increment();
		}

		// Record any sales marked as voided by POS system
		if(!data.valid){
			voided.increment();
		}
	}
}
