package io.spring.application.article;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import io.spring.application.ArticleQueryService;
import io.spring.application.data.ArticleData;
import java.util.Optional;
import javax.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DuplicatedArticleValidatorTest {

  @Mock private ArticleQueryService articleQueryService;

  @Mock private ConstraintValidatorContext context;

  @InjectMocks private DuplicatedArticleValidator validator;

  @BeforeEach
  public void setUp() {
    try {
      java.lang.reflect.Field field =
          DuplicatedArticleValidator.class.getDeclaredField("articleQueryService");
      field.setAccessible(true);
      field.set(validator, articleQueryService);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  public void should_be_valid_when_article_does_not_exist() {
    when(articleQueryService.findBySlug(any(String.class), eq(null))).thenReturn(Optional.empty());

    boolean result = validator.isValid("New Article Title", context);

    assertThat(result).isTrue();
  }

  @Test
  public void should_be_invalid_when_article_already_exists() {
    ArticleData existingArticle =
        new ArticleData("id", "slug", "title", "desc", "body", false, 0, null, null, null, null);
    when(articleQueryService.findBySlug(any(String.class), eq(null)))
        .thenReturn(Optional.of(existingArticle));

    boolean result = validator.isValid("Existing Article Title", context);

    assertThat(result).isFalse();
  }

  @Test
  public void should_handle_null_title() {
    try {
      validator.isValid(null, context);
      assertThat(false).as("Expected NullPointerException for null title").isTrue();
    } catch (NullPointerException e) {
      assertThat(e.getMessage()).contains("Cannot invoke \"String.toLowerCase()\"");
    }
  }

  @Test
  public void should_handle_empty_title() {
    when(articleQueryService.findBySlug(any(String.class), eq(null))).thenReturn(Optional.empty());

    boolean result = validator.isValid("", context);

    assertThat(result).isTrue();
  }

  @Test
  public void should_handle_whitespace_title() {
    when(articleQueryService.findBySlug(any(String.class), eq(null))).thenReturn(Optional.empty());

    boolean result = validator.isValid("   ", context);

    assertThat(result).isTrue();
  }

  @Test
  public void should_handle_special_characters_in_title() {
    when(articleQueryService.findBySlug(any(String.class), eq(null))).thenReturn(Optional.empty());

    boolean result = validator.isValid("Article with Special Chars!@#$%^&*()", context);

    assertThat(result).isTrue();
  }

  @Test
  public void should_handle_unicode_characters_in_title() {
    when(articleQueryService.findBySlug(any(String.class), eq(null))).thenReturn(Optional.empty());

    boolean result = validator.isValid("文章标题 with unicode characters", context);

    assertThat(result).isTrue();
  }

  @Test
  public void should_handle_long_title() {
    String longTitle = "Very Long Article Title ".repeat(20);
    when(articleQueryService.findBySlug(any(String.class), eq(null))).thenReturn(Optional.empty());

    boolean result = validator.isValid(longTitle, context);

    assertThat(result).isTrue();
  }

  @Test
  public void should_handle_title_with_numbers() {
    when(articleQueryService.findBySlug(any(String.class), eq(null))).thenReturn(Optional.empty());

    boolean result = validator.isValid("Article Title 123 with Numbers 456", context);

    assertThat(result).isTrue();
  }

  @Test
  public void should_handle_title_with_mixed_case() {
    when(articleQueryService.findBySlug(any(String.class), eq(null))).thenReturn(Optional.empty());

    boolean result = validator.isValid("MiXeD CaSe ArTiClE TiTlE", context);

    assertThat(result).isTrue();
  }

  @Test
  public void should_be_invalid_when_similar_title_exists() {
    ArticleData existingArticle =
        new ArticleData("id", "slug", "title", "desc", "body", false, 0, null, null, null, null);
    when(articleQueryService.findBySlug(any(String.class), eq(null)))
        .thenReturn(Optional.of(existingArticle));

    boolean result = validator.isValid("Similar Article Title", context);

    assertThat(result).isFalse();
  }

  @Test
  public void should_handle_title_with_punctuation() {
    when(articleQueryService.findBySlug(any(String.class), eq(null))).thenReturn(Optional.empty());

    boolean result = validator.isValid("Article Title: A Comprehensive Guide!", context);

    assertThat(result).isTrue();
  }

  @Test
  public void should_handle_title_with_quotes() {
    when(articleQueryService.findBySlug(any(String.class), eq(null))).thenReturn(Optional.empty());

    boolean result = validator.isValid("\"Quoted Article Title\" and 'Single Quotes'", context);

    assertThat(result).isTrue();
  }
}
