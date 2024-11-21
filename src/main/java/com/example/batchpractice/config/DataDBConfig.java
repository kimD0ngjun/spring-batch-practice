package com.example.batchpractice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * data DB config for batch
 */
@Configuration
@EnableJpaRepositories(
        basePackages = "com.example.batchpractice.repository",
        entityManagerFactoryRef = "dataEntityManager",
        transactionManagerRef = "dataTransactionManager"
)
public class DataDBConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource-data")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    /**
     * JPA 사용한 데이터베이스 연결을 위한 EntityManagerFactory 설정하는 코드
     * JDBC 배치 처리와는 직접적인 연관 x
     */
    @Bean
    public LocalContainerEntityManagerFactoryBean dataEntityManager() {

        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();

        factoryBean.setDataSource(dataSource());
        factoryBean.setPackagesToScan("com.example.batchpractice.entity");
        factoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "update");
        properties.put("hibernate.show_sql", true);

        factoryBean.setJpaPropertyMap(properties);
        return factoryBean;
    }

    // JPA 트랜잭션 매니저 설정
    @Bean
    public PlatformTransactionManager dataTransactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(dataEntityManager().getObject());

        return transactionManager;
    }

    // JDBC 트랜잭션 매니저 설정 (JDBC 배치용)
    @Bean
    public PlatformTransactionManager jdbcTransactionManager() {
        return new DataSourceTransactionManager(dataSource());  // JDBC 트랜잭션 관리
    }

    // JDBC 기반 JdbcTemplate 설정
    @Bean
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(dataSource());
    }

}
