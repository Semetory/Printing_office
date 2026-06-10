package com.printing.service;

import com.printing.dto.OrderRequestDTO;
import com.printing.dto.OrderResponseDTO;
import java.util.List;
import java.util.Optional;

/**
 * Интерфейс высокоуровневой бизнес-логики для управления заказами типографии.
 *
 * @author Дмитрий
 * @version 1.0
 */
public interface OrderService {

    /**
     * Создает и калькулирует новый заказ.
     * @param request входные параметры заказа от пользователя
     * @return DTO-данные созданного и сохраненного заказа
     */
    OrderResponseDTO createOrder(OrderRequestDTO request);

    /**
     * Получает полную карточку заказа по его уникальному текстовому номеру.
     * @param orderNumber строковый номер заказа
     * @return {@link Optional} с результатом поиска
     */
    Optional<OrderResponseDTO> getOrderByNumber(String orderNumber);

    /**
     * Возвращает полный реестр активных заказов из операционной БД.
     * @return список DTO всех заказов
     */
    List<OrderResponseDTO> getAllOrders();

    /**
     * Принудительно изменяет статус заказа (например, оператором вручную).
     * @param orderNumber строковый номер заказа
     * @param status      целевой новый статус
     * @return {@link Optional} с обновленными данными
     */
    Optional<OrderResponseDTO> updateOrderStatus(String orderNumber, String status);

    /**
     * Безвозвратно удаляет заказ из текущей базы данных.
     * @param orderNumber строковый номер удаляемого заказа
     * @return true, если заказ существовал и был удален, иначе false
     */
    boolean deleteOrder(String orderNumber);

    /**
     * Сохраняет прикрепленный к заказу файл-макет в хранилище БД.
     * @param orderId ID связанного заказа
     * @param file    загружаемый файл
     * @throws java.io.IOException при ошибках дискового/сетевого ввода-вывода
     */
    void storeFile(Long orderId, org.springframework.web.multipart.MultipartFile file) throws java.io.IOException;

    /**
     * Возвращает перечень бинарных объектов файлов, закрепленных за заказом.
     * @param orderId ID искомого заказа
     * @return список сущностей хранилища файлов
     */
    java.util.List<com.printing.model.OrderFileStorage> getFilesByOrderId(Long orderId);
}