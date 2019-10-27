# Spring Cloud Data Flow Reference

This repository provides the code the support the demonstration of the abilities of Spring Cloud Data Flow.

## Modules

### `kafka-writer-json`
This module creates a Kafka bound `Source` application that writes json messages with the following format. 
```json
{
  "id":1572210122,
  "sourceName":"KafkaJsonWriter",
  "valid":false,
  "profit":1967695307,
  "weekend":false
}
```

### `kafka-reader-json`
This module creates a Kafka bound `Processor` application that reads json messages and transforms them into the domain
object below 