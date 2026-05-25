package com.printing.model;

public enum OrderStatus {
    ACCEPTED("Принят"),
    PRINTING("В печати"),
    READY("Готов"),
    DELIVERED("Выдан");

    private final String rusName;

    OrderStatus(String rusName) {
        this.rusName = rusName;
    }

    public String getRusName() {
        return rusName;
    }
}