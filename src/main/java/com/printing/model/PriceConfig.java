package com.printing.model;

import jakarta.persistence.*;

/**
 * JPA-сущность конфигурации стоимости составных элементов полиграфической продукции.
 * <p>
 * Хранит тарифную сетку для форматов бумаги, типов покрытий и дополнительных услуг
 * в таблице {@code price_configs}.
 * </p>
 *
 * @author Дмитрий
 * @version 1.0
 */
@Entity
@Table(name = "price_configs")
public class PriceConfig {

    /** Системный текстовый уникальный ключ позиции (например, "A4", "coated", "lamination"). Служит первичным ключом. */
    @Id
    @Column(name = "item_key", nullable = false)
    private String itemKey;

    /** Понятное русское название позиции для отображения в панели администратора. */
    @Column(name = "item_name")
    private String itemName;

    /** Стоимость позиции в целочисленном формате (рублях). */
    @Column(nullable = false)
    private Integer price;

    /**
     * Конструктор по умолчанию (необходим для спецификации Hibernate).
     */
    public PriceConfig() {}

    /**
     * Конструктор для экспресс-инициализации объектов конфигурации цен.
     *
     * @param itemKey  строковый системный ключ позиции
     * @param itemName русское наименование элемента калькулятора
     * @param price    устанавливаемая базовая цена
     */
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