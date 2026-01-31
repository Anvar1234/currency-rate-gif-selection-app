package ru.yandex.kingartaved.currencyrategifselectionapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GifDto {
    private String type;
    private String id;
    private String url;

    @JsonProperty("bitly_url")
    private String bitlyUrl;

    @JsonProperty("embed_url")
    private String embedUrl;
}
