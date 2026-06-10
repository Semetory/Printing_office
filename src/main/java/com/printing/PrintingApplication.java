package com.printing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Главный класс и точка входа в приложение автоматизации типографии.
 * <p>
 * Аннотация {@link SpringBootApplication} активирует автоматическую конфигурацию,
 * сканирование компонентов (Component Scanning) и управление свойствами.
 * Аннотация {@link EnableScheduling} включает внутренний планировщик задач Spring
 * для обеспечения работоспособности классов автоматического обновления статусов и архивации.
 * </p>
 *
 * @author Дмитрий
 * @version 1.0
 */
@SpringBootApplication
@EnableScheduling
public class PrintingApplication {

    /**
     * Главный исполняемый метод Java (Main Method), запускающий ядро Spring Boot.
     *
     * @param args массив входящих аргументов командной строки при старте JAR/WAR-файла
     */
    public static void main(String[] args) {
        SpringApplication.run(PrintingApplication.class, args);
    }
}