package by.innowise.internship.payments.mapper;

import by.innowise.internship.payments.model.dto.PaymentRequestDto;
import by.innowise.internship.payments.model.dto.PaymentResponseDto;
import by.innowise.internship.payments.model.entity.Payment;
import org.bson.types.ObjectId;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = BaseMapper.class)
public interface PaymentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    Payment toEntity(PaymentRequestDto d, @Context Long userId);

    PaymentResponseDto toDto(Payment e);

    @AfterMapping
    default void mapAdditionalProperties(@MappingTarget Payment payment, @Context Long userId) {
        if (payment == null) {
            return;
        }
        payment.setUserId(userId);
    }

    default String mapObjectIdToString(ObjectId id) {
        return id != null ? id.toHexString() : null;
    }

}
