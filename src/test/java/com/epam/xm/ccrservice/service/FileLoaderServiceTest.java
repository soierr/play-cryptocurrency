package com.epam.xm.ccrservice.service;

import com.epam.xm.ccrservice.model.FileInfo;
import com.epam.xm.ccrservice.model.PriceFile;
import com.epam.xm.ccrservice.repository.CurrencyRepository;
import com.epam.xm.ccrservice.repository.FileInfoRepository;
import com.epam.xm.ccrservice.repository.FileRepository;
import com.epam.xm.ccrservice.service.impl.FileLoaderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FileLoaderServiceTest {
    @Mock
    private FileInfoRepository fileInfoRepository;
    @Mock
    private FileRepository fileRepository;
    @Mock
    private CurrencyRepository currencyRepository;
    @Mock
    private ParsingService parsingService;

    @Test
    public void testLoadAllReadMethodsInvoked() {

        new FileLoaderService(fileInfoRepository,
                fileRepository,
                currencyRepository,
                parsingService);

        verify(fileRepository).loadFileInfos();
        verify(fileInfoRepository).findAll();
    }

    @Test
    public void testLoadAllSavingMethodsInvoked() {

        Set<PriceFile> samplePriceFileInfos = new HashSet<>();

        Instant sampleCreated = Instant.now();
        Instant sampleModified = Instant.now().plus(10, ChronoUnit.SECONDS);

        PriceFile samplePriceFile = PriceFile.builder()
                .filename("BTC_values.csv")
                .created(sampleCreated)
                .modified(sampleModified)
                .build();

        samplePriceFileInfos.add(samplePriceFile);

        samplePriceFile = PriceFile.builder()
                .filename("DOGE_values.csv")
                .created(sampleCreated)
                .modified(sampleModified)
                .build();

        samplePriceFileInfos.add(samplePriceFile);

        when(fileRepository.loadFileInfos()).thenReturn(samplePriceFileInfos);

        new FileLoaderService(fileInfoRepository,
                fileRepository,
                currencyRepository,
                parsingService);

        verify(currencyRepository, times(2)).saveAll(anyList());
        verify(fileInfoRepository, times(2)).save(any(FileInfo.class));
    }

    @Test
    public void testWeDontParseAlreadyProcessedFiles() {

        ArgumentCaptor<PriceFile> priceFileCaptor = ArgumentCaptor.forClass(PriceFile.class);

        Set<PriceFile> samplePriceFileInfos = new HashSet<>();

        Instant sampleCreated = Instant.now();
        Instant sampleModified = Instant.now().plus(10, ChronoUnit.SECONDS);

        PriceFile samplePriceFile = PriceFile.builder()
                .filename("BTC_values.csv")
                .created(sampleCreated)
                .modified(sampleModified)
                .build();

        samplePriceFileInfos.add(samplePriceFile);

        samplePriceFile = PriceFile.builder()
                .filename("DOGE_values.csv")
                .created(sampleCreated)
                .modified(sampleModified)
                .build();

        samplePriceFileInfos.add(samplePriceFile);

        FileInfo sampleDuplicatedFileInfo = new FileInfo();
        sampleDuplicatedFileInfo.setLoaded(Instant.now());
        sampleDuplicatedFileInfo.setCreated(sampleCreated);
        sampleDuplicatedFileInfo.setModified(sampleModified);
        sampleDuplicatedFileInfo.setFilename("BTC_values.csv");

        when(fileRepository.loadFileInfos()).thenReturn(samplePriceFileInfos);
        when(fileInfoRepository.findAll()).thenReturn(List.of(sampleDuplicatedFileInfo));

        new FileLoaderService(fileInfoRepository,
                fileRepository,
                currencyRepository,
                parsingService);

        PriceFile expectedDogeFile = PriceFile.builder()
                .filename("DOGE_values.csv")
                .created(sampleCreated)
                .modified(sampleModified)
                .build();

        verify(parsingService).parseFile(priceFileCaptor.capture()); //fails if more than one time invoked
        assertEquals(expectedDogeFile, priceFileCaptor.getValue());
    }
}
