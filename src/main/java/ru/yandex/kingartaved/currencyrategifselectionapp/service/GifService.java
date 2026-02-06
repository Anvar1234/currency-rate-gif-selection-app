package ru.yandex.kingartaved.currencyrategifselectionapp.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.yandex.kingartaved.currencyrategifselectionapp.data.model.CurrencyRateEntity;
import ru.yandex.kingartaved.currencyrategifselectionapp.data.repository.CurrencyRateRepository;
import ru.yandex.kingartaved.currencyrategifselectionapp.dto.GifDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

/**
 * Основной сервис приложения.
 */
@Service
@RequiredArgsConstructor
public class GifService {

    @Value("${external-giphy.search-word.positive-rate}")
    private String positiveRateSearchWord;

    @Value("${external-giphy.search-word.negative-rate}")
    private String negativeRateSearchWord;

    private final GifSearchService gifSearchService;

    private final CurrencyRateDeliveryService currencyRateDeliveryService;

    private final CurrencyRateRepository currencyRateRepository;

    public String getGifsUrl(String baseCurrency, String currency) {

        return getRandomGif(isRateIncreased(baseCurrency, currency)).getUrl();
    }

    private GifDto getRandomGif(boolean isRateIncreased) {
        String searchWord = isRateIncreased ?
                positiveRateSearchWord :
                negativeRateSearchWord;

        List<GifDto> gifs = gifSearchService.getGifsForWord(searchWord);

        return gifs.get(new Random().nextInt(gifs.size()));
    }

    private boolean isRateIncreased(
            String incomingBaseCurrency,
            String incomingCurrency
    ) {
        // Получаем текущий и предыдущий курсы валют
        CurrencyRateEntity actualCurrencyRateEntity =
                currencyRateDeliveryService.getActualCurrencyRateEntity(
                        incomingBaseCurrency, incomingCurrency
                );

        CurrencyRateEntity lastCurrencyRateEntity =
                currencyRateRepository.
                        findLatestCurrencyRateByBaseCurrencyAndCurrencyAndDate(
                                incomingBaseCurrency,
                                incomingCurrency,
                                actualCurrencyRateEntity.getDate().minusSeconds(1) // минус 1 секунда для поиска ПРЕДЫДУЩЕЙ записи
                        ).orElseThrow(() ->
                                new EntityNotFoundException("Не найден предыдущий курс указанных валют."));

        // Сравниваем курсы
        BigDecimal actualRate = actualCurrencyRateEntity.getRate();
        BigDecimal lastRate = lastCurrencyRateEntity.getRate();
        int comparison = actualRate.compareTo(lastRate);

        return comparison > 0;
    }
}


