package com.printing.service.impl;

import com.printing.model.Order;
import com.printing.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderStatusScheduler {

    @Autowired
    private OrderRepository orderRepository;

    /**
     * Метод запускается автоматически.
     * fixedRate = 3600000 означает проверку каждый час (1 час = 3600000 мс).
     * Можно поставить выполнение раз в сутки (86400000) или по Cron-выражению.
     */
    @Scheduled(fixedRate = 3600000)
    @Transactional
    public void autoUpdateOrderStatuses() {
        LocalDateTime now = LocalDateTime.now();
        List<Order> activeOrders = orderRepository.findAll();

        for (Order order : activeOrders) {
            if (order.getCreatedAt() == null) continue;

            String currentStatus = order.getStatus();

            // 1. Спустя 3 дня: Принят -> В печати
            if ("Принят".equals(currentStatus)) {
                if (order.getCreatedAt().plusDays(3).isBefore(now)) {
                    order.setStatus("В печати");
                    orderRepository.save(order);
                    System.out.println("[Авто-Статус] Заказ №" + order.getOrderNumber() + " изменен на 'В печати' (прошло 3 дня).");
                    continue; // Переходим к следующему заказу
                }
            }

            // 2. Спустя еще 7 дней (итого 10 дней с момента создания): В печати -> Готов
            if ("В печати".equals(currentStatus)) {
                if (order.getCreatedAt().plusDays(3 + 7).isBefore(now)) {
                    order.setStatus("Готов");
                    orderRepository.save(order);
                    System.out.println("[Авто-Статус] Заказ №" + order.getOrderNumber() + " изменен на 'Готов' (прошло 7 дней в печати).");
                    continue;
                }
            }

            // 3. Спустя еще 1 день (итого 11 дней с момента создания): Готов -> Выдан
            if ("Готов".equals(currentStatus)) {
                if (order.getCreatedAt().plusDays(3 + 7 + 1).isBefore(now)) {
                    order.setStatus("Выдан");
                    orderRepository.save(order);
                    System.out.println("[Авто-Статус] Заказ №" + order.getOrderNumber() + " изменен на 'Выдан' (прошел 1 день хранения).");
                }
            }
        }
    }
}