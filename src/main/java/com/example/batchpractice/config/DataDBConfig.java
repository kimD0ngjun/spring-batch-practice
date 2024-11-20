package com.example.batchpractice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
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

    @Bean
    public PlatformTransactionManager dataTransactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(dataEntityManager().getObject());

        return transactionManager;
    }

}
