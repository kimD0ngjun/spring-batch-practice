package com.example.batchpractice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

/**
 * meta DB config for batch
 */
@Configuration
public class MetaDBConfig {

    @Primary  // @Primary 설정한 테이블에 테이블을 자동으로 메타데이터 테이블 생성
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource-meta")
    public DataSource metaSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean
    public PlatformTransactionManager metaTransactionManager() {
        return new DataSourceTransactionManager(metaSource());
    }

}
