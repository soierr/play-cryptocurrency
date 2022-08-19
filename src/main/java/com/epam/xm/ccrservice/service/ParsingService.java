package com.epam.xm.ccrservice.service;

import com.epam.xm.ccrservice.model.Currency;
import com.epam.xm.ccrservice.model.PriceFile;

import java.util.List;

public interface ParsingService {

    List<Currency> parseFile(PriceFile priceFile);

}
