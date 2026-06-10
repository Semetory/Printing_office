package com.printing.repository;

import com.printing.model.PriceConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Репозиторий для управления конфигурацией цен и тарифов типографии.
 * <p>
 * Обеспечивает чтение и изменение записей в таблице {@code price_configs}.
 * Используется сервером для верификации итоговой стоимости при регистрации новых заказов.
 * </p>
 *
 * @author Дмитрий
 * @version 1.0
 */
public interface PriceConfigRepository extends JpaRepository<PriceConfig, String> {

    /**
     * Производит поиск тарифной стоимости по уникальному системному строковому ключу.
     *
     * @param itemKey уникальный строковый идентификатор позиции (например, "A4", "glossy")
     * @return {@link Optional} с объектом ценовой конфигурации {@link PriceConfig}
     */
    Optional<PriceConfig> findByItemKey(String itemKey);
}