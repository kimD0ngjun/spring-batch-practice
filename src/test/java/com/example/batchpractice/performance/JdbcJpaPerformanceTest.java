package com.example.batchpractice.performance;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
public class JdbcJpaPerformanceTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private JobRegistry jobRegistry;

    private long executeBatchJob(String jobName) throws Exception {
        UUID parameter = UUID.randomUUID();

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("date", parameter.toString())
                .toJobParameters();

        Job job = jobRegistry.getJob(jobName);

        long startTime = System.nanoTime();
        jobLauncher.run(job, jobParameters);
        long endTime = System.nanoTime();

        return endTime - startTime;
    }

    @BeforeEach
    public void setUp() {
        jdbcTemplate.execute("TRUNCATE TABLE AfterEntity;");
    }

    @AfterEach
    public void cleanup() {
        jdbcTemplate.execute("TRUNCATE TABLE AfterEntity;");
    }

    @DisplayName("JDBC 기반 배치 처리 실행시간 < JPA 기반 배치 처리 실행시간")
    @Test
    public void test() throws Exception {
        // given & when
        long jdbcBatchExecutionTime = executeBatchJob("jdbcFirstBatchJob");

        jdbcTemplate.execute("TRUNCATE TABLE AfterEntity;");

        long jpaBatchExecutionTime = executeBatchJob("firstJob");

        // then
        assertThat(jpaBatchExecutionTime)
                .describedAs(
                        String.format(
                                "JDBC Batch 실행시간: %d nanoseconds, JPA Batch 실행시간: %d nanoseconds",
                                jdbcBatchExecutionTime, jpaBatchExecutionTime))
                .isGreaterThan(jdbcBatchExecutionTime);

    }
}
