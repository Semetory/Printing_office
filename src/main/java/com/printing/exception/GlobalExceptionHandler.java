package com.printing.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * Глобальный обработчик исключений (AOP-перехватчик) для слоя веб-контроллеров.
 * <p>
 * Помечен аннотацией {@link RestControllerAdvice}, что позволяет централизованно
 * перехватывать аварийные ситуации бизнес-логики и трансформировать их в понятные
 * HTTP-ответы с корректными статус-кодами.
 * </p>
 *
 * @author Дмитрий
 * @version 1.0
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Перехватывает исключение отсутствия заказа {@link OrderNotFoundException}.
     * Автоматически транслирует ошибку бэкенда в сетевой статус 404 Not Found.
     *
     * @param e объект перехваченного исключения, содержащий текстовое описание причины
     * @return строка с понятным текстом ошибки, отправляемая в тело HTTP-ответа
     */
    @ExceptionHandler(OrderNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(OrderNotFoundException e) {
        return e.getMessage();
    }
}