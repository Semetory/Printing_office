package com.printing.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * JPA-сущность для контроля и учета попыток аутентификации пользователей.
 * <p>
 * Отображается на таблицу {@code login_attempts} в основной базе данных.
 * Используется подсистемой информационной безопасности для предотвращения атак
 * типа Brute-Force (подбор паролей по словарю) в административной панели.
 * </p>
 *
 * @author Дмитрий
 * @version 1.0
 */
@Entity
@Table(name = "login_attempts")
public class LoginAttempt {

    /** Имя пользователя (логин), выступающее в качестве первичного ключа. */
    @Id
    @Column(nullable = false, unique = true)
    private String username;

    /** Текущее количество идущих подряд неудачных попыток входа. */
    private int attempts;

    /** Время окончания действия временной блокировки учетной записи. Если null — аккаунт не заблокирован. */
    private LocalDateTime lockTime;

    /**
     * Конструктор по умолчанию. Требуется инфраструктурой JPA.
     */
    public LoginAttempt() {}

    /**
     * Конструктор со всеми параметрами для явной инициализации записи попытки входа.
     *
     * @param username имя учетной записи (логин)
     * @param attempts начальное количество ошибок
     * @param lockTime временная метка применения блокировки
     */
    public LoginAttempt(String username, int attempts, LocalDateTime lockTime) {
        this.username = username;
        this.attempts = attempts;
        this.lockTime = lockTime;
    }

    // Геттеры и сеттеры
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public int getAttempts() { return attempts; }
    public void setAttempts(int attempts) { this.attempts = attempts; }

    public LocalDateTime getLockTime() { return lockTime; }
    public void setLockTime(LocalDateTime lockTime) { this.lockTime = lockTime; }
}