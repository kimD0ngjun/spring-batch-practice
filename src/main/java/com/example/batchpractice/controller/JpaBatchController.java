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

/**
 * 동기적으로 처리되기 때문에 요청 → 처리 → 응답에 대한 딜레이가 발생
 * callable 과 같은 도구로 비동기 처리를 진행해도 좋음
 */
@Controller
@RequestMapping("/jpa")
@RequiredArgsConstructor
public class JpaBatchController {

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

        jobLauncher.run(jobRegistry.getJob("jdbcFirstBatchJob"), jobParameters);
        return new ResponseEntity<>("first batch complete for JPA", HttpStatus.OK);
    }
}
