package com.example.batchpractice.controller;

import com.example.batchpractice.repository.jpa.BeforeJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 동기적으로 처리되기 때문에 요청 → 처리 → 응답에 대한 딜레이가 발생
 * callable 과 같은 도구로 비동기 처리를 진행해도 좋음
 */
@Controller
@RequiredArgsConstructor
public class BatchController {

    // job 실행을 위한 의존성들
    private final JobLauncher jobLauncher;
    private final JobRegistry jobRegistry;

    private final BeforeJpaRepository beforeJpaRepository;

    /**
     * 컨트롤러 기반 배치 처리 실행 메소드
     * @param value:배치를 실행시키기 위한 조건부 파라미터
     *
     */
    @GetMapping("/first")
    public ResponseEntity<?> firstApi(@RequestParam("value") String value) throws Exception {

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("date", value)
                .toJobParameters();

        // FirstBatch 클래스의 firstJob() 에서 지정한 작업 명칭
        // JobLauncher 에서 Job 실행시 JobParameter 를 주는 이유
        // : 실행한 작업에 대한 일자, 순번등을 부여해 동일한 일자에 대한 작업의 수행 여부를 확인하여 중복 실행 및 미실행을 예방
        jobLauncher.run(jobRegistry.getJob("firstJob"), jobParameters);
        return new ResponseEntity<>("first batch complete", HttpStatus.OK);
    }

    @GetMapping("/second")
    public ResponseEntity<?> secondApi(@RequestParam("value") String value) throws Exception {

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("data", value)
                .toJobParameters();

        jobLauncher.run(jobRegistry.getJob("secondJob"), jobParameters);
        return new ResponseEntity<>("second batch complete", HttpStatus.OK);
    }

//    @GetMapping
//    public ResponseEntity<?> test() {
//        if (beforeJpaRepository.findById(1L).isPresent()) return new ResponseEntity<>("test OK", HttpStatus.OK);
//
//        throw new IllegalArgumentException();
//    }
}
