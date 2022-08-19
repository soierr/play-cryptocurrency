package com.epam.xm.ccrservice.service;

import com.epam.xm.ccrservice.model.CurrencyFiltered;
import com.epam.xm.ccrservice.model.projection.CurrencyRange;

import java.util.List;

public interface CurrencyService {

    /* return a descending sorted list of all the cryptos, comparing the normalized range (i.e. (max-min)/min) */
    List<CurrencyRange> getCurrencyRanges();

    /* return the oldest/newest/min/max values for a requested crypto */
    List<CurrencyFiltered> getCurrencyFiltered(String currencyCode);

    /* return the crypto with the highest normalized range for a specific day */
    CurrencyRange getHighestRange(String date);

}
