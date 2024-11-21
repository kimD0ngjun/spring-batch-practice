package com.example.batchpractice.batch;

import com.example.batchpractice.entity.WinEntity;
import com.example.batchpractice.repository.WinRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Collections;
import java.util.Map;

/**
 * 대상 엔티티: WinEntity
 * 시나리오: 테이블에서 데이터를 읽어 "win" 컬럼 값이 10이 넘으면 "reward" 컬럼에 true 부여
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class SecondBatch {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final WinRepository winRepository;

    @Bean
    public Job secondJob() {
        log.info("win 엔티티 테이블 조건부 처리");

        return new JobBuilder("secondJob", jobRepository)
                .start(secondStep())
                .build();
    }

    /**
     * 단위 스뱁의 구성 : 읽기 -> 처리 -> 쓰기
     * 이 작업 순서 진행 단위가 chunk, 대량의 데이터를 얼만큼 끊어서 처리할 지
     * (너무 작으면 IO 처리 많아지면서 오버헤드, 너무 적으면 리소스 사용 비용 상승 및 실패 부담)
     */
    @Bean
    public Step secondStep() {
        log.info("두 번째 스탭");

        return new StepBuilder("secondStep", jobRepository)
                .<WinEntity, WinEntity> chunk(10, transactionManager)
                .reader(winReader())
                .processor(trueProcessor())
                .writer(winWriter())
                .build();
    }

    // WinEntity 테이블에서 읽어오는 Reader
    @Bean
    public RepositoryItemReader<WinEntity> winReader() {

        return new RepositoryItemReaderBuilder<WinEntity>()
                .name("winReader")
                .pageSize(10)
                .methodName("findByWinGreaterThanEqual")
                .arguments(Collections.singletonList(10L))
                .repository(winRepository)
                .sorts(Map.of("id", Sort.Direction.ASC))
                .build();
    }

    // 읽어온 데이터를 처리하는 Process
    @Bean
    public ItemProcessor<WinEntity, WinEntity> trueProcessor() {

        return item -> {
            item.setReward(true);
            return item;
        };
    }

    // WinEntity 에 처리한 결과를 저장(Write)
    @Bean
    public RepositoryItemWriter<WinEntity> winWriter() {

        return new RepositoryItemWriterBuilder<WinEntity>()
                .repository(winRepository)
                .methodName("save")
                .build();
    }
}
