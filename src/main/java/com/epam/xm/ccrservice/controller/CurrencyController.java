package com.epam.xm.ccrservice.controller;

import com.epam.xm.ccrservice.dto.CurrencyFilteredZoned;
import com.epam.xm.ccrservice.exception.ControllerNoContentException;
import com.epam.xm.ccrservice.exception.ControllerValidationException;
import com.epam.xm.ccrservice.model.projection.CurrencyRange;
import com.epam.xm.ccrservice.service.CurrencyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/currency")
@RequiredArgsConstructor
@Validated
public class CurrencyController {

    private static final String DATE_FORMAT = "yyyy-MM-dd";

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_FORMAT);

    private final CurrencyService currencyService;

    @Operation(description = "Returns a descending sorted list of all the cryptos, comparing the normalized range (i.e. (max-min)/min)")
    @GetMapping(value = "/ranges", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CurrencyRange> getCurrencyRanges() {
        return currencyService.getCurrencyRanges();
    }

    @Operation(description = "Returns the oldest/newest/min/max values for a requested crypto")
    @GetMapping(value = "/filtered", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CurrencyFilteredZoned> getCurrencyFiltered(@NotNull @NotEmpty @RequestParam("currency_code")
                                                          @Parameter(required = true,
                                                                  schema = @Schema(type = "string", example = "BTC"),
                                                                  description = "Target currency code for filtering")
                                                                  String currencyCode) {
        return currencyService.getCurrencyFiltered(currencyCode).stream()
                .map(CurrencyFilteredZoned::from)
                .collect(Collectors.toList());
    }

    @Operation(description = "Returns crypto with the highest normalized range for a specific day")
    @GetMapping(value = "/highest-range", produces = MediaType.APPLICATION_JSON_VALUE )
    public CurrencyRange getCurrencyRange(@NotNull @NotEmpty @RequestParam("date")
                                               @Parameter(required = true,
                                                       schema = @Schema(type = "string", example = "2022-01-01"),
                                                       description = "Target day for filtering")
                                                       String date) {
        try {
            LocalDate.parse(date, dateTimeFormatter);
        } catch (RuntimeException re) {
            throw new ControllerValidationException("Could not parse date parameter, format: " + DATE_FORMAT);
        }

        Optional<CurrencyRange> optionalCurrencyRange = currencyService.getHighestRange(date);

        if (optionalCurrencyRange.isEmpty()) {
            throw new ControllerNoContentException("No range available for requested date");
        } else {
            return optionalCurrencyRange.get();
        }
    }

}
