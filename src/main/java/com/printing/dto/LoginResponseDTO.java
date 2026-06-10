package com.printing.dto;

/**
 * Объект передачи данных (DTO) для ответа на успешную аутентификацию.
 * <p>
 * Формируется сервером после успешной проверки логина и пароля и содержит
 * информацию о роли вошедшего пользователя, а также целевой URL для перенаправления.
 * </p>
 *
 * @author Дмитрий
 * @version 1.0
 */
public class LoginResponseDTO {

    /** Уровень доступа/роль пользователя в системе (например, "ADMIN" или "SYSADMIN"). */
    private String role;

    /** Относительный путь к HTML-странице интерфейса, соответствующей роли. */
    private String redirectUrl;

    /**
     * Конструктор для создания объекта ответа аутентификации.
     *
     * @param role        присвоенная роль авторизованного пользователя
     * @param redirectUrl адрес страницы веб-интерфейса для редиректа
     */
    public LoginResponseDTO(String role, String redirectUrl) {
        this.role = role;
        this.redirectUrl = redirectUrl;
    }

    /** @return уровень доступа пользователя */
    public String getRole() { return role; }

    /** @return целевой URL веб-интерфейса панели управления */
    public String getRedirectUrl() { return redirectUrl; }
}