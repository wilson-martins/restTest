package com.bench.resttest.service;

import com.bench.resttest.dto.FinancialTransactionsDailyBalanceDto;
import com.bench.resttest.dto.FinancialTransactionsDto;
import com.bench.resttest.dto.FinancialTransactionsItemDto;
import com.bench.resttest.provider.FinancialTransactionsProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FinancialTransactionsService {

    private final FinancialTransactionsProvider financialTransactionsProvider;

    @Autowired
    public FinancialTransactionsService(FinancialTransactionsProvider financialTransactionsProvider) {
        this.financialTransactionsProvider = financialTransactionsProvider;
    }

    public void printFinancialTransactionsDailyBalance() {
        List<FinancialTransactionsDailyBalanceDto> balanceDtoList = this.getFinancialTransactionsDailyBalance();

        if (balanceDtoList.isEmpty()) {
            log.error("");
            return;
        }

        balanceDtoList = balanceDtoList.stream().sorted(Comparator.comparing(FinancialTransactionsDailyBalanceDto::getDate).reversed()).collect(Collectors.toList());

        for (FinancialTransactionsDailyBalanceDto balanceDto : balanceDtoList) {
            System.out.println(balanceDto.getDate() + " " + balanceDto.getDailyBalance());
        }
    }

    private List<FinancialTransactionsDailyBalanceDto> getFinancialTransactionsDailyBalance() {

        Map<String, BigDecimal> transactionsMap = new ConcurrentHashMap<>();
        int page = 1, totalPages = 1;
        do {
            FinancialTransactionsDto financialTransactionsDto = this.financialTransactionsProvider.getTransactions(page);

            if (financialTransactionsDto == null) {
                log.error("FinancialTransactionsDto is Null");
                return new ArrayList<>();
            }

            if (totalPages == 1) {
                totalPages = financialTransactionsDto.getTotalCount();
            }

            for (FinancialTransactionsItemDto itemDto : financialTransactionsDto.getTransactions()) {
                BigDecimal balance = transactionsMap.getOrDefault(itemDto.getDate(), BigDecimal.ZERO);
                transactionsMap.put(itemDto.getDate(), balance.add(itemDto.getAmount()));
            }

        } while (page < totalPages);

        List<FinancialTransactionsDailyBalanceDto> balanceDtoList = new ArrayList<>();
        transactionsMap.forEach((k, v) -> balanceDtoList.add(new FinancialTransactionsDailyBalanceDto(k, v)));

        return balanceDtoList;
    }
}
