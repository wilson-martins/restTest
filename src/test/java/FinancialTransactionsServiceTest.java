import com.bench.resttest.app.Application;
import com.bench.resttest.dto.FinancialTransactionsDailyBalanceDto;
import com.bench.resttest.dto.FinancialTransactionsDto;
import com.bench.resttest.provider.FinancialTransactionsProvider;
import com.bench.resttest.service.FinancialTransactionsService;
import com.bench.resttest.util.FileUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.AdditionalAnswers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.ArrayList;
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

    @MockBean
    private FinancialTransactionsService financialTransactionsServiceMock;

    @Autowired
    private FinancialTransactionsProvider financialTransactionsProvider;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    private void mockProvider(final Map<Integer, FinancialTransactionsDto> response) {
        FinancialTransactionsProvider financialTransactionsProviderMock = Mockito.mock(FinancialTransactionsProvider.class, AdditionalAnswers.delegatesTo(this.financialTransactionsProvider));
        response.forEach((k, v) -> Mockito.doReturn(v).when(financialTransactionsProviderMock).getTransactions(k));
        ReflectionTestUtils.setField(financialTransactionsServiceMock, "financialTransactionsProvider", financialTransactionsProviderMock);
    }

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @After
    public void restoreStreams() {
        System.setOut(originalOut);
    }

    @Test
    public void a_getFinancialTransactionsDailyBalance_ValidCase() {
        // Mock provider
        Map<Integer, FinancialTransactionsDto> mockResponse = new HashMap<>();
        mockResponse.put(1, getFinancialTransactionsDto("transactions_a_1.json"));
        mockProvider(mockResponse);

        Mockito.doCallRealMethod().when(financialTransactionsServiceMock).getFinancialTransactionsDailyBalance();
        List<FinancialTransactionsDailyBalanceDto> balanceDtoList = this.financialTransactionsServiceMock.getFinancialTransactionsDailyBalance();

        Assert.assertEquals("2013-12-12", balanceDtoList.get(0).getDate());
        Assert.assertEquals(BigDecimal.valueOf(-45.05), balanceDtoList.get(0).getDailyBalance());
        Assert.assertEquals("2013-12-13", balanceDtoList.get(1).getDate());
        Assert.assertEquals(BigDecimal.valueOf(-55.55), balanceDtoList.get(1).getDailyBalance());
        Assert.assertEquals("2013-12-14", balanceDtoList.get(2).getDate());
        Assert.assertEquals(BigDecimal.valueOf(-30.55), balanceDtoList.get(2).getDailyBalance());
    }

    @Test
    public void b_getFinancialTransactionsDailyBalance_MultiplePagesValidCase() {
        // Mock provider
        Map<Integer, FinancialTransactionsDto> mockResponse = new HashMap<>();
        mockResponse.put(1, getFinancialTransactionsDto("transactions_b_1.json"));
        mockResponse.put(2, getFinancialTransactionsDto("transactions_b_2.json"));

        mockProvider(mockResponse);

        Mockito.doCallRealMethod().when(financialTransactionsServiceMock).getFinancialTransactionsDailyBalance();
        List<FinancialTransactionsDailyBalanceDto> balanceDtoList = this.financialTransactionsServiceMock.getFinancialTransactionsDailyBalance();

        Assert.assertEquals("2013-12-21", balanceDtoList.get(0).getDate());
        Assert.assertEquals(BigDecimal.valueOf(-17.98), balanceDtoList.get(0).getDailyBalance());
        Assert.assertEquals("2013-12-22", balanceDtoList.get(1).getDate());
        Assert.assertEquals(BigDecimal.valueOf(-128.69), balanceDtoList.get(1).getDailyBalance());
    }

    @Test
    public void c_getFinancialTransactionsDailyBalance_ProviderReturnNullInvalidCase() {
        // Mock provider
        FinancialTransactionsProvider financialTransactionsProviderMock = Mockito.mock(FinancialTransactionsProvider.class, AdditionalAnswers.delegatesTo(this.financialTransactionsProvider));
        Mockito.doReturn(null).when(financialTransactionsProviderMock).getTransactions(Mockito.anyInt());
        ReflectionTestUtils.setField(financialTransactionsServiceMock, "financialTransactionsProvider", financialTransactionsProviderMock);

        Mockito.doCallRealMethod().when(financialTransactionsServiceMock).getFinancialTransactionsDailyBalance();
        List<FinancialTransactionsDailyBalanceDto> balanceDtoList = this.financialTransactionsServiceMock.getFinancialTransactionsDailyBalance();
        balanceDtoList = balanceDtoList.stream().sorted(Comparator.comparing(FinancialTransactionsDailyBalanceDto::getDate)).collect(Collectors.toList());

        Assert.assertNotNull(balanceDtoList);
        Assert.assertEquals(0, balanceDtoList.size());
    }

    @Test
    public void d_getFinancialTransactionsDailyBalance_ProviderReturnEmptyObjectInvalidCase() {
        // Mock provider
        FinancialTransactionsDto financialTransactionsDto = new FinancialTransactionsDto();
        financialTransactionsDto.setPage(1);
        financialTransactionsDto.setTotalCount(0);
        financialTransactionsDto.setTransactions(new ArrayList<>());

        FinancialTransactionsProvider financialTransactionsProviderMock = Mockito.mock(FinancialTransactionsProvider.class, AdditionalAnswers.delegatesTo(this.financialTransactionsProvider));
        Mockito.doReturn(null).when(financialTransactionsProviderMock).getTransactions(Mockito.anyInt());
        Mockito.doReturn(financialTransactionsDto).when(financialTransactionsProviderMock).getTransactions(1);
        ReflectionTestUtils.setField(financialTransactionsServiceMock, "financialTransactionsProvider", financialTransactionsProviderMock);

        Mockito.doCallRealMethod().when(financialTransactionsServiceMock).getFinancialTransactionsDailyBalance();
        List<FinancialTransactionsDailyBalanceDto> balanceDtoList = this.financialTransactionsServiceMock.getFinancialTransactionsDailyBalance();
        balanceDtoList = balanceDtoList.stream().sorted(Comparator.comparing(FinancialTransactionsDailyBalanceDto::getDate)).collect(Collectors.toList());

        Assert.assertNotNull(balanceDtoList);
        Assert.assertEquals(0, balanceDtoList.size());
    }

    @Test
    public void e_printFinancialTransactionsDailyBalance_validCase() {
        List<FinancialTransactionsDailyBalanceDto> balanceDtoList = new ArrayList<>();
        FinancialTransactionsDailyBalanceDto balanceDto = new FinancialTransactionsDailyBalanceDto("2013-12-21", BigDecimal.valueOf(-17.98));
        balanceDtoList.add(balanceDto);
        balanceDto = new FinancialTransactionsDailyBalanceDto("2013-12-22", BigDecimal.valueOf(-128.69));
        balanceDtoList.add(balanceDto);

        Mockito.doReturn(balanceDtoList).when(financialTransactionsServiceMock).getFinancialTransactionsDailyBalance();
        Mockito.doCallRealMethod().when(financialTransactionsServiceMock).printFinancialTransactionsDailyBalance();

        this.financialTransactionsServiceMock.printFinancialTransactionsDailyBalance();

        Assert.assertEquals("2013-12-21 -17.98\n2013-12-22 -128.69\n", outContent.toString());
    }

    @Test
    public void f_printFinancialTransactionsDailyBalance_invalidCase() {
        Mockito.doReturn(new ArrayList<>()).when(financialTransactionsServiceMock).getFinancialTransactionsDailyBalance();
        Mockito.doCallRealMethod().when(financialTransactionsServiceMock).printFinancialTransactionsDailyBalance();

        this.financialTransactionsServiceMock.printFinancialTransactionsDailyBalance();

        Assert.assertEquals("", outContent.toString());
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
