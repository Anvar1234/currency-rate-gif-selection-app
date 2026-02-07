package ru.yandex.kingartaved.currencyrategifselectionapp.exception.handler;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.kingartaved.currencyrategifselectionapp.exception.CurrencyRateNotFoundException;
import ru.yandex.kingartaved.currencyrategifselectionapp.exception.response.ErrorResponseDto;
import ru.yandex.kingartaved.currencyrategifselectionapp.exception.GifNotFoundException;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGenericException(
            Exception e
    ) {
        log.error("Обработка Exception в GlobalExceptionHandler", e);

        var errorDto = new ErrorResponseDto(
                "Внутренняя ошибка сервера",
                e.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR) //500
                .body(errorDto);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleEntityNotFound(
            EntityNotFoundException e
    ) {
        log.error("Обработка EntityNotFoundException в GlobalExceptionHandler", e);

        var errorDto = new ErrorResponseDto(
                "Entity в БД не найдена",
                e.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND) //404
                .body(errorDto);
    }

    @ExceptionHandler(GifNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleGifNotFound(
            GifNotFoundException e
    ) {
        log.error("Обработка GifNotFoundException в GlobalExceptionHandler", e);

        var errorDto = new ErrorResponseDto(
                "Гифка не найдена",
                e.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND) //404
                .body(errorDto);
    }

    @ExceptionHandler(CurrencyRateNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleCurrencyNotFound(
            CurrencyRateNotFoundException e
    ) {
        log.error("Обработка CurrencyRateNotFoundException в GlobalExceptionHandler", e);

        var errorDto = new ErrorResponseDto(
                "Ошибка получения курса валют",
                e.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND) //404
                .body(errorDto);
    }
}
