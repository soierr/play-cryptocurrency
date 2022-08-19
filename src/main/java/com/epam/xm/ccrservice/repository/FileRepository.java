package com.epam.xm.ccrservice.repository;

import com.epam.xm.ccrservice.model.PriceFile;

import java.util.Optional;
import java.util.Set;

public interface FileRepository {

    Set<PriceFile> loadFileInfos();

    Optional<PriceFile> loadFile(String filename);

}

