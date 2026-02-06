package ru.yandex.kingartaved.currencyrategifselectionapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.kingartaved.currencyrategifselectionapp.service.GifService;

import java.io.IOException;

@RestController
@RequestMapping("api/v1/gifs")
@RequiredArgsConstructor
public class GifController {
    private final GifService gifService;

    private final Logger logger = LoggerFactory.getLogger(GifController.class);

    @Operation(
            summary = "Получить GIF по динамике курса валют",
            description = """
                    Возвращает позитивную гифку, если курс выбранной валюты к базовой валюте 
                    выше, чем в предыдущий рабочий день, и негативную - если ниже или данные 
                    за предыдущий день отсутствуют.
                    
                    Пример запроса: `/api/v1/gifs?base_currency=USD&currency=EUR`
                    
                    **Параметры:**
                    - `base_currency` - код базовой валюты (например: USD, EUR)
                    - `currency` - код целевой валюты для сравнения
                    
                    **Примечание:** Метод выполняет 302 редирект на ресурс с GIF-изображением.
                    """
    )
    @ApiResponse(
            responseCode = "302",
            description = "Redirect to GIF")
    @GetMapping()
    public ResponseEntity<Void> getGif(
            @Parameter(hidden = true)
            HttpServletResponse response,
            @Parameter(description = "Базовая валюта", example = "USD")
            @RequestParam(value = "base_currency", defaultValue = "USD") String baseCurrency,
            @Parameter(description = "Целевая валюта", example = "EUR")
            @RequestParam(value = "currency", defaultValue = "RUB") String currency
    ) throws IOException {
        logger.info("Вызван метод: getGif() с параметрами: {} и {}", baseCurrency, currency + " в контроллере.");
        String gifUrl = gifService.getGifsUrl(baseCurrency, currency);

        logger.info("Попытка редиректа в контроллере на gifUrl: {}", gifUrl);
        response.sendRedirect(gifUrl);
        return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT).build();
    }
}
