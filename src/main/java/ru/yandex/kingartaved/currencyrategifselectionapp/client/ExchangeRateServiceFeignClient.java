package ru.yandex.kingartaved.currencyrategifselectionapp.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.kingartaved.currencyrategifselectionapp.dto.response.CurrencyRateResponseDto;

/**
 * Feign клиент для получения курса валют.
 */
@FeignClient(
        name = "rate-service",
        url = "${external-exchangerate.url}",
        configuration = ExchangeRateServiceFeignClientConfig.class
)
public interface ExchangeRateServiceFeignClient {

    @GetMapping("/{base_currency}")
    CurrencyRateResponseDto getRate(@PathVariable("base_currency") String baseCurrency);
}
