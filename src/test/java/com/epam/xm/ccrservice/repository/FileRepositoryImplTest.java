package com.epam.xm.ccrservice.repository;

import com.epam.xm.ccrservice.config.FileRepositoryConfiguration;
import com.epam.xm.ccrservice.exception.FileRepositoryException;
import com.epam.xm.ccrservice.model.PriceFile;
import com.epam.xm.ccrservice.repository.impl.FileRepositoryImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FileRepositoryImplTest {

    private static final String TEST_PRICE_FOLDER_PATH = "src\\test\\resources\\prices";
    @Mock
    private FileRepositoryConfiguration fileRepositoryConfiguration;

    @Test
    public void testConfigurationRead() {

        when(fileRepositoryConfiguration.getPriceFolderPath()).thenReturn("prices");

        FileRepository fileRepository = new FileRepositoryImpl(fileRepositoryConfiguration);

        fileRepository.loadFileInfos();

        verify(fileRepositoryConfiguration).getPriceFolderPath();
    }

    @Test
    public void testExceptionRaisedWhenNullConfigurationProvided() {

        when(fileRepositoryConfiguration.getPriceFolderPath()).thenReturn(null);

        assertThrows(FileRepositoryException.class,
                () -> new FileRepositoryImpl(fileRepositoryConfiguration),
                "Price folder configuration is null");
    }

    @Test
    public void testExactPriceFileInfoNumberLoaded() {

        when(fileRepositoryConfiguration.getPriceFolderPath()).thenReturn(TEST_PRICE_FOLDER_PATH);

        FileRepository fileRepository = new FileRepositoryImpl(fileRepositoryConfiguration);

        Set<PriceFile> prices = fileRepository.loadFileInfos();

        assertEquals(2, prices.size());

        /* strict check against size as well in contrast to hasItems*/
        assertThat(prices, containsInAnyOrder(allOf(hasProperty("filename", equalTo("BTC_values.csv")),
                hasProperty("created", instanceOf(Instant.class)),
                hasProperty("modified", instanceOf(Instant.class)),
                hasProperty("data", is(nullValue()))),
                allOf(hasProperty("filename", equalTo("DOGE_values.csv")),
                        hasProperty("created", instanceOf(Instant.class)),
                        hasProperty("modified", instanceOf(Instant.class)),
                        hasProperty("data", is(nullValue()))))); //nullValue() should be used, otherwise, null pointer
    }

    @Test
    public void testFileDataLoaded() {

        byte[] expectedData = ("timestamp,symbol,price\r\n" +
                "1641013200000,DOGE,0.1702\r\n" +
                "1641074400000,DOGE,0.1722\r\n" +
                "1641078000000,DOGE,0.1727\r\n").getBytes();

        when(fileRepositoryConfiguration.getPriceFolderPath()).thenReturn(TEST_PRICE_FOLDER_PATH);

        FileRepository fileRepository = new FileRepositoryImpl(fileRepositoryConfiguration);

        Optional<PriceFile> actualPriceFile = fileRepository.loadFile("DOGE_values.csv");

        assertTrue(actualPriceFile.isPresent());

        assertArrayEquals(expectedData, actualPriceFile.get().getData());

    }

}
