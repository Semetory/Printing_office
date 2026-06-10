package com.printing.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Объект передачи данных (DTO), представляющий полный ответ системы по заказу.
 * <p>
 * Используется для возврата актуальной информации о заказе из СУБД на фронтенд.
 * Включает сгенерированный сервером уникальный номер, финальную стоимость, статус
 * выполнения и временные метки жизненного цикла. Реализует ручной паттерн {@code Builder}.
 * </p>
 *
 * @author Дмитрий
 * @version 1.0
 */
public class OrderResponseDTO {

    /** Первичный ключ (ID) записи в базе данных. */
    private Long id;

    /** Уникальный буквенно-цифровой рабочий номер заказа. */
    private String orderNumber;

    /** Данные заказчика. */
    private String fullname;

    /** Контактный телефон. */
    private String phone;

    /** Электронная почта. */
    private String email;

    /** Формат изделия. */
    private String format;

    /** Тип бумаги. */
    private String paper;

    /** Размер тиража. */
    private Integer quantity;

    /** Выбранный тип оплаты. */
    private String payment;

    /** Финальная стоимость, подтвержденная сервером. */
    private Integer total;

    /** Перечень имен файлов макетов. */
    private List<String> files;

    /** Текущий статус обработки (например, "Принят", "В печати", "Готов"). */
    private String status;

    /** Дата и время первоначальной регистрации заказа. */
    private LocalDateTime createdAt;

    /** Список примененных к заказу дополнительных услуг. */
    private List<String> services;

    /** Дата и время последнего изменения производственного статуса. */
    private LocalDateTime statusUpdatedAt;

    /**
     * Дефолтный конструктор для инициализации пустого объекта.
     */
    public OrderResponseDTO() {}

    /**
     * Инициализирует и возвращает экземпляр строителя (Builder) для пошаговой сборки объекта.
     *
     * @return новый экземпляр внутреннего класса {@link Builder}
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Внутренний вспомогательный класс, реализующий паттерн Строитель (Builder)
     * для удобного инкапсулированного конструирования {@link OrderResponseDTO}.
     */
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

        /**
         * Завершает процесс конфигурации и возвращает собранный объект ответа.
         *
         * @return полностью сконфигурированный экземпляр {@link OrderResponseDTO}
         */
        public OrderResponseDTO build() { return dto; }
    }

    // Геттеры и Сеттеры с документированными связями
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

    public List<String> getServices() { return services; }
    public void setServices(List<String> services) { this.services = services; }

    public LocalDateTime getStatusUpdatedAt() { return statusUpdatedAt; }
    public void setStatusUpdatedAt(LocalDateTime statusUpdatedAt) { this.statusUpdatedAt = statusUpdatedAt; }
}