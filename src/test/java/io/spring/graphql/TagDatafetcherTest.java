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
class TagDatafetcherTest {

    @Mock
    private TagsQueryService tagsQueryService;

    @InjectMocks
    private TagDatafetcher tagDatafetcher;

    @Test
    void shouldReturnAllTags() {
        List<String> expectedTags = Arrays.asList("java", "spring", "graphql", "testing");
        when(tagsQueryService.allTags()).thenReturn(expectedTags);

        List<String> result = tagDatafetcher.getTags();

        assertEquals(expectedTags, result);
        assertEquals(4, result.size());
        assertTrue(result.contains("java"));
        assertTrue(result.contains("spring"));
        assertTrue(result.contains("graphql"));
        assertTrue(result.contains("testing"));
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
    void shouldHandleNullFromService() {
        when(tagsQueryService.allTags()).thenReturn(null);

        List<String> result = tagDatafetcher.getTags();

        assertNull(result);
        verify(tagsQueryService).allTags();
    }

    @Test
    void shouldReturnSingleTag() {
        List<String> singleTag = Arrays.asList("kotlin");
        when(tagsQueryService.allTags()).thenReturn(singleTag);

        List<String> result = tagDatafetcher.getTags();

        assertEquals(singleTag, result);
        assertEquals(1, result.size());
        assertEquals("kotlin", result.get(0));
        verify(tagsQueryService).allTags();
    }

    @Test
    void shouldHandleDuplicateTags() {
        List<String> tagsWithDuplicates = Arrays.asList("java", "spring", "java", "testing");
        when(tagsQueryService.allTags()).thenReturn(tagsWithDuplicates);

        List<String> result = tagDatafetcher.getTags();

        assertEquals(tagsWithDuplicates, result);
        assertEquals(4, result.size());
        verify(tagsQueryService).allTags();
    }
}
