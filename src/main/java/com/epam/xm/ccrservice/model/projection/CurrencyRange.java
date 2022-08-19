package com.epam.xm.ccrservice.model.projection;

import java.math.BigDecimal;

public interface CurrencyRange {

    String getCurrencyCode();

    BigDecimal getNormalizedRange();

}
