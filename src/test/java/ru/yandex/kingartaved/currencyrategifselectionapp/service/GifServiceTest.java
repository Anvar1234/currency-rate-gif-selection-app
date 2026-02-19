package ru.yandex.kingartaved.currencyrategifselectionapp.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import ru.yandex.kingartaved.currencyrategifselectionapp.dto.GifDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
public class GifServiceTest {

    @Value("${test-external-exchangerate.base-currency}")
    private String baseCurrency;

    @Value("${test-external-exchangerate.target-currency}")
    private String currency;

    @Value("${test-external-giphy.search-word.positive-rate}")
    private String positiveRateSearchWord;

    @Value("${test-external-giphy.search-word.negative-rate}")
    private String negativeRateSearchWord;

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

    @ParameterizedTest(name = "{0}")
    @CsvFileSource(
            resources = "/rates/getGifsUrl_higherAndLowerExchangeRates_cases.csv",
            numLinesToSkip = 1
    )
    @DisplayName("""
            Должен вернуть гифку из позитивного списка, если курс возрос и
            гифку из негативного списка, если курс уменьшился или равен предыдущему.
            """)
    void getGifsUrl_shouldPassAllCases(
            String description,
            BigDecimal actualRate,
            BigDecimal lastRate,
            boolean expectedRateIncreased
    ) {

        // given
        // ожидаемые сущности актуального и предыдущего курсов валют для приватного метода isRateIncreased()
        CurrencyRateEntity actualRateEntity = CurrencyRateEntity.builder()
                .baseCurrency(baseCurrency)
                .currency(currency)
                .rate(actualRate)
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

        // ожидаемые гифки по словам для приватного метода getGifsForWord()
        GifDto gifDto = GifDto.builder()
                .url("positive_url1")
                .build();

        GifDto gifDto2 = GifDto.builder()
                .url("positive_url2")
                .build();

        List<GifDto> positiveGifDtos = List.of(gifDto, gifDto2);

        GifDto gifDto3 = GifDto.builder()
                .url("negative_url1")
                .build();

        GifDto gifDto4 = GifDto.builder()
                .url("negative_url2")
                .build();

        List<GifDto> negativeGifDtos = List.of(gifDto3, gifDto4);

        when(gifSearchService.getGifsForWord(positiveRateSearchWord))
                .thenReturn(positiveGifDtos);
        when(gifSearchService.getGifsForWord(negativeRateSearchWord))
                .thenReturn(negativeGifDtos);

        // when
        String result = gifService.getGifsUrl();

        // then
        if (expectedRateIncreased) {
            assertTrue(result.contains("positive_url"));

            verify(gifSearchService).getGifsForWord(positiveRateSearchWord);
        } else {
            assertTrue(result.contains("negative_url"));

            verify(gifSearchService).getGifsForWord(negativeRateSearchWord);
        }

        verify(currencyRateDeliveryService).getActualCurrencyRateEntity(
                baseCurrency,
                currency
        );
        verify(repository).findLatestCurrencyRateByBaseCurrencyAndCurrencyAndDate(
                baseCurrency,
                currency,
                actualRateEntity.getDate()
        );

    }
}
