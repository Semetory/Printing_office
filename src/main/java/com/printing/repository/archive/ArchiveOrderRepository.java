package com.printing.repository.archive;

import com.printing.model.archive.ArchiveOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий для работы с архивными заказами в изолированной базе данных.
 * <p>
 * Расширяет {@link JpaRepository}, предоставляя стандартный набор CRUD-операций
 * над сущностью {@link ArchiveOrder}. Обслуживается конфигурацией {@code ArchiveDbConfig}
 * и используется для долгосрочного хранения завершенных производственных циклов.
 * </p>
 *
 * @author Дмитрий
 * @version 1.0
 */
@Repository
public interface ArchiveOrderRepository extends JpaRepository<ArchiveOrder, Long> {
    // Здесь при необходимости можно будет описывать методы поиска по архивной БД
    // Например: Optional<ArchiveOrder> findByOrderNumber(String orderNumber);
}