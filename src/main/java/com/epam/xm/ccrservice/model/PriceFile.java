package com.epam.xm.ccrservice.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.Instant;

@Builder
@EqualsAndHashCode(exclude = "data") //no need to compare arrays, might be time-consuming
@Getter
public class PriceFile {

    private final String filename;

    private final Instant created;

    private final Instant modified;

    private final byte[] data;
}
