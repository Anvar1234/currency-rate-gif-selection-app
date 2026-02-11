package ru.yandex.kingartaved.currencyrategifselectionapp.client;

import feign.Logger;
import feign.RequestInterceptor;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация Feign-клиентов для взаимодействия с внешним API.
 */
@Configuration
@Slf4j
public class GifServiceFeignClientConfig {

    @Value("${external-giphy.api-key}")
    private String apiKey;

    @Value("${external-giphy.search-limit}")
    private String limit;

    /**
     * Автоматически добавляет API-ключ и лимит ко всем запросам для Giphy API.
     */
    @Bean
    public RequestInterceptor giphyServiceFeignRequestInterceptor() {
        log.info("Вызван перехватчик запросов в GifServiceFeignClientConfig");

        return requestTemplate -> {
            if (requestTemplate.feignTarget().name().equals("giphy-service")) {
                requestTemplate.query("api_key", apiKey);
                requestTemplate.query("limit", limit);

                log.debug("Добавлены параметры запроса: api_key, limit для Giphy API");
            }

        };
    }

    /**
     * Общий ErrorDecoder для всех Feign-клиентов.
     */
    @Bean
    public ErrorDecoder feignErrorDecoder() {
        return (methodKey, response) -> {
            if (response.status() == 401) {
                return new RuntimeException("Неверный API-ключ");
            } else if (response.status() == 404) {
                return new RuntimeException("Не найдено");
            }
            return new RuntimeException("Ошибка внешнего API: " + response.status());
        };
    }

    /**
     * Общий Retryer для всех Feign-клиентов.
     */
    @Bean
    public Retryer retryer() {
        log.info("Вызван метод retryer() в GifServiceFeignClientConfig");

        return new Retryer.Default(
                100,
                1000,
                3
        );
    }

    /**
     * Уровень логирования для Feign-клиента GiphyServiceFeignClient.
     */
    @Bean
    Logger.Level giphyServiceFeignLoggerLevel() {
        return Logger.Level.BASIC;
    }
}
