package io.spring.graphql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

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
public class TagDatafetcherTest {

  @Mock private TagsQueryService tagsQueryService;

  @InjectMocks private TagDatafetcher tagDatafetcher;

  @Test
  public void should_get_all_tags() {
    List<String> expectedTags = Arrays.asList("java", "spring", "graphql", "testing");
    when(tagsQueryService.allTags()).thenReturn(expectedTags);

    List<String> result = tagDatafetcher.getTags();

    assertThat(result).isNotNull();
    assertThat(result).hasSize(4);
    assertThat(result).containsExactly("java", "spring", "graphql", "testing");
  }

  @Test
  public void should_handle_empty_tags_list() {
    when(tagsQueryService.allTags()).thenReturn(Collections.emptyList());

    List<String> result = tagDatafetcher.getTags();

    assertThat(result).isNotNull();
    assertThat(result).isEmpty();
  }

  @Test
  public void should_handle_single_tag() {
    List<String> singleTag = Arrays.asList("spring-boot");
    when(tagsQueryService.allTags()).thenReturn(singleTag);

    List<String> result = tagDatafetcher.getTags();

    assertThat(result).hasSize(1);
    assertThat(result).containsExactly("spring-boot");
  }

  @Test
  public void should_handle_tags_with_special_characters() {
    List<String> specialTags =
        Arrays.asList("spring-boot", "java_8", "test-driven-development", "micro-services");
    when(tagsQueryService.allTags()).thenReturn(specialTags);

    List<String> result = tagDatafetcher.getTags();

    assertThat(result).hasSize(4);
    assertThat(result)
        .containsExactly("spring-boot", "java_8", "test-driven-development", "micro-services");
  }

  @Test
  public void should_handle_tags_with_unicode_characters() {
    List<String> unicodeTags = Arrays.asList("java", "中文", "español", "français");
    when(tagsQueryService.allTags()).thenReturn(unicodeTags);

    List<String> result = tagDatafetcher.getTags();

    assertThat(result).hasSize(4);
    assertThat(result).containsExactly("java", "中文", "español", "français");
  }

  @Test
  public void should_handle_large_number_of_tags() {
    List<String> manyTags =
        Arrays.asList(
            "tag1", "tag2", "tag3", "tag4", "tag5", "tag6", "tag7", "tag8", "tag9", "tag10",
            "tag11", "tag12", "tag13", "tag14", "tag15", "tag16", "tag17", "tag18", "tag19",
            "tag20");
    when(tagsQueryService.allTags()).thenReturn(manyTags);

    List<String> result = tagDatafetcher.getTags();

    assertThat(result).hasSize(20);
    assertThat(result).containsAll(manyTags);
  }

  @Test
  public void should_preserve_tag_order() {
    List<String> orderedTags = Arrays.asList("z-tag", "a-tag", "m-tag", "b-tag");
    when(tagsQueryService.allTags()).thenReturn(orderedTags);

    List<String> result = tagDatafetcher.getTags();

    assertThat(result).containsExactly("z-tag", "a-tag", "m-tag", "b-tag");
  }

  @Test
  public void should_handle_tags_with_whitespace() {
    List<String> whitespaceTags =
        Arrays.asList("  leading-space", "trailing-space  ", "  both-spaces  ", "no-space");
    when(tagsQueryService.allTags()).thenReturn(whitespaceTags);

    List<String> result = tagDatafetcher.getTags();

    assertThat(result).hasSize(4);
    assertThat(result)
        .containsExactly("  leading-space", "trailing-space  ", "  both-spaces  ", "no-space");
  }
}
