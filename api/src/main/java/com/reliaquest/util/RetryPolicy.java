package com.reliaquest.util;

import com.reliaquest.exceptions.RetryFailedException;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;

@Slf4j
public class RetryPolicy {

    /**
     * Retry strategy:
     * - Retries only for:
     *    * 429 TOO MANY REQUESTS
     *
     * - Do NOT retry for:
     *    * 404 NOT FOUND
     *    * 400 BAD REQUEST
     *    * Other 4xx errors
     *    * Business exceptions thrown after onErrorMap
     */
    public static Retry retrySpec() {
        return Retry.backoff(3, Duration.ofMillis(300)) // 3 retries, exponential backoff
                .filter(RetryPolicy::shouldRetry) // custom retry conditions
                .doBeforeRetry(retrySignal -> {
                    log.warn(
                            "Retry attempt {} due to {}",
                            retrySignal.totalRetries(),
                            retrySignal.failure().getMessage());
                })
                .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> {
                    log.error(
                            "Retry exhausted after {} attempts. Last error: {}",
                            retrySignal.totalRetries(),
                            retrySignal.failure().getMessage());
                    // Retry finally failed after max attempts
                    Throwable lastFailure = retrySignal.failure();
                    throw new RetryFailedException("Retry attempts exhausted", lastFailure);
                }); // throw original error after max retries
    }

    /**
     * Retry only for 429
     */
    private static boolean shouldRetry(Throwable ex) {

        if (ex instanceof WebClientResponseException wex) {

            // Retry 429 Too Many Requests
            if (wex.getStatusCode().value() == 429) {
                return true;
            }

            // Retry 5xx server errors
            if (wex.getStatusCode().is5xxServerError()) {
                return true;
            }

            // Do NOT retry for other 4xx client errors (400, 401, 403, 404)
            return false;
        }

        return false;
    }
}
