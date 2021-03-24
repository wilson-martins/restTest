package com.bench.resttest.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FinancialTransactionsItemDto {

    @JsonProperty(value = "Date")
    private String date;

    @JsonProperty(value = "Ledger")
    private String ledger;

    @JsonProperty(value = "Amount")
    private BigDecimal amount;

    @JsonProperty(value = "Company")
    private String company;
}