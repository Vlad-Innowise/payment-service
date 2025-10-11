package by.innowise.internship.payments.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "application.feign.clients.random-org")
public class RandomOrgProperty {

    private static final int EXTERNAL_SERVICE_MAX_INT_VALUE = 1_000_000_000;
    private static final String INTEGER_FORMAT_PLAIN = "plain";
    private static final String RANDOM_GEN_TYPE = "new";

    private String url;

    private int num = 1;

    private int min = 1;

    private int max = EXTERNAL_SERVICE_MAX_INT_VALUE;

    private int col = 1;

    private int base = 10;

    private String format = INTEGER_FORMAT_PLAIN;

    private String rnd = RANDOM_GEN_TYPE;
}
