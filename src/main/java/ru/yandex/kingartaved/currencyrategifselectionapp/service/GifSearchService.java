package ru.yandex.kingartaved.currencyrategifselectionapp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import ru.yandex.kingartaved.currencyrategifselectionapp.client.GifServiceFeignClient;
import ru.yandex.kingartaved.currencyrategifselectionapp.dto.GifDto;
import ru.yandex.kingartaved.currencyrategifselectionapp.dto.response.GifSearchResponseDto;
import ru.yandex.kingartaved.currencyrategifselectionapp.exception.GifNotFoundException;

import java.util.List;

/**
 * Вспомогательный сервис приложения для поиска GIF по ключевому слову.
 */
@Service
@RequiredArgsConstructor
public class GifSearchService {

    private final GifServiceFeignClient gifServiceFeignClient;

    @Cacheable(value = "gifs", key = "#searchWord")
    public List<GifDto> getGifsForWord(String searchWord) {
        GifSearchResponseDto responseDto = gifServiceFeignClient.getGifData(searchWord);

        if (responseDto == null || responseDto.getData() == null || responseDto.getData().isEmpty()) {
            throw new GifNotFoundException("Не найдено GIF для слова: " + searchWord);
        }
        return responseDto.getData();
    }
}
