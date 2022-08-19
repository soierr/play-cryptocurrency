package com.epam.xm.ccrservice.service.impl;

import com.epam.xm.ccrservice.model.Currency;
import com.epam.xm.ccrservice.model.CurrencyFiltered;
import com.epam.xm.ccrservice.model.CurrencyFilteredType;
import com.epam.xm.ccrservice.model.projection.CurrencyRange;
import com.epam.xm.ccrservice.repository.CurrencyRepository;
import com.epam.xm.ccrservice.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CurrencyServiceImpl implements CurrencyService {

    private final CurrencyRepository currencyRepository;

    @Override
    @Cacheable(value = "ranges")
    public List<CurrencyRange> getCurrencyRanges() {
        return currencyRepository.getCurrencyRanges();
    }

    @Override
    @Cacheable(value = "currency-filtered", key="#currencyCode")
    public List<CurrencyFiltered> getCurrencyFiltered(String currencyCode) {

        List<Currency> currencies = currencyRepository.getAllByCurrencyCodeOrderByCurrencyDateDesc(currencyCode);

        if (!currencies.isEmpty()) {

            List<CurrencyFiltered> currenciesFiltered = new ArrayList<>();

            CurrencyFiltered currencyFiltered = new CurrencyFiltered(currencyCode,
                    currencies.get(0).getCurrencyDate(),
                    CurrencyFilteredType.NEWEST,
                    currencies.get(0).getPrice());

            currenciesFiltered.add(currencyFiltered);

            currencyFiltered = new CurrencyFiltered(currencyCode,
                    currencies.get(currencies.size() - 1).getCurrencyDate(),
                    CurrencyFilteredType.OLDEST,
                    currencies.get(currencies.size() - 1).getPrice());

            currenciesFiltered.add(currencyFiltered);

            currenciesFiltered.add(currencies.stream()
                    .max(Comparator.comparing(Currency::getPrice))
                    .map(c -> new CurrencyFiltered(currencyCode,
                            c.getCurrencyDate(),
                            CurrencyFilteredType.MAX,
                            c.getPrice()))
                    .orElse(null));

            currenciesFiltered.add(currencies.stream()
                    .min(Comparator.comparing(Currency::getPrice))
                    .map(c -> new CurrencyFiltered(currencyCode,
                            c.getCurrencyDate(),
                            CurrencyFilteredType.MIN,
                            c.getPrice()))
                    .orElse(null));

            return currenciesFiltered;

        } else {
            return List.of();
        }

    }

    @Override
    public CurrencyRange getHighestRange(String date) {
        return currencyRepository.getCurrencyHighestRange(date);
    }
}