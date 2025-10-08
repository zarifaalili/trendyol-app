package org.example.trendyolfinalproject.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.trendyolfinalproject.dao.entity.ResetCode;
import org.example.trendyolfinalproject.dao.entity.User;
import org.example.trendyolfinalproject.dao.repository.ResetCodeRepository;
import org.example.trendyolfinalproject.dao.repository.UserRepository;
import org.example.trendyolfinalproject.exception.customExceptions.NotFoundException;
import org.example.trendyolfinalproject.model.enums.NotificationType;
import org.example.trendyolfinalproject.service.AuditLogService;
import org.example.trendyolfinalproject.service.EmailService;
import org.example.trendyolfinalproject.service.NotificationService;
import org.example.trendyolfinalproject.service.PasswordResetService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class PasswordResetServiceImpl implements PasswordResetService {
    private final ResetCodeRepository codeRepo;
    private final UserRepository userRepo;
    private final PasswordEncoder encoder;
    private final EmailService emailService;
    private final NotificationService notificationService;
    private final AuditLogService auditLogService;

    @Override
    public void sendCode(String email) {
        codeRepo.deleteByEmail(email);

        String code = String.format("%06d", new SecureRandom().nextInt(1_000_000));
        ResetCode rc = new ResetCode();
        rc.setEmail(email);
        rc.setCode(code);
        rc.setExpireTime(LocalDateTime.now().plusMinutes(5));
        codeRepo.save(rc);

        emailService.sendOtp(email, code);
    }


    @Override
    public void verifyCode(String email, String code) {
        ResetCode rc = codeRepo.findByEmailAndCode(email, code)
                .orElseThrow(() -> new RuntimeException("Kod yalnışdır"));
        if (rc.getExpireTime().isBefore(LocalDateTime.now()))
            throw new RuntimeException("Kodun vaxtı bitib");
    }



    @Override
    public void resetPassword(String email, String code, String newPassword, String confirmPassword) {
        verifyCode(email, code);

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User tapılmadı"));

        if (!newPassword.equals(confirmPassword)) {
            throw new RuntimeException("Parollar uyğun deyil");
        }

        user.setPasswordHash(encoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        userRepo.save(user);

        codeRepo.deleteByEmail(email);

        notificationService.sendNotification(user, "Parolunuz yenilendi", NotificationType.PASSWORD_RESET, user.getId());
        auditLogService.createAuditLog(user, "Password Reset", "Password reset successfully. User id: " + user.getId());

    }


}
