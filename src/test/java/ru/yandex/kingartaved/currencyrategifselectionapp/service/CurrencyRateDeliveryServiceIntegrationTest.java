package ru.yandex.kingartaved.currencyrategifselectionapp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
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

@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
public class CurrencyRateDeliveryServiceIntegrationTest {

    private String baseCurrency;
    private String currency;
    private BigDecimal expectedRate;

    @MockBean
    private ExchangeRateServiceFeignClient exchangeRateServiceFeignClient;

    @MockBean
    private CurrencyRateRepository repository;

    @Autowired
    private CurrencyRateDeliveryService service;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeEach
    void setUp() {
        this.baseCurrency = "USD";
        this.currency = "RUB";
        this.expectedRate = new BigDecimal("97.50");
    }

    @Test
    @DisplayName("Тестирование getActualCurrencyRateEntity с входящими в нижнем регистре валютами")
    void getActualCurrencyRateEntity_currenciesAreWrittenInUppercase_whenCalledInLowerCase() {

        // given
        String baseCurrencyInLowerCase = "usd";
        String currencyInLowerCase = "rub";

        Map<String, BigDecimal> conversionRates = Map.of("RUB", expectedRate);
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
    @DisplayName("Тестирование fetchCurrencyRate при получении null в ответе от внешнего сервиса")
    void fetchCurrencyRate_throwsException_whenResponseIsNull() {

        // given
        when(exchangeRateServiceFeignClient.getRates(baseCurrency)).thenReturn(null);

        // when
        Executable action = () -> service.fetchCurrencyRate(baseCurrency, currency);

        // then
        assertThrows(CurrencyRateNotFoundException.class, action);

        verify(repository, never()).save(any(CurrencyRateEntity.class));
    }

    @Test
    @DisplayName("Тестирование fetchCurrencyRate при получении null в поле conversionRates")
    void fetchCurrencyRate_throwsException_whenConversionRatesIsNull() {

        // given
        CurrencyRateResponseDto responseDto = new CurrencyRateResponseDto();
        responseDto.setConversionRates(null);

        when(exchangeRateServiceFeignClient.getRates(baseCurrency)).thenReturn(responseDto);

        // when
        Executable action = () -> service.fetchCurrencyRate(baseCurrency, currency);

        // then
        assertThrows(CurrencyRateNotFoundException.class, action);

        verify(repository, never()).save(any(CurrencyRateEntity.class));
    }

    @Test
    @DisplayName("Тестирование fetchCurrencyRate при отсутствии ключа (валюты) в мапе conversionRates")
    void fetchCurrencyRate_throwsException_whenCurrencyNotInResponse() {

        // given
        CurrencyRateResponseDto responseDto = new CurrencyRateResponseDto();
        Map<String, BigDecimal> conversionRates = Map.of("EUR", expectedRate);
        responseDto.setConversionRates(conversionRates);

        when(exchangeRateServiceFeignClient.getRates(baseCurrency)).thenReturn(responseDto);

        // when
        Executable action = () -> service.fetchCurrencyRate(baseCurrency, currency);

        // then
        assertThrows(CurrencyRateNotFoundException.class, action);

        verify(repository, never()).save(any(CurrencyRateEntity.class));
    }

    @Test
    @DisplayName("Тестирование fetchCurrencyRate при успешном получении курса валюты")
    void getActualCurrencyRateEntity_success() {

        // given
        CurrencyRateResponseDto responseDto = new CurrencyRateResponseDto(); // для метода getRate
        Map<String, BigDecimal> conversionRates = Map.of(currency, expectedRate);
        responseDto.setConversionRates(conversionRates);

        CurrencyRateEntity expectedEntity = CurrencyRateEntity.builder() // для метода findLatestCurrencyRateByBaseCurrencyAndCurrencyAndDate
                .baseCurrency(baseCurrency)
                .currency(currency)
                .rate(expectedRate)
                .date(LocalDateTime.now())
                .build();

        when(exchangeRateServiceFeignClient.getRates("USD")).thenReturn(responseDto);
        when(repository.save(any(CurrencyRateEntity.class))).thenReturn(expectedEntity);

        // when
        CurrencyRateEntity actual = service.getActualCurrencyRateEntity(baseCurrency, currency);

        // then
        assertNotNull(actual);
        assertEquals("USD", actual.getBaseCurrency());
        assertEquals("RUB", actual.getCurrency());
        assertEquals(expectedRate, actual.getRate());

        verify(exchangeRateServiceFeignClient).getRates("USD");
        verify(repository).save(any(CurrencyRateEntity.class));
    }


}
