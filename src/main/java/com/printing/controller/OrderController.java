package com.printing.controller;

import com.printing.dto.OrderRequestDTO;
import com.printing.dto.OrderResponseDTO;
import com.printing.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * REST-контроллер для управления жизненным циклом заказов на полиграфию.
 * <p>
 * Предоставляет эндпоинты для оформления новых заказов со стороны клиентской части,
 * поиска индивидуального заказа, просмотра агрегированного списка, оперативного изменения
 * производственного статуса и удаления записей.
 * </p>
 *
 * @author Дмитрий
 * @version 1.0
 */
@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * Регистрирует новый заказ в системе с автоматическим пересчетом его стоимости на бэкенде.
     *
     * @param request заполненное DTO с параметрами тиража, материалами и контактными данными
     * @return {@link ResponseEntity} со статусом 201 (Created) и DTO детальной информации о заказе
     */
    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(@RequestBody OrderRequestDTO request) {
        OrderResponseDTO created = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Осуществляет поиск и выдачу параметров заказа по его уникальному строковому номеру.
     * Используется клиентом для трекинга и администратором для просмотра карточки.
     *
     * @param orderNumber уникальный сгенерированный номер заказа
     * @return {@link ResponseEntity} с данными заказа, либо статус 404 (Not Found)
     */
    @GetMapping("/{orderNumber}")
    public ResponseEntity<OrderResponseDTO> getOrder(@PathVariable String orderNumber) {
        return orderService.getOrderByNumber(orderNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Извлекает из СУБД полный перечень всех зарегистрированных заказов.
     *
     * @return {@link ResponseEntity} со списком {@link OrderResponseDTO}
     */
    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    /**
     * Производит частичное обновление производственного статуса заказа (например: Принят -> В печати -> Готов).
     *
     * @param orderNumber номер целевого заказа
     * @param status новый статус, присваиваемый заказу
     * @return {@link ResponseEntity} с обновленной карточкой заказа, либо статус 404
     */
    @PatchMapping("/{orderNumber}/status")
    public ResponseEntity<OrderResponseDTO> updateStatus(
            @PathVariable String orderNumber,
            @RequestParam String status) {
        return orderService.updateOrderStatus(orderNumber, status)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Безвозвратно удаляет заказ из операционной базы данных.
     *
     * @param orderNumber номер удаляемого заказа
     * @return {@link ResponseEntity} со статусом 244 (No Content) в случае успеха, либо 404
     */
    @DeleteMapping("/{orderNumber}")
    public ResponseEntity<Void> deleteOrder(@PathVariable String orderNumber) {
        if (orderService.deleteOrder(orderNumber)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}