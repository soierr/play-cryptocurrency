package com.epam.xm.ccrservice.repository.impl;

import com.epam.xm.ccrservice.config.FileRepositoryConfiguration;
import com.epam.xm.ccrservice.model.PriceFile;
import com.epam.xm.ccrservice.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileRepositoryImpl implements FileRepository {

    private final FileRepositoryConfiguration fileRepositoryConfiguration;

    @Override
    public Set<PriceFile> loadFileInfos() {

        File[] files = Path.of(fileRepositoryConfiguration.getPriceFolderPath()).toFile().listFiles();

        if (files != null) {
            return Arrays.stream(files)
                    .map(this::createPriceFile)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toSet());
        } else {
            return Set.of();
        }
    }

    @Override
    public Optional<PriceFile> loadFile(String filename) {
        return createPriceFile(filename);
    }

    /*
     * Looks a bit complicated, but this is to avoid try/catch duplication
     * we use lend to reuse try catch wrapping
     * */
    private Optional<PriceFile> createPriceFile(File file) {

        return lend(file, () -> Files.readAttributes(file.toPath(), BasicFileAttributes.class))
                .flatMap(attrs -> lend(file, () -> PriceFile.builder()
                        .filename(file.getName())
                        .created(attrs.creationTime().toInstant())
                        .modified(attrs.lastModifiedTime().toInstant())
                        .build()));
    }

    private Optional<PriceFile> createPriceFile(String filePath) {

        filePath = fileRepositoryConfiguration.getPriceFolderPath() + "/" + filePath;

        File file = new File(filePath);

        PriceSupplier<PriceFile> supplier = () -> PriceFile.builder()
                .filename(file.getName())
                .data(IOUtils.toByteArray(file.toURI())) //input stream managed inside
                .build();

        return lend(file, supplier);
    }

    public static <T> Optional<T> lend(File file, PriceSupplier<T> supplier) {

        try {

            return Optional.of(supplier.get());

        } catch (IOException e) {
            log.error(String.format("Failed processing file: %s", file.toPath()), e);
            return Optional.empty();

        }
    }

    private interface PriceSupplier<T> {
        T get() throws IOException;
    }

}
