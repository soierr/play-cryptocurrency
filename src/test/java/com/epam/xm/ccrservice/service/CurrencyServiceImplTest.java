package com.epam.xm.ccrservice.service;

import com.epam.xm.ccrservice.model.Currency;
import com.epam.xm.ccrservice.model.CurrencyFiltered;
import com.epam.xm.ccrservice.model.CurrencyFilteredType;
import com.epam.xm.ccrservice.repository.CurrencyRepository;
import com.epam.xm.ccrservice.service.impl.CurrencyServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CurrencyServiceImplTest {
    @Mock
    private CurrencyRepository currencyRepository;
    @InjectMocks
    private CurrencyServiceImpl currencyService;

    @Test
    void getCurrencyFilteredSucceededTest() {

        Instant oldestInstantSample = Instant.now().minus(10, ChronoUnit.MINUTES);
        Instant newestInstantSample = Instant.now().plus(10, ChronoUnit.MINUTES);
        Instant middleInstantMinPriceSample = Instant.now();
        Instant middleInstantMaxPriceSample = Instant.now();

        List<Currency> sampleCurrencies = new ArrayList<>();

        Currency currency = new Currency();
        currency.setCurrencyDate(middleInstantMinPriceSample);
        currency.setPrice(new BigDecimal(10));
        currency.setCurrencyCode("BTC");

        sampleCurrencies.add(currency);

        currency = new Currency();
        currency.setCurrencyDate(middleInstantMaxPriceSample);
        currency.setPrice(new BigDecimal(40));
        currency.setCurrencyCode("BTC");

        sampleCurrencies.add(currency);

        currency = new Currency();
        currency.setCurrencyDate(oldestInstantSample);
        currency.setPrice(new BigDecimal(22));
        currency.setCurrencyCode("BTC");

        sampleCurrencies.add(currency);

        currency = new Currency();
        currency.setCurrencyDate(newestInstantSample);
        currency.setPrice(new BigDecimal(23));
        currency.setCurrencyCode("BTC");

        sampleCurrencies.add(currency);

        when(currencyRepository.getAllByCurrencyCode("BTC")).thenReturn(sampleCurrencies);

        List<CurrencyFiltered> actualCurrencyFiltered = currencyService.getCurrencyFiltered("BTC");

        Set<CurrencyFiltered> expectedCurrencyFiltered = new HashSet<>();

        CurrencyFiltered currencyFiltered = new CurrencyFiltered("BTC",
                middleInstantMinPriceSample,
                CurrencyFilteredType.MIN,
                new BigDecimal(10));

        expectedCurrencyFiltered.add(currencyFiltered);

        currencyFiltered = new CurrencyFiltered("BTC",
                middleInstantMaxPriceSample,
                CurrencyFilteredType.MAX,
                new BigDecimal(40));

        expectedCurrencyFiltered.add(currencyFiltered);

        currencyFiltered = new CurrencyFiltered("BTC",
                oldestInstantSample,
                CurrencyFilteredType.OLDEST,
                new BigDecimal(22));

        expectedCurrencyFiltered.add(currencyFiltered);

        currencyFiltered = new CurrencyFiltered("BTC",
                newestInstantSample,
                CurrencyFilteredType.NEWEST,
                new BigDecimal(23));

        expectedCurrencyFiltered.add(currencyFiltered);

        assertIterableEquals(expectedCurrencyFiltered, new HashSet<>(actualCurrencyFiltered));

    }
}
