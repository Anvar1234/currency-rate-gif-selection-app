package ru.yandex.kingartaved.currencyrategifselectionapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class CurrencyRateGifSelectionApplication {

    public static void main(String[] args) {
        SpringApplication.run(CurrencyRateGifSelectionApplication.class, args);
    }

}
