import com.bench.resttest.app.Application;
import com.bench.resttest.dto.FinancialTransactionsDailyBalanceDto;
import com.bench.resttest.dto.FinancialTransactionsDto;
import com.bench.resttest.provider.FinancialTransactionsProvider;
import com.bench.resttest.service.FinancialTransactionsService;
import com.bench.resttest.util.FileUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.AdditionalAnswers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RunWith(SpringRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SpringBootTest(classes = {Application.class})
public class FinancialTransactionsServiceTest {

    @Autowired
    private FinancialTransactionsService financialTransactionsService;

    @Autowired
    private FinancialTransactionsProvider financialTransactionsProvider;

    private void mockProvider(final Map<Integer, FinancialTransactionsDto> response) {
        FinancialTransactionsProvider financialTransactionsProviderMock = Mockito.mock(FinancialTransactionsProvider.class, AdditionalAnswers.delegatesTo(this.financialTransactionsProvider));
        response.forEach((k, v) -> {
            Mockito.doReturn(v).when(financialTransactionsProviderMock).getTransactions(k);
        });
        ReflectionTestUtils.setField(financialTransactionsService, "financialTransactionsProvider", financialTransactionsProviderMock);
    }

    @Test
    public void a_getFinancialTransactionsDailyBalance_validCase() {
        // Mock provider
        Map<Integer, FinancialTransactionsDto> mockResponse = new HashMap<>();
        mockResponse.put(1, getFinancialTransactionsDto("transactions_a_1.json"));
        mockProvider(mockResponse);

        List<FinancialTransactionsDailyBalanceDto> balanceDtoList = this.financialTransactionsService.getFinancialTransactionsDailyBalance();
        balanceDtoList = balanceDtoList.stream().sorted(Comparator.comparing(FinancialTransactionsDailyBalanceDto::getDate)).collect(Collectors.toList());

        Assert.assertEquals("2013-12-12", balanceDtoList.get(0).getDate());
        Assert.assertEquals(BigDecimal.valueOf(-45.05), balanceDtoList.get(0).getDailyBalance());
        Assert.assertEquals("2013-12-13", balanceDtoList.get(1).getDate());
        Assert.assertEquals(BigDecimal.valueOf(-55.55), balanceDtoList.get(1).getDailyBalance());
        Assert.assertEquals("2013-12-14", balanceDtoList.get(2).getDate());
        Assert.assertEquals(BigDecimal.valueOf(-30.55), balanceDtoList.get(2).getDailyBalance());
    }

    private FinancialTransactionsDto getFinancialTransactionsDto(final String fileName) {
        try {
            File file = FileUtil.loadFileFromPath("/files/" + fileName);
            String fileData = FileUtil.readFile(file.getPath());
            return new ObjectMapper().readValue(fileData, FinancialTransactionsDto.class);
        } catch (IOException e) {
            log.error("Error reading file", e);
        }

        return new FinancialTransactionsDto();
    }


}
