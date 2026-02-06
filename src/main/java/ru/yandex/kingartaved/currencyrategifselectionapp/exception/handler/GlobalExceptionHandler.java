package ru.yandex.kingartaved.currencyrategifselectionapp.exception.handler;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.kingartaved.currencyrategifselectionapp.exception.response.ErrorResponseDto;
import ru.yandex.kingartaved.currencyrategifselectionapp.exception.GifNotFoundException;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final Logger logger = org.slf4j.LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGenericException(
            Exception e
    ) {
        logger.error("Обработка Exception", e);

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
        logger.error("Обработка EntityNotFoundException", e);

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
            EntityNotFoundException e
    ) {
        logger.error("Обработка GifNotFoundException", e);

        var errorDto = new ErrorResponseDto(
                "Гифка не найдена",
                e.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND) //404
                .body(errorDto);
    }
}
