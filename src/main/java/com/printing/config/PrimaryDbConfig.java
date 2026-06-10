package com.printing.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;

/**
 * Конфигурационный класс для основной базы данных приложения (Primary DB).
 * <p>
 * Отвечает за обработку основных бизнес-сущностей типографии (заказы, конфигурации цен, попытки входа).
 * Все бины помечены аннотацией {@link Primary}, что указывает Spring использовать их по умолчанию.
 * Исключает из области видимости подпакет {@code archive}, предотвращая пересечение и конфликты контекстов данных.
 * </p>
 *
 * @author Дмитрий
 * @version 1.0
 */
@Configuration
@EnableJpaRepositories(
        basePackages = "com.printing.repository",
        entityManagerFactoryRef = "entityManagerFactory",
        transactionManagerRef = "transactionManager",
        excludeFilters = @org.springframework.context.annotation.ComponentScan.Filter(
                type = org.springframework.context.annotation.FilterType.ASPECTJ,
                pattern = "com.printing.repository.archive..*"
        )
)
public class PrimaryDbConfig {

    /**
     * Создает и конфигурирует основной источник данных {@link DataSource} системы.
     * Параметры подключения вычитываются из префикса {@code spring.datasource.primary}.
     * Компонент является приоритетным (Primary) для приложения.
     *
     * @return основной экземпляр {@link DataSource}
     */
    @Primary
    @Bean(name = "dataSource")
    @ConfigurationProperties(prefix = "spring.datasource.primary")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    /**
     * Создает основную фабрику менеджера сущностей {@link LocalContainerEntityManagerFactoryBean}.
     * <p>
     * Настраивает диалект PostgreSQLDialect и автообновление схем таблиц базы данных.
     * Сканирует базовый пакет моделей {@code com.printing.model} для поиска сущностей,
     * при этом за счет разделения пакетов исключает архивные таблицы.
     * </p>
     *
     * @param builder утилитарный сборщик для JPA EntityManagerFactory
     * @param dataSource основной источник данных, внедряемый по имени {@code dataSource}
     * @return приоритетная фабрика менеджера сущностей для основных бизнес-задач
     */
    @Primary
    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            EntityManagerFactoryBuilder builder, @Qualifier("dataSource") DataSource dataSource) {

        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "update");
        properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");

        return builder
                .dataSource(dataSource)
                .properties(properties)
                .packages("com.printing.model")
                .persistenceUnit("primary")
                .build();
    }

    /**
     * Создает и настраивает основной менеджер транзакций {@link PlatformTransactionManager} приложения.
     * Необходим для корректной поддержки аннотаций {@code @Transactional} в сервисах обработки заказов.
     *
     * @param entityManagerFactory фабрика менеджера сущностей, внедряемая по имени {@code entityManagerFactory}
     * @return основной менеджер транзакций операционной зоны приложения
     */
    @Primary
    @Bean(name = "transactionManager")
    public PlatformTransactionManager transactionManager(
            @Qualifier("entityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}