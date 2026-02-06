package ru.yandex.kingartaved.currencyrategifselectionapp.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyRateResponseDto {

    @JsonProperty("base_code")
    private String baseCurrency;

    @JsonProperty("conversion_rates")
    private Map<String, BigDecimal> conversionRates;
}
