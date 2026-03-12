package ru.yandex.kingartaved.currencyrategifselectionapp.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.kingartaved.currencyrategifselectionapp.client.ExchangeRateServiceFeignClient;
import ru.yandex.kingartaved.currencyrategifselectionapp.data.model.CurrencyRateEntity;
import ru.yandex.kingartaved.currencyrategifselectionapp.data.repository.CurrencyRateRepository;
import ru.yandex.kingartaved.currencyrategifselectionapp.dto.response.CurrencyRateResponseDto;
import ru.yandex.kingartaved.currencyrategifselectionapp.exception.CurrencyRateNotFoundException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CurrencyRateDeliveryServiceUnitTest {

    @Mock
    private ExchangeRateServiceFeignClient exchangeRateServiceFeignClient;

    @Mock
    private CurrencyRateRepository repository;

    @InjectMocks
    private CurrencyRateDeliveryService service;

    private static final String BASE_CURRENCY = "USD";
    private static final String TARGET_CURRENCY = "RUB";
    private static final BigDecimal EXPECTED_RATE = new BigDecimal("97.50");

    @Test
    @DisplayName("""
            Должен выбросить исключение при получении null от внешнего сервиса.
             Вызов репозитория осуществляться не должен.
            """)
    void getActualCurrencyRateEntity_shouldThrowException_whenResponseIsNull() {

        // given
        when(exchangeRateServiceFeignClient.getRates(BASE_CURRENCY)).thenReturn(null);

        // when
        Executable action = () -> service.getActualCurrencyRateEntity(BASE_CURRENCY, TARGET_CURRENCY);

        // then
        assertThrows(CurrencyRateNotFoundException.class, action);

        verify(repository, never()).save(any(CurrencyRateEntity.class));
    }

    @Test
    @DisplayName("""
            Должен выбросить исключение CurrencyRateNotFoundException при
             отсутствии поля conversionRates в ответе от внешнего сервиса.
             Вызов репозитория осуществляться не должен.
            """)
    void getActualCurrencyRateEntity_shouldThrowException_whenConversionRatesIsNull() {

        // given
        CurrencyRateResponseDto responseDto = new CurrencyRateResponseDto();
        responseDto.setConversionRates(null);

        when(exchangeRateServiceFeignClient.getRates(BASE_CURRENCY)).thenReturn(responseDto);

        // when
        Executable action = () -> service.getActualCurrencyRateEntity(BASE_CURRENCY, TARGET_CURRENCY);

        // then
        assertThrows(CurrencyRateNotFoundException.class, action);

        verify(repository, never()).save(any(CurrencyRateEntity.class));
    }

    @Test
    @DisplayName("""
            Должен выбросить исключение при отсутствии запрашиваемой валюты в мапе conversionRates.
             Вызов репозитория осуществляться не должен.
            """)
    void getActualCurrencyRateEntity_shouldThrowException_whenCurrencyNotInResponse() {

        // given
        CurrencyRateResponseDto responseDto = new CurrencyRateResponseDto();
        Map<String, BigDecimal> conversionRates = Map.of("EUR", EXPECTED_RATE);
        responseDto.setConversionRates(conversionRates);

        when(exchangeRateServiceFeignClient.getRates(BASE_CURRENCY)).thenReturn(responseDto);

        // when
        Executable action = () -> service.getActualCurrencyRateEntity(BASE_CURRENCY, TARGET_CURRENCY);

        // then
        assertThrows(CurrencyRateNotFoundException.class, action);

        verify(repository, never()).save(any(CurrencyRateEntity.class));
    }

    @Test
    @DisplayName("""
            Должен преобразовать значения валют к верхнему регистру при входе аргументов в нижнем регистре.
             Вызов метода feign-клиента getRates() должен осуществляться единожды и
             со значением валюты в верхнем регистре.
            """)
    void getActualCurrencyRateEntity_shouldConvertToUpperCase_whenParamsInLowerCase() {

        // given
        String baseCurrencyInLowerCase = "usd";
        String currencyInLowerCase = "rub";

        Map<String, BigDecimal> conversionRates = Map.of("RUB", EXPECTED_RATE);
        CurrencyRateResponseDto responseDto = new CurrencyRateResponseDto();
        responseDto.setConversionRates(conversionRates);

        when(exchangeRateServiceFeignClient.getRates("USD")).thenReturn(responseDto);
        when(repository.save(any(CurrencyRateEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        CurrencyRateEntity actualEntity = service.getActualCurrencyRateEntity(
                baseCurrencyInLowerCase,
                currencyInLowerCase
        );

        // then
        assertEquals(baseCurrencyInLowerCase.toUpperCase(), actualEntity.getBaseCurrency());
        assertEquals(currencyInLowerCase.toUpperCase(), actualEntity.getCurrency());

        verify(exchangeRateServiceFeignClient).getRates(baseCurrencyInLowerCase.toUpperCase());
    }

    @Test
    @DisplayName("""
            Должен успешно получить и сохранить курс валюты.
             Вызов метода feign-клиента getRates() должен осуществляться единожды и со значением валюты 'USD'.
             Вызов метода репозитория save() должен осуществляться единожды и с сущностью типа CurrencyRateEntity.
            """)
    void getActualCurrencyRateEntity_shouldReturnSavedEntity_whenSuccessful() {

        // given
        CurrencyRateResponseDto responseDto = new CurrencyRateResponseDto(); // для метода getRate
        Map<String, BigDecimal> conversionRates = Map.of(TARGET_CURRENCY, EXPECTED_RATE);
        responseDto.setConversionRates(conversionRates);

        CurrencyRateEntity expectedEntity = CurrencyRateEntity.builder() // для метода findLatestCurrencyRateByBaseCurrencyAndCurrencyAndDate
                .baseCurrency(BASE_CURRENCY)
                .currency(TARGET_CURRENCY)
                .rate(EXPECTED_RATE)
                .date(LocalDateTime.now())
                .build();

        when(exchangeRateServiceFeignClient.getRates("USD")).thenReturn(responseDto);
        when(repository.save(any(CurrencyRateEntity.class))).thenReturn(expectedEntity);

        // when
        CurrencyRateEntity actual = service.getActualCurrencyRateEntity(BASE_CURRENCY, TARGET_CURRENCY);

        // then
        assertNotNull(actual);
        assertEquals("USD", actual.getBaseCurrency());
        assertEquals("RUB", actual.getCurrency());
        assertEquals(EXPECTED_RATE, actual.getRate());

        verify(exchangeRateServiceFeignClient).getRates("USD");
        verify(repository).save(any(CurrencyRateEntity.class));
    }
}
