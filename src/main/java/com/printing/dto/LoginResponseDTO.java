package com.printing.dto;

public class LoginResponseDTO {
    private String role; // "ADMIN" или "SYSADMIN"
    private String redirectUrl;

    public LoginResponseDTO(String role, String redirectUrl) {
        this.role = role;
        this.redirectUrl = redirectUrl;
    }

    public String getRole() { return role; }
    public String getRedirectUrl() { return redirectUrl; }
}