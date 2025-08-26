package org.example.trendyolfinalproject.mapper;

import org.example.trendyolfinalproject.dao.entity.PaymentTransaction;
import org.example.trendyolfinalproject.response.PaymentTransactionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PaymentTransactionMapper {

    PaymentTransactionMapper INSTANCE = Mappers.getMapper(PaymentTransactionMapper.class);


//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "order", ignore = true)
//    @Mapping(target = "payment", ignore = true)
//    PaymentTransaction toEntity(PaymentTransactionCreateRequest request);



    @Mapping(source = "order.id", target = "orderId")
    @Mapping(source = "payment.id", target = "paymentMethodId")
    @Mapping(source = "payment.cardNumber", target = "maskedCardNumber", qualifiedByName = "maskLast4Digits")
    PaymentTransactionResponse toResponse(PaymentTransaction paymentTransaction);

    @Named("maskLast4Digits")
    default String maskLast4Digits(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "Invalid/Unknown Card";
        }
        return "XXXX XXXX XXXX " + cardNumber.substring(cardNumber.length() - 4);
    }


}
