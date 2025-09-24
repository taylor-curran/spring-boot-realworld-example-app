package io.spring.graphql;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.spring.application.TagsQueryService;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TagDatafetcherSimpleTest {

    @Mock
    private TagsQueryService tagsQueryService;

    @InjectMocks
    private TagDatafetcher tagDatafetcher;

    @Test
    void shouldReturnAllTags() {
        List<String> expectedTags = Arrays.asList("java", "spring", "graphql", "testing");
        when(tagsQueryService.allTags()).thenReturn(expectedTags);

        List<String> result = tagDatafetcher.getTags();

        assertNotNull(result);
        assertEquals(expectedTags, result);
        assertEquals(4, result.size());
        assertTrue(result.contains("java"));
        assertTrue(result.contains("spring"));
        verify(tagsQueryService).allTags();
    }

    @Test
    void shouldReturnEmptyListWhenNoTags() {
        when(tagsQueryService.allTags()).thenReturn(Collections.emptyList());

        List<String> result = tagDatafetcher.getTags();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(tagsQueryService).allTags();
    }

    @Test
    void shouldHandleSingleTag() {
        List<String> singleTag = Arrays.asList("java");
        when(tagsQueryService.allTags()).thenReturn(singleTag);

        List<String> result = tagDatafetcher.getTags();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("java", result.get(0));
        verify(tagsQueryService).allTags();
    }
}
