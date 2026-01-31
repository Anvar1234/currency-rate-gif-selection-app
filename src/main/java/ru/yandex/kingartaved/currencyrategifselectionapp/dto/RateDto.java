package ru.yandex.kingartaved.currencyrategifselectionapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RateDto {

    @JsonProperty("result")
    private String result;

    @JsonProperty("base_code")
    private String baseCode;

    // --- Основные валюты ---
    @JsonProperty("USD")
    private BigDecimal usd;

    @JsonProperty("EUR")
    private BigDecimal eur;

    @JsonProperty("GBP")
    private BigDecimal gbp;

    @JsonProperty("JPY")
    private BigDecimal jpy;

    @JsonProperty("CHF")
    private BigDecimal chf;

    @JsonProperty("CAD")
    private BigDecimal cad;

    @JsonProperty("AUD")
    private BigDecimal aud;

    @JsonProperty("CNY")
    private BigDecimal cny;

    @JsonProperty("RUB")
    private BigDecimal rub;

    @JsonProperty("INR")
    private BigDecimal inr;
}
