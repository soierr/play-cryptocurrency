package com.epam.xm.ccrservice.dto;

import com.epam.xm.ccrservice.model.FilterType;
import com.epam.xm.ccrservice.model.projection.CurrencyFiltered;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Getter
public class CurrencyFilteredZoned {
    private final String currencyCode;
    private final ZonedDateTime currencyDate;
    private final BigDecimal price;
    private final FilterType filterType;

    private CurrencyFilteredZoned(String currencyCode,
                                  ZonedDateTime currencyDate,
                                  BigDecimal price,
                                  FilterType filterType) {
        this.currencyCode = currencyCode;
        this.currencyDate = currencyDate;
        this.price = price;
        this.filterType = filterType;
    }

    public static CurrencyFilteredZoned from(CurrencyFiltered currencyFiltered) {
        return new CurrencyFilteredZoned(currencyFiltered.getCurrencyCode(),
                ZonedDateTime.ofInstant(currencyFiltered.getCurrencyDate(), ZoneId.systemDefault()),
                currencyFiltered.getPrice(),
                FilterType.valueOf(currencyFiltered.getFilterType()));
    }
}
