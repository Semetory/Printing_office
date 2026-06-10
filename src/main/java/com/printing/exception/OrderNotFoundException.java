package com.printing.exception;

/**
 * Исключение, выбрасываемое в случае, если запрашиваемый заказ не найден в СУБД.
 * <p>
 * Наследуется от {@link RuntimeException}, что позволяет использовать его внутри
 * транзакционных методов без обязательного объявления проверяемых исключений в сигнатуре.
 * </p>
 *
 * @author Дмитрий
 * @version 1.0
 */
public class OrderNotFoundException extends RuntimeException {

    /**
     * Создает исключение с предзаполненным системным сообщением.
     *
     * @param orderNumber номер заказа, поиск которого завершился неудачно
     */
    public OrderNotFoundException(String orderNumber) {
        super("Заказ с номером " + orderNumber + " не найден");
    }
}