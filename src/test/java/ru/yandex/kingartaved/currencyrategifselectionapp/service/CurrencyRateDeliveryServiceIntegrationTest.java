package ru.yandex.kingartaved.currencyrategifselectionapp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
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
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Интеграционный тест для CurrencyRateDeliveryService.
 * Тестирует взаимодействие с реальной БД (через Testcontainers) и моками внешнего API.
 */
@DataJpaTest
@Testcontainers
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(CurrencyRateDeliveryService.class)
public class CurrencyRateDeliveryServiceIntegrationTest {

    @Value("${test-external-exchangerate.base-currency}")
    private String baseCurrency;

    @Value("${test-external-exchangerate.target-currency}")
    private String currency;

    @Value("${test-external-exchangerate.expected-rate}")
    private BigDecimal expectedRate;

    @MockBean
    private ExchangeRateServiceFeignClient exchangeRateServiceFeignClient;

    @Autowired
    private CurrencyRateDeliveryService service;

    @Autowired
    private CurrencyRateRepository repository;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    @DisplayName("Должен успешно получить курс валюты и сохранить его в БД")
    void getActualCurrencyRateEntity_shouldReturnExpectedRateEntityAndSaveToDatabase() {

        // given
        CurrencyRateResponseDto responseDto = new CurrencyRateResponseDto();
        responseDto.setConversionRates(Map.of(currency, expectedRate));

        when(exchangeRateServiceFeignClient.getRates(baseCurrency)).thenReturn(responseDto);

        // when
        CurrencyRateEntity actualCurrencyRateEntity = service.getActualCurrencyRateEntity(baseCurrency, currency); //сохраняет в БД

        // then
        verify(exchangeRateServiceFeignClient, times(1)).getRates(baseCurrency);

        // Проверяем, что сущность сохранена в БД
        List<CurrencyRateEntity> allSavedEntities = repository.findAll();
        assertFalse(allSavedEntities.isEmpty(), "В БД не найдено сохранённых сущностей");
        assertEquals(1, allSavedEntities.size());

        CurrencyRateEntity savedEntity = allSavedEntities.get(0);

        // Проверяем поля сущности
        assertEquals(baseCurrency, savedEntity.getBaseCurrency());
        assertEquals(currency, savedEntity.getCurrency());
        assertEquals(expectedRate, savedEntity.getRate());
        assertNotNull(savedEntity.getDate());

        // Проверяем, что сервис вернул ту же сущность
        assertEquals(savedEntity, actualCurrencyRateEntity);
    }

    @Test
    @DisplayName("Должен выбросить исключение и не сохранять данные в БД при ошибке внешнего сервиса")
    void getActualCurrencyRateEntity_shouldNotSaveToDatabase_whenExternalServiceFails() {
        // given
        when(exchangeRateServiceFeignClient.getRates(baseCurrency)).thenReturn(null);

        // when
        Executable action = () -> service.getActualCurrencyRateEntity(baseCurrency, currency);

        // then
        assertThrows(CurrencyRateNotFoundException.class, action);

        // Проверяем, что в БД ничего не сохранилось
        assertTrue(repository.findAll().isEmpty());
    }
}
