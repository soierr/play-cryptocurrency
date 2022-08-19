package com.epam.xm.ccrservice.repository;

import com.epam.xm.ccrservice.model.Currency;
import com.epam.xm.ccrservice.model.projection.CurrencyRange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CurrencyRepository extends JpaRepository<Currency, Long> {

    List<Currency> findAll();

    /* may be better to make price calculation in java */
    @Query(nativeQuery = true, value = "select currency_code as currencyCode, ((max(price) - min(price))/min(price)) as normalizedRange \n" +
            "from currency\n" +
            "group by currency_code\n" +
            "order by normalizedRange desc") //no underscore in alias, otherwise null fields returned
    List<CurrencyRange> getCurrencyRanges();


    List<Currency> getAllByCurrencyCodeOrderByCurrencyDateDesc(String code);

    @Query(nativeQuery = true, value = "select currency_code as currencyCode, ((max(price) - min(price))/min(price)) as normalizedRange\n" +
            "from currency\n" +
            "where date_format(currency_date, '%Y-%m-%d') = ?1\n" + //example, just string: 2022-01-01
            "group by currencyCode\n" +
            "order by normalizedRange desc\n" +
            "limit 1")
    CurrencyRange getCurrencyHighestRange(String date);
}
