package com.printing.repository;

import com.printing.model.SystemLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий для логирования системных событий и аудита безопасности.
 * <p>
 * Отвечает за сохранение записей о действиях системного администратора
 * (таких как полная очистка баз данных, дампы и сбросы) в таблицу {@code system_logs}.
 * </p>
 *
 * @author Дмитрий
 * @version 1.0
 */
@Repository
public interface SystemLogRepository extends JpaRepository<SystemLog, Long> {
}