package com.printing.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Основная JPA-сущность операционной зоны, описывающая заказ на полиграфическую печать.
 * <p>
 * Отображается на таблицу {@code orders} и содержит в себе полные сведения о клиенте,
 * калькуляции стоимости, выбранных услугах и связях с файлами макетов.
 * </p>
 *
 * @author Дмитрий
 * @version 1.0
 */
@Entity
@Table(name = "orders")
public class Order {

    /** Время последнего изменения производственного статуса заказа. */
    @Column(name = "status_updated_at")
    private LocalDateTime statusUpdatedAt;

    /** Уникальный числовой идентификатор (первичный ключ) заказа. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Уникальный текстовый рабочий номер заказа (генерируется по бизнес-алгоритму). */
    @Column(name = "order_number", nullable = false, columnDefinition = "varchar(255) default 'TEMPORARY_NUM'")
    private String orderNumber;

    /** ФИО заказчика. */
    @Column(nullable = false)
    private String fullname;

    /** Контактный телефон. */
    @Column(nullable = false)
    private String phone;

    /** Электронный адрес. */
    @Column(nullable = false)
    private String email;

    /** Выбранный формат продукции (A0-A6). */
    @Column(nullable = false)
    private String format;

    /** Тип используемого материала (бумаги/картона). */
    @Column(nullable = false)
    private String paper;

    /** Общий тираж заказа. */
    @Column(nullable = false)
    private Integer quantity;

    /** Способ оплаты. */
    private String payment;

    /** Финальная подтвержденная стоимость заказа, рассчитанная бэкендом. */
    @Column(nullable = false)
    private Integer total;

    /** Список текстовых имен файлов макетов, загруженных клиентом. */
    @ElementCollection
    @CollectionTable(name = "order_files", joinColumns = @JoinColumn(name = "order_id"))
    @Column(name = "filename")
    private List<String> files = new ArrayList<>();

    /** Список системных ключей выбранных дополнительных постпечатных услуг (например, lamination, folding). */
    @ElementCollection
    @CollectionTable(name = "order_services", joinColumns = @JoinColumn(name = "order_id"))
    @Column(name = "service_key")
    private List<String> services;

    /** Текущий статус обработки заказа (по умолчанию "Принят"). */
    private String status = "Принят";

    /** Автоматическая временная метка создания заказа. */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * JPA Callback-метод, выполняемый автоматически перед первоначальным сохранением (инсертом) записи в БД.
     * Инициализирует дату создания заказа и синхронизирует с ней время установки начального статуса.
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.statusUpdatedAt = LocalDateTime.now();
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }

    public String getFullname() { return fullname; }
    public void setFullname(String fullname) { this.fullname = fullname; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }

    public String getPaper() { return paper; }
    public void setPaper(String paper) { this.paper = paper; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public String getPayment() { return payment; }
    public void setPayment(String payment) { this.payment = payment; }

    public Integer getTotal() { return total; }
    public void setTotal(Integer total) { this.total = total; }

    public List<String> getFiles() { return files; }
    public void setFiles(List<String> files) { this.files = files; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<String> getServices() { return services; }
    public void setServices(List<String> services) { this.services = services; }

    public LocalDateTime getStatusUpdatedAt() { return statusUpdatedAt; }
    public void setStatusUpdatedAt(LocalDateTime statusUpdatedAt) { this.statusUpdatedAt = statusUpdatedAt; }
}