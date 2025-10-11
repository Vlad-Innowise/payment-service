package by.innowise.internship.payments.service.impl;

import by.innowise.common.library.exception.ApplicationException;
import by.innowise.internship.payments.config.RandomOrgProperty;
import by.innowise.internship.payments.feign.RandomIntClient;
import by.innowise.internship.payments.service.RandomIntService;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RandomIntServiceImpl implements RandomIntService {

    private static final int FALLBACK_INT_VALUE = 1;
    private final RandomIntClient client;
    private final RandomOrgProperty property;

    @Retry(name = "randomIntServiceRetry", fallbackMethod = "fallbackGetRandomInt")
    @Override
    public Integer getRandomInt() {
        log.info("Sending a request to the external service to get a random int");
        String rawInt = client.getRandomInt(property.getNum(),
                                            property.getMin(),
                                            property.getMax(),
                                            property.getCol(),
                                            property.getBase(),
                                            property.getFormat(),
                                            property.getRnd());

        Integer number = convertToInt(rawInt);
        log.info("Got a random number from the external service: {}", number);
        return number;
    }

    public Integer fallbackGetRandomInt(Exception e) {
        log.warn("Fallback: Wasn't able to get a random int from the client: {}. Returning a fallback int value: [{}]",
                 property.getUrl(), FALLBACK_INT_VALUE, e);
        return FALLBACK_INT_VALUE;
    }

    private Integer convertToInt(String rawInt) {
        log.info("Converting a raw number to an Integer: {}", rawInt);
        int num;
        try {
            num = Integer.parseInt(rawInt.trim());
        } catch (NumberFormatException e) {
            throw new ApplicationException("Failed to convert a num: [%s] to an integer".formatted(rawInt),
                                           HttpStatus.INTERNAL_SERVER_ERROR, e);
        }
        return num;
    }
}
