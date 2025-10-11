package by.innowise.internship.payments.config;

import io.github.resilience4j.retry.RetryRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class Resilence4jRetryConfig {

    private final RetryRegistry retryRegistry;

    @PostConstruct
    public void registerGlobalRetryEventListener() {
        retryRegistry.getAllRetries()
                     .forEach(
                             retry -> {
                                 retry.getEventPublisher()
                                      .onRetry(event ->
                                                       log.warn("Retry: [{}], attempt: [{}] caused by: ",
                                                                retry.getName(),
                                                                event.getNumberOfRetryAttempts(),
                                                                event.getLastThrowable())
                                      )
                                      .onSuccess(event ->
                                                         log.info("Retry: [{}], success after [{}] attempts",
                                                                  retry.getName(),
                                                                  event.getNumberOfRetryAttempts())
                                      )
                                      .onError(event -> log.error("[{}] Failed after [{}] attempts: ",
                                                                  retry.getName(),
                                                                  event.getNumberOfRetryAttempts(),
                                                                  event.getLastThrowable()));
                             }
                     );
        /** for retries added in runtime*/
        retryRegistry.getEventPublisher()
                     .onEntryAdded(entry ->
                                           entry.getAddedEntry()
                                                .getEventPublisher()
                                                .onRetry(event ->
                                                                 log.warn("Retry: [{}], attempt: [{}] caused by: ",
                                                                          entry.getAddedEntry().getName(),
                                                                          event.getNumberOfRetryAttempts(),
                                                                          event.getLastThrowable()))
                     );
    }

}
