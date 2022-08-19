package com.epam.xm.ccrservice.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@AllArgsConstructor
@Setter
@Getter
@EqualsAndHashCode
public class CurrencyFiltered {

    private final String currencyCode;
    private final Instant currencyDate;
    private final CurrencyFilteredType type;
    private final BigDecimal price;

}
