package com.grumpyarab.db.migration.config.batch;

import com.grumpyarab.db.migration.tasklet.ExecuteSqlTasklet;
import com.grumpyarab.db.migration.PathUtils;
import com.grumpyarab.db.migration.mapper.HashmapRowMapper;
import com.grumpyarab.db.migration.processor.RowProcessor;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import javax.sql.DataSource;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.support.SimpleFlow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.PassThroughLineAggregator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

@Configuration
@EnableBatchProcessing
@AllArgsConstructor
@ComponentScan(basePackages = {"com.grumpyarab"})
@Slf4j
public class BatchConfig {
    public JobBuilderFactory jobBuilderFactory;
    public StepBuilderFactory stepBuilderFactory;

    public PathUtils pathUtils;
    private JobSetup jobSetup;
    private DataSource dataSource;

    @Qualifier("destination")
    private DataSource destination;

    @Bean
    public Job migrateToNewDB() {
        log.info("Migration Job Started");
        List<Step> steps = jobSetup.getTableNames()
            .stream().map(this::step).toList();

        FlowBuilder<Flow> flowBuilder = new FlowBuilder<>("migration_flow");
        Flow flow = flowBuilder.start(createParallelFlow(steps)).build();
        return jobBuilderFactory.get("migrateTONewDB")
            .incrementer(new RunIdIncrementer())
            .start(flow)
            .next(writeToDestination(jobSetup.getTableNames()))
            .build().build();
    }

    private static Flow createParallelFlow(List<Step> steps){
        SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();
        taskExecutor.setConcurrencyLimit(steps.size());

        List<Flow> flows = steps.stream()
            .map(step ->
                new FlowBuilder<Flow>("flow_" + step.getName())
                    .start(step)
                    .build()).toList();

        return new FlowBuilder<SimpleFlow>("parallelStepsFlow").split(taskExecutor)
            .add(flows.toArray(new Flow[flows.size()]))
            .build();
    }

    public Step step(String tableName) {
        try {
            return stepBuilderFactory.get("migrate_" + tableName)
                .<HashMap<String, Object>, String>chunk(100)
                .reader(itemReader(dataSource, tableName))
                .processor(processor(tableName))
                .writer(itemWriter(tableName)).build();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Step writeToDestination(List<String> tableNames) {
        return stepBuilderFactory.get("write")
            .tasklet(new ExecuteSqlTasklet(destination, tableNames)).build();
    }

    public ItemReader<HashMap<String, Object>> itemReader(DataSource source, String tableName) throws SQLException {
        log.info("Pull from source db: " + source.getConnection().toString() + " table: " + tableName);
        return new JdbcCursorItemReaderBuilder<HashMap<String, Object>>()
            .name("oracleReader")
            .dataSource(source)
            .sql("SELECT * from " + tableName)
            .rowMapper(new HashmapRowMapper())
            .build();
    }

    public RowProcessor processor(String url) {
        return new RowProcessor(url, pathUtils.getBasePath());
    }

    public FlatFileItemWriter<String> itemWriter(String tableName) {
        return new FlatFileItemWriterBuilder<String>()
            .name("itemWriter")
            .resource(new FileSystemResource("target/sql/output_" + tableName + ".sql"))
            .lineAggregator(new PassThroughLineAggregator<>())
            .build();
    }


}
