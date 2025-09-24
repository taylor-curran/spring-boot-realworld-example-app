package io.spring.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.spring.infrastructure.mybatis.readservice.TagReadService;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TagsQueryServiceTest {

    @Mock
    private TagReadService tagReadService;

    @InjectMocks
    private TagsQueryService tagsQueryService;

    @Test
    void shouldReturnAllTagsSuccessfully() {
        List<String> expectedTags = Arrays.asList("java", "spring", "boot", "graphql", "testing");
        when(tagReadService.all()).thenReturn(expectedTags);

        List<String> result = tagsQueryService.allTags();

        assertEquals(expectedTags, result);
        assertEquals(5, result.size());
        assertTrue(result.contains("java"));
        assertTrue(result.contains("spring"));
        assertTrue(result.contains("boot"));
        assertTrue(result.contains("graphql"));
        assertTrue(result.contains("testing"));
        verify(tagReadService).all();
    }

    @Test
    void shouldReturnEmptyListWhenNoTags() {
        List<String> emptyTags = Collections.emptyList();
        when(tagReadService.all()).thenReturn(emptyTags);

        List<String> result = tagsQueryService.allTags();

        assertEquals(emptyTags, result);
        assertTrue(result.isEmpty());
        assertEquals(0, result.size());
        verify(tagReadService).all();
    }

    @Test
    void shouldReturnSingleTag() {
        List<String> singleTag = Arrays.asList("java");
        when(tagReadService.all()).thenReturn(singleTag);

        List<String> result = tagsQueryService.allTags();

        assertEquals(singleTag, result);
        assertEquals(1, result.size());
        assertEquals("java", result.get(0));
        verify(tagReadService).all();
    }

    @Test
    void shouldHandleTagsWithSpecialCharacters() {
        List<String> specialTags = Arrays.asList("c++", "c#", ".net", "node.js", "vue.js");
        when(tagReadService.all()).thenReturn(specialTags);

        List<String> result = tagsQueryService.allTags();

        assertEquals(specialTags, result);
        assertEquals(5, result.size());
        assertTrue(result.contains("c++"));
        assertTrue(result.contains("c#"));
        assertTrue(result.contains(".net"));
        assertTrue(result.contains("node.js"));
        assertTrue(result.contains("vue.js"));
    }

    @Test
    void shouldHandleTagsWithSpaces() {
        List<String> spaceTags = Arrays.asList("machine learning", "data science", "web development");
        when(tagReadService.all()).thenReturn(spaceTags);

        List<String> result = tagsQueryService.allTags();

        assertEquals(spaceTags, result);
        assertEquals(3, result.size());
        assertTrue(result.contains("machine learning"));
        assertTrue(result.contains("data science"));
        assertTrue(result.contains("web development"));
    }

    @Test
    void shouldHandleTagsWithUnicodeCharacters() {
        List<String> unicodeTags = Arrays.asList("编程", "プログラミング", "программирование");
        when(tagReadService.all()).thenReturn(unicodeTags);

        List<String> result = tagsQueryService.allTags();

        assertEquals(unicodeTags, result);
        assertEquals(3, result.size());
        assertTrue(result.contains("编程"));
        assertTrue(result.contains("プログラミング"));
        assertTrue(result.contains("программирование"));
    }

    @Test
    void shouldHandleDuplicateTags() {
        List<String> duplicateTags = Arrays.asList("java", "spring", "java", "boot", "spring");
        when(tagReadService.all()).thenReturn(duplicateTags);

        List<String> result = tagsQueryService.allTags();

        assertEquals(duplicateTags, result);
        assertEquals(5, result.size());
        assertEquals(2, Collections.frequency(result, "java"));
        assertEquals(2, Collections.frequency(result, "spring"));
        assertEquals(1, Collections.frequency(result, "boot"));
    }

    @Test
    void shouldHandleTagsWithDifferentCasing() {
        List<String> caseTags = Arrays.asList("Java", "SPRING", "boot", "GraphQL", "Testing");
        when(tagReadService.all()).thenReturn(caseTags);

        List<String> result = tagsQueryService.allTags();

        assertEquals(caseTags, result);
        assertEquals(5, result.size());
        assertTrue(result.contains("Java"));
        assertTrue(result.contains("SPRING"));
        assertTrue(result.contains("boot"));
        assertTrue(result.contains("GraphQL"));
        assertTrue(result.contains("Testing"));
    }

    @Test
    void shouldHandleVeryLongTagList() {
        List<String> longTagList = Arrays.asList(
            "tag1", "tag2", "tag3", "tag4", "tag5", "tag6", "tag7", "tag8", "tag9", "tag10",
            "tag11", "tag12", "tag13", "tag14", "tag15", "tag16", "tag17", "tag18", "tag19", "tag20"
        );
        when(tagReadService.all()).thenReturn(longTagList);

        List<String> result = tagsQueryService.allTags();

        assertEquals(longTagList, result);
        assertEquals(20, result.size());
        assertTrue(result.contains("tag1"));
        assertTrue(result.contains("tag20"));
    }

    @Test
    void shouldHandleMultipleCalls() {
        List<String> tags = Arrays.asList("java", "spring", "boot");
        when(tagReadService.all()).thenReturn(tags);

        List<String> result1 = tagsQueryService.allTags();
        List<String> result2 = tagsQueryService.allTags();

        assertEquals(tags, result1);
        assertEquals(tags, result2);
        assertEquals(result1, result2);
        verify(tagReadService, times(2)).all();
    }

    @Test
    void shouldHandleNullFromReadService() {
        when(tagReadService.all()).thenReturn(null);

        List<String> result = tagsQueryService.allTags();

        assertNull(result);
        verify(tagReadService).all();
    }
}
