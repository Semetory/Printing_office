package com.printing.model.archive;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "archive_orders")
public class ArchiveOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_number", nullable = false)
    private String orderNumber;

    private String fullname;
    private String phone;
    private String email;
    private String format;
    private String paper;
    private Integer quantity;
    private String payment;
    private Integer total;
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "status_updated_at")
    private LocalDateTime statusUpdatedAt;

    // Пустой конструктор (обязателен для Hibernate)
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