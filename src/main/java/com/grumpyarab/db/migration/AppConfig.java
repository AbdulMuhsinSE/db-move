package com.grumpyarab.db.migration;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Getter
@ComponentScan(basePackages = {"com.grumpyarab"})
@Configuration
public class AppConfig {
    @Value("${files.location}")
    private String basePath;

    @Value("${write.to.destination}")
    private int writeToDestination;
}
