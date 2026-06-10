package com.printing.dto;

/**
 * Объект передачи данных (DTO) для отправки запроса на аутентификацию.
 * <p>
 * Используется клиентской стороной для передачи учетных данных пользователя
 * в REST-контроллер при попытке входа в защищенную панель управления.
 * </p>
 *
 * @author Дмитрий
 * @version 1.0
 */
public class LoginRequestDTO {

    /** Уникальное имя пользователя (логин) администратора или системного администратора. */
    private String username;

    /** Пароль для верификации учетной записи. */
    private String password;

    /** @return текущее имя пользователя (логин) */
    public String getUsername() { return username; }

    /** @param username новое имя пользователя (логин) */
    public void setUsername(String username) { this.username = username; }

    /** @return введенный пароль */
    public String getPassword() { return password; }

    /** @param password новый пароль для аутентификации */
    public void setPassword(String password) { this.password = password; }
}