package com.bench.resttest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class FinancialTransactionsDailyBalanceDto {

    private String date;

    private BigDecimal dailyBalance;

}
