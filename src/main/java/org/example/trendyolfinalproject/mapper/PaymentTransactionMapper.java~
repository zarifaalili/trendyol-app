package org.example.trendyolfinalproject.mapper;

import org.example.trendyolfinalproject.dao.entity.PaymentTransaction;
import org.example.trendyolfinalproject.model.response.PaymentTransactionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PaymentTransactionMapper {



    @Mapping(source = "order.id", target = "orderId")
    @Mapping(source = "payment.id", target = "paymentMethodId")
    @Mapping(source = "payment.cardNumber", target = "maskedCardNumber", qualifiedByName = "maskLast4Digits")
    PaymentTransactionResponse toResponse(PaymentTransaction paymentTransaction);

    List<PaymentTransactionResponse> toResponse(List<PaymentTransaction> paymentTransactions);

    @Named("maskLast4Digits")
    default String maskLast4Digits(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "Invalid/Unknown Card";
        }
        return "XXXX XXXX XXXX " + cardNumber.substring(cardNumber.length() - 4);
    }


}
