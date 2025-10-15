package org.example.trendyolfinalproject.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.example.trendyolfinalproject.service.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender sender;

    @Value("${mail.from}")
    private String fromEmail;

    public EmailServiceImpl(JavaMailSender sender) {
        this.sender = sender;
    }

    @Override
    public void sendOtp(String to, String code) {
        log.info("Sending email to {}", to);
        SimpleMailMessage m = new SimpleMailMessage();
        m.setFrom(fromEmail);
        m.setTo(to);
        m.setSubject("Trendyol Təsdiqləmə Kodu");
        m.setText("Kodunuz: " + code + " (5 dəqiqə etibarlıdır)");
        sender.send(m);
        log.info("Email sent to {}", to);
    }

    @Override
    public void sendEmail(String to, String subject, String text) {
        log.info("Sending email to {}", to);
        SimpleMailMessage m = new SimpleMailMessage();
        m.setFrom(fromEmail);
        m.setTo(to);
        m.setSubject(subject);
        m.setText(text);
        sender.send(m);
        log.info("Email sent to {}", to);
    }

    @Override
    public void sendEmailWithImage(String to, String subject, String text, File image) throws MessagingException {
        log.info("Sending email with image to {}", to);
        MimeMessage message = sender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text, true);
        helper.addAttachment("sekil.jpg", image);
        sender.send(message);
        log.info("Email with image sent to {}", to);
    }

    @Override
    public void sendEmailWithHtml(String to, String subject) throws MessagingException {
        log.info("Sending email with html to {}", to);
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromEmail);
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
        log.info("Email with html sent to {}", to);
    }

}
