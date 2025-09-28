package org.example.trendyolfinalproject.model.response;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.trendyolfinalproject.dao.entity.Adress;
import org.example.trendyolfinalproject.dao.entity.PaymentMethod;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileResponse {
    private Long id;
    private String fullName;
    private String username;
    private String email;
    private String phoneNumber;
    private LocalDate dateOfBirth;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<Adress> addresses;
    private PaymentMethod defaultPaymentMethod;

    private Integer wishlistCount;
    private Integer orderCount;
    private Double totalSpent;
}
