package com.printing.exception;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(String orderNumber) {
        super("Заказ с номером " + orderNumber + " не найден");
    }
}
