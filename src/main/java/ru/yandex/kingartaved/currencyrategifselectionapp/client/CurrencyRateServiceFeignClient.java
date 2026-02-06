package ru.yandex.kingartaved.currencyrategifselectionapp.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.kingartaved.currencyrategifselectionapp.dto.response.CurrencyRateResponseDto;

@FeignClient(
        name = "rate-service",
        url = "${external-exchangerate.url}"
)
public interface CurrencyRateServiceFeignClient {

    @GetMapping("/{base_currency}")
    CurrencyRateResponseDto getRate(@PathVariable("base_currency") String baseCurrency);
}
