package ru.yandex.kingartaved.currencyrategifselectionapp.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.yandex.kingartaved.currencyrategifselectionapp.data.model.CurrencyRateEntity;
import ru.yandex.kingartaved.currencyrategifselectionapp.data.repository.CurrencyRateRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
public class GifServiceTest {

    private String baseCurrency;
    private String currency;
    private BigDecimal expectedRate;

    @MockBean
    private GifSearchService gifSearchService;

    @MockBean
    private CurrencyRateRepository repository;

    @MockBean
    private CurrencyRateDeliveryService currencyRateDeliveryService;

    @Autowired
    private GifService gifService;

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
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

    @ParameterizedTest(name = "{0}")
    @CsvFileSource(
            resources = "/rates/isRateIncreased_higherAndLowerExchangeRates_cases.csv",
            numLinesToSkip = 1)
    @DisplayName("Должен вернуть true, если курс возрос и false, если упал или равен предыдущему")
    void isRateIncreased_shouldReturnTrue_whenRateIncreased(
            String description,
            BigDecimal lastRate,
            boolean expectedValid
    ) {

        // given
        CurrencyRateEntity actualRateEntity = CurrencyRateEntity.builder()
                .baseCurrency(baseCurrency)
                .currency(currency)
                .rate(expectedRate)
                .date(LocalDateTime.now())
                .build();

        CurrencyRateEntity lastRateEntity = CurrencyRateEntity.builder()
                .baseCurrency(baseCurrency)
                .currency(currency)
                .rate(lastRate)
                .date(LocalDateTime.now().minusMinutes(1))
                .build();

        when(currencyRateDeliveryService.getActualCurrencyRateEntity(
                baseCurrency,
                currency
        )).thenReturn(actualRateEntity);

        when(repository.findLatestCurrencyRateByBaseCurrencyAndCurrencyAndDate(
                baseCurrency,
                currency,
                actualRateEntity.getDate()
        )).thenReturn(Optional.of(lastRateEntity));

        // when
        boolean result = gifService.isRateIncreased(baseCurrency, currency);

        // then
        if (expectedValid) {
            Assertions.assertTrue(result);
        } else {
            Assertions.assertFalse(result);
        }
    }
}
