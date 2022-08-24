package com.epam.xm.ccrservice.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Component
@ConfigurationProperties(prefix = "xm.file-loader")
@NoArgsConstructor
@Getter
@Setter
@Validated
public class FileRepositoryConfiguration {

    @NotNull
    @NotEmpty
    private String priceFolderPath;

}
