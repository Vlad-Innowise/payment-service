package by.innowise.internship.payments.config;

import feign.Logger;
import org.springframework.context.annotation.Bean;

public class RandomIntClientFeignConfig {

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

}
