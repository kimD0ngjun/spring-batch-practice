package com.example.batchpractice.batch.jpa;

import com.example.batchpractice.entity.AfterEntity;
import com.example.batchpractice.entity.BeforeEntity;
import com.example.batchpractice.repository.AfterJpaRepository;
import com.example.batchpractice.repository.BeforeJpaRepository;
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

import java.util.Map;

/**
 * 단위 작업(job) 배치 정의
 *
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class FirstBatch {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    private final BeforeJpaRepository beforeJpaRepository;
    private final AfterJpaRepository afterJpaRepository;

    /**
     * 하나의 배치 작업 단위
     */
    @Bean
    public Job firstJob() {
        log.info("before 엔티티 테이블 -> after 엔티티 테이블 옮기기");

        return new JobBuilder("firstJob", jobRepository)  // 파라미터: 작업명 및 트래킹용 레포
                .start(firstStep())  // 스탭 파라미터
//                .next()  // 스탭 파라미터
                .build();
    }

    /**
     * 단위 스뱁의 구성 : 읽기 -> 처리 -> 쓰기
     * 이 작업 순서 진행 단위가 chunk, 대량의 데이터를 얼만큼 끊어서 처리할 지
     * (너무 작으면 IO 처리 많아지면서 오버헤드, 너무 적으면 리소스 사용 비용 상승 및 실패 부담)
     */
    @Bean
    public Step firstStep() {
        log.info("첫 번쨰 스탭");

        return new StepBuilder("firstStep", jobRepository)
                .<BeforeEntity, AfterEntity>chunk(10, transactionManager)
                .reader(beforeReader())  // 읽기 메소드 파라미터
                .processor(middleProcessor())  // 처리 메소드 파라미터
                .writer(afterWriter())  // 쓰기 메소드 파라미터
                .build();
    }

    /**
     * BeforeEntity 테이블에서 읽어오는 Reader
     * JPA 기반 쿼리 수행이므로 RepositoryItemReader 사용
     */
    @Bean
    public RepositoryItemReader<BeforeEntity> beforeReader() {
        return new RepositoryItemReaderBuilder<BeforeEntity>()
                .name("beforeReader")
                .pageSize(10)  // findAll 메소드의 페이징 처리
                .methodName("findAll")
                .repository(beforeJpaRepository)
                .sorts(Map.of("id", Sort.Direction.ASC))  // 자원 낭비 방지용 sort
                .build();
    }

    /**
     * 읽어온 데이터를 처리하는 Process
     * (큰 작업을 수행하지 않을 경우 생략 가능, 지금과 같이 단순 이동은 사실 필요 없음)
     */
    @Bean
    public ItemProcessor<BeforeEntity, AfterEntity> middleProcessor() {
        return item -> {
            AfterEntity afterEntity = new AfterEntity();
            afterEntity.setUsername(item.getUsername());

            // 대응되는 AfterEntity 엔티티를 생성
            return afterEntity;
        };
    }

    /**
     * AfterEntity 테이블에 처리한 결과를 저장하는 Writer
     */
    @Bean
    public RepositoryItemWriter<AfterEntity> afterWriter() {
        return new RepositoryItemWriterBuilder<AfterEntity>()
                .repository(afterJpaRepository)
                .methodName("save")  // save 메소드
                .build();
    }
}
