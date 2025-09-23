package io.spring.application.article;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.spring.core.article.Article;
import io.spring.core.article.ArticleRepository;
import io.spring.core.user.User;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@ExtendWith(MockitoExtension.class)
public class ArticleCommandServiceTest {

  @Mock private ArticleRepository articleRepository;

  private ArticleCommandService articleCommandService;
  private User testUser;
  private Validator validator;

  @BeforeEach
  public void setUp() {
    articleCommandService = new ArticleCommandService(articleRepository);
    testUser = new User("test@example.com", "testuser", "password123", "Test bio", "test.jpg");
    
    LocalValidatorFactoryBean validatorFactory = new LocalValidatorFactoryBean();
    validatorFactory.afterPropertiesSet();
    validator = validatorFactory.getValidator();
  }

  @Test
  public void should_create_article_successfully() {
    List<String> tagList = Arrays.asList("tag1", "tag2");
    NewArticleParam newArticleParam = new NewArticleParam("Test Title", "Test Description", "Test Body", tagList);

    Article result = articleCommandService.createArticle(newArticleParam, testUser);

    assertThat(result, notNullValue());
    assertThat(result.getTitle(), is("Test Title"));
    assertThat(result.getDescription(), is("Test Description"));
    assertThat(result.getBody(), is("Test Body"));
    assertThat(result.getTags().size(), is(tagList.size()));
    assertThat(result.getUserId(), is(testUser.getId()));
    verify(articleRepository).save(any(Article.class));
  }

  @Test
  public void should_create_article_with_empty_tag_list() {
    NewArticleParam newArticleParam = new NewArticleParam("Title", "Description", "Body", Collections.emptyList());

    Article result = articleCommandService.createArticle(newArticleParam, testUser);

    assertThat(result, notNullValue());
    assertThat(result.getTags().isEmpty(), is(true));
    verify(articleRepository).save(any(Article.class));
  }

  @Test
  public void should_create_article_with_null_tag_list() {
    NewArticleParam newArticleParam = new NewArticleParam("Title", "Description", "Body", Collections.emptyList());

    Article result = articleCommandService.createArticle(newArticleParam, testUser);

    assertThat(result, notNullValue());
    assertThat(result.getTags().isEmpty(), is(true));
    verify(articleRepository).save(any(Article.class));
  }

  @Test
  public void should_create_article_with_long_content() {
    String longTitle = "This is a very long title that contains multiple words and should be handled correctly";
    String longDescription = "This is a very long description that contains multiple sentences and should be handled correctly by the article creation service without any issues.";
    String longBody = "This is a very long body content that contains multiple paragraphs and should be handled correctly by the article creation service. It includes various types of content and formatting that might be present in a real article.";
    List<String> tagList = Arrays.asList("long-content", "test", "article");
    
    NewArticleParam newArticleParam = new NewArticleParam(longTitle, longDescription, longBody, tagList);

    Article result = articleCommandService.createArticle(newArticleParam, testUser);

    assertThat(result, notNullValue());
    assertThat(result.getTitle(), is(longTitle));
    assertThat(result.getDescription(), is(longDescription));
    assertThat(result.getBody(), is(longBody));
    verify(articleRepository).save(any(Article.class));
  }

  @Test
  public void should_create_article_with_special_characters() {
    String titleWithSpecialChars = "Test Title with Special Chars: @#$%^&*()";
    String descriptionWithSpecialChars = "Description with unicode: 你好世界 and symbols: ©®™";
    String bodyWithSpecialChars = "Body with various chars: <script>alert('test')</script> and quotes: \"Hello 'World'\"";
    
    NewArticleParam newArticleParam = new NewArticleParam(titleWithSpecialChars, descriptionWithSpecialChars, bodyWithSpecialChars, Arrays.asList("special", "chars"));

    Article result = articleCommandService.createArticle(newArticleParam, testUser);

    assertThat(result, notNullValue());
    assertThat(result.getTitle(), is(titleWithSpecialChars));
    assertThat(result.getDescription(), is(descriptionWithSpecialChars));
    assertThat(result.getBody(), is(bodyWithSpecialChars));
    verify(articleRepository).save(any(Article.class));
  }

  @Test
  public void should_update_article_successfully() {
    Article existingArticle = new Article("Original Title", "Original Description", "Original Body", Arrays.asList("tag1"), testUser.getId());
    UpdateArticleParam updateParam = new UpdateArticleParam("Updated Title", "Updated Body", "Updated Description");

    Article result = articleCommandService.updateArticle(existingArticle, updateParam);

    assertThat(result, is(existingArticle));
    assertThat(result.getTitle(), is("Updated Title"));
    assertThat(result.getDescription(), is("Updated Description"));
    assertThat(result.getBody(), is("Updated Body"));
    verify(articleRepository).save(existingArticle);
  }

  @Test
  public void should_update_article_with_partial_data() {
    Article existingArticle = new Article("Original Title", "Original Description", "Original Body", Arrays.asList("tag1"), testUser.getId());
    UpdateArticleParam updateParam = new UpdateArticleParam("Updated Title", null, null);

    Article result = articleCommandService.updateArticle(existingArticle, updateParam);

    assertThat(result, is(existingArticle));
    assertThat(result.getTitle(), is("Updated Title"));
    assertThat(result.getDescription(), is("Original Description"));
    assertThat(result.getBody(), is("Original Body"));
    verify(articleRepository).save(existingArticle);
  }

