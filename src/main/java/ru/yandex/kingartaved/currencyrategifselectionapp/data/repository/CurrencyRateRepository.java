package ru.yandex.kingartaved.currencyrategifselectionapp.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.yandex.kingartaved.currencyrategifselectionapp.data.model.CurrencyRateEntity;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface CurrencyRateRepository extends JpaRepository<CurrencyRateEntity, Long> {

    @Query("""
            SELECT r FROM CurrencyRateEntity r
            WHERE r.baseCurrency = :baseCurrency
            AND r.currency = :currency
            AND r.date <= :date
            ORDER BY r.date DESC
            LIMIT 1
            """)
    Optional<CurrencyRateEntity> findLatestCurrencyRateByBaseCurrencyAndCurrencyAndDate(
            @Param("baseCurrency") String baseCurrency,
            @Param("currency") String currency,
            @Param("date") LocalDateTime date
    );
}
