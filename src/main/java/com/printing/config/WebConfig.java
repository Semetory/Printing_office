package com.printing.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

/**
 * Конфигурационный класс глобальных веб-настроек приложения (Spring MVC).
 * <p>
 * Реализует интерфейс {@link WebMvcConfigurer} для расширения поведения базовой
 * инфраструктуры обработки HTTP-запросов и ответов.
 * </p>
 *
 * @author Дмитрий
 * @version 1.0
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Настраивает политику совместного использования ресурсов между разными источниками (CORS)
     * для REST API бэкенда.
     * <p>
     * Позволяет Фронтенд-приложению (UI калькулятора и админ-панели), расположенному на другом домене
     * или порту, выполнять асинхронные AJAX-запросы (GET, POST, PATCH, DELETE) к конечным точкам
     * по маске {@code /api/**}.
     * </p>
     *
     * @param registry реестр конфигураций CORS сопоставлений
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PATCH", "DELETE");
    }
}