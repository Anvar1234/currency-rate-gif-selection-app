package ru.yandex.kingartaved.currencyrategifselectionapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.yandex.kingartaved.currencyrategifselectionapp.client.ExchangeRateServiceFeignClient;

@SpringBootTest
public class CurrencyRateDeliveryServiceTest {

    @MockBean
    private ExchangeRateServiceFeignClient exchangeRateServiceFeignClient;

    @Autowired
    private CurrencyRateDeliveryService currencyRateDeliveryService;



}
