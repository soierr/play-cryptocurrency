package com.epam.xm.ccrservice;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableConfigurationProperties
@EnableCaching
public class RecommendationService {

    public static void main(String[] args) {
        new SpringApplicationBuilder(RecommendationService.class)
                .build()
                .run(args);
    }

}
