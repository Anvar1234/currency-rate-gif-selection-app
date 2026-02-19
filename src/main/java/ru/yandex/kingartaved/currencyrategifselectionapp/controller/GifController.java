package ru.yandex.kingartaved.currencyrategifselectionapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.kingartaved.currencyrategifselectionapp.service.GifService;

import java.net.URI;

/**
 * Основной контроллер приложения.
 */
@RestController
@RequestMapping("api/v1/gifs")
@RequiredArgsConstructor
public class GifController {

    private final GifService gifService;

    @Operation(
            summary = "Получить GIF по динамике курса валют",
            description = """
                    Возвращает позитивную гифку, если курс валюты к базовой валюте
                    выше, чем предыдущий ближайший из БД, негативную - если ниже или равен,
                     и исключение, если данные за предыдущее время отсутствуют.
                    
                    Пример запроса: `/api/v1/gifs'
                    
                    **Примечание:** Метод выполняет 302 редирект на ресурс с GIF-изображением.
                    """
    )
    @ApiResponse(
            responseCode = "302",
            description = "Redirect to GIF")
    @GetMapping()
    public ResponseEntity<Void> getGif() {

        String gifUrl = gifService.getGifsUrl();

        return ResponseEntity
                .status(HttpStatus.TEMPORARY_REDIRECT)   // Код 302 — временный редирект
                .location(URI.create(gifUrl))            // куда редиректить
                .build();                                // Собираем финальный ответ
    }
}
