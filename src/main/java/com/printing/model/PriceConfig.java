package com.printing.model;

import jakarta.persistence.*;

@Entity
@Table(name = "price_configs")
public class PriceConfig {

    @Id
    private String itemKey; // Уникальный ключ, например: "A4", "A3", "GlossyPaper", "MattePaper"

    @Column(nullable = false)
    private String itemName; // Понятное название для админа: "Формат А4", "Глянцевая бумага"

    @Column(nullable = false)
    private Integer price; // Цена в рублях

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