package com.epam.xm.ccrservice.service.impl;

import com.epam.xm.ccrservice.model.projection.CurrencyFiltered;
import com.epam.xm.ccrservice.model.projection.CurrencyRange;
import com.epam.xm.ccrservice.repository.CurrencyRepository;
import com.epam.xm.ccrservice.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
    @Cacheable(value = "currency-filtered", key = "#currencyCode")
    public List<CurrencyFiltered> getCurrencyFiltered(String currencyCode) {
        return currencyRepository.getCurrencyFiltered(currencyCode);
    }

    @Override
    public Optional<CurrencyRange> getHighestRange(String date) {
        return currencyRepository.getCurrencyHighestRange(date);
    }

}
