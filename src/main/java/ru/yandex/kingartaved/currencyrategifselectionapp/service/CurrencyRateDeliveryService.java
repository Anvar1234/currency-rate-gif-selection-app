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
    public CurrencyRateEntity getActualCurrencyRateEntity(
            String incomingBaseCurrency,
            String incomingCurrency
    ) {
        log.debug("Вызов метода getActualCurrencyRateEntity() для базовой валюты: {}, и целевой валюты: {}",
                incomingBaseCurrency,
                incomingCurrency
        );

        BigDecimal actualRate = fetchCurrencyRate(incomingBaseCurrency, incomingCurrency);

        CurrencyRateEntity actualCurrencyRateEntity = buildCurrencyRateEntity(
                incomingBaseCurrency,
                incomingCurrency,
                actualRate
        );

        return saveCurrencyRateEntity(actualCurrencyRateEntity);
    }

    private BigDecimal fetchCurrencyRate(String incomingBaseCurrency, String incomingCurrency) {
        log.debug("Получение курса для валют: {}/{}", incomingBaseCurrency, incomingCurrency);

        incomingBaseCurrency = incomingBaseCurrency.toUpperCase();
        incomingCurrency = incomingCurrency.toUpperCase();

        CurrencyRateResponseDto currencyRateResponseDto = exchangeRateServiceFeignClient.
                getRate(incomingBaseCurrency);

        if (currencyRateResponseDto == null || currencyRateResponseDto.getConversionRates() == null) {

            log.error("Сервис курсов валют вернул некорректный ответ для валют: {}/{}",
                    incomingBaseCurrency,
                    incomingCurrency
            );

            throw new CurrencyRateNotFoundException(
                    String.format("Не найден курс для валют: %s/%s", incomingBaseCurrency, incomingCurrency)
            );
        }

        BigDecimal actualRate = currencyRateResponseDto.getConversionRates().get(incomingCurrency);

        if (actualRate == null) {
            log.error("Курс для валюты {} не найден в ответе от сервиса курсов валют", incomingCurrency);

            throw new CurrencyRateNotFoundException(
                    String.format("Курс для валюты %s не найден", incomingCurrency));
        }

        return actualRate;
    }

    private CurrencyRateEntity buildCurrencyRateEntity(
            String incomingBaseCurrency,
            String incomingCurrency,
            BigDecimal actualRate
    ) {

        log.debug("Формирование сущности для валют: {}/{}", incomingBaseCurrency, incomingCurrency);

        return CurrencyRateEntity.builder().
                baseCurrency(incomingBaseCurrency).
                currency(incomingCurrency).
                rate(actualRate).
                date(LocalDateTime.now()).
                build();
    }

    private CurrencyRateEntity saveCurrencyRateEntity(CurrencyRateEntity currencyRateEntity) {

        log.debug("Сохранение сущности {} в БД", currencyRateEntity);

        return currencyRateRepository.save(currencyRateEntity);
    }
}
