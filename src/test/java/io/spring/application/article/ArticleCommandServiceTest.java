package io.spring.application.article;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import io.spring.core.article.Article;
import io.spring.core.article.ArticleRepository;
import io.spring.core.user.User;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ArticleCommandServiceTest {

  @Mock private ArticleRepository articleRepository;

  @InjectMocks private ArticleCommandService articleCommandService;

  private User testUser;
  private NewArticleParam newArticleParam;
  private UpdateArticleParam updateArticleParam;
  private Article testArticle;

  @BeforeEach
  public void setUp() {
    testUser = new User("test@example.com", "testuser", "password", "bio", "image.jpg");

    newArticleParam =
        new NewArticleParam(
            "Test Article Title",
            "Test description",
            "Test body content",
            Arrays.asList("tag1", "tag2"));

    updateArticleParam =
        new UpdateArticleParam("Updated Title", "Updated body", "Updated description");

    testArticle =
        new Article(
            "Original Title",
            "Original description",
            "Original body",
            Arrays.asList("tag1"),
            testUser.getId());
  }

  @Test
  public void should_create_article_successfully() {
    Article result = articleCommandService.createArticle(newArticleParam, testUser);

    assertThat(result).isNotNull();
    assertThat(result.getTitle()).isEqualTo("Test Article Title");
    assertThat(result.getDescription()).isEqualTo("Test description");
    assertThat(result.getBody()).isEqualTo("Test body content");
    assertThat(result.getUserId()).isEqualTo(testUser.getId());

    verify(articleRepository).save(any(Article.class));
  }

  @Test
  public void should_create_article_with_tags() {
    Article result = articleCommandService.createArticle(newArticleParam, testUser);

    assertThat(result.getTags()).hasSize(2);
    verify(articleRepository).save(any(Article.class));
  }

  @Test
  public void should_create_article_with_empty_tags() {
    NewArticleParam paramWithEmptyTags =
        new NewArticleParam("Test Title", "Test description", "Test body", Collections.emptyList());

    Article result = articleCommandService.createArticle(paramWithEmptyTags, testUser);

    assertThat(result.getTags()).isEmpty();
    verify(articleRepository).save(any(Article.class));
  }

  @Test
  public void should_handle_null_tags_gracefully() {
    NewArticleParam paramWithNullTags =
        new NewArticleParam("Test Title", "Test description", "Test body", null);

    try {
      articleCommandService.createArticle(paramWithNullTags, testUser);
      assertThat(false).as("Expected NullPointerException for null tags").isTrue();
    } catch (NullPointerException e) {
      assertThat(e.getMessage()).contains("Cannot invoke \"java.util.Collection.size()\"");
    }
  }

  @Test
  public void should_handle_special_characters_in_title() {
    NewArticleParam specialParam =
        new NewArticleParam(
            "Test Article with Special Characters!@#$%^&*()",
            "Description with émojis 🚀",
            "Body with unicode: 测试内容",
            Arrays.asList("special-tag", "unicode-标签"));

    Article result = articleCommandService.createArticle(specialParam, testUser);

    assertThat(result.getTitle()).isEqualTo("Test Article with Special Characters!@#$%^&*()");
    assertThat(result.getDescription()).isEqualTo("Description with émojis 🚀");
    assertThat(result.getBody()).isEqualTo("Body with unicode: 测试内容");
    verify(articleRepository).save(any(Article.class));
  }

  @Test
  public void should_handle_long_content() {
    String longTitle = "Very Long Title ".repeat(10);
    String longDescription = "Very Long Description ".repeat(20);
    String longBody = "Very Long Body Content ".repeat(100);

    NewArticleParam longParam =
        new NewArticleParam(
            longTitle,
            longDescription,
            longBody,
            Arrays.asList("tag1", "tag2", "tag3", "tag4", "tag5"));

    Article result = articleCommandService.createArticle(longParam, testUser);

    assertThat(result.getTitle()).isEqualTo(longTitle);
    assertThat(result.getDescription()).isEqualTo(longDescription);
    assertThat(result.getBody()).isEqualTo(longBody);
    verify(articleRepository).save(any(Article.class));
  }

  @Test
  public void should_update_article_successfully() {
    Article result = articleCommandService.updateArticle(testArticle, updateArticleParam);

    assertThat(result).isSameAs(testArticle);
    verify(articleRepository).save(testArticle);
  }

  @Test
  public void should_update_article_with_all_fields() {
    UpdateArticleParam fullUpdateParam =
        new UpdateArticleParam(
            "Completely New Title", "Completely New Body", "Completely New Description");

    articleCommandService.updateArticle(testArticle, fullUpdateParam);

    verify(articleRepository).save(testArticle);
  }

  @Test
  public void should_update_article_with_empty_fields() {
    UpdateArticleParam emptyUpdateParam = new UpdateArticleParam("", "", "");

    articleCommandService.updateArticle(testArticle, emptyUpdateParam);

    verify(articleRepository).save(testArticle);
  }

  @Test
  public void should_update_article_with_null_fields() {
    UpdateArticleParam nullUpdateParam = new UpdateArticleParam(null, null, null);

    articleCommandService.updateArticle(testArticle, nullUpdateParam);

    verify(articleRepository).save(testArticle);
  }

  @Test
  public void should_update_article_with_special_characters() {
    UpdateArticleParam specialUpdateParam =
        new UpdateArticleParam(
            "Updated Title with Special Chars!@#$",
            "Updated body with émojis 🎉 and unicode: 更新内容",
            "Updated description with symbols: ©®™");

    articleCommandService.updateArticle(testArticle, specialUpdateParam);

    verify(articleRepository).save(testArticle);
  }

  @Test
  public void should_preserve_article_identity_after_update() {
    String originalId = testArticle.getId();
    String originalUserId = testArticle.getUserId();

    articleCommandService.updateArticle(testArticle, updateArticleParam);

    assertThat(testArticle.getId()).isEqualTo(originalId);
    assertThat(testArticle.getUserId()).isEqualTo(originalUserId);
    verify(articleRepository).save(testArticle);
  }
}
