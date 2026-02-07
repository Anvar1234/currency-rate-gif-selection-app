package ru.yandex.kingartaved.currencyrategifselectionapp.client;

import feign.RequestInterceptor;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация Feign-клиента для взаимодействия с внешним API Giphy.
 */
@Configuration
@Slf4j
public class GifServiceFeignClientConfig {

    @Value("${external-giphy.api-key}")
    private String apiKey;
    @Value("${external-giphy.search-limit}")
    private String limit;

    /**
     * Автоматически добавляет API-ключ и лимит ко всем запросам.
     */
    @Bean
    public RequestInterceptor feignRequestInterceptor() {
        log.info("Вызван перехватчик запросов в GifServiceFeignClientConfig");

        return requestTemplate -> {
            requestTemplate.query("api_key", apiKey);
            requestTemplate.query("limit", limit);
        };
    }

    @Bean
    public ErrorDecoder feignErrorDecoder() {
        return (methodKey, response) -> {
            if (response.status() == 401) {
                return new RuntimeException("Неверный API-ключ");
            } else if (response.status() == 404) {
                return new RuntimeException("Гифка не найдена");
            }
            return new RuntimeException("Ошибка внешнего API: " + response.status());
        };
    }

    @Bean
    public Retryer retryer() {
        log.info("Вызван метод retryer() в GifServiceFeignClientConfig");

        return new Retryer.Default(
                100,
                1000,
                3
        );
    }
}
