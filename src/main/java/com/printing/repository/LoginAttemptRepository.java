package com.printing.repository;

import com.printing.model.LoginAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Репозиторий для управления записями попыток аутентификации пользователей.
 * <p>
 * Обеспечивает доступ к таблице {@code login_attempts} в основной базе данных.
 * Используется сервисом аутентификации для считывания, инкремента и сброса
 * счетчиков неудачных входов в систему администрирования.
 * </p>
 *
 * @author Дмитрий
 * @version 1.0
 */
public interface LoginAttemptRepository extends JpaRepository<LoginAttempt, String> {
}