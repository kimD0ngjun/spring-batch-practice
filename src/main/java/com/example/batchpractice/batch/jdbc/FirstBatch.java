package com.example.batchpractice.batch.jdbc;

import com.example.batchpractice.entity.AfterEntity;
import com.example.batchpractice.entity.BeforeEntity;
import com.example.batchpractice.repository.jdbc.AfterJdbcRepository;
import com.example.batchpractice.repository.jdbc.BeforeJdbcRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class FirstBatch {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    private final BeforeJdbcRepository beforeJdbcRepository;
    private final AfterJdbcRepository afterJdbcRepository;

    @Bean
    public Job jdbcBatchJob() {
        return new JobBuilder("jdbcFirstBatchJob", jobRepository)
                .start(jdbcBatchStep())
                .build();
    }

    @Bean
    public Step jdbcBatchStep() {
        return new StepBuilder("jdbcFirstBatchStep", jobRepository)
                .<BeforeEntity, AfterEntity>chunk(10, transactionManager)
                .reader(jdbcReader())   // Reader
                .processor(jdbcProcessor()) // Processor
                .writer(jdbcWriter())   // Writer
                .build();
    }

    // Reader: 데이터를 페이지 단위로 읽어오는 로직
    @Bean
    public ItemReader<BeforeEntity> jdbcReader() {
        return new ItemReader<>() {
            private int offset = 0; // 현재 오프셋

            @Override
            public BeforeEntity read() {
                int pageSize = 10;
                List<BeforeEntity> entities = beforeJdbcRepository.findAll(pageSize, offset);
                if (entities.isEmpty()) {
                    return null; // 더 이상 데이터가 없을 경우 null 반환 (Spring Batch 종료 조건)
                }
                offset += pageSize;
                return entities.getFirst(); // 한 번에 하나씩 반환
            }
        };
    }

    // Processor: 데이터를 변환 (BeforeEntity -> AfterEntity)
    @Bean
    public ItemProcessor<BeforeEntity, AfterEntity> jdbcProcessor() {
        return item -> {
            AfterEntity afterEntity = new AfterEntity();
            afterEntity.setId(item.getId()); // ID를 그대로 유지
            afterEntity.setUsername(item.getUsername()); // Username 그대로 복사
            return afterEntity;
        };
    }

    // Writer: 배치 저장
    @Bean
    public ItemWriter<AfterEntity> jdbcWriter() {
        return chunk -> {
            // Chunk 에서 AfterEntity 리스트 추출
            List<? extends AfterEntity> items = chunk.getItems();

            // 배치 저장 호출
            afterJdbcRepository.batchSave(items); // Batch Insert 호출
        };
    }
}
