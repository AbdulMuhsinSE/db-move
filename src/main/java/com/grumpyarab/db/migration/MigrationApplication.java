package com.grumpyarab.db.migration;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.util.ResourceUtils;

@PropertySource(ResourceUtils.CLASSPATH_URL_PREFIX + "application.properties")
@SpringBootApplication(scanBasePackages = {"com.grumpyarab"})
@Slf4j
public class MigrationApplication extends DefaultBatchConfigurer implements CommandLineRunner {

    @Autowired
    private Job job;

    @Autowired
    private Environment environment;


	public static void main(String[] args) {
		System.exit(SpringApplication.exit(SpringApplication.run(MigrationApplication.class, args)));
	}

    @PostConstruct
    public void logValues() {
        log.warn("Source DB: {}", environment.getProperty("spring.datasource.jdbcUrl"));
        log.warn("Destination DB: {}", environment.getProperty("destination.datasource.jdbcUrl"));
    }

    @Override
    public void setDataSource(DataSource dataSource) {
        super.setDataSource(null);
    }

    @Override
    public void run(final String... args) throws Exception {
    }
}
