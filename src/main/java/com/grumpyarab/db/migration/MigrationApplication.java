package com.grumpyarab.db.migration;

import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
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
    private Job job;
    private Environment environment;


	public static void main(String[] args) {
		System.exit(SpringApplication.exit(SpringApplication.run(MigrationApplication.class, args)));
	}

    @Override
    public void setDataSource(DataSource dataSource) {
        super.setDataSource(null);
    }

    @Override
    public void run(final String... args) throws Exception {
    }
}
