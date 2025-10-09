package by.innowise.internship.payments.service.impl;

import by.innowise.common.library.exception.ApplicationException;
import by.innowise.internship.payments.config.RandomOrgProperty;
import by.innowise.internship.payments.feign.RandomIntClient;
import by.innowise.internship.payments.model.entity.PaymentStatus;
import by.innowise.internship.payments.service.PaymentProcessorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentProcessorServiceImpl implements PaymentProcessorService {

    private final RandomIntClient client;
    private final RandomOrgProperty property;

    @Override
    public PaymentStatus processPayment() {
        return isPaymentSucceed() ? PaymentStatus.SUCCEED : PaymentStatus.FAILED;
    }

    private boolean isPaymentSucceed() {
        return getRandomInt() % 2 == 0;
    }

    private Integer getRandomInt() {
        log.info("Sending response to external service to get random int");
        String rawInt = client.getRandomInt(property.getNum(),
                                            property.getMin(),
                                            property.getMax(),
                                            property.getCol(),
                                            property.getBase(),
                                            property.getFormat(),
                                            property.getRnd());
        Integer number = convertToInt(rawInt);
        log.info("Got random number from external service: {}", number);
        return number;
    }

    private Integer convertToInt(String rawInt) {
        log.info("Converting raw number to Integer: {}", rawInt);
        int num;
        try {
            num = Integer.parseInt(rawInt.trim());
        } catch (NumberFormatException e) {
            throw new ApplicationException("Failed to convert num: [%s] to integer".formatted(rawInt),
                                           HttpStatus.INTERNAL_SERVER_ERROR, e);
        }
        return num;
    }

}
