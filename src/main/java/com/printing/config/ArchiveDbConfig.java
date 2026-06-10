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

/**
 * Конфигурационный класс для настройки изолированной архивной базы данных (Archive DB).
 * <p>
 * Данный класс настраивает механизмы доступа к данным исключительно для сущностей и
 * репозиториев, расположенных в подпакетах {@code archive}. Использует собственную фабрику
 * менеджера сущностей, источник данных и менеджер транзакций.
 * </p>
 * * @author Дмитрий
 * @version 1.0
 */
@Configuration
@EnableJpaRepositories(
        basePackages = "com.printing.repository.archive",
        entityManagerFactoryRef = "archiveEntityManagerFactory",
        transactionManagerRef = "archiveTransactionManager"
)
public class ArchiveDbConfig {

    /**
     * Создает и настраивает компонент {@link DataSource} для архивной базы данных.
     * Параметры подключения считываются из файла конфигурации с префиксом {@code spring.datasource.archive}.
     *
     * @return настроенный экземпляр {@link DataSource} для архива
     */
    @Bean(name = "archiveDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.archive")
    public DataSource archiveDataSource() {
        return DataSourceBuilder.create().build();
    }

    /**
     * Создает фабрику менеджера сущностей {@link LocalContainerEntityManagerFactoryBean} для архивной базы данных.
     * <p>
     * Настраивает использование СУБД PostgreSQL, определяет автоматическое обновление схем таблиц (hbm2ddl.auto = update)
     * и ограничивает область сканирования сущностей пакетом {@code com.printing.model.archive}.
     * </p>
     *
     * @param builder утилитарный сборщик для JPA EntityManagerFactory
     * @param dataSource источник данных архивной БД, внедряемый по имени {@code archiveDataSource}
     * @return фабрика менеджера сущностей для контекста архива
     */
    @Bean(name = "archiveEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean archiveEntityManagerFactory(
            EntityManagerFactoryBuilder builder, @Qualifier("archiveDataSource") DataSource dataSource) {
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "update");
        properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");

        return builder
                .dataSource(dataSource)
                .properties(properties)
                .packages("com.printing.model.archive")
                .persistenceUnit("archive")
                .build();
    }

    /**
     * Конфигурирует изолированный менеджер транзакций {@link PlatformTransactionManager} для архивной базы данных.
     *
     * @param entityManagerFactory фабрика менеджера сущностей, внедряемая по имени {@code archiveEntityManagerFactory}
     * @return менеджер транзакций, привязанный к архивной БД
     */
    @Bean(name = "archiveTransactionManager")
    public PlatformTransactionManager archiveTransactionManager(
            @Qualifier("archiveEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}