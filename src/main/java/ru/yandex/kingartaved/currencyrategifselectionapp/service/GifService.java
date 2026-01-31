package ru.yandex.kingartaved.currencyrategifselectionapp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.yandex.kingartaved.currencyrategifselectionapp.client.GifServiceFeignClient;
import ru.yandex.kingartaved.currencyrategifselectionapp.client.RateServiceFeignClient;

@Service
@RequiredArgsConstructor
public class GifService {
    @Value("${external-giphy.search-word.positive-rate}")
    private String positiveRateSearchWord;
    @Value("${external-giphy.search-word.negative-rate}")
    private String negativeRateSearchWord;

    private Double currentRate; // todo: получаем из сервиса exchangerate-api
    private Double yesterdaysRate; // todo: получаем из БД

    private final RateServiceFeignClient rateServiceFeignClient;
    private final GifServiceFeignClient gifServiceFeignClient;

    public String getGifUrl() {
        return currentRate > yesterdaysRate ?
                gifServiceFeignClient.getRandomGif(positiveRateSearchWord).getUrl() :
                gifServiceFeignClient.getRandomGif(negativeRateSearchWord).getUrl();

    }

}
