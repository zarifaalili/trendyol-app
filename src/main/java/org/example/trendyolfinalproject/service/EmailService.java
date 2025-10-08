package org.example.trendyolfinalproject.service;

import jakarta.mail.MessagingException;

import java.io.File;

public interface EmailService {

    void sendOtp(String to, String code);

    void sendEmail(String to, String subject, String text);

    void sendEmailWithImage(String to, String subject, String text, File image) throws MessagingException;

    void sendEmailWithHtml(String to, String subject) throws MessagingException;

}
