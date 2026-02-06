package ru.yandex.kingartaved.currencyrategifselectionapp.exception.response;

import java.time.LocalDateTime;

public record ErrorResponseDto(
        String message,
        String detailedMessage,
        LocalDateTime errorTime
) {
}