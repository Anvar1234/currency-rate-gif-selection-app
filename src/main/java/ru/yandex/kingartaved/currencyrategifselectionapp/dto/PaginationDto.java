package ru.yandex.kingartaved.currencyrategifselectionapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PaginationDto {

    @JsonProperty("total_count")
    private Integer totalCount;

    @JsonProperty("count")
    private Integer count;

    @JsonProperty("offset")
    private Integer offset;
}
