package com.epam.xm.ccrservice.repository.impl;

import com.epam.xm.ccrservice.config.FileRepositoryConfiguration;
import com.epam.xm.ccrservice.exception.FileRepositoryException;
import com.epam.xm.ccrservice.model.PriceFile;
import com.epam.xm.ccrservice.repository.FileRepository;
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
@Slf4j
public class FileRepositoryImpl implements FileRepository {

    private static final String PRICE_FOLDER_NULL = "Price folder configuration is null";

    private final String priceFolder;

    public FileRepositoryImpl(FileRepositoryConfiguration fileRepositoryConfiguration) {

        String priceFolder = fileRepositoryConfiguration.getPriceFolderPath();

        if (priceFolder == null) { /* unlikely, though spring validation can be disabled... somehow */
            throw new FileRepositoryException(PRICE_FOLDER_NULL);
        }

        this.priceFolder = priceFolder;
    }

    @Override
    public Set<PriceFile> loadFileInfos() {

        File[] files = Path.of(priceFolder).toFile().listFiles();

        if (files != null) {
            return Arrays.stream(files)
                    .map(this::createPriceFile)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toSet());
        } else {
            log.info("No files found in target folder: " + priceFolder);
            return Set.of();
        }
    }

    @Override
    public Optional<PriceFile> loadFile(String filename) {
        return createPriceFile(filename, priceFolder);
    }

    private Optional<PriceFile> createPriceFile(String filePath,
                                                String priceFolder) {

        filePath = priceFolder + "/" + filePath;

        File file = new File(filePath);

        PriceSupplier<PriceFile> supplier = () -> PriceFile.builder()
                .filename(file.getName())
                .data(IOUtils.toByteArray(file.toURI())) //input stream is managed inside
                .build();

        return lend(file, supplier);
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

    private static <T> Optional<T> lend(File file, PriceSupplier<T> supplier) {
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
