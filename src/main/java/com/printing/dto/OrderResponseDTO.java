package com.printing.dto;

import java.time.LocalDateTime;
import java.util.List;

public class OrderResponseDTO {
    private Long id;
    private String orderNumber;
    private String fullname;
    private String phone;
    private String email;
    private String format;
    private String paper;
    private Integer quantity;
    private String payment;
    private Integer total;
    private List<String> files;
    private String status;
    private LocalDateTime createdAt;

    // Конструктор
    public OrderResponseDTO() {}

    // Билдер (ручной)
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private OrderResponseDTO dto = new OrderResponseDTO();

        public Builder id(Long id) { dto.id = id; return this; }
        public Builder orderNumber(String orderNumber) { dto.orderNumber = orderNumber; return this; }
        public Builder fullname(String fullname) { dto.fullname = fullname; return this; }
        public Builder phone(String phone) { dto.phone = phone; return this; }
        public Builder email(String email) { dto.email = email; return this; }
        public Builder format(String format) { dto.format = format; return this; }
        public Builder paper(String paper) { dto.paper = paper; return this; }
        public Builder quantity(Integer quantity) { dto.quantity = quantity; return this; }
        public Builder payment(String payment) { dto.payment = payment; return this; }
        public Builder total(Integer total) { dto.total = total; return this; }
        public Builder files(List<String> files) { dto.files = files; return this; }
        public Builder status(String status) { dto.status = status; return this; }
        public Builder createdAt(LocalDateTime createdAt) { dto.createdAt = createdAt; return this; }

        public OrderResponseDTO build() { return dto; }
    }

    // Геттеры
    public Long getId() { return id; }
    public String getOrderNumber() { return orderNumber; }
    public String getFullname() { return fullname; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getFormat() { return format; }
    public String getPaper() { return paper; }
    public Integer getQuantity() { return quantity; }
    public String getPayment() { return payment; }
    public Integer getTotal() { return total; }
    public List<String> getFiles() { return files; }
    public String getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // Сеттеры
    public void setId(Long id) { this.id = id; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }
    public void setFullname(String fullname) { this.fullname = fullname; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setEmail(String email) { this.email = email; }
    public void setFormat(String format) { this.format = format; }
    public void setPaper(String paper) { this.paper = paper; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public void setPayment(String payment) { this.payment = payment; }
    public void setTotal(Integer total) { this.total = total; }
    public void setFiles(List<String> files) { this.files = files; }
    public void setStatus(String status) { this.status = status; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}