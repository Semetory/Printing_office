package com.printing.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String orderNumber;

    @Column(nullable = false)
    private String fullname;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String format;

    @Column(nullable = false)
    private String paper;

    @Column(nullable = false)
    private Integer quantity;

    private String payment;

    @Column(nullable = false)
    private Integer total;

    @ElementCollection
    @CollectionTable(name = "order_files", joinColumns = @JoinColumn(name = "order_id"))
    @Column(name = "filename")
    private List<String> files = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "order_services", joinColumns = @JoinColumn(name = "order_id"))
    @Column(name = "service_key")
    private List<String> services;

    private String status = "Принят";

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
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

    public List<String> getServices() {
        return services;
    }

    public void setServices(List<String> services) {
        this.services = services;
    }
}