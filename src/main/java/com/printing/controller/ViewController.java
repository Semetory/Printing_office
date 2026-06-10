package com.printing.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Контроллер представлений Spring MVC для маршрутизации корневых запросов.
 * <p>
 * Выполняет роль диспетчера начальной страницы (лендинга), перенаправляя пользователя
 * со стандартного корневого URL на статический файл интерфейса.
 * </p>
 *
 * @author Дмитрий
 * @version 1.0
 */
@Controller
public class ViewController {

    /**
     * Осуществляет внутреннее перенаправление (forward) входящего запроса с корневого пути
     * на стартовую HTML-страницу интерактивного каталога.
     *
     * @return строка перенаправления представления {@code forward:/index.html}
     */
    @GetMapping("/")
    public String index() {
        return "forward:/index.html";
    }
}