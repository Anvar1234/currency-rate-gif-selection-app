package ru.yandex.kingartaved.currencyrategifselectionapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.kingartaved.currencyrategifselectionapp.service.GifService;

import java.io.IOException;

@RestController
@RequestMapping("api/v1/gifs")
@RequiredArgsConstructor
public class GifController {
    private final GifService gifService;

    private Logger logger = LoggerFactory.getLogger(GifController.class);

    @Operation(
            summary = "Получить гифку",
            description = """
                    Возвращает позитивную гифку,
                    если курс валюты выше вчерашнего или вчерашний курс отсутствует,
                    и негативную, если ниже.
                    """
    )
//    @GetMapping("/positive")
//    public ResponseEntity<String> getGif() {
//        logger.info("Вызван метод: getContrPositiveGif(): limit = " + limit + ", lang = " + lang);
//        return ResponseEntity.status(HttpStatus.OK) //200
//                .contentType(MediaType.TEXT_HTML)
//                .body(gifService.getHtml(limit, lang));
//    }

//    @GetMapping("/positive1")
//    public ResponseEntity<Void> getContrPositiveGif1(
//            HttpServletResponse response,
//            @RequestParam("limit") int limit,
//            @RequestParam("lang") String lang
//    ) throws IOException {
//        logger.info("Вызван метод: getContrPositiveGif(): limit = " + limit + ", lang = " + lang);
//        response.sendRedirect(gifService.getHtml(limit, lang)); // Редирект на наш html
//        return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT)
//                .build();
//    }

    @GetMapping()
    public ResponseEntity<Void> getGif(HttpServletResponse response) throws IOException {
        logger.info("Вызван метод: getGif() в контроллере");
        String gifUrl = gifService.getGifUrl();

        logger.info("Попытка редиректа на gifUrl в контроллере: {}", gifUrl);
        response.sendRedirect(gifUrl); // Редирект на giphy.com
        return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT).build();
    }
}
