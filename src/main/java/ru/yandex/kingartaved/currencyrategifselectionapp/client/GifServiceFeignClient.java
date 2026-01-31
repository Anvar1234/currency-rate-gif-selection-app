package ru.yandex.kingartaved.currencyrategifselectionapp.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import ru.yandex.kingartaved.currencyrategifselectionapp.dto.GifDto;

@FeignClient(
        name = "gif-service",
        url = "${external-giphy.url}",
        configuration = GifServiceFeignClientConfig.class
)
public interface GifServiceFeignClient {

    @GetMapping()
    GifDto getRandomGif(String tag);
}
