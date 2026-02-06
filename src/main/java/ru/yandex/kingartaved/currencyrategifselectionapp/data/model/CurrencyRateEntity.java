package ru.yandex.kingartaved.currencyrategifselectionapp.data.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "currency_rate")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CurrencyRateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "base_currency", nullable = false)
    private String baseCurrency; //базовая валюта

    @Column(name = "currency", nullable = false)
    private String currency; //валюта сравнения к базовой валюте

    @Column(name = "rate", nullable = false, precision = 10, scale = 4)
    private BigDecimal rate; //курс к базовой валюте

    @Column(name = "date", nullable = false)
    private LocalDateTime date;
}
