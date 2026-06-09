package com.printing.service.impl;

import com.printing.model.Order;
import com.printing.model.archive.ArchiveOrder;
import com.printing.model.SystemLog;
import com.printing.repository.OrderRepository;
import com.printing.repository.archive.ArchiveOrderRepository;
import com.printing.repository.SystemLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ArchiveScheduler {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ArchiveOrderRepository archiveOrderRepository;

    @Autowired
    private SystemLogRepository logRepository;

    // Cron выражение: секунда, минута, час, день месяца, месяц, день недели.
    // "0 0 2 * * ?" означает: запускать каждый день ровно в 02:00 ночи.
    @Scheduled(cron = "0 0 2 * * ?")
    @Transactional
    public void moveOldOrdersToArchive() {
        // Вычисляем точку отсчета: текущее время минус 31 день
        LocalDateTime threshold = LocalDateTime.now().minusDays(31);

        // Ищем заказы, у которых статус "Выдан" И статус был обновлен более 31 дня назад
        List<Order> ordersToArchive = orderRepository.findByStatusAndStatusUpdatedAtBefore("Выдан", threshold);

        if (!ordersToArchive.isEmpty()) {
            for (Order order : ordersToArchive) {
                ArchiveOrder ao = new ArchiveOrder();

                // Переносим абсолютно ВСЕ метаданные, чтобы не потерять информацию в архиве
                ao.setOrderNumber(order.getOrderNumber());
                ao.setFullname(order.getFullname());
                ao.setPhone(order.getPhone());
                ao.setEmail(order.getEmail());
                ao.setFormat(order.getFormat());
                ao.setPaper(order.getPaper());
                ao.setQuantity(order.getQuantity());
                ao.setPayment(order.getPayment());
                ao.setTotal(order.getTotal());
                ao.setStatus(order.getStatus());
                ao.setCreatedAt(order.getCreatedAt());
                ao.setStatusUpdatedAt(order.getStatusUpdatedAt());

                // 1. Сохраняем в архивную базу данных
                archiveOrderRepository.save(ao);

                // 2. Удаляем из основной базы данных
                orderRepository.delete(order);
            }

            // 3. Записываем событие автоматики в системные логи
            logRepository.save(new SystemLog(
                    "Авто-Архивация",
                    "Успешно перенесено в архивную БД заказов: " + ordersToArchive.size()
            ));

            System.out.println("[Scheduler] Архивация завершена. Перенесено объектов: " + ordersToArchive.size());
        }
    }
}