package com.printing.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.printing.repository.archive", // Сканируем ТОЛЬКО эту папку
        entityManagerFactoryRef = "archiveEntityManagerFactory",
        transactionManagerRef = "archiveTransactionManager"
)
public class ArchiveDbConfig {

    @Bean(name = "archiveDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.archive")
    public DataSource archiveDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "archiveEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean archiveEntityManagerFactory(
            EntityManagerFactoryBuilder builder, @Qualifier("archiveDataSource") DataSource dataSource) {
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "update");
        properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");

        return builder
                .dataSource(dataSource)
                .properties(properties)
                .packages("com.printing.model.archive") // Сущности архива кладем сюда!
                .persistenceUnit("archive")
                .build();
    }

    @Bean(name = "archiveTransactionManager")
    public PlatformTransactionManager archiveTransactionManager(
            @Qualifier("archiveEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}