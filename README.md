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
  "profit":19676,
  "weekend":false
}
```

### `kafka-writer-xml`
This module creates a Kafka bound `Source` application that writes xml messages with the following structure
```xml
<SALE_RECORD>
    <SALE_ID>2937703603923367659</SALE_ID>
    <SOURCE_ID>KAFKA_XML_SOURCE</SOURCE_ID>
    <IN_PROCESS>true</IN_PROCESS>
    <ACCEPTED>true</ACCEPTED>
    <VALUE>11389</VALUE>
</SALE_RECORD>
```

### `kafka-reader-json / kafka-reader-xml`
This module creates a Kafka bound `Processor` application that reads json messages and transforms them into the domain
object below note the absence of the source name fields and the translation of the profit field and the weekend field. 
This demonstrates business and domain translation.

```json
{
  "saleId":2061885782503076571,
  "finalized":true,
  "voided":false,
  "profit":194.3
}
```

### `custom-header-processor`
Simple application that takes the finalized and voided attributes and mapps them to headers so that the router plugin 
be used to map to different log apps. 
This should be doable with the standard [Header Processor Application](https://docs.spring.io/spring-cloud-stream-app-starters/docs/Einstein.SR3/reference/htmlsingle/#spring-cloud-stream-modules-header-enricher-processor)
This can be downloaded with the [custom spring initializer](https://start-scs.cfapps.io/)

### `filter`
We will use the standard filter application with a spel expression to route based on header values set in the `custom-header-processor`

### `log-drain`
We will use the standard log application to log each type of messsages.

### `dataflow_stream_definition`
This will be a definition file that creates the stream definition. 
