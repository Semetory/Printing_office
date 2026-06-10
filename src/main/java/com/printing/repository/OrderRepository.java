package com.printing.repository;

import com.printing.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Основной репозиторий для управления жизненным циклом заказов на полиграфию.
 * <p>
 * Инкапсулирует логику взаимодействия с таблицей {@code orders} в основной базе данных.
 * Поддерживает кастомные запросы для трекинга заказов и автоматической архивации устаревших данных.
 * </p>
 *
 * @author Дмитрий
 * @version 1.0
 */
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Находит заказ по его уникальному буквенно-цифровому номеру.
     * Используется в системе трекинга на фронтенде и при изменении статусов в админ-панели.
     *
     * @param orderNumber уникальный строковый номер заказа
     * @return {@link Optional}, содержащий сущность {@link Order}, если она найдена, или пустой контейнер
     */
    Optional<Order> findByOrderNumber(String orderNumber);

    /**
     * Выбирает список заказов, находящихся в определенном статусе, время последнего
     * обновления которых строго меньше (раньше) заданного порогового значения.
     * <p>
     * Метод автоматически преобразуется Spring Data в SQL-запрос. Используется
     * планировщиком задач (Scheduler) для автоматического переноса старых выданных заказов в архив.
     * </p>
     *
     * @param status    целевой статус для фильтрации (например, "Выдан")
     * @param threshold временная граница (дедлайн) для отсечения старых записей
     * @return список сущностей {@link Order}, удовлетворяющих условиям фильтрации
     */
    List<Order> findByStatusAndStatusUpdatedAtBefore(String status, LocalDateTime threshold);
}