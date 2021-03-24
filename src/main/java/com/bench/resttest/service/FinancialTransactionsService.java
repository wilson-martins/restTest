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
        log.info("Start to run printFinancialTransactionsDailyBalance");

        List<FinancialTransactionsDailyBalanceDto> balanceDtoList = this.getFinancialTransactionsDailyBalance();

        if (balanceDtoList.isEmpty()) {
            log.error("");
            return;
        }

        for (FinancialTransactionsDailyBalanceDto balanceDto : balanceDtoList) {
            System.out.println(balanceDto.getDate() + " " + balanceDto.getDailyBalance());
        }

        log.info("Finished to run printFinancialTransactionsDailyBalance");
    }

    public List<FinancialTransactionsDailyBalanceDto> getFinancialTransactionsDailyBalance() {
        log.info("Start to run getFinancialTransactionsDailyBalance");

        Map<String, BigDecimal> transactionsMap = new ConcurrentHashMap<>();
        int page = 1, totalElements = 1, elementsRemaining = 1;
        do {
            FinancialTransactionsDto financialTransactionsDto = this.financialTransactionsProvider.getTransactions(page);

            if (financialTransactionsDto == null) {
                log.warn("FinancialTransactionsDto is Null");
            } else {
                if (totalElements == 1) {
                    totalElements = elementsRemaining = financialTransactionsDto.getTotalCount();
                    log.info("Total elements: [{}]", totalElements);
                }

                elementsRemaining -= financialTransactionsDto.getTransactions().size();
                log.info("Fetched [{}] transactions. [{}] remaining", financialTransactionsDto.getTransactions().size(), elementsRemaining);

                for (FinancialTransactionsItemDto itemDto : financialTransactionsDto.getTransactions()) {
                    BigDecimal balance = transactionsMap.getOrDefault(itemDto.getDate(), BigDecimal.ZERO);
                    transactionsMap.put(itemDto.getDate(), balance.add(itemDto.getAmount()));
                }
            }

            page++;
        } while (elementsRemaining > 0);

        List<FinancialTransactionsDailyBalanceDto> balanceDtoList = new ArrayList<>();

        for (Map.Entry<String, BigDecimal> entry : transactionsMap.entrySet()) {
            balanceDtoList.add(new FinancialTransactionsDailyBalanceDto(entry.getKey(), entry.getValue()));
        }
        balanceDtoList = balanceDtoList.stream().sorted(Comparator.comparing(FinancialTransactionsDailyBalanceDto::getDate)).collect(Collectors.toList());

        for (int i = 1; i < balanceDtoList.size(); i++) {
            balanceDtoList.get(i).setDailyBalance(balanceDtoList.get(i-1).getDailyBalance().add(balanceDtoList.get(i).getDailyBalance()));
        }

        log.info("Finished to run getFinancialTransactionsDailyBalance");
        return balanceDtoList;
    }
}
