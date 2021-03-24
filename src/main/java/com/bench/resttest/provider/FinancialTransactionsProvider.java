package com.bench.resttest.provider;

import com.bench.resttest.dto.FinancialTransactionsDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

@Slf4j
@Service
public class FinancialTransactionsProvider {

    private final String API_URL = "https://resttest.bench.co/transactions/{page}.json";

    public FinancialTransactionsDto getTransactions(Integer page) {
        return this.getTransactions(page, 3);
    }

    private FinancialTransactionsDto getTransactions(Integer page, int retriesLeft) {
        if (page == null) {
            log.warn("Page is Null. Skipped GET - Transactions call.");
            return null;
        }

        ResponseEntity<FinancialTransactionsDto> responseEntity;
        try {
            URL url = new URL(API_URL.replace("{page}", page.toString()));
            RestTemplate restTemplate = new RestTemplate();
            responseEntity = restTemplate.getForEntity(url.toURI(), FinancialTransactionsDto.class);
        } catch (MalformedURLException | URISyntaxException e) {
            log.warn("Could not call financial transactions API", e);
            responseEntity = null;
        }

        if (responseEntity == null || !HttpStatus.OK.equals(responseEntity.getStatusCode())) {
            if (retriesLeft > 0) {
                log.warn("Could not retrieve valid FinancialTransactionsDto. Trying again! Retries left [{}]", retriesLeft);
                return getTransactions(page, --retriesLeft);
            } else {
                return null;
            }
        }

        return responseEntity.getBody();
    }
}
