package ru.yandex.kingartaved.currencyrategifselectionapp.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import ru.yandex.kingartaved.currencyrategifselectionapp.dto.GifDto;

@FeignClient(
        name = "rate-service",
        url = "${external-exchangerate.url}"
)
public interface RateServiceFeignClient {

    @GetMapping()
    GifDto getRate();
}
