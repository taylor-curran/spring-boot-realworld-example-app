package io.spring.graphql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.netflix.graphql.dgs.DgsDataFetchingEnvironment;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import io.spring.api.exception.ResourceNotFoundException;
import io.spring.application.ArticleQueryService;
import io.spring.application.CursorPager;
import io.spring.application.data.ArticleData;
import io.spring.application.data.CommentData;
import io.spring.application.data.ProfileData;
import io.spring.core.user.User;
import io.spring.core.user.UserRepository;
import io.spring.graphql.exception.AuthenticationException;
import io.spring.graphql.types.Article;
import io.spring.graphql.types.ArticlesConnection;
import io.spring.graphql.types.Profile;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ArticleDatafetcherEnhancedTest {

  @Mock
  private ArticleQueryService articleQueryService;

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private ArticleDatafetcher articleDatafetcher;

  @Test
  public void getFeed_should_throw_exception_when_both_first_and_last_are_null() {
    DgsDataFetchingEnvironment dfe = Mockito.mock(DgsDataFetchingEnvironment.class);
    
    try (MockedStatic<SecurityUtil> mockedSecurityUtil = Mockito.mockStatic(SecurityUtil.class)) {
      mockedSecurityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.empty());
      
      assertThrows(IllegalArgumentException.class, () ->
        articleDatafetcher.getFeed(null, null, null, null, dfe));
    }
  }

  @Test
  public void getFeed_should_handle_anonymous_user_with_first_parameter() {
    CursorPager<ArticleData> mockPager = createMockCursorPager();
    DgsDataFetchingEnvironment dfe = Mockito.mock(DgsDataFetchingEnvironment.class);
    
    try (MockedStatic<SecurityUtil> mockedSecurityUtil = Mockito.mockStatic(SecurityUtil.class)) {
      mockedSecurityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.empty());
      when(articleQueryService.findUserFeedWithCursor(any(), any()))
          .thenReturn(mockPager);

      DataFetcherResult<ArticlesConnection> result = articleDatafetcher.getFeed(
          10, null, null, null, dfe);

      assertThat(result).isNotNull();
      assertThat(result.getData()).isNotNull();
    }
  }

  @Test
  public void userFeed_should_throw_resource_not_found_when_user_not_exists() {
    DgsDataFetchingEnvironment dfe = Mockito.mock(DgsDataFetchingEnvironment.class);
    Profile mockProfile = Mockito.mock(Profile.class);
    when(mockProfile.getUsername()).thenReturn("nonexistent");
    when(dfe.getSource()).thenReturn(mockProfile);
    when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () ->
      articleDatafetcher.userFeed(10, null, null, null, dfe));
  }

  @Test
  public void userFavorites_should_handle_anonymous_user() {
    CursorPager<ArticleData> mockPager = createMockCursorPager();
    DgsDataFetchingEnvironment dfe = Mockito.mock(DgsDataFetchingEnvironment.class);
    Profile mockProfile = Mockito.mock(Profile.class);
    when(mockProfile.getUsername()).thenReturn("testuser");
    when(dfe.getSource()).thenReturn(mockProfile);
    
    try (MockedStatic<SecurityUtil> mockedSecurityUtil = Mockito.mockStatic(SecurityUtil.class)) {
      mockedSecurityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.empty());
      when(articleQueryService.findRecentArticlesWithCursor(any(), any(), eq("testuser"), any(), any()))
          .thenReturn(mockPager);

      DataFetcherResult<ArticlesConnection> result = articleDatafetcher.userFavorites(
          10, null, null, null, dfe);

      assertThat(result).isNotNull();
      assertThat(result.getData()).isNotNull();
    }
  }

  @Test
  public void getArticles_should_handle_all_filter_parameters() {
    CursorPager<ArticleData> mockPager = createMockCursorPager();
    DgsDataFetchingEnvironment dfe = Mockito.mock(DgsDataFetchingEnvironment.class);
    
    try (MockedStatic<SecurityUtil> mockedSecurityUtil = Mockito.mockStatic(SecurityUtil.class)) {
      mockedSecurityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.empty());
      when(articleQueryService.findRecentArticlesWithCursor(eq("java"), eq("author"), eq("favoriter"), any(), any()))
          .thenReturn(mockPager);

      DataFetcherResult<ArticlesConnection> result = articleDatafetcher.getArticles(
          10, null, null, null, "author", "favoriter", "java", dfe);

      assertThat(result).isNotNull();
      assertThat(result.getData()).isNotNull();
    }
  }

  @Test
  public void findArticleBySlug_should_throw_resource_not_found_when_article_not_exists() {
    try (MockedStatic<SecurityUtil> mockedSecurityUtil = Mockito.mockStatic(SecurityUtil.class)) {
      mockedSecurityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.empty());
      when(articleQueryService.findBySlug("nonexistent-slug", null)).thenReturn(Optional.empty());

      assertThrows(ResourceNotFoundException.class, () ->
        articleDatafetcher.findArticleBySlug("nonexistent-slug"));
    }
  }

  @Test
  public void findArticleBySlug_should_handle_authenticated_user() {
    User mockUser = createMockUser();
    ArticleData mockArticleData = createMockArticleData();
    
    try (MockedStatic<SecurityUtil> mockedSecurityUtil = Mockito.mockStatic(SecurityUtil.class)) {
      mockedSecurityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(mockUser));
      when(articleQueryService.findBySlug("test-slug", mockUser)).thenReturn(Optional.of(mockArticleData));

      DataFetcherResult<Article> result = articleDatafetcher.findArticleBySlug("test-slug");

      assertThat(result).isNotNull();
      assertThat(result.getData()).isNotNull();
    }
  }

  private CursorPager<ArticleData> createMockCursorPager() {
    return new CursorPager<>(Collections.emptyList(), CursorPager.Direction.NEXT, false);
  }

  private User createMockUser() {
    User user = new User("test@example.com", "testuser", "password", "bio", "image");
    return user;
  }

  private ArticleData createMockArticleData() {
    return new ArticleData(
        "article-id", "test-slug", "Test Title", "Test Description", "Test Body",
        false, 0, DateTime.now(), DateTime.now(), Arrays.asList("tag1"), createMockProfileData());
  }

  private ProfileData createMockProfileData() {
    return new ProfileData("user-id", "testuser", "Test Bio", "image.jpg", false);
  }

  @Test
  public void getArticle_should_handle_local_context_with_current_user() {
    DataFetchingEnvironment dfe = Mockito.mock(DataFetchingEnvironment.class);
    io.spring.core.article.Article coreArticle = Mockito.mock(io.spring.core.article.Article.class);
    User mockUser = createMockUser();
    ArticleData mockArticleData = createMockArticleData();
    
    when(dfe.getLocalContext()).thenReturn(coreArticle);
    when(coreArticle.getId()).thenReturn("article-id");
    when(articleQueryService.findById("article-id", mockUser)).thenReturn(Optional.of(mockArticleData));

    try (MockedStatic<SecurityUtil> mockedSecurityUtil = Mockito.mockStatic(SecurityUtil.class)) {
      mockedSecurityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(mockUser));

      DataFetcherResult<Article> result = articleDatafetcher.getArticle(dfe);

      assertThat(result).isNotNull();
      assertThat(result.getData()).isNotNull();
      assertThat(result.getData().getSlug()).isEqualTo("test-slug");
      assertThat(result.getLocalContext()).isNotNull();
    }
  }

  @Test
  public void getArticle_should_handle_local_context_without_current_user() {
    DataFetchingEnvironment dfe = Mockito.mock(DataFetchingEnvironment.class);
    io.spring.core.article.Article coreArticle = Mockito.mock(io.spring.core.article.Article.class);
    ArticleData mockArticleData = createMockArticleData();
    
    when(dfe.getLocalContext()).thenReturn(coreArticle);
    when(coreArticle.getId()).thenReturn("article-id");
    when(articleQueryService.findById("article-id", null)).thenReturn(Optional.of(mockArticleData));

    try (MockedStatic<SecurityUtil> mockedSecurityUtil = Mockito.mockStatic(SecurityUtil.class)) {
      mockedSecurityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.empty());

      DataFetcherResult<Article> result = articleDatafetcher.getArticle(dfe);

      assertThat(result).isNotNull();
      assertThat(result.getData()).isNotNull();
      assertThat(result.getData().getSlug()).isEqualTo("test-slug");
    }
  }

  @Test
  public void getArticle_should_throw_exception_when_article_not_found() {
    DataFetchingEnvironment dfe = Mockito.mock(DataFetchingEnvironment.class);
    io.spring.core.article.Article coreArticle = Mockito.mock(io.spring.core.article.Article.class);
    
    when(dfe.getLocalContext()).thenReturn(coreArticle);
    when(coreArticle.getId()).thenReturn("non-existent-id");
    when(articleQueryService.findById("non-existent-id", null)).thenReturn(Optional.empty());

    try (MockedStatic<SecurityUtil> mockedSecurityUtil = Mockito.mockStatic(SecurityUtil.class)) {
      mockedSecurityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.empty());

      assertThrows(ResourceNotFoundException.class, () ->
        articleDatafetcher.getArticle(dfe));
    }
  }

  @Test
  public void getCommentArticle_should_handle_comment_local_context() {
    DataFetchingEnvironment dfe = Mockito.mock(DataFetchingEnvironment.class);
    CommentData mockCommentData = createMockCommentData();
    ArticleData mockArticleData = createMockArticleData();
    User mockUser = createMockUser();
    
    when(dfe.getLocalContext()).thenReturn(mockCommentData);
    when(articleQueryService.findById("article-id", mockUser)).thenReturn(Optional.of(mockArticleData));

    try (MockedStatic<SecurityUtil> mockedSecurityUtil = Mockito.mockStatic(SecurityUtil.class)) {
      mockedSecurityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(mockUser));

      DataFetcherResult<Article> result = articleDatafetcher.getCommentArticle(dfe);

      assertThat(result).isNotNull();
      assertThat(result.getData()).isNotNull();
      assertThat(result.getData().getSlug()).isEqualTo("test-slug");
      assertThat(result.getLocalContext()).isNotNull();
    }
  }

  @Test
  public void getCommentArticle_should_handle_comment_without_current_user() {
    DataFetchingEnvironment dfe = Mockito.mock(DataFetchingEnvironment.class);
    CommentData mockCommentData = createMockCommentData();
    ArticleData mockArticleData = createMockArticleData();
    
    when(dfe.getLocalContext()).thenReturn(mockCommentData);
    when(articleQueryService.findById("article-id", null)).thenReturn(Optional.of(mockArticleData));

    try (MockedStatic<SecurityUtil> mockedSecurityUtil = Mockito.mockStatic(SecurityUtil.class)) {
      mockedSecurityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.empty());

      DataFetcherResult<Article> result = articleDatafetcher.getCommentArticle(dfe);

      assertThat(result).isNotNull();
      assertThat(result.getData()).isNotNull();
      assertThat(result.getData().getSlug()).isEqualTo("test-slug");
    }
  }

  @Test
  public void getCommentArticle_should_throw_exception_when_article_not_found() {
    DataFetchingEnvironment dfe = Mockito.mock(DataFetchingEnvironment.class);
    CommentData mockCommentData = createMockCommentDataWithArticleId("non-existent-article");
    
    when(dfe.getLocalContext()).thenReturn(mockCommentData);
    when(articleQueryService.findById("non-existent-article", null)).thenReturn(Optional.empty());

    try (MockedStatic<SecurityUtil> mockedSecurityUtil = Mockito.mockStatic(SecurityUtil.class)) {
      mockedSecurityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.empty());

      assertThrows(ResourceNotFoundException.class, () ->
        articleDatafetcher.getCommentArticle(dfe));
    }
  }

  @Test
  public void userFeed_should_handle_authenticated_user_with_pagination() {
    CursorPager<ArticleData> mockPager = createMockCursorPager();
    DgsDataFetchingEnvironment dfe = Mockito.mock(DgsDataFetchingEnvironment.class);
    Profile mockProfile = Mockito.mock(Profile.class);
    User mockUser = createMockUser();
    
    when(mockProfile.getUsername()).thenReturn("testuser");
    when(dfe.getSource()).thenReturn(mockProfile);
    when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(mockUser));
    when(articleQueryService.findUserFeedWithCursor(eq(mockUser), any()))
        .thenReturn(mockPager);

    try (MockedStatic<SecurityUtil> mockedSecurityUtil = Mockito.mockStatic(SecurityUtil.class)) {
      mockedSecurityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(mockUser));

      DataFetcherResult<ArticlesConnection> result = articleDatafetcher.userFeed(
          20, "1640995200000", null, null, dfe);

      assertThat(result).isNotNull();
      assertThat(result.getData()).isNotNull();
    }
  }

  @Test
  public void userArticles_should_handle_authenticated_user() {
    CursorPager<ArticleData> mockPager = createMockCursorPager();
    DgsDataFetchingEnvironment dfe = Mockito.mock(DgsDataFetchingEnvironment.class);
    Profile mockProfile = Mockito.mock(Profile.class);
    User mockUser = createMockUser();
    
    when(mockProfile.getUsername()).thenReturn("testuser");
    when(dfe.getSource()).thenReturn(mockProfile);
    when(articleQueryService.findRecentArticlesWithCursor(any(), eq("testuser"), any(), any(), any()))
        .thenReturn(mockPager);

    try (MockedStatic<SecurityUtil> mockedSecurityUtil = Mockito.mockStatic(SecurityUtil.class)) {
      mockedSecurityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(mockUser));

      DataFetcherResult<ArticlesConnection> result = articleDatafetcher.userArticles(
          15, null, null, null, dfe);

      assertThat(result).isNotNull();
      assertThat(result.getData()).isNotNull();
    }
  }

  @Test
  public void userArticles_should_handle_null_articles_gracefully() {
    DgsDataFetchingEnvironment dfe = Mockito.mock(DgsDataFetchingEnvironment.class);
    Profile mockProfile = Mockito.mock(Profile.class);
    User mockUser = createMockUser();
    
    when(mockProfile.getUsername()).thenReturn("testuser");
    when(dfe.getSource()).thenReturn(mockProfile);
    when(articleQueryService.findRecentArticlesWithCursor(any(), eq("testuser"), any(), any(), any()))
        .thenReturn(null);

    try (MockedStatic<SecurityUtil> mockedSecurityUtil = Mockito.mockStatic(SecurityUtil.class)) {
      mockedSecurityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(mockUser));

      assertThrows(NullPointerException.class, () ->
        articleDatafetcher.userArticles(10, null, null, null, dfe));
    }
  }

  @Test
  public void userFavorites_should_handle_authenticated_user_with_pagination() {
    CursorPager<ArticleData> mockPager = createMockCursorPager();
    DgsDataFetchingEnvironment dfe = Mockito.mock(DgsDataFetchingEnvironment.class);
    Profile mockProfile = Mockito.mock(Profile.class);
    User mockUser = createMockUser();
    
    when(mockProfile.getUsername()).thenReturn("testuser");
    when(dfe.getSource()).thenReturn(mockProfile);
    when(articleQueryService.findRecentArticlesWithCursor(any(), any(), eq("testuser"), any(), any()))
        .thenReturn(mockPager);

    try (MockedStatic<SecurityUtil> mockedSecurityUtil = Mockito.mockStatic(SecurityUtil.class)) {
      mockedSecurityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(mockUser));

      DataFetcherResult<ArticlesConnection> result = articleDatafetcher.userFavorites(
          25, "1640995200000", null, null, dfe);

      assertThat(result).isNotNull();
      assertThat(result.getData()).isNotNull();
    }
  }

  @Test
  public void getArticles_should_handle_with_first_parameter() {
    CursorPager<ArticleData> mockPager = createMockCursorPager();
    DgsDataFetchingEnvironment dfe = Mockito.mock(DgsDataFetchingEnvironment.class);
    
    try (MockedStatic<SecurityUtil> mockedSecurityUtil = Mockito.mockStatic(SecurityUtil.class)) {
      mockedSecurityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.empty());
      when(articleQueryService.findRecentArticlesWithCursor(any(), any(), any(), any(), any()))
          .thenReturn(mockPager);

      DataFetcherResult<ArticlesConnection> result = articleDatafetcher.getArticles(
          10, null, null, null, null, null, null, dfe);

      assertThat(result).isNotNull();
      assertThat(result.getData()).isNotNull();
    }
  }

  @Test
  public void getArticles_should_handle_authenticated_user_with_all_filters() {
    CursorPager<ArticleData> mockPager = createMockCursorPager();
    DgsDataFetchingEnvironment dfe = Mockito.mock(DgsDataFetchingEnvironment.class);
    User mockUser = createMockUser();
    
    try (MockedStatic<SecurityUtil> mockedSecurityUtil = Mockito.mockStatic(SecurityUtil.class)) {
      mockedSecurityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(mockUser));
      when(articleQueryService.findRecentArticlesWithCursor(eq("spring"), eq("john"), eq("jane"), any(), eq(mockUser)))
          .thenReturn(mockPager);

      DataFetcherResult<ArticlesConnection> result = articleDatafetcher.getArticles(
          30, "1640995200000", null, null, "john", "jane", "spring", dfe);

      assertThat(result).isNotNull();
      assertThat(result.getData()).isNotNull();
    }
  }

  @Test
  public void getFeed_should_handle_authenticated_user_with_last_parameter() {
    CursorPager<ArticleData> mockPager = createMockCursorPager();
    DgsDataFetchingEnvironment dfe = Mockito.mock(DgsDataFetchingEnvironment.class);
    User mockUser = createMockUser();
    
    try (MockedStatic<SecurityUtil> mockedSecurityUtil = Mockito.mockStatic(SecurityUtil.class)) {
      mockedSecurityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(mockUser));
      when(articleQueryService.findUserFeedWithCursor(eq(mockUser), any()))
          .thenReturn(mockPager);

      DataFetcherResult<ArticlesConnection> result = articleDatafetcher.getFeed(
          null, null, 10, "1640995200000", dfe);

      assertThat(result).isNotNull();
      assertThat(result.getData()).isNotNull();
    }
  }

  @Test
  public void getFeed_should_handle_anonymous_user_with_last_parameter() {
    CursorPager<ArticleData> mockPager = createMockCursorPager();
    DgsDataFetchingEnvironment dfe = Mockito.mock(DgsDataFetchingEnvironment.class);
    
    try (MockedStatic<SecurityUtil> mockedSecurityUtil = Mockito.mockStatic(SecurityUtil.class)) {
      mockedSecurityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.empty());
      when(articleQueryService.findUserFeedWithCursor(any(), any()))
          .thenReturn(mockPager);

      DataFetcherResult<ArticlesConnection> result = articleDatafetcher.getFeed(
          null, null, 5, "1640995200000", dfe);

      assertThat(result).isNotNull();
      assertThat(result.getData()).isNotNull();
    }
  }

  private CommentData createMockCommentData() {
    ProfileData profileData = createMockProfileData();
    return new CommentData(
        "comment-id", "Test comment", "article-id", DateTime.now(), DateTime.now(), profileData);
  }

  private CommentData createMockCommentDataWithArticleId(String articleId) {
    ProfileData profileData = createMockProfileData();
    return new CommentData(
        "comment-id", "Test comment", articleId, DateTime.now(), DateTime.now(), profileData);
  }
}
