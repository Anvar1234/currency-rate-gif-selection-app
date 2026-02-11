package ru.yandex.kingartaved.currencyrategifselectionapp.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.kingartaved.currencyrategifselectionapp.client.ExchangeRateServiceFeignClient;
import ru.yandex.kingartaved.currencyrategifselectionapp.data.model.CurrencyRateEntity;
import ru.yandex.kingartaved.currencyrategifselectionapp.data.repository.CurrencyRateRepository;
import ru.yandex.kingartaved.currencyrategifselectionapp.dto.response.CurrencyRateResponseDto;
import ru.yandex.kingartaved.currencyrategifselectionapp.exception.CurrencyRateNotFoundException;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Вспомогательный сервис приложения для получения актуального курса валют.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CurrencyRateDeliveryService {

    private final ExchangeRateServiceFeignClient exchangeRateServiceFeignClient;

    private final CurrencyRateRepository currencyRateRepository;

    @Transactional
    public CurrencyRateEntity getAndSaveActualCurrencyRateEntity(
            String incomingBaseCurrency,
            String incomingCurrency
    ) {
        log.debug("Вызов метода getActualCurrencyRateEntity() для базовой валюты: {}, и целевой валюты: {}",
                incomingBaseCurrency,
                incomingCurrency
        );

        incomingBaseCurrency = incomingBaseCurrency.toUpperCase();
        incomingCurrency = incomingCurrency.toUpperCase();

        BigDecimal actualRate = fetchCurrencyRate(incomingBaseCurrency, incomingCurrency);

        if (actualRate == null) {
            log.debug("Не удалось получить курс для валют: {}/{}", incomingBaseCurrency, incomingCurrency);

            throw new CurrencyRateNotFoundException(String.format("Курс для валют %s/%s не найден",
                    incomingBaseCurrency, incomingCurrency));
        }

        CurrencyRateEntity actualCurrencyRateEntity = buildCurrencyRateEntity(
                incomingBaseCurrency,
                incomingCurrency,
                actualRate
        );

        saveCurrencyRateEntity(actualCurrencyRateEntity);

        return actualCurrencyRateEntity;
    }

    private BigDecimal fetchCurrencyRate(String incomingBaseCurrency, String incomingCurrency) {
        log.debug("Получение курса для валют: {}/{}", incomingBaseCurrency, incomingCurrency);

        CurrencyRateResponseDto currencyRateResponseDto = exchangeRateServiceFeignClient.
                getRate(incomingBaseCurrency);

        if (currencyRateResponseDto == null ||
                currencyRateResponseDto.getConversionRates() == null ||
                currencyRateResponseDto.getConversionRates().isEmpty()) {

            log.error("Сервис курсов валют вернул некорректный ответ для валют: {}/{}",
                    incomingBaseCurrency,
                    incomingCurrency);

            throw new CurrencyRateNotFoundException(
                    String.format("Сервис курсов валют вернул некорректный ответ для валют: %s/%s",
                            incomingBaseCurrency,
                            incomingCurrency
                    )
            );
        }
        return currencyRateResponseDto.getConversionRates().get(incomingCurrency);
    }

    private CurrencyRateEntity buildCurrencyRateEntity(String incomingBaseCurrency, String incomingCurrency, BigDecimal actualRate) {

        log.debug("Формирование сущности для валют: {}/{}", incomingBaseCurrency, incomingCurrency);

        return CurrencyRateEntity.builder().
                baseCurrency(incomingBaseCurrency).
                currency(incomingCurrency).
                rate(actualRate).
                date(LocalDateTime.now()).
                build();
    }

    private void saveCurrencyRateEntity(CurrencyRateEntity currencyRateEntity) {

        log.debug("Сохранение сущности {} в БД", currencyRateEntity);

        currencyRateRepository.save(currencyRateEntity);
    }
}
