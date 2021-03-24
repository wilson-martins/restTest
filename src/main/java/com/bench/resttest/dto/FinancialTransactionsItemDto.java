package com.bench.resttest.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FinancialTransactionsItemDto {

    @JsonProperty(value = "Date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date date;

    @JsonProperty(value = "Ledger")
    private String ledger;

    @JsonProperty(value = "Amount")
    private BigDecimal amount;

    @JsonProperty(value = "Company")
    private String company;
}