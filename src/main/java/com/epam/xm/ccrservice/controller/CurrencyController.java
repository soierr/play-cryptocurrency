package com.epam.xm.ccrservice.controller;

import com.epam.xm.ccrservice.model.CurrencyFiltered;
import com.epam.xm.ccrservice.model.projection.CurrencyRange;
import com.epam.xm.ccrservice.service.CurrencyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/currency")
@RequiredArgsConstructor
public class CurrencyController {

    private final CurrencyService currencyService;

    @Operation(description = "Returns a descending sorted list of all the cryptos, comparing the normalized range (i.e. (max-min)/min)")
    @GetMapping(value = "/ranges", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CurrencyRange> getCurrencyRanges() {
        return currencyService.getCurrencyRanges();
    }

    @Operation(description = "Returns the oldest/newest/min/max values for a requested crypto")
    @GetMapping(value = "/filtered", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CurrencyFiltered> getCurrencyFiltered(@RequestParam("currency_code")
                                                          @Parameter(required = true,
                                                                  schema = @Schema(type = "string", example = "BTC"),
                                                                  description = "Target currency code for filtering")
                                                                  String currencyCode) {
        return currencyService.getCurrencyFiltered(currencyCode);
    }

    @Operation(description = "Returns crypto with the highest normalized range for a specific day")
    @GetMapping(value = "/highest-range", produces = MediaType.APPLICATION_JSON_VALUE )
    public CurrencyRange getCurrencyRanges(@RequestParam("date")
                                               @Parameter(required = true,
                                                       schema = @Schema(type = "string", example = "2022-01-01"),
                                                       description = "Target day for filtering")
                                                       String date) {
        return currencyService.getHighestRange(date);
    }

}
