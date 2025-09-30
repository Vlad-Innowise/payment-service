package by.innowise.internship.payments.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties
public class RandomOrgProperty {

    private static final String INTEGER_FORMAT_PLAIN = "plain";
    private static final String RANDOM_GEN_TYPE = "new";

    private int num = 1;

    @Value("${application.feign.clients.random-org.min}")
    private int min = 1;

    @Value("${application.feign.clients.random-org.max}")
    private int max = Integer.MAX_VALUE;

    private int col = 1;

    private int base = 10;

    private String format = INTEGER_FORMAT_PLAIN;

    private String rnd = RANDOM_GEN_TYPE;
}
