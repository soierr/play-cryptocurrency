package com.epam.xm.ccrservice.model.projection;

import java.math.BigDecimal;

public interface CurrencyRangeDay {

    String getCurrencyCode();

    BigDecimal getNormalizedRange();

    String getRequestedDay();

}
