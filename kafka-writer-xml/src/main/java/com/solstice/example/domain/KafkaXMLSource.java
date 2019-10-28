package com.solstice.example.domain;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

@Data
@JacksonXmlRootElement(localName = "SALE_RECORD")
public class KafkaXMLSource {
    // This class simulates the experience of receiving data from multiple sources.  This data is in XML format vs json.
    // we will map this data to our domain object and finish processing from there.

    @JacksonXmlProperty(localName = "SALE_ID")
    public long id;
    @JacksonXmlProperty(localName = "SOURCE_ID")
    public String source = "KAFKA_XML_SOURCE";
    @JacksonXmlProperty(localName = "IN_PROCESS")
    public boolean finalized;
    @JacksonXmlProperty(localName = "ACCEPTED")
    public boolean voided;
    @JacksonXmlProperty(localName = "VALUE")
    public int profit;
}
