package com.printing.dto;

import java.util.List;

public class OrderRequestDTO {
    private String fullname;
    private String phone;
    private String email;
    private String format;
    private String paper;
    private Integer quantity;
    private String payment;
    private Integer total;
    private List<String> files;
    private String orderNumber;

    // Геттеры и сеттеры
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
}