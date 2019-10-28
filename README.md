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

### `kafka-router`
Simple application that takes the finalized and voided attributes and mapps them to headers so that the router plugin 
be used to map to different log apps. 
ALTERNATIVELY, don't us `@SendTo` or change the Send to's to be Messages as a return and apply the headers there. 