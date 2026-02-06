package ru.yandex.kingartaved.currencyrategifselectionapp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.yandex.kingartaved.currencyrategifselectionapp.dto.GifDto;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class GifService {
    @Value("${external-giphy.search-word.positive-rate}")
    private String positiveRateSearchWord;
    @Value("${external-giphy.search-word.negative-rate}")
    private String negativeRateSearchWord;

    private final Map<String, List<GifDto>> cache = new ConcurrentHashMap<>();

    private final GifSearchService gifSearchService;
    private final CurrencyRateService currencyRateService;

    public String getGifsUrl(String baseCurrency, String currency) {
        return getRandomGif(currencyRateService.
                isRateIncreased(baseCurrency, currency)).
                getUrl();
    }

    public GifDto getRandomGif(boolean isRateIncreased) {
        String searchWord = isRateIncreased ? positiveRateSearchWord : negativeRateSearchWord;
        List<GifDto> gifs = gifSearchService.getGifsForWord(searchWord);

        return gifs.get(new Random().nextInt(gifs.size()));
    }
}


