package com.solstice.example.domain;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SaleRecord {
    public long saleId;
    public boolean finalized;
    public boolean voided;
    public BigDecimal profit;
}
