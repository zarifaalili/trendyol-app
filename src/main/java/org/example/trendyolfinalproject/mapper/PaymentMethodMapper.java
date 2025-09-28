package org.example.trendyolfinalproject.mapper;

import org.example.trendyolfinalproject.dao.entity.PaymentMethod;
import org.example.trendyolfinalproject.model.request.PaymentMethodCreateRequest;
import org.example.trendyolfinalproject.model.response.PaymentMethodResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface PaymentMethodMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "balance", ignore = true)
    PaymentMethod toEntity(PaymentMethodCreateRequest request);

    @Mapping(source = "userId.id", target = "userId")
    @Mapping(source = "cardNumber", target = "maskedCardNumber", qualifiedByName = "maskCardNumber")
    @Mapping(target = "balance",ignore = true)
    PaymentMethodResponse toResponse(PaymentMethod paymentMethod);



    @Named("maskCardNumber")
    default String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return cardNumber;
        }

        return "XXXX XXXX XXXX " + cardNumber.substring(cardNumber.length() - 4);
    }

}
