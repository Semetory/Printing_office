package com.printing.repository;

import com.printing.model.OrderFileStorage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Репозиторий для работы с бинарным хранилищем файлов макетов в основной БД.
 * <p>
 * Предоставляет низкоуровневый доступ к таблице {@code order_files_storage}
 * для записи байтовых потоков файлов, загружаемых клиентами к своим заказам.
 * </p>
 *
 * @author Дмитрий
 * @version 1.0
 */
@Repository
public interface OrderFileStorageRepository extends JpaRepository<OrderFileStorage, Long> {

    /**
     * Выполняет поиск всех загруженных файлов и макетов, принадлежащих конкретному заказу.
     *
     * @param orderId числовой идентификатор (первичный ключ) связанного заказа
     * @return список объектов {@link OrderFileStorage}, содержащих метаданные и бинарный контент
     */
    List<OrderFileStorage> findByOrderId(Long orderId);
}