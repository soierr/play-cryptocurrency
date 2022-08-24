package com.epam.xm.ccrservice.controller;

import com.epam.xm.ccrservice.exception.ControllerValidationException;
import com.epam.xm.ccrservice.service.CurrencyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.context.request.WebRequest;

import javax.validation.ConstraintViolationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = CurrencyController.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CurrencyControllerExceptionTest extends BaseController {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @SpyBean
    @Autowired
    private CurrencyAdvice currencyAdvice;
    @MockBean
    private CurrencyService currencyService;

    @BeforeAll
    void setUp() {
        init("currency-controller", objectMapper);
    }

    @Test
    public void testGetRangesRuntimeException() throws Exception{

        DataIntegrityViolationException sampleException = new DataIntegrityViolationException("Data integrity violation");

        when(currencyService.getCurrencyRanges()).thenThrow(sampleException);

        String expectedResponse = getJson("expected-exception-div.json");

        MvcResult mvcResult = mockMvc.perform(get("/currency/ranges"))
                .andExpect(status().isOk())
                .andReturn();

        verify(currencyAdvice).handleRuntime(sampleException);
        assertEquals(expectedResponse, mvcResult.getResponse().getContentAsString());
    }

    @Test
    public void testGetCurrencyFilteredConstraintViolationException() throws Exception {

        String expectedResponse = getJson("expected-exception-cviol-filtered.json");

        MvcResult mvcResult = mockMvc.perform(get("/currency/filtered?currency_code="))
                .andExpect(status().isBadRequest())
                .andReturn();

        verify(currencyAdvice).handleConstraintViolation(any(ConstraintViolationException.class));
        assertEquals(expectedResponse, mvcResult.getResponse().getContentAsString());
    }

    @Test
    public void testGetCurrencyFilteredMissingParameterException() throws Exception {

        String expectedResponse = getJson("expected-exception-mispar-filtered.json");

        MvcResult mvcResult = mockMvc.perform(get("/currency/filtered"))
                .andExpect(status().isBadRequest())
                .andReturn();

        verify(currencyAdvice).handleMissingServletRequestParameter(any(MissingServletRequestParameterException.class),
                any(HttpHeaders.class),
                eq(HttpStatus.BAD_REQUEST),
                any(WebRequest.class));
        assertEquals(expectedResponse, mvcResult.getResponse().getContentAsString());
    }

    @Test
    public void testGetHighestRangeMissingParameterException() throws Exception {

        String expectedResponse = getJson("expected-exception-mispar-highest-range.json");

        MvcResult mvcResult = mockMvc.perform(get("/currency/highest-range"))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertEquals(expectedResponse, mvcResult.getResponse().getContentAsString());
    }

    @Test
    public void testGetHighestRangeConstraintViolationException() throws Exception{

        String expectedResponse = getJson("expected-exception-cviol-highest-range.json");

        MvcResult mvcResult = mockMvc.perform(get("/currency/highest-range?date="))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertEquals(expectedResponse, mvcResult.getResponse().getContentAsString());
    }

    @Test
    public void testGetHighestRangeControllerValidationException() throws Exception{

        String expectedResponse = getJson("expected-exception-cval-highest-range.json");

        MvcResult mvcResult = mockMvc.perform(get("/currency/highest-range?date=2010"))
                .andExpect(status().isBadRequest())
                .andReturn();

        verify(currencyAdvice).handleConstraintViolation(any(ControllerValidationException.class));
        assertEquals(expectedResponse, mvcResult.getResponse().getContentAsString());
    }
}
