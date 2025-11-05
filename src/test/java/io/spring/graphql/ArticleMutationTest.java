package io.spring.graphql;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import graphql.execution.DataFetcherResult;
import io.spring.application.article.ArticleCommandService;
import io.spring.application.article.NewArticleParam;
import io.spring.application.article.UpdateArticleParam;
import io.spring.core.article.Article;
import io.spring.core.article.ArticleRepository;
import io.spring.core.favorite.ArticleFavorite;
import io.spring.core.favorite.ArticleFavoriteRepository;
import io.spring.core.user.User;
import io.spring.graphql.exception.AuthenticationException;
import io.spring.graphql.types.ArticlePayload;
import io.spring.graphql.types.CreateArticleInput;
import io.spring.graphql.types.DeletionStatus;
import io.spring.graphql.types.UpdateArticleInput;
import java.util.Arrays;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
public class ArticleMutationTest {

  @Mock private ArticleCommandService articleCommandService;
  @Mock private ArticleFavoriteRepository articleFavoriteRepository;
  @Mock private ArticleRepository articleRepository;

  private ArticleMutation articleMutation;
  private Article testArticle;
  private User user;

  @BeforeEach
  public void setUp() {
    articleMutation = new ArticleMutation(articleCommandService, articleFavoriteRepository, articleRepository);
    user = new User("test@example.com", "testuser", "password123", "Test bio", "test.jpg");
    testArticle = new Article("Test Article", "Test Description", "Test Body", Arrays.asList("tag1", "tag2"), user.getId());
  }

  private void setAuthenticatedUser(User user) {
    TestingAuthenticationToken authentication = new TestingAuthenticationToken(user, null);
    SecurityContextHolder.getContext().setAuthentication(authentication);
  }

