package com.printing.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * JPA-сущность для ведения журнала системного аудита и логирования действий администратора.
 * <p>
 * Записи аккумулируются в таблице {@code system_logs} и выводятся исключительно
 * в специализированной панели системного администратора (SysAdmin) для контроля целостности данных.
 * </p>
 *
 * @author Дмитрий
 * @version 1.0
 */
@Entity
@Table(name = "system_logs")
public class SystemLog {

    /** Уникальный числовой идентификатор (ID) записи лога. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Точная временная метка совершения описываемого действия. */
    @Column(nullable = false)
    private LocalDateTime timestamp;

    /** Краткое наименование совершенной системной операции (например, "Очистка архива"). */
    @Column(nullable = false)
    private String action;

    /** Подробное описание или метаданные события (текстовое поле неограниченной длины). */
    @Column(columnDefinition = "TEXT")
    private String details;

    /**
     * Конструктор по умолчанию для нужд JPA-провайдера.
     */
    public SystemLog() {}

    /**
     * Удобный конструктор для быстрой регистрации нового системного события.
     * Временная метка проставляется автоматически в момент вызова конструктора.
     *
     * @param action   категория или название действия
     * @param details  развернутое текстовое описание параметров события
     */
    public SystemLog(String action, String details) {
        this.timestamp = LocalDateTime.now();
        this.action = action;
        this.details = details;
    }

    // Геттеры и Сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
}