package com.epam.xm.ccrservice.service.impl;

import com.epam.xm.ccrservice.model.FileInfo;
import com.epam.xm.ccrservice.model.PriceFile;
import com.epam.xm.ccrservice.repository.CurrencyRepository;
import com.epam.xm.ccrservice.repository.FileInfoRepository;
import com.epam.xm.ccrservice.repository.FileRepository;
import com.epam.xm.ccrservice.service.ParsingService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
/*
 * Can be implemented further with parallel processing for price files
 * */
public class FileLoaderService {

    private final FileInfoRepository fileInfoRepository;

    private final FileRepository fileRepository;

    private final CurrencyRepository currencyRepository;

    private final ParsingService parsingService;

    public FileLoaderService(FileInfoRepository fileInfoRepository,
                             FileRepository fileRepository,
                             CurrencyRepository currencyRepository,
                             ParsingService parsingService) {
        this.fileInfoRepository = fileInfoRepository;
        this.fileRepository = fileRepository;
        this.currencyRepository = currencyRepository;
        this.parsingService = parsingService;

        load(); //may be "try/catch"
    }

    private void load() {

        Set<PriceFile> targetFiles = fileRepository.loadFileInfos();

        targetFiles = new HashSet<>(targetFiles);

        Set<PriceFile> processed = fileInfoRepository.findAll().stream()
                .map(fi -> PriceFile.builder()
                        .filename(fi.getFilename())
                        .created(fi.getCreated())
                        .modified(fi.getModified())
                        .build())
                .collect(Collectors.toSet());

        targetFiles.removeAll(processed);

        targetFiles.forEach(tf -> {

            currencyRepository.saveAll(parsingService.parseFile(tf));

            FileInfo fileInfo = new FileInfo();
            fileInfo.setFilename(tf.getFilename());
            fileInfo.setCreated(tf.getCreated());
            fileInfo.setModified(tf.getModified());
            fileInfo.setLoaded(Instant.now());

            fileInfoRepository.save(fileInfo);
        });

    }
}
