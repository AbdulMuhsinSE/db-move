package com.grumpyarab.db.migration.config.batch;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import com.grumpyarab.db.migration.AppConfig;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@Slf4j
public class JobSetup {
    List<String> tableNames;
    AppConfig appConfig;

    public JobSetup(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    @PostConstruct
    public void afterPropertiesSet() throws IOException {
        Path path = Paths.get(appConfig.getBasePath() ,"tables.txt");
        try(Stream<String> lines = Files.lines(path)) {
            List<String> strings = lines.collect(Collectors.toList());
            log.info("Tables to migrate from: " + String.join(",", strings));
            setTableNames(strings);
        }
    }
}
