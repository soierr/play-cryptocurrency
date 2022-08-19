package com.epam.xm.ccrservice.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.Instant;


@Data
@Entity
public class Currency {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private long code;

    private String currencyCode;

    private Instant currencyDate;

    private BigDecimal price;
}
