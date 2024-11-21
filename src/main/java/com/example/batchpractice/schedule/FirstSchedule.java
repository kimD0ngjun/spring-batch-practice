package com.example.batchpractice.schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class FirstSchedule {

    private final JobLauncher jobLauncher;
    private final JobRegistry jobRegistry;

//    /**
//     * 스케줄러 기반 배치 처리 실행 메소드
//     */
//    @Scheduled(cron = "10 * * * * *", zone = "Asia/Seoul")  // 매 분 10초마다 해당 배치 실행
//    public void firstSchedule() throws Exception {
//        log.info("first schedule start");
//
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
//        String date = dateFormat.format(new Date());
//
//        JobParameters jobParameters = new JobParametersBuilder()
//                .addString("date", date)
//                .toJobParameters();
//
//        jobLauncher.run(jobRegistry.getJob("firstJob"), jobParameters);
//    }

}
