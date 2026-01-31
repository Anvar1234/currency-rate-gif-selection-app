package ru.yandex.kingartaved.currencyrategifselectionapp.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ru.yandex.kingartaved.currencyrategifselectionapp.dto.GifDto;

@Data
public class GifSearchResponseDto {
    @JsonProperty("data")
    private GifDto gifDto;
//    @JsonProperty("pagination")
//    private PaginationDto paginationDto;
//    @JsonProperty("meta")
//    private MetaDto metaDto;
}