  @Test
  public void should_update_article_with_empty_strings() {
    Article existingArticle = new Article("Original Title", "Original Description", "Original Body", Arrays.asList("tag1"), testUser.getId());
    UpdateArticleParam updateParam = new UpdateArticleParam("", "", "");

    Article result = articleCommandService.updateArticle(existingArticle, updateParam);

    assertThat(result, is(existingArticle));
    assertThat(result.getTitle(), is("Original Title"));
    assertThat(result.getDescription(), is("Original Description"));
    assertThat(result.getBody(), is("Original Body"));
    verify(articleRepository).save(existingArticle);
  }

  @Test
  public void should_update_article_with_same_content() {
    Article existingArticle = new Article("Same Title", "Same Description", "Same Body", Arrays.asList("tag1"), testUser.getId());
    UpdateArticleParam updateParam = new UpdateArticleParam("Same Title", "Same Description", "Same Body");

    Article result = articleCommandService.updateArticle(existingArticle, updateParam);

    assertThat(result, is(existingArticle));
    verify(articleRepository).save(existingArticle);
  }

  @Test
  public void should_update_article_with_long_content() {
    Article existingArticle = new Article("Original Title", "Original Description", "Original Body", Arrays.asList("tag1"), testUser.getId());
    String longTitle = "This is a very long updated title that contains multiple words and should be handled correctly during the update process";
    String longDescription = "This is a very long updated description that contains multiple sentences and should be handled correctly by the article update service without any issues or truncation.";
    String longBody = "This is a very long updated body content that contains multiple paragraphs and should be handled correctly by the article update service. It includes various types of updated content and formatting that might be present in a real article update scenario.";
    
    UpdateArticleParam updateParam = new UpdateArticleParam(longTitle, longBody, longDescription);

    Article result = articleCommandService.updateArticle(existingArticle, updateParam);

    assertThat(result, is(existingArticle));
    assertThat(result.getTitle(), is(longTitle));
    assertThat(result.getDescription(), is(longDescription));
    assertThat(result.getBody(), is(longBody));
    verify(articleRepository).save(existingArticle);
  }

  @Test
  public void should_update_article_with_special_characters() {
    Article existingArticle = new Article("Original Title", "Original Description", "Original Body", Arrays.asList("tag1"), testUser.getId());
    String titleWithSpecialChars = "Updated Title with Special Chars: @#$%^&*()";
    String descriptionWithSpecialChars = "Updated description with unicode: 你好世界 and symbols: ©®™";
    String bodyWithSpecialChars = "Updated body with various chars: <script>alert('updated')</script> and quotes: \"Hello 'Updated World'\"";
    
    UpdateArticleParam updateParam = new UpdateArticleParam(titleWithSpecialChars, bodyWithSpecialChars, descriptionWithSpecialChars);

    Article result = articleCommandService.updateArticle(existingArticle, updateParam);

    assertThat(result, is(existingArticle));
    assertThat(result.getTitle(), is(titleWithSpecialChars));
    assertThat(result.getDescription(), is(descriptionWithSpecialChars));
    assertThat(result.getBody(), is(bodyWithSpecialChars));
    verify(articleRepository).save(existingArticle);
  }

  @Test
  public void should_handle_repository_save_during_create() {
    NewArticleParam newArticleParam = new NewArticleParam("Title", "Description", "Body", Arrays.asList("tag"));

    Article result = articleCommandService.createArticle(newArticleParam, testUser);

    assertThat(result, notNullValue());
    verify(articleRepository).save(any(Article.class));
  }

  @Test
  public void should_handle_repository_save_during_update() {
    Article existingArticle = new Article("Title", "Description", "Body", Arrays.asList("tag"), testUser.getId());
    UpdateArticleParam updateParam = new UpdateArticleParam("Updated Title", "Updated Body", "Updated Description");

    Article result = articleCommandService.updateArticle(existingArticle, updateParam);

    assertThat(result, is(existingArticle));
    verify(articleRepository).save(existingArticle);
  }

  @Test
  public void should_create_article_with_different_users() {
    User anotherUser = new User("another@example.com", "anotheruser", "password456", "Another bio", "another.jpg");
    NewArticleParam newArticleParam = new NewArticleParam("Title", "Description", "Body", Arrays.asList("tag"));

    Article result = articleCommandService.createArticle(newArticleParam, anotherUser);

    assertThat(result, notNullValue());
    assertThat(result.getUserId(), is(anotherUser.getId()));
    verify(articleRepository).save(any(Article.class));
  }

  @Test
  public void should_preserve_article_properties_during_update() {
    List<String> originalTags = Arrays.asList("original", "tags");
    Article existingArticle = new Article("Original Title", "Original Description", "Original Body", originalTags, testUser.getId());
    String originalSlug = existingArticle.getSlug();
    String originalUserId = existingArticle.getUserId();
    
    UpdateArticleParam updateParam = new UpdateArticleParam("Updated Title", "Updated Body", "Updated Description");

    Article result = articleCommandService.updateArticle(existingArticle, updateParam);

    assertThat(result.getTags().size(), is(originalTags.size()));
    assertThat(result.getUserId(), is(originalUserId));
    verify(articleRepository).save(existingArticle);
  }
}
