package ru.yandex.kingartaved.currencyrategifselectionapp.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.kingartaved.currencyrategifselectionapp.dto.response.GifSearchResponseDto;

@FeignClient(
        name = "gif-service",
        url = "${external-giphy.url}",
        configuration = GifServiceFeignClientConfig.class
)
public interface GifServiceFeignClient {

    @GetMapping()
    GifSearchResponseDto getGifData(@RequestParam("q") String searchWord);

}