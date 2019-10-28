package com.solstice.example.domain;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

@Data
@JacksonXmlRootElement(localName = "SALE_RECORD")
public class XMLSource {
    @JacksonXmlProperty(localName = "SALE_ID")
    public long id;
    @JacksonXmlProperty(localName = "SOURCE_ID")
    public String source = "KAFKA_XML_SOURCE";
    @JacksonXmlProperty(localName = "IN_PROCESS")
    public boolean inProcess;
    @JacksonXmlProperty(localName = "ACCEPTED")
    public boolean accepted;
    @JacksonXmlProperty(localName = "VALUE")
    public int profit;
}