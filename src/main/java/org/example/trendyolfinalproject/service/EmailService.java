package org.example.trendyolfinalproject.service;

import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EmailService {
    private final JavaMailSender sender;

    public void sendOtp(String to, String code) {
        SimpleMailMessage m = new SimpleMailMessage();
        m.setFrom("alilizarifa@gmail.com");
        m.setTo(to);
        m.setSubject("Trendyol Təsdiqləmə Kodu");
        m.setText("Kodunuz: " + code + " (5 dəqiqə etibarlıdır)");
        sender.send(m);
    }

    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage m = new SimpleMailMessage();
        m.setFrom("alilizarifa@gmail.com");
        m.setTo(to);
        m.setSubject(subject);
        m.setText(text);
        sender.send(m);
    }
}
