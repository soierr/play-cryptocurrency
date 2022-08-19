package com.epam.xm.ccrservice.service.impl;

import com.epam.xm.ccrservice.model.Currency;
import com.epam.xm.ccrservice.model.PriceFile;
import com.epam.xm.ccrservice.repository.FileRepository;
import com.epam.xm.ccrservice.service.ParsingService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class CsvParsingService implements ParsingService {

    private static final String COMMA_DELIMITER = ",";

    private final FileRepository fileRepository;

    @Override
    public List<Currency> parseFile(PriceFile priceFile) {
        return fileRepository.loadFile(priceFile.getFilename())
                .map(this::parse)
                .orElse(List.of());
    }

    private List<Currency> parse(PriceFile file) {

        BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(file.getData()));

        List<Currency> currencies = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(bis))) {

            String line;

            br.readLine(); //skip header

            while ((line = br.readLine()) != null) {

                String[] values = line.split(COMMA_DELIMITER);

                Currency currency = new Currency();

                currency.setCurrencyDate(Instant.ofEpochMilli(Long.parseLong(values[0])));
                currency.setCurrencyCode(values[1]);
                currency.setPrice(new BigDecimal(values[2]));

                currencies.add(currency);

            }

        } catch (IOException e) {

            log.error("Failed to process file", e);
        }

        return currencies;
    }

}
