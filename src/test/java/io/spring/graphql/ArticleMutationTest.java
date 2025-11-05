package io.spring.graphql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import graphql.execution.DataFetcherResult;
import io.spring.api.exception.NoAuthorizationException;
import io.spring.api.exception.ResourceNotFoundException;
import io.spring.application.article.ArticleCommandService;
import io.spring.application.article.NewArticleParam;
import io.spring.application.article.UpdateArticleParam;
import io.spring.core.article.Article;
import io.spring.core.article.ArticleRepository;
import io.spring.core.favorite.ArticleFavorite;
import io.spring.core.favorite.ArticleFavoriteRepository;
import io.spring.core.service.AuthorizationService;
import io.spring.core.user.User;
import io.spring.graphql.exception.AuthenticationException;
import io.spring.graphql.types.ArticlePayload;
import io.spring.graphql.types.CreateArticleInput;
import io.spring.graphql.types.DeletionStatus;
import io.spring.graphql.types.UpdateArticleInput;
import java.util.Arrays;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ArticleMutationTest {

  @Mock private ArticleCommandService articleCommandService;

  @Mock private ArticleFavoriteRepository articleFavoriteRepository;

  @Mock private ArticleRepository articleRepository;

  @InjectMocks private ArticleMutation articleMutation;

  private User testUser;
  private Article testArticle;

  @BeforeEach
  public void setUp() {
    testUser = new User("test@example.com", "testuser", "password", "bio", "image.jpg");
    testArticle =
        new Article(
            "Test Title", "Test Description", "Test Body", Arrays.asList("test"), testUser.getId());
  }

  @Test
  public void should_create_article_successfully() {
    CreateArticleInput input =
        CreateArticleInput.newBuilder()
            .title("Test Article")
            .description("Test Description")
            .body("Test Body")
            .tagList(Arrays.asList("java", "spring"))
            .build();

    when(articleCommandService.createArticle(any(NewArticleParam.class), any(User.class)))
        .thenReturn(testArticle);

    try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
      securityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));

      DataFetcherResult<ArticlePayload> result = articleMutation.createArticle(input);

      assertThat(result).isNotNull();
      assertThat(result.getData()).isInstanceOf(ArticlePayload.class);
      assertThat(result.getLocalContext()).isEqualTo(testArticle);
      verify(articleCommandService).createArticle(any(NewArticleParam.class), any(User.class));
    }
  }

  @Test
  public void should_create_article_with_empty_tag_list() {
    CreateArticleInput input =
        CreateArticleInput.newBuilder()
            .title("Test Article")
            .description("Test Description")
            .body("Test Body")
            .tagList(null)
            .build();

    when(articleCommandService.createArticle(any(NewArticleParam.class), any(User.class)))
        .thenReturn(testArticle);

    try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
      securityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));

      DataFetcherResult<ArticlePayload> result = articleMutation.createArticle(input);

      assertThat(result).isNotNull();
      assertThat(result.getData()).isInstanceOf(ArticlePayload.class);
      assertThat(result.getLocalContext()).isEqualTo(testArticle);
    }
  }

  @Test
  public void should_throw_authentication_exception_when_user_not_authenticated_for_create() {
    CreateArticleInput input =
        CreateArticleInput.newBuilder()
            .title("Test Article")
            .description("Test Description")
            .body("Test Body")
            .build();

    try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
      securityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.empty());

      try {
        articleMutation.createArticle(input);
      } catch (AuthenticationException e) {
        assertThat(e).isInstanceOf(AuthenticationException.class);
      }

      verify(articleCommandService, never())
          .createArticle(any(NewArticleParam.class), any(User.class));
    }
  }

  @Test
  public void should_update_article_successfully() {
    String slug = "test-article";
    UpdateArticleInput input =
        UpdateArticleInput.newBuilder()
            .title("Updated Title")
            .description("Updated Description")
            .body("Updated Body")
            .build();

    when(articleRepository.findBySlug(slug)).thenReturn(Optional.of(testArticle));
    when(articleCommandService.updateArticle(any(Article.class), any(UpdateArticleParam.class)))
        .thenReturn(testArticle);

    try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class);
        MockedStatic<AuthorizationService> authService = mockStatic(AuthorizationService.class)) {
      securityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));
      authService
          .when(() -> AuthorizationService.canWriteArticle(testUser, testArticle))
          .thenReturn(true);

      DataFetcherResult<ArticlePayload> result = articleMutation.updateArticle(slug, input);

      assertThat(result).isNotNull();
      assertThat(result.getData()).isInstanceOf(ArticlePayload.class);
      assertThat(result.getLocalContext()).isEqualTo(testArticle);
      verify(articleCommandService)
          .updateArticle(any(Article.class), any(UpdateArticleParam.class));
    }
  }

  @Test
  public void should_throw_resource_not_found_when_article_not_exists_for_update() {
    String slug = "non-existent-article";
    UpdateArticleInput input = UpdateArticleInput.newBuilder().title("Updated Title").build();

    when(articleRepository.findBySlug(slug)).thenReturn(Optional.empty());

    try {
      articleMutation.updateArticle(slug, input);
    } catch (ResourceNotFoundException e) {
      assertThat(e).isInstanceOf(ResourceNotFoundException.class);
    }

    verify(articleCommandService, never())
        .updateArticle(any(Article.class), any(UpdateArticleParam.class));
  }

  @Test
  public void should_throw_no_authorization_exception_when_user_cannot_write_article() {
    String slug = "test-article";
    UpdateArticleInput input = UpdateArticleInput.newBuilder().title("Updated Title").build();

    when(articleRepository.findBySlug(slug)).thenReturn(Optional.of(testArticle));

    try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class);
        MockedStatic<AuthorizationService> authService = mockStatic(AuthorizationService.class)) {
      securityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));
      authService
          .when(() -> AuthorizationService.canWriteArticle(testUser, testArticle))
          .thenReturn(false);

      try {
        articleMutation.updateArticle(slug, input);
      } catch (NoAuthorizationException e) {
        assertThat(e).isInstanceOf(NoAuthorizationException.class);
      }

      verify(articleCommandService, never())
          .updateArticle(any(Article.class), any(UpdateArticleParam.class));
    }
  }

  @Test
  public void should_favorite_article_successfully() {
    String slug = "test-article";

    when(articleRepository.findBySlug(slug)).thenReturn(Optional.of(testArticle));

    try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
      securityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));

      DataFetcherResult<ArticlePayload> result = articleMutation.favoriteArticle(slug);

      assertThat(result).isNotNull();
      assertThat(result.getData()).isInstanceOf(ArticlePayload.class);
      assertThat(result.getLocalContext()).isEqualTo(testArticle);
      verify(articleFavoriteRepository).save(any(ArticleFavorite.class));
    }
  }

  @Test
  public void should_throw_resource_not_found_when_article_not_exists_for_favorite() {
    String slug = "non-existent-article";

    when(articleRepository.findBySlug(slug)).thenReturn(Optional.empty());

    try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
      securityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));

      try {
        articleMutation.favoriteArticle(slug);
      } catch (ResourceNotFoundException e) {
        assertThat(e).isInstanceOf(ResourceNotFoundException.class);
      }

      verify(articleFavoriteRepository, never()).save(any(ArticleFavorite.class));
    }
  }

  @Test
  public void should_unfavorite_article_successfully() {
    String slug = "test-article";
    ArticleFavorite favorite = new ArticleFavorite(testArticle.getId(), testUser.getId());

    when(articleRepository.findBySlug(slug)).thenReturn(Optional.of(testArticle));
    when(articleFavoriteRepository.find(testArticle.getId(), testUser.getId()))
        .thenReturn(Optional.of(favorite));

    try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
      securityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));

      DataFetcherResult<ArticlePayload> result = articleMutation.unfavoriteArticle(slug);

      assertThat(result).isNotNull();
      assertThat(result.getData()).isInstanceOf(ArticlePayload.class);
      assertThat(result.getLocalContext()).isEqualTo(testArticle);
      verify(articleFavoriteRepository).remove(favorite);
    }
  }

  @Test
  public void should_unfavorite_article_when_favorite_not_exists() {
    String slug = "test-article";

    when(articleRepository.findBySlug(slug)).thenReturn(Optional.of(testArticle));
    when(articleFavoriteRepository.find(testArticle.getId(), testUser.getId()))
        .thenReturn(Optional.empty());

    try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
      securityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));

      DataFetcherResult<ArticlePayload> result = articleMutation.unfavoriteArticle(slug);

      assertThat(result).isNotNull();
      assertThat(result.getData()).isInstanceOf(ArticlePayload.class);
      assertThat(result.getLocalContext()).isEqualTo(testArticle);
      verify(articleFavoriteRepository, never()).remove(any(ArticleFavorite.class));
    }
  }

  @Test
  public void should_delete_article_successfully() {
    String slug = "test-article";

    when(articleRepository.findBySlug(slug)).thenReturn(Optional.of(testArticle));

    try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class);
        MockedStatic<AuthorizationService> authService = mockStatic(AuthorizationService.class)) {
      securityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));
      authService
          .when(() -> AuthorizationService.canWriteArticle(testUser, testArticle))
          .thenReturn(true);

      DeletionStatus result = articleMutation.deleteArticle(slug);

      assertThat(result).isNotNull();
      assertThat(result.getSuccess()).isTrue();
      verify(articleRepository).remove(testArticle);
    }
  }

  @Test
  public void should_throw_no_authorization_exception_when_user_cannot_delete_article() {
    String slug = "test-article";

    when(articleRepository.findBySlug(slug)).thenReturn(Optional.of(testArticle));

    try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class);
        MockedStatic<AuthorizationService> authService = mockStatic(AuthorizationService.class)) {
      securityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));
      authService
          .when(() -> AuthorizationService.canWriteArticle(testUser, testArticle))
          .thenReturn(false);

      try {
        articleMutation.deleteArticle(slug);
      } catch (NoAuthorizationException e) {
        assertThat(e).isInstanceOf(NoAuthorizationException.class);
      }

      verify(articleRepository, never()).remove(testArticle);
    }
  }
}
