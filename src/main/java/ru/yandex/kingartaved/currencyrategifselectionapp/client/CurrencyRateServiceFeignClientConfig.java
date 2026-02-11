package ru.yandex.kingartaved.currencyrategifselectionapp.client;

import feign.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CurrencyRateServiceFeignClientConfig {

    @Bean
    Logger.Level currencyRateServiceFeignLoggerLevel() {
        return Logger.Level.HEADERS;
    }
}
