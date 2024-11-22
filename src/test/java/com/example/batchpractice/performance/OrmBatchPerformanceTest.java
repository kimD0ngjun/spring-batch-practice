package com.example.batchpractice.performance;

import com.example.batchpractice.entity.AfterEntity;
import com.example.batchpractice.entity.BeforeEntity;
import com.example.batchpractice.repository.jpa.AfterJpaRepository;
import com.example.batchpractice.repository.jpa.BeforeJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.UUID;

@Slf4j
@SpringBootTest
public class OrmBatchPerformanceTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private JobRegistry jobRegistry;

    @Autowired
    private BeforeJpaRepository beforeJpaRepository;

    @Autowired
    private AfterJpaRepository afterJpaRepository;

    // JPA 기반 ORM 메소드 실행시간 계측 메소드
    private long executeOrmMethod() {
        long startTime = System.nanoTime();

        List<BeforeEntity> beforeEntities = beforeJpaRepository.findAll();
        List<AfterEntity> afterEntities =
                beforeEntities.stream().map(e -> new AfterEntity(e.getUsername())).toList();

        afterJpaRepository.saveAll(afterEntities);

        long endTime = System.nanoTime();

        return endTime - startTime;
    }

    // JPA 기반 배치 처리 실행시간 계측 메소드
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

}
