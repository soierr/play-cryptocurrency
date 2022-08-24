package com.epam.xm.ccrservice.model.projection;

import java.math.BigDecimal;
import java.time.Instant;

public interface CurrencyFiltered {

    String getCurrencyCode();

    Instant getCurrencyDate();

    String getFilterType();

    BigDecimal getPrice();

}
