package com.printing.model.archive;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * JPA-сущность для представления архивного заказа в базе данных.
 * <p>
 * Отображается на таблицу {@code archive_orders} в изолированной архивной БД.
 * Служит для долгосрочного хранения информации о завершенных или отмененных заказах
 * с целью снижения нагрузки на основную операционную базу данных типографии.
 * </p>
 *
 * @author Дмитрий
 * @version 1.0
 */
@Entity
@Table(name = "archive_orders")
public class ArchiveOrder {

    /** Уникальный идентификатор записи (первичный ключ) с автогенерацией. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Уникальный рабочий номер архивного заказа. */
    @Column(name = "order_number", nullable = false)
    private String orderNumber;

    /** ФИО заказчика. */
    private String fullname;

    /** Контактный телефон заказчика. */
    private String phone;

    /** Электронная почта для связи. */
    private String email;

    /** Формат полиграфического изделия (например, A3, A4). */
    private String format;

    /** Тип использованной бумаги. */
    private String paper;

    /** Объем тиража (количество экземпляров). */
    private Integer quantity;

    /** Метод или способ проведения оплаты. */
    private String payment;

    /** Финальная стоимость выполненного заказа. */
    private Integer total;

    /** Последний статус заказа перед переносом в архив. */
    private String status;

    /** Дата и время первоначального создания заказа. */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /** Дата и время последнего изменения производственного статуса. */
    @Column(name = "status_updated_at")
    private LocalDateTime statusUpdatedAt;

    /**
     * Конструктор по умолчанию. Обязателен для корректной работы спецификации JPA/Hibernate.
     */
    public ArchiveOrder() {}

    // Геттеры и Сеттеры
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

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getStatusUpdatedAt() { return statusUpdatedAt; }
    public void setStatusUpdatedAt(LocalDateTime statusUpdatedAt) { this.statusUpdatedAt = statusUpdatedAt; }
}