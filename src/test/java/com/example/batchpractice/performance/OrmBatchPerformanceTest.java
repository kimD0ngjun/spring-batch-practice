package com.example.batchpractice.performance;

import com.example.batchpractice.entity.AfterEntity;
import com.example.batchpractice.entity.BeforeEntity;
import com.example.batchpractice.repository.jpa.AfterJpaRepository;
import com.example.batchpractice.repository.jpa.BeforeJpaRepository;
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

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

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

        // JPA 배치 처리의 메소드 호출과 조건을 동일시하기 위한 단일 저장 반복 처리
        for (AfterEntity afterEntity : afterEntities) {
            afterJpaRepository.save(afterEntity);
        }

        long endTime = System.nanoTime();

        return endTime - startTime;
    }

    // JPA 기반 배치 처리 실행시간 계측 메소드
    private long executeBatchJob() throws Exception {
        UUID parameter = UUID.randomUUID();

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("date", parameter.toString())
                .toJobParameters();

        Job job = jobRegistry.getJob("firstJob");

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

    // 결론: JPA 기반 배치처리는 상당히 비효율적인듯?
    // 데이터를 10000개나 했는데...
    // 2024.11.22: 청크와 페이지 크기를 100으로 늘이니 통과된다
    @DisplayName("JPA 배치 처리 기반 실행시간 < ORM 기반 실행시간")
    @Test
    public void test() throws Exception {
        long jpaMethodExecutionTime = executeOrmMethod();

        jdbcTemplate.execute("TRUNCATE TABLE AfterEntity;");

        long jpaBatchExecutionTime = executeBatchJob();

        // then
        assertThat(jpaMethodExecutionTime)
                .describedAs(
                        String.format(
                                "ORM 메소드 실행시간: %d nanoseconds, JPA Batch 실행시간: %d nanoseconds",
                                jpaMethodExecutionTime, jpaBatchExecutionTime))
                .isGreaterThan(jpaBatchExecutionTime);
    }

}
