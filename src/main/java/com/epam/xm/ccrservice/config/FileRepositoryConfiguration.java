package com.epam.xm.ccrservice.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "xm.file-loader")
@NoArgsConstructor
@Getter
@Setter
public class FileRepositoryConfiguration {

    private String priceFolderPath;

}
