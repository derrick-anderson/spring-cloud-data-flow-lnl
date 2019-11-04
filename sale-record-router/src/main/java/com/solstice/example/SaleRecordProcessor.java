package com.solstice.example;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface SaleRecordProcessor{

    String INPUT = "input";
    String FINAL = "finalized";
    String VOID = "voided";

    @Input(INPUT)
    MessageChannel input();

    @Output(FINAL)
    MessageChannel finalized();

    @Output(VOID)
    MessageChannel voided();
}
