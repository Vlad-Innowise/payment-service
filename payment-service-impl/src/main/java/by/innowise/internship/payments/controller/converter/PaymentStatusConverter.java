package by.innowise.internship.payments.controller.converter;

import by.innowise.internship.payments.model.entity.PaymentStatus;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class PaymentStatusConverter implements Converter<String, PaymentStatus> {

    @Override
    public PaymentStatus convert(String rawStatus) {
        return PaymentStatus.fromString(rawStatus);
    }
}
