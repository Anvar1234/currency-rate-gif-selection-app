package ru.yandex.kingartaved.currencyrategifselectionapp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.yandex.kingartaved.currencyrategifselectionapp.client.ExchangeRateServiceFeignClient;
import ru.yandex.kingartaved.currencyrategifselectionapp.client.GiphyServiceFeignClient;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
class CurrencyRateGifSelectionApplicationTests {

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

    // Мокаем Feign-клиенты, чтобы они не ходили в реальные API
    @MockBean
    private ExchangeRateServiceFeignClient exchangeRateServiceFeignClient;

    @MockBean
    private GiphyServiceFeignClient giphyServiceFeignClient;

    @Test
    void contextLoads() {
        // Просто проверяем, что контекст загружается
    }
}