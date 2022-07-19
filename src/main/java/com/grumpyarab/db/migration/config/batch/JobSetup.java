package com.grumpyarab.db.migration.config.batch;

import com.grumpyarab.db.migration.PathUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@Slf4j
public class JobSetup {
    List<String> tableNames;

    @Autowired
    PathUtils pathUtils;

    @PostConstruct
    public void afterPropertiesSet() throws IOException {
        Path path = Paths.get(pathUtils.getBasePath() ,"tables.txt");
        try(Stream<String> lines = Files.lines(path)) {
            List<String> strings = lines.toList();
            log.info("Tables to migrate from: " + String.join(",", strings));
            setTableNames(strings);
        }
    }
}
