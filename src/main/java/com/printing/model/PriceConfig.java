package com.printing.model;

import jakarta.persistence.*;

@Entity
@Table(name = "price_configs")
public class PriceConfig {

    @Id
    // Явно указываем имя колонки с нижним подчёркиванием:
    @Column(name = "item_key", nullable = false)
    private String itemKey;

    // Подстраиваемся под реальное имя колонки для названия (слитно)
    @Column(name = "item_name")
    private String itemName;

    @Column(nullable = false)
    private Integer price;

    public PriceConfig() {}

    public PriceConfig(String itemKey, String itemName, Integer price) {
        this.itemKey = itemKey;
        this.itemName = itemName;
        this.price = price;
    }

    // Геттеры и сеттеры
    public String getItemKey() { return itemKey; }
    public void setItemKey(String itemKey) { this.itemKey = itemKey; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public Integer getPrice() { return price; }
    public void setPrice(Integer price) { this.price = price; }
}