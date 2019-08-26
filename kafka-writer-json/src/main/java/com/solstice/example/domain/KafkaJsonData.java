package com.solstice.example.domain;

import lombok.Data;

@Data
public class KafkaJsonData {

	public Long id;
	public String sourceName = "KafkaJsonWriter";
	public boolean valid;
	public int profit;
	public boolean weekend;

}
