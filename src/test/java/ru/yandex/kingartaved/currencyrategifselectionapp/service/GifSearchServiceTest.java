package ru.yandex.kingartaved.currencyrategifselectionapp.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.kingartaved.currencyrategifselectionapp.client.GiphyServiceFeignClient;
import ru.yandex.kingartaved.currencyrategifselectionapp.dto.GifDto;
import ru.yandex.kingartaved.currencyrategifselectionapp.dto.response.GifSearchResponseDto;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest // Тяжелее, но нужно для проверки кэширования
@ActiveProfiles("test")
public class GifSearchServiceTest {

    @MockBean
    private GiphyServiceFeignClient giphyServiceFeignClient;

    @Autowired
    private GifSearchService gifSearchService;

    /**
     * Должен вызываться один раз для одного слова
     */
    @Test
    void getGifsForWord_callOnceForWord_whenCalledTwice() {

        // given
        String searchWord = "test";
        GifSearchResponseDto mockResponse = new GifSearchResponseDto();
        mockResponse.setData(List.of(GifDto.builder().build())); // непустой ответ

        when(giphyServiceFeignClient.getGifData(searchWord)).thenReturn(mockResponse);

        // when
        for (int i = 0; i < 2; i++) {
            gifSearchService.getGifsForWord(searchWord);
        }

        // then
        verify(giphyServiceFeignClient, times(1)).getGifData(searchWord);
    }

    /**
     * Должен возвращать список GifDto при успешном ответе от GiphyService
     */
    @Test
    void getGifsForWord_returnGifList_whenGiphyServiceReturnsNonEmptyResponse() {

        //given
        GifDto gif1 = GifDto.builder().build();
        GifDto gif2 = GifDto.builder().build();

        GifSearchResponseDto gifSearchResponseDto = new GifSearchResponseDto();
        gifSearchResponseDto.setData(List.of(gif1, gif2));

        when(giphyServiceFeignClient.getGifData(anyString())).thenReturn(gifSearchResponseDto);

        //when
        List<GifDto> result = gifSearchService.getGifsForWord("testSearchWord");

        //then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(gif1, gif2);
        verify(giphyServiceFeignClient, times(1)).getGifData("testSearchWord");
    }
}
