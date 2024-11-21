package com.example.batchpractice.performance;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class JdbcJpaPerformanceTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private JobRegistry jobRegistry;

    private long memoryBefore;
    private long memoryAfter;

    @BeforeEach
    public void setup() {
        // 초기 메모리 측정
        memoryBefore = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
    }

    private long executeBatchJob(String jobName) throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("date", "testValue")
                .toJobParameters();

        Job job = jobRegistry.getJob(jobName);

        // 배치 작업 실행 시작 시간
        long startTime = System.nanoTime();

        // 배치 작업 실행
        jobLauncher.run(job, jobParameters);

        // 배치 작업 실행 후 시간 기록
        long endTime = System.nanoTime();

        // 메모리 사용량 측정
        memoryAfter = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        return endTime - startTime;
    }

    private void truncateAfterEntityTable() {
        // TRUNCATE SQL 문 실행 (AfterEntity 테이블 초기화)
        jdbcTemplate.execute("TRUNCATE TABLE AfterEntity;");
    }

    @Test
    public void testBatchPerformanceComparison() throws Exception {
        // 첫 번째 배치 실행 (JDBC Batch)
        long jdbcBatchExecutionTime = executeBatchJob("jdbcFirstBatchJob");
        long jdbcBatchMemoryUsage = memoryAfter - memoryBefore;

        // TRUNCATE 실행 (AfterEntity 테이블 초기화)
        truncateAfterEntityTable();

        // 두 번째 배치 실행 (JPA Batch)
        long jpaBatchExecutionTime = executeBatchJob("firstJob");
        long jpaBatchMemoryUsage = memoryAfter - memoryBefore;

        // 실행 시간과 메모리 사용량 비교 (로그로 출력)
        System.out.println("JDBC Batch 실행시간: " + jdbcBatchExecutionTime + " nanoseconds");
        System.out.println("JDBC Batch 메모리 사용량: " + jdbcBatchMemoryUsage + " bytes");

        System.out.println("JPA Batch 실행시간: " + jpaBatchExecutionTime + " nanoseconds");
        System.out.println("JPA Batch 메모리 사용량: " + jpaBatchMemoryUsage + " bytes");

        // 결과를 출력
        if (jdbcBatchExecutionTime < jpaBatchExecutionTime) {
            System.out.println("JDBC Batch 실행시간 더 짧음");
        } else {
            System.out.println("JPA Batch 실행시간 더 짧음");
        }

        if (jdbcBatchMemoryUsage < jpaBatchMemoryUsage) {
            System.out.println("JDBC Batch 메모리 효율 더 좋음");
        } else {
            System.out.println("JPA Batch 메모리 효율 더 좋음");
        }

        // 비교 결과를 반환
        ResponseEntity<String> response = new ResponseEntity<>("Batch performance comparison complete", HttpStatus.OK);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
