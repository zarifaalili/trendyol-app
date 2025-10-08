package org.example.trendyolfinalproject.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.example.trendyolfinalproject.service.EmailService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@AllArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender sender;

    @Override
    public void sendOtp(String to, String code) {
        SimpleMailMessage m = new SimpleMailMessage();
        m.setFrom("alilizarifa@gmail.com");
        m.setTo(to);
        m.setSubject("Trendyol Təsdiqləmə Kodu");
        m.setText("Kodunuz: " + code + " (5 dəqiqə etibarlıdır)");
        sender.send(m);
    }

    @Override
    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage m = new SimpleMailMessage();
        m.setFrom("alilizarifa@gmail.com");
        m.setTo(to);
        m.setSubject(subject);
        m.setText(text);
        sender.send(m);
    }

    @Override
    public void sendEmailWithImage(String to, String subject, String text, File image) throws MessagingException {
        MimeMessage message = sender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom("alilizarifa@gmail.com");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text, true);

        helper.addAttachment("sekil.jpg", image);

        sender.send(message);
    }

    @Override
    public void sendEmailWithHtml(String to, String subject) throws MessagingException {
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom("alilizarifa@gmail.com");
        helper.setTo(to);
        helper.setSubject(subject);

        String html = """
        <html>
          <body style="font-family: Arial, sans-serif;">
            <h2 style="color: #f27a1a;">Trendyol</h2>
            <p>Endirimləri qaçırma!</p>
            <a href="https://www.trendyol.com" 
               style="padding: 10px 20px; background: #f27a1a; color: white; text-decoration: none; border-radius: 5px;">
               Trendyola keç
            </a>
          </body>
        </html>
        """;

        helper.setText(html, true);
        sender.send(message);
    }

}
