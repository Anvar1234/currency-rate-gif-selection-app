package ru.yandex.kingartaved.currencyrategifselectionapp.service;

import org.junit.jupiter.api.Test;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
public class CurrencyRateDeliveryServiceIntegrationTest {

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

    @Test
    void getActualCurrencyRateEntity_success() {
        // given
        String baseCurrency = "USD";
        String currency = "RUB";
        BigDecimal expectedRate = new BigDecimal("97.50");

        CurrencyRateResponseDto responseDto = new CurrencyRateResponseDto(); // для метода getRate
        Map<String, BigDecimal> conversionRates = Map.of(currency, expectedRate);
        responseDto.setConversionRates(conversionRates);

        CurrencyRateEntity expectedEntity = CurrencyRateEntity.builder() // для метода findLatestCurrencyRateByBaseCurrencyAndCurrencyAndDate
                .baseCurrency(baseCurrency)
                .currency(currency)
                .rate(expectedRate)
                .date(LocalDateTime.now())
                .build();

        when(exchangeRateServiceFeignClient.getRate("USD")).thenReturn(responseDto);
        when(repository.save(any(CurrencyRateEntity.class))).thenReturn(expectedEntity);

        // when
        CurrencyRateEntity actual = service.getActualCurrencyRateEntity(baseCurrency, currency);

        // then
        assertNotNull(actual);
        assertEquals("USD", actual.getBaseCurrency());
        assertEquals("RUB", actual.getCurrency());
        assertEquals(expectedRate, actual.getRate());

        verify(exchangeRateServiceFeignClient).getRate("USD");
        verify(repository).save(any(CurrencyRateEntity.class));
    }


}
