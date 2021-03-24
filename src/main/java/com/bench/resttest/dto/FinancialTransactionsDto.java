package com.bench.resttest.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FinancialTransactionsDto {

    @JsonProperty(value = "totalCount")
    private Integer totalCount;

    @JsonProperty(value = "page")
    private Integer page;

    @JsonProperty(value = "transactions")
    private List<FinancialTransactionsItemDto> transactions = new ArrayList<>();
}
