package ru.yandex.kingartaved.currencyrategifselectionapp.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class GifService {

    @Value("${external-giphy.search-word.positive-rate}")
    private String positiveRateSearchWord;

    @Value("${external-giphy.search-word.negative-rate}")
    private String negativeRateSearchWord;

    private final GifSearchService gifSearchService;

    private final CurrencyRateDeliveryService currencyRateDeliveryService;

    private final CurrencyRateRepository currencyRateRepository;

    public String getGifsUrl(String baseCurrency, String currency) {
        log.info("Вызов метода getGifsUrl() в классе GifService. Получение URL гифки.");

        return getRandomGif(isRateIncreased(baseCurrency, currency)).getUrl();
    }

    private GifDto getRandomGif(boolean isRateIncreased) {
        log.info("Вызов метода getRandomGif() в классе GifService. Получение случайной гифки.");

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
        log.info("Вызов метода isRateIncreased() в классе GifService. Проверка на увеличение курса валюты.");

        // Получаем текущий и предыдущий курсы валют
        CurrencyRateEntity actualCurrencyRateEntity =
                currencyRateDeliveryService.getAndSaveActualCurrencyRateEntity(
                        incomingBaseCurrency, incomingCurrency
                );

        CurrencyRateEntity lastCurrencyRateEntity =
                currencyRateRepository.
                        findLatestCurrencyRateByBaseCurrencyAndCurrencyAndDate(
                                incomingBaseCurrency,
                                incomingCurrency,
                                actualCurrencyRateEntity.getDate().minusSeconds(1) // минус 1 секунда для поиска ПРЕДЫДУЩЕЙ записи
                        ).orElseThrow(() ->
                                new EntityNotFoundException("Не найден предыдущий курс валют."));

        // Сравниваем курсы
        BigDecimal actualRate = actualCurrencyRateEntity.getRate();
        BigDecimal lastRate = lastCurrencyRateEntity.getRate();
        int comparison = actualRate.compareTo(lastRate);

        return comparison > 0;
    }
}


