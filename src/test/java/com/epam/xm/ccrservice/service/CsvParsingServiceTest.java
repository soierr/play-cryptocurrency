package com.epam.xm.ccrservice.service;

import com.epam.xm.ccrservice.model.Currency;
import com.epam.xm.ccrservice.model.PriceFile;
import com.epam.xm.ccrservice.repository.FileRepository;
import com.epam.xm.ccrservice.service.impl.CsvParsingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.core.AllOf.allOf;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CsvParsingServiceTest {

    @Mock
    private FileRepository fileRepository;

    @Test
    public void testParseFileSuccess() {

        byte[] data = "timestamp,symbol,price\r\n1641013200000,DOGE,0.1702\r\n".getBytes();

        PriceFile samplePriceFile = PriceFile.builder()
                .filename("DOGE_values.csv")
                .data(data)
                .build();

        when(fileRepository.loadFile("DOGE_values.csv")).thenReturn(Optional.of(samplePriceFile));

        CsvParsingService csvParsingService = new CsvParsingService(fileRepository);

        List<Currency> currencies = csvParsingService.parseFile(samplePriceFile);

        verify(fileRepository).loadFile("DOGE_values.csv");

        Currency expectedCurrency = new Currency();
        expectedCurrency.setCurrencyCode("DOGE");
        expectedCurrency.setPrice(BigDecimal.valueOf(0.1702));

        assertEquals(1, currencies.size());

        assertThat(currencies.get(0), allOf(hasProperty("currencyCode", equalTo("DOGE")),
                hasProperty("price", equalTo(BigDecimal.valueOf(0.1702)))));
    }

    @Test
    public void testWeWontFaiInCaseOfNullData() {

        PriceFile samplePriceFile = PriceFile.builder()
                .filename("DOGE_values.csv")
                .data(null)
                .build();

        when(fileRepository.loadFile("DOGE_values.csv")).thenReturn(Optional.of(samplePriceFile));

        CsvParsingService csvParsingService = new CsvParsingService(fileRepository);

        assertDoesNotThrow(() -> csvParsingService.parseFile(samplePriceFile));
    }

    @Test
    public void testLoadIsInvoked() {

        PriceFile samplePriceFile = PriceFile.builder()
                .filename("DOGE_values.csv")
                .data(null)
                .build();

        when(fileRepository.loadFile("DOGE_values.csv")).thenReturn(Optional.of(samplePriceFile));

        CsvParsingService csvParsingService = new CsvParsingService(fileRepository);

        List<Currency> currencies = csvParsingService.parseFile(samplePriceFile);

        verify(fileRepository).loadFile("DOGE_values.csv");

    }

    @Test
    public void testNullDataResultInEmptyList() {

        PriceFile samplePriceFile = PriceFile.builder()
                .filename("DOGE_values.csv")
                .data(null)
                .build();

        when(fileRepository.loadFile("DOGE_values.csv")).thenReturn(Optional.of(samplePriceFile));

        CsvParsingService csvParsingService = new CsvParsingService(fileRepository);

        List<Currency> currencies = csvParsingService.parseFile(samplePriceFile);

        assertEquals(0, currencies.size());
    }
}
