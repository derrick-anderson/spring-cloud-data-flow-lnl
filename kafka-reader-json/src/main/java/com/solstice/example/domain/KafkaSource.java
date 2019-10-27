package com.solstice.example.domain;

import lombok.Data;

@Data
public class KafkaSource {
    public Long id;
    public String sourceName;
    public boolean valid;
    public int profit;
    public boolean weekend;
}
