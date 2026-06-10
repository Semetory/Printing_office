package com.printing.dto;

import java.util.List;

/**
 * Объект передачи данных (DTO) для создания или изменения заказа.
 * <p>
 * Содержит в себе всю необходимую конфигурацию полиграфической продукции
 * (формат, тип бумаги, тираж, дополнительные услуги) и персональные данные,
 * отправляемые клиентом через веб-форму онлайн-калькулятора.
 * </p>
 *
 * @author Дмитрий
 * @version 1.0
 */
public class OrderRequestDTO {

    /** Полное имя (ФИО) заказчика. */
    private String fullname;

    /** Контактный номер телефона в строковом формате. */
    private String phone;

    /** Адрес электронной почты для отправки уведомлений. */
    private String email;

    /** Выбранный формат полиграфии (например, "A2", "A4"). */
    private String format;

    /** Системный идентификатор типа используемой бумаги (например, "glossy", "matte"). */
    private String paper;

    /** Необходимый объем тиража (количество экземпляров). */
    private Integer quantity;

    /** Выбранный тип проведения оплаты (например, "Онлайн"). */
    private String payment;

    /** Предварительная стоимость, рассчитанная калькулятором на стороне фронтенда. */
    private Integer total;

    /** Список уникальных имен или путей к файлам загруженных макетов. */
    private List<String> files;

    /** Уникальный номер заказа (используется при обновлении или поиске). */
    private String orderNumber;

    /** Список строковых ключей дополнительных услуг (например, ["lamination", "folding"]). */
    private List<String> services;

    /** @return ФИО заказчика */
    public String getFullname() { return fullname; }
    /** @param fullname новое значение ФИО заказчика */
    public void setFullname(String fullname) { this.fullname = fullname; }

    /** @return контактный телефон */
    public String getPhone() { return phone; }
    /** @param phone новый контактный телефон */
    public void setPhone(String phone) { this.phone = phone; }

    /** @return адрес электронной почты */
    public String getEmail() { return email; }
    /** @param email новый адрес электронной почты */
    public void setEmail(String email) { this.email = email; }

    /** @return формат печати */
    public String getFormat() { return format; }
    /** @param format новый формат печати */
    public void setFormat(String format) { this.format = format; }

    /** @return тип бумаги */
    public String getPaper() { return paper; }
    /** @param paper новый тип бумаги */
    public void setPaper(String paper) { this.paper = paper; }

    /** @return объем тиража */
    public Integer getQuantity() { return quantity; }
    /** @param quantity новый объем тиража */
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    /** @return способ оплаты */
    public String getPayment() { return payment; }
    /** @param payment новый способ оплаты */
    public void setPayment(String payment) { this.payment = payment; }

    /** @return предварительная стоимость с фронтенда */
    public Integer getTotal() { return total; }
    /** @param total новая предварительная стоимость */
    public void setTotal(Integer total) { this.total = total; }

    /** @return список прикрепленных файлов макетов */
    public List<String> getFiles() { return files; }
    /** @param files новый список имен файлов */
    public void setFiles(List<String> files) { this.files = files; }

    /** @return список выбранных дополнительных услуг */
    public List<String> getServices() { return services; }
    /** @param services новый список идентификаторов услуг */
    public void setServices(List<String> services) { this.services = services; }
}