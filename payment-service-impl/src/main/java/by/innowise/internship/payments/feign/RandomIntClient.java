package by.innowise.internship.payments.feign;

import by.innowise.internship.payments.config.RandomIntClientFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "random-org", url = "${application.feign.clients.random-org.url}", configuration = {RandomIntClientFeignConfig.class})
public interface RandomIntClient {

    @GetMapping
    String getRandomInt(@RequestParam("num") int num,
                        @RequestParam("min") int min,
                        @RequestParam("max") int max,
                        @RequestParam("col") int col,
                        @RequestParam("base") int base,
                        @RequestParam("format") String format,
                        @RequestParam("rnd") String rnd
    );

}
