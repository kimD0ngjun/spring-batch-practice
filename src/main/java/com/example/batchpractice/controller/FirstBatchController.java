package com.example.batchpractice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class FirstBatchController {

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
        jobLauncher.run(jobRegistry.getJob("firstJob"), jobParameters);

        return new ResponseEntity<>("first batch complete", HttpStatus.OK);
    }

}
