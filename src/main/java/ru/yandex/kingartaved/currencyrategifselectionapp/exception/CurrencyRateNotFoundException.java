package ru.yandex.kingartaved.currencyrategifselectionapp.exception;

public class CurrencyRateNotFoundException extends RuntimeException {

    public CurrencyRateNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
