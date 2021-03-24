package com.bench.resttest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinancialTransactionsDailyBalanceDto {

    private String date;

    private BigDecimal dailyBalance;

}
