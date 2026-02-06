package ru.yandex.kingartaved.currencyrategifselectionapp.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ru.yandex.kingartaved.currencyrategifselectionapp.dto.GifDto;
import ru.yandex.kingartaved.currencyrategifselectionapp.dto.PaginationDto;

import java.util.List;

@Data
public class GifSearchResponseDto {
    @JsonProperty("data")
    private List<GifDto> data;

    @JsonProperty("pagination")
    private PaginationDto pagination;
}

