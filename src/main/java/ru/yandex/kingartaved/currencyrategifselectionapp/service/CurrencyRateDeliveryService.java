package ru.yandex.kingartaved.currencyrategifselectionapp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.kingartaved.currencyrategifselectionapp.client.CurrencyRateServiceFeignClient;
import ru.yandex.kingartaved.currencyrategifselectionapp.data.model.CurrencyRateEntity;
import ru.yandex.kingartaved.currencyrategifselectionapp.data.repository.CurrencyRateRepository;
import ru.yandex.kingartaved.currencyrategifselectionapp.dto.response.CurrencyRateResponseDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Вспомогательный сервис приложения для получения актуального курса валют.
 */
@Service
@RequiredArgsConstructor
public class CurrencyRateDeliveryService {

    private final CurrencyRateServiceFeignClient currencyRateServiceFeignClient;

    private final CurrencyRateRepository currencyRateRepository;

    public CurrencyRateEntity getActualCurrencyRateEntity(
            String incomingBaseCurrency,
            String incomingCurrency
    ) {
        incomingBaseCurrency = incomingBaseCurrency.toUpperCase();
        incomingCurrency = incomingCurrency.toUpperCase();

        // Получаем текущий курс
        CurrencyRateResponseDto currencyRateResponseDto = currencyRateServiceFeignClient.
                getRate(incomingBaseCurrency);
        BigDecimal actualRate = currencyRateResponseDto.getConversionRates().
                get(incomingCurrency);

        // Формируем сущность БД
        CurrencyRateEntity actualCurrencyRateEntity =
                CurrencyRateEntity.builder().
                        baseCurrency(incomingBaseCurrency).
                        currency(incomingCurrency).
                        rate(actualRate).
                        date(LocalDateTime.now()).
                        build();

        return currencyRateRepository.save(actualCurrencyRateEntity);
    }
}
