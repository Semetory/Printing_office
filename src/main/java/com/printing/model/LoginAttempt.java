package com.printing.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "login_attempts")
public class LoginAttempt {

    @Id
    @Column(nullable = false, unique = true)
    private String username;

    private int attempts;

    private LocalDateTime lockTime;

    public LoginAttempt() {}

    public LoginAttempt(String username, int attempts, LocalDateTime lockTime) {
        this.username = username;
        this.attempts = attempts;
        this.lockTime = lockTime;
    }

    // Геттеры и сеттеры
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public int getAttempts() { return attempts; }
    public void setAttempts(int attempts) { this.attempts = attempts; }

    public LocalDateTime getLockTime() { return lockTime; }
    public void setLockTime(LocalDateTime lockTime) { this.lockTime = lockTime; }
}