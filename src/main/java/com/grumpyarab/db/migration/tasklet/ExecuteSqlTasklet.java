package com.grumpyarab.db.migration.tasklet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.grumpyarab.db.migration.AppConfig;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.JdbcTemplate;

@AllArgsConstructor
@Slf4j
public class ExecuteSqlTasklet implements Tasklet {
    private DataSource destination;
    private AppConfig appConfig;
    private final List<String> tableNames;

    @Override
    public RepeatStatus execute(final StepContribution stepContribution, final ChunkContext chunkContext) throws Exception {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(destination);
        List<String> sqlStatements = new ArrayList<>();
        tableNames.forEach(tableName -> {
            try {
                sqlStatements.addAll(Files.readAllLines(Paths.get(new FileSystemResource("target/sql/output_" + tableName + ".sql").getURI())));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        if(appConfig.getWriteToDestination() == 1) {
            log.info("Performing bulk insert to destination DB");
            jdbcTemplate.batchUpdate(sqlStatements.toArray(new String[0]));
        } else {
            log.info("No write to DB due to settings");
        }
        return RepeatStatus.FINISHED;
    }
}
