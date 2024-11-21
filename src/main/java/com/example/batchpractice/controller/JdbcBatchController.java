package com.example.batchpractice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/jdbc")
@RequiredArgsConstructor
public class JdbcBatchController {

    // job 실행을 위한 의존성들
    private final JobLauncher jobLauncher;
    private final JobRegistry jobRegistry;

    /**
     * 컨트롤러 기반 배치 처리 실행 메소드
     * @param value:배치를 실행시키기 위한 조건부 파라미터
     */
    @GetMapping("/first")
    public ResponseEntity<?> firstApi(@RequestParam("value") String value) throws Exception {

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("date", value)
                .toJobParameters();

        // FirstBatch 클래스의 firstJob() 에서 지정한 작업 명칭
        // JobLauncher 에서 Job 실행시 JobParameter 를 주는 이유
        // : 실행한 작업에 대한 일자, 순번등을 부여해 동일한 일자에 대한 작업의 수행 여부를 확인하여 중복 실행 및 미실행을 예방
        jobLauncher.run(jobRegistry.getJob("jdbcFirstBatchJob"), jobParameters);
        return new ResponseEntity<>("first batch complete for JDBC", HttpStatus.OK);
    }

}
