package com.epam.xm.ccrservice.controller;

import com.epam.xm.ccrservice.model.projection.CurrencyFiltered;
import com.epam.xm.ccrservice.model.projection.CurrencyRange;
import com.epam.xm.ccrservice.service.CurrencyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = CurrencyController.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CurrencyControllerTest extends BaseController {

    /* https://youtrack.jetbrains.com/issue/IDEA-295144/IDE-doesnt-see-autowired-mockmvc */
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private CurrencyService currencyService;

    @BeforeAll
    void setUp() {
        init("currency-controller", objectMapper);
    }

    @Test
    public void testGetRangesSuccess() throws Exception {

        List<CurrencyRange> sampleCurrencyRanges = new ArrayList<>();

        /* order matters */
        sampleCurrencyRanges.add(sampleCurrencyRange("ETH", BigDecimal.valueOf(0.638381)));
        sampleCurrencyRanges.add(sampleCurrencyRange("XRP", BigDecimal.valueOf(0.517857)));
        sampleCurrencyRanges.add(sampleCurrencyRange("LTC", BigDecimal.valueOf(0.465184)));
        sampleCurrencyRanges.add(sampleCurrencyRange("DOGE", BigDecimal.valueOf(0.461538)));
        sampleCurrencyRanges.add(sampleCurrencyRange("BTC", BigDecimal.valueOf(0.434121)));

        when(currencyService.getCurrencyRanges()).thenReturn(sampleCurrencyRanges);

        String expectedRanges = getJson("expected-ranges.json");

        MvcResult mvcResult = mockMvc.perform(get("/currency/ranges"))
                .andExpect(status().isOk())
                .andReturn();

        verify(currencyService).getCurrencyRanges();
        assertEquals(expectedRanges, mvcResult.getResponse().getContentAsString());
    }

    @Test
    public void testGetCurrencyFilteredBtc() throws Exception {

        List<CurrencyFiltered> sampleCurrencyFiltered = new ArrayList<>();

        sampleCurrencyFiltered.add(sampleCurrencyFiltered("BTC", Instant.ofEpochMilli(1643659200000L), "NEWEST", BigDecimal.valueOf(38415.79)));
        sampleCurrencyFiltered.add(sampleCurrencyFiltered("BTC", Instant.ofEpochMilli(1641009600000L), "OLDEST", BigDecimal.valueOf(46813.21)));
        sampleCurrencyFiltered.add(sampleCurrencyFiltered("BTC", Instant.ofEpochMilli(1641081600000L), "MAX", BigDecimal.valueOf(47722.66)));
        sampleCurrencyFiltered.add(sampleCurrencyFiltered("BTC", Instant.ofEpochMilli(1643022000000L), "MIN", BigDecimal.valueOf(33276.59)));

        when(currencyService.getCurrencyFiltered("BTC")).thenReturn(sampleCurrencyFiltered);

        String expectedFilteredBtc = getJson("expected-filtered-btc.json");

        MvcResult mvcResult = mockMvc.perform(get("/currency/filtered?currency_code=BTC"))
                .andExpect(status().isOk())
                .andReturn();

        verify(currencyService).getCurrencyFiltered("BTC");
        assertEquals(expectedFilteredBtc, mvcResult.getResponse().getContentAsString());
    }

    @Test
    public void testGetCurrencyFilteredDoge() throws Exception {

        List<CurrencyFiltered> sampleCurrencyFiltered = new ArrayList<>();

        sampleCurrencyFiltered.add(sampleCurrencyFiltered("DOGE", Instant.ofEpochMilli(1643655600000L), "NEWEST", BigDecimal.valueOf(0.14)));
        sampleCurrencyFiltered.add(sampleCurrencyFiltered("DOGE", Instant.ofEpochMilli(1641013200000L), "OLDEST", BigDecimal.valueOf(0.17)));
        sampleCurrencyFiltered.add(sampleCurrencyFiltered("DOGE", Instant.ofEpochMilli(1642176000000L), "MAX", BigDecimal.valueOf(0.19)));
        sampleCurrencyFiltered.add(sampleCurrencyFiltered("DOGE", Instant.ofEpochMilli(1642849200000L), "MIN", BigDecimal.valueOf(0.13)));

        when(currencyService.getCurrencyFiltered("DOGE")).thenReturn(sampleCurrencyFiltered);

        String expectedFilteredDoge = getJson("expected-filtered-doge.json");

        MvcResult mvcResult = mockMvc.perform(get("/currency/filtered?currency_code=DOGE"))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(expectedFilteredDoge, mvcResult.getResponse().getContentAsString());
    }

    @Test
    public void testGetCurrencyFilteredEth() throws Exception {

        List<CurrencyFiltered> sampleCurrencyFiltered = new ArrayList<>();

        sampleCurrencyFiltered.add(sampleCurrencyFiltered("ETH", Instant.ofEpochMilli(1643659200000L), "NEWEST", BigDecimal.valueOf(2672.50)));
        sampleCurrencyFiltered.add(sampleCurrencyFiltered("ETH", Instant.ofEpochMilli(1641024000000L), "OLDEST", BigDecimal.valueOf(3715.32)));
        sampleCurrencyFiltered.add(sampleCurrencyFiltered("ETH", Instant.ofEpochMilli(1641168000000L), "MAX", BigDecimal.valueOf(3828.11)));
        sampleCurrencyFiltered.add(sampleCurrencyFiltered("ETH", Instant.ofEpochMilli(1643018400000L), "MIN", BigDecimal.valueOf(2336.52)));

        when(currencyService.getCurrencyFiltered("ETH")).thenReturn(sampleCurrencyFiltered);

        String expectedFilteredDoge = getJson("expected-filtered-eth.json");

        MvcResult mvcResult = mockMvc.perform(get("/currency/filtered?currency_code=ETH"))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(expectedFilteredDoge, mvcResult.getResponse().getContentAsString());
    }

    @Test
    public void testGetCurrencyFilteredLtc() throws Exception {

        List<CurrencyFiltered> sampleCurrencyFiltered = new ArrayList<>();

        sampleCurrencyFiltered.add(sampleCurrencyFiltered("LTC", Instant.ofEpochMilli(1643648400000L), "NEWEST", BigDecimal.valueOf(109.60)));
        sampleCurrencyFiltered.add(sampleCurrencyFiltered("LTC", Instant.ofEpochMilli(1641016800000L), "OLDEST", BigDecimal.valueOf(148.10)));
        sampleCurrencyFiltered.add(sampleCurrencyFiltered("LTC", Instant.ofEpochMilli(1642420800000L), "MAX", BigDecimal.valueOf(151.50)));
        sampleCurrencyFiltered.add(sampleCurrencyFiltered("LTC", Instant.ofEpochMilli(1643018400000L), "MIN", BigDecimal.valueOf(103.40)));

        when(currencyService.getCurrencyFiltered("LTC")).thenReturn(sampleCurrencyFiltered);

        String expectedFilteredDoge = getJson("expected-filtered-ltc.json");

        MvcResult mvcResult = mockMvc.perform(get("/currency/filtered?currency_code=LTC"))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(expectedFilteredDoge, mvcResult.getResponse().getContentAsString());
    }

    @Test
    public void testGetCurrencyFilteredXrp() throws Exception {

        List<CurrencyFiltered> sampleCurrencyFiltered = new ArrayList<>();

        sampleCurrencyFiltered.add(sampleCurrencyFiltered("XRP", Instant.ofEpochMilli(1643590800000L), "NEWEST", BigDecimal.valueOf(0.59)));
        sampleCurrencyFiltered.add(sampleCurrencyFiltered("XRP", Instant.ofEpochMilli(1640995200000L), "OLDEST", BigDecimal.valueOf(0.83)));
        sampleCurrencyFiltered.add(sampleCurrencyFiltered("XRP", Instant.ofEpochMilli(1641070800000L), "MAX", BigDecimal.valueOf(0.85)));
        sampleCurrencyFiltered.add(sampleCurrencyFiltered("XRP", Instant.ofEpochMilli(1643022000000L), "MIN", BigDecimal.valueOf(0.56)));

        when(currencyService.getCurrencyFiltered("XRP")).thenReturn(sampleCurrencyFiltered);

        String expectedFilteredDoge = getJson("expected-filtered-xrp.json");

        MvcResult mvcResult = mockMvc.perform(get("/currency/filtered?currency_code=XRP"))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(expectedFilteredDoge, mvcResult.getResponse().getContentAsString());
    }

    @Test
    public void testGetHighestRangeSuccess() throws Exception {

        CurrencyRange sampleCurrencyRange = sampleCurrencyRange("XRP", BigDecimal.valueOf(0.024096));

        when(currencyService.getHighestRange("2022-01-01")).thenReturn(Optional.of(sampleCurrencyRange));

        String expectedRanges = getJson("expected-highest-range.json");

        MvcResult mvcResult = mockMvc.perform(get("/currency/highest-range?date=2022-01-01"))
                .andExpect(status().isOk())
                .andReturn();

        verify(currencyService).getHighestRange("2022-01-01");
        assertEquals(expectedRanges, mvcResult.getResponse().getContentAsString());
    }

    protected static CurrencyRange sampleCurrencyRange(String currencyCode, BigDecimal normalizedRange) {
        return new CurrencyRange() {
            @Override
            public String getCurrencyCode() {
                return currencyCode;
            }

            @Override
            public BigDecimal getNormalizedRange() {
                return normalizedRange;
            }
        };
    }

    private static CurrencyFiltered sampleCurrencyFiltered(String currencyCode, Instant currencyDate, String filterType, BigDecimal price) {
        return new CurrencyFiltered() {
            @Override
            public String getCurrencyCode() {
                return currencyCode;
            }

            @Override
            public Instant getCurrencyDate() {
                return currencyDate;
            }

            @Override
            public String getFilterType() {
                return filterType;
            }

            @Override
            public BigDecimal getPrice() {
                return price;
            }
        };
    }

}
