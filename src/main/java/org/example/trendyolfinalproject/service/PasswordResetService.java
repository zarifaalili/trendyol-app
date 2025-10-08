package org.example.trendyolfinalproject.service;

public interface PasswordResetService {

    void sendCode(String email);

    void verifyCode(String email, String code);

    void resetPassword(String email, String code, String newPassword, String confirmPassword);


}
