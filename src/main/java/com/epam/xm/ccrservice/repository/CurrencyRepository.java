package com.epam.xm.ccrservice.repository;

import com.epam.xm.ccrservice.model.Currency;
import com.epam.xm.ccrservice.model.projection.CurrencyFiltered;
import com.epam.xm.ccrservice.model.projection.CurrencyRange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface CurrencyRepository extends JpaRepository<Currency, Long> {

    List<Currency> findAll();

    /* may be better to make price calculation in java */
    @Query(nativeQuery = true, value = "select currency_code as currencyCode, ((max(price) - min(price))/min(price)) as normalizedRange \n" +
            "from currency\n" +
            "group by currency_code\n" +
            "order by normalizedRange desc") //no underscore in alias, otherwise null fields returned
    List<CurrencyRange> getCurrencyRanges();

    @Query(nativeQuery = true, value = "(select currency_code currencyCode, currency_date currencyDate, price, 'NEWEST' filterType\n" +
            "from currency\n" +
            "where currency.currency_code = ?1\n" +
            "order by currency_date desc\n" +
            "limit 1)\n" +
            "union all\n" +
            "(select currency_code currencyCode, currency_date currencyDate, price, 'OLDEST' filterType\n" +
            "from currency\n" +
            "where currency.currency_code = ?1\n" +
            "order by currency_date\n" +
            "limit 1)\n" +
            "union all\n" +
            "(select currency_code currencyCode, currency_date currencyDate, price, 'MAX' filterType\n" +
            "from currency\n" +
            "where currency.currency_code = ?1\n" +
            "order by price desc\n" +
            "limit 1)\n" +
            "union all\n" +
            "(select currency_code currencyCode, currency_date currencyDate, price, 'MIN' filterType\n" +
            "from currency\n" +
            "where currency.currency_code = ?1\n" +
            "order by price\n" +
            "limit 1)")
    List<CurrencyFiltered> getCurrencyFiltered(String code); //looks a bit bulky but has key advantages over programming filtering

    @Query(nativeQuery = true, value = "select currency_code as currencyCode, ((max(price) - min(price))/min(price)) as normalizedRange\n" +
            "from currency\n" +
            "where date_format(currency_date, '%Y-%m-%d') = ?1\n" + //example, just string: 2022-01-01
            "group by currencyCode\n" +
            "order by normalizedRange desc\n" +
            "limit 1")
    Optional<CurrencyRange> getCurrencyHighestRange(String date);
}