  private void setAnonymousUser() {
    SecurityContextHolder.getContext().setAuthentication(
        new AnonymousAuthenticationToken("key", "anonymous", Arrays.asList(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))));
  }

  @AfterEach
  public void cleanup() {
    SecurityContextHolder.clearContext();
  }

  @Test
  public void should_create_article_successfully() {
    setAuthenticatedUser(user);
    
    CreateArticleInput input = CreateArticleInput.newBuilder()
        .title("Test Article")
        .description("Test Description")
        .body("Test Body")
        .tagList(Arrays.asList("tag1", "tag2"))
        .build();
    
    when(articleCommandService.createArticle(any(NewArticleParam.class), eq(user)))
        .thenReturn(testArticle);

    DataFetcherResult<ArticlePayload> result = articleMutation.createArticle(input);

    assertThat(result, notNullValue());
    assertThat(result.getData(), notNullValue());
    assertThat(result.getLocalContext(), is(testArticle));
    verify(articleCommandService).createArticle(any(NewArticleParam.class), eq(user));
  }

  @Test
  public void should_create_article_with_empty_tag_list() {
    setAuthenticatedUser(user);
    
    CreateArticleInput input = CreateArticleInput.newBuilder()
        .title("Test Article")
        .description("Test Description")
        .body("Test Body")
        .tagList(Arrays.asList())
        .build();
    
    when(articleCommandService.createArticle(any(NewArticleParam.class), eq(user)))
        .thenReturn(testArticle);

    DataFetcherResult<ArticlePayload> result = articleMutation.createArticle(input);

    assertThat(result, notNullValue());
    assertThat(result.getData(), notNullValue());
    assertThat(result.getLocalContext(), is(testArticle));
    verify(articleCommandService).createArticle(any(NewArticleParam.class), eq(user));
  }

  @Test
  public void should_throw_authentication_exception_when_user_not_authenticated_for_create() {
    setAnonymousUser();
    
    assertThat(SecurityUtil.getCurrentUser().isEmpty(), is(true));
    
    CreateArticleInput input = CreateArticleInput.newBuilder()
        .title("Test Article")
        .description("Test Description")
        .body("Test Body")
        .tagList(Arrays.asList("tag1", "tag2"))
        .build();

    AuthenticationException exception = assertThrows(AuthenticationException.class, () -> {
      articleMutation.createArticle(input);
    });
    
    assertThat(exception, notNullValue());
  }

  @Test
  public void should_update_article_successfully() {
    setAuthenticatedUser(user);
    
    UpdateArticleInput input = UpdateArticleInput.newBuilder()
        .title("Updated Title")
        .description("Updated Description")
        .body("Updated Body")
        .build();
    
    when(articleRepository.findBySlug("test-slug")).thenReturn(Optional.of(testArticle));
    when(articleCommandService.updateArticle(eq(testArticle), any(UpdateArticleParam.class)))
        .thenReturn(testArticle);

    DataFetcherResult<ArticlePayload> result = articleMutation.updateArticle("test-slug", input);

    assertThat(result, notNullValue());
    assertThat(result.getData(), notNullValue());
    assertThat(result.getLocalContext(), is(testArticle));
    verify(articleCommandService).updateArticle(eq(testArticle), any(UpdateArticleParam.class));
  }

  @Test
  public void should_throw_exception_when_article_not_found_for_update() {
    UpdateArticleInput input = UpdateArticleInput.newBuilder()
        .title("Updated Title")
        .build();
    
    when(articleRepository.findBySlug("nonexistent")).thenReturn(Optional.empty());

    assertThrows(Exception.class, () -> {
      articleMutation.updateArticle("nonexistent", input);
    });
  }

  @Test
  public void should_favorite_article_successfully() {
    setAuthenticatedUser(user);
    
    when(articleRepository.findBySlug("test-slug")).thenReturn(Optional.of(testArticle));

    DataFetcherResult<ArticlePayload> result = articleMutation.favoriteArticle("test-slug");

    assertThat(result, notNullValue());
    assertThat(result.getData(), notNullValue());
    assertThat(result.getLocalContext(), is(testArticle));
    verify(articleFavoriteRepository).save(any(ArticleFavorite.class));
  }

  @Test
  public void should_throw_exception_when_article_not_found_for_favorite() {
    setAuthenticatedUser(user);
    when(articleRepository.findBySlug("nonexistent")).thenReturn(Optional.empty());

    assertThrows(Exception.class, () -> {
      articleMutation.favoriteArticle("nonexistent");
    });
  }

  @Test
  public void should_unfavorite_article_successfully() {
    setAuthenticatedUser(user);
    
    when(articleRepository.findBySlug("test-slug")).thenReturn(Optional.of(testArticle));
    when(articleFavoriteRepository.find(testArticle.getId(), user.getId()))
        .thenReturn(Optional.of(new ArticleFavorite(testArticle.getId(), user.getId())));

    DataFetcherResult<ArticlePayload> result = articleMutation.unfavoriteArticle("test-slug");

    assertThat(result, notNullValue());
    assertThat(result.getData(), notNullValue());
    assertThat(result.getLocalContext(), is(testArticle));
    verify(articleFavoriteRepository).remove(any(ArticleFavorite.class));
  }

  @Test
  public void should_unfavorite_article_when_favorite_not_found() {
    setAuthenticatedUser(user);
    
    when(articleRepository.findBySlug("test-slug")).thenReturn(Optional.of(testArticle));
    when(articleFavoriteRepository.find(testArticle.getId(), user.getId()))
        .thenReturn(Optional.empty());

    DataFetcherResult<ArticlePayload> result = articleMutation.unfavoriteArticle("test-slug");

    assertThat(result, notNullValue());
    assertThat(result.getData(), notNullValue());
    assertThat(result.getLocalContext(), is(testArticle));
  }

  @Test
  public void should_delete_article_successfully() {
    setAuthenticatedUser(user);
    
    when(articleRepository.findBySlug("test-slug")).thenReturn(Optional.of(testArticle));

    DeletionStatus result = articleMutation.deleteArticle("test-slug");

    assertThat(result, notNullValue());
    assertThat(result.getSuccess(), is(true));
    verify(articleRepository).remove(testArticle);
  }

  @Test
  public void should_throw_exception_when_article_not_found_for_delete() {
    assertThrows(Exception.class, () -> {
      articleMutation.deleteArticle("nonexistent");
    });
  }

  @Test
  public void should_throw_authentication_exception_when_user_not_authenticated_for_favorite() {
    setAnonymousUser();

    assertThrows(AuthenticationException.class, () -> {
      articleMutation.favoriteArticle("test-slug");
    });
  }

  @Test
  public void should_throw_authentication_exception_when_user_not_authenticated_for_unfavorite() {
    setAnonymousUser();

    assertThrows(AuthenticationException.class, () -> {
      articleMutation.unfavoriteArticle("test-slug");
    });
  }

  @Test
  public void should_throw_authentication_exception_when_user_not_authenticated_for_delete() {
    setAnonymousUser();

    assertThrows(AuthenticationException.class, () -> {
      articleMutation.deleteArticle("test-slug");
    });
  }

  @Test
  public void should_throw_authentication_exception_when_user_not_authenticated_for_update() {
    setAnonymousUser();
    
    when(articleRepository.findBySlug("test-slug")).thenReturn(Optional.of(testArticle));
    
    UpdateArticleInput input = UpdateArticleInput.newBuilder()
        .title("Updated Title")
        .build();

    assertThrows(AuthenticationException.class, () -> {
      articleMutation.updateArticle("test-slug", input);
    });
  }

  @Test
  public void should_create_article_with_null_tag_list() {
    setAuthenticatedUser(user);
    
    CreateArticleInput input = CreateArticleInput.newBuilder()
        .title("Test Article")
        .description("Test Description")
        .body("Test Body")
        .tagList(null)
        .build();
    
    when(articleCommandService.createArticle(any(NewArticleParam.class), eq(user)))
        .thenReturn(testArticle);

    DataFetcherResult<ArticlePayload> result = articleMutation.createArticle(input);

    assertThat(result, notNullValue());
    assertThat(result.getData(), notNullValue());
    assertThat(result.getLocalContext(), is(testArticle));
    verify(articleCommandService).createArticle(any(NewArticleParam.class), eq(user));
  }

  @Test
  public void should_throw_authorization_exception_when_user_cannot_update_article() {
    User otherUser = new User("other@example.com", "otheruser", "password456", "Other bio", "other.jpg");
    setAuthenticatedUser(otherUser);
    
    UpdateArticleInput input = UpdateArticleInput.newBuilder()
        .title("Updated Title")
        .build();
    
    when(articleRepository.findBySlug("test-slug")).thenReturn(Optional.of(testArticle));

    assertThrows(Exception.class, () -> {
      articleMutation.updateArticle("test-slug", input);
    });
  }

  @Test
  public void should_throw_authorization_exception_when_user_cannot_delete_article() {
    User otherUser = new User("other@example.com", "otheruser", "password456", "Other bio", "other.jpg");
    setAuthenticatedUser(otherUser);
    
    when(articleRepository.findBySlug("test-slug")).thenReturn(Optional.of(testArticle));

    assertThrows(Exception.class, () -> {
      articleMutation.deleteArticle("test-slug");
    });
  }
}
