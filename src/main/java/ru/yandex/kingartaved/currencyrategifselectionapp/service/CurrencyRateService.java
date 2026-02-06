package ru.yandex.kingartaved.currencyrategifselectionapp.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.kingartaved.currencyrategifselectionapp.client.CurrencyRateServiceFeignClient;
import ru.yandex.kingartaved.currencyrategifselectionapp.data.model.CurrencyRateEntity;
import ru.yandex.kingartaved.currencyrategifselectionapp.data.repository.CurrencyRateRepository;
import ru.yandex.kingartaved.currencyrategifselectionapp.dto.response.CurrencyRateResponseDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CurrencyRateService {

    private final CurrencyRateServiceFeignClient currencyRateServiceFeignClient;
    private final CurrencyRateRepository currencyRateRepository;

    public boolean isRateIncreased(String incomingBaseCurrency, String incomingCurrency) {
        CurrencyRateEntity actualCurrencyRateEntity = getActualCurrencyRateEntity(incomingBaseCurrency, incomingCurrency);
        CurrencyRateEntity lastCurrencyRateEntity = currencyRateRepository.findLatestCurrencyRateByBaseCurrencyAndCurrencyAndDate(
                incomingBaseCurrency,
                incomingCurrency,
                actualCurrencyRateEntity.getDate().minusSeconds(1) // минус 1 секунда для поиска ПРЕДЫДУЩЕЙ записи
        ).orElseThrow(() -> new EntityNotFoundException("Не найден предыдущий курс указанных валют."));

        // Сравниваем курсы
        BigDecimal actualRate = actualCurrencyRateEntity.getRate();
        BigDecimal lastRate = lastCurrencyRateEntity.getRate();
        int comparison = actualRate.compareTo(lastRate);
        return comparison > 0;
    }

    private CurrencyRateEntity getActualCurrencyRateEntity(String incomingBaseCurrency, String incomingCurrency) {
        incomingBaseCurrency = incomingBaseCurrency.toUpperCase();
        incomingCurrency = incomingCurrency.toUpperCase();

        // Получаем текущий курс
        CurrencyRateResponseDto currencyRateResponseDto = currencyRateServiceFeignClient.getRate(incomingBaseCurrency);
        BigDecimal actualRate = currencyRateResponseDto.getConversionRates().get(incomingCurrency);

        // Формируем сущность БД
        CurrencyRateEntity actualCurrencyRateEntity =
                CurrencyRateEntity.builder().
                        baseCurrency(incomingBaseCurrency).
                        currency(incomingCurrency).
                        rate(actualRate).
                        date(LocalDateTime.now()).
                        build();

        // Сохраняем в БД
        return currencyRateRepository.save(actualCurrencyRateEntity);
    }
}
