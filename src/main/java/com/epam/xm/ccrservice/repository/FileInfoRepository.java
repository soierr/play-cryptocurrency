package com.epam.xm.ccrservice.repository;

import com.epam.xm.ccrservice.model.FileInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileInfoRepository extends JpaRepository<FileInfo, Long> {

    /* Implied to be truncated from time to time */
    List<FileInfo> findAll();

}
