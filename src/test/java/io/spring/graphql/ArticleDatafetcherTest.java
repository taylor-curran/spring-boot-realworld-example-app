package io.spring.graphql;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.netflix.graphql.dgs.DgsDataFetchingEnvironment;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import io.spring.api.exception.ResourceNotFoundException;
import io.spring.application.ArticleQueryService;
import io.spring.application.CursorPageParameter;
import io.spring.application.CursorPager;
import io.spring.application.CursorPager.Direction;
import io.spring.application.DateTimeCursor;
import io.spring.application.data.ArticleData;
import io.spring.application.data.CommentData;
import io.spring.application.data.ProfileData;
import io.spring.core.article.Article;
import io.spring.core.user.User;
import io.spring.core.user.UserRepository;
import io.spring.graphql.types.ArticlesConnection;
import io.spring.graphql.types.Profile;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.joda.time.DateTime;
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
public class ArticleDatafetcherTest {

  @Mock private ArticleQueryService articleQueryService;
  @Mock private UserRepository userRepository;
  @Mock private DataFetchingEnvironment environment;

  private ArticleDatafetcher articleDatafetcher;
  private ArticleData testArticleData;
  private CommentData testCommentData;
  private User user;
  private User targetUser;
  private Profile testProfile;

  @BeforeEach
  public void setUp() {
    articleDatafetcher = new ArticleDatafetcher(articleQueryService, userRepository);
    user = new User("test@example.com", "testuser", "password123", "Test bio", "test.jpg");
    targetUser = new User("target@example.com", "targetuser", "password456", "Target bio", "target.jpg");
    
    ProfileData profileData = new ProfileData("author-id", "testauthor", "Author Bio", "author.jpg", false);
    
    testArticleData = new ArticleData(
        "article-id",
        "test-article", 
        "Test Article",
        "Test Description",
        "Test Body",
        false,
        0,
        DateTime.now(),
        DateTime.now(),
        Arrays.asList("tag1", "tag2"),
        profileData
    );

    testCommentData = new CommentData(
        "comment-id",
        "Test comment",
        "article-id",
        DateTime.now(),
        DateTime.now(),
        profileData
    );

    testProfile = Profile.newBuilder()
        .username("targetuser")
        .bio("Target bio")
        .image("target.jpg")
        .following(false)
        .build();
  }

  @AfterEach
  public void cleanup() {
    SecurityContextHolder.clearContext();
  }

  @Test
  public void should_get_feed_with_first_parameter() {
    SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken(user, null));
    
    CursorPager<ArticleData> mockPager = createMockPager(Arrays.asList(testArticleData));
    when(articleQueryService.findUserFeedWithCursor(eq(user), any(CursorPageParameter.class)))
        .thenReturn(mockPager);

    DataFetcherResult<ArticlesConnection> result = articleDatafetcher.getFeed(10, null, null, null, null);

    assertThat(result, notNullValue());
    assertThat(result.getData(), notNullValue());
    verify(articleQueryService).findUserFeedWithCursor(eq(user), any(CursorPageParameter.class));
  }

  @Test
  public void should_get_feed_with_last_parameter() {
    SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken(user, null));
    
    CursorPager<ArticleData> mockPager = createMockPager(Arrays.asList(testArticleData));
    when(articleQueryService.findUserFeedWithCursor(eq(user), any(CursorPageParameter.class)))
        .thenReturn(mockPager);

    DataFetcherResult<ArticlesConnection> result = articleDatafetcher.getFeed(null, null, 10, null, null);

    assertThat(result, notNullValue());
    assertThat(result.getData(), notNullValue());
    verify(articleQueryService).findUserFeedWithCursor(eq(user), any(CursorPageParameter.class));
  }

  @Test
  public void should_throw_exception_when_both_first_and_last_are_null_in_feed() {
    assertThrows(IllegalArgumentException.class, () -> {
      articleDatafetcher.getFeed(null, null, null, null, null);
    });
  }

  @Test
  public void should_get_articles_with_filters() {
    CursorPager<ArticleData> mockPager = createMockPager(Arrays.asList(testArticleData));
    when(articleQueryService.findRecentArticlesWithCursor(anyString(), anyString(), anyString(), any(CursorPageParameter.class), any()))
        .thenReturn(mockPager);

    DataFetcherResult<ArticlesConnection> result = articleDatafetcher.getArticles(10, null, null, null, "testuser", "favuser", "tag1", null);

    assertThat(result, notNullValue());
    assertThat(result.getData(), notNullValue());
    verify(articleQueryService).findRecentArticlesWithCursor(eq("tag1"), eq("testuser"), eq("favuser"), any(CursorPageParameter.class), any());
  }

  @Test
  public void should_get_article_from_local_context() {
    Article article = new Article("Test Article", "Test Description", "Test Body", Arrays.asList("tag1", "tag2"), user.getId());
    when(environment.getLocalContext()).thenReturn(article);
    when(articleQueryService.findById(eq(article.getId()), any()))
        .thenReturn(Optional.of(testArticleData));

    DataFetcherResult<io.spring.graphql.types.Article> result = articleDatafetcher.getArticle(environment);

    assertThat(result, notNullValue());
    assertThat(result.getData(), notNullValue());
    verify(articleQueryService).findById(eq(article.getId()), any());
  }

  @Test
  public void should_get_comment_article() {
    when(environment.getLocalContext()).thenReturn(testCommentData);
    when(articleQueryService.findById(eq(testCommentData.getArticleId()), any()))
        .thenReturn(Optional.of(testArticleData));

    DataFetcherResult<io.spring.graphql.types.Article> result = articleDatafetcher.getCommentArticle(environment);

    assertThat(result, notNullValue());
    assertThat(result.getData(), notNullValue());
    verify(articleQueryService).findById(eq(testCommentData.getArticleId()), any());
  }

  @Test
  public void should_find_article_by_slug() {
    when(articleQueryService.findBySlug(eq("test-article"), any()))
        .thenReturn(Optional.of(testArticleData));

    DataFetcherResult<io.spring.graphql.types.Article> result = articleDatafetcher.findArticleBySlug("test-article");

    assertThat(result, notNullValue());
    assertThat(result.getData(), notNullValue());
    verify(articleQueryService).findBySlug(eq("test-article"), any());
  }

  @Test
  public void should_throw_exception_when_article_not_found_by_slug() {
    when(articleQueryService.findBySlug(eq("nonexistent"), any()))
        .thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> {
      articleDatafetcher.findArticleBySlug("nonexistent");
    });
  }

  @Test
  public void should_handle_anonymous_user_in_feed() {
    SecurityContextHolder.getContext().setAuthentication(
        new AnonymousAuthenticationToken("key", "anonymous", Arrays.asList(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))));
    
    CursorPager<ArticleData> mockPager = createMockPager(Arrays.asList(testArticleData));
    when(articleQueryService.findUserFeedWithCursor(any(), any(CursorPageParameter.class)))
        .thenReturn(mockPager);

    DataFetcherResult<ArticlesConnection> result = articleDatafetcher.getFeed(10, null, null, null, null);

    assertThat(result, notNullValue());
    assertThat(result.getData(), notNullValue());
  }

  @Test
  public void should_handle_empty_article_list() {
    SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken(user, null));
    
    CursorPager<ArticleData> mockPager = createMockPager(Collections.emptyList());
    when(articleQueryService.findUserFeedWithCursor(eq(user), any(CursorPageParameter.class)))
        .thenReturn(mockPager);

    DataFetcherResult<ArticlesConnection> result = articleDatafetcher.getFeed(10, null, null, null, null);

    assertThat(result, notNullValue());
    assertThat(result.getData(), notNullValue());
    verify(articleQueryService).findUserFeedWithCursor(eq(user), any(CursorPageParameter.class));
  }

  @Test
  public void should_get_user_feed_with_first_parameter() {
    DgsDataFetchingEnvironment mockDfe = mock(DgsDataFetchingEnvironment.class);
    when(mockDfe.getSource()).thenReturn(testProfile);
    when(userRepository.findByUsername("targetuser")).thenReturn(Optional.of(targetUser));
    
    CursorPager<ArticleData> mockPager = createMockPager(Arrays.asList(testArticleData));
    when(articleQueryService.findUserFeedWithCursor(eq(targetUser), any(CursorPageParameter.class)))
        .thenReturn(mockPager);

    DataFetcherResult<ArticlesConnection> result = articleDatafetcher.userFeed(10, null, null, null, mockDfe);

    assertThat(result, notNullValue());
    assertThat(result.getData(), notNullValue());
    verify(articleQueryService).findUserFeedWithCursor(eq(targetUser), any(CursorPageParameter.class));
  }

  @Test
  public void should_get_user_feed_with_last_parameter() {
    DgsDataFetchingEnvironment mockDfe = mock(DgsDataFetchingEnvironment.class);
    when(mockDfe.getSource()).thenReturn(testProfile);
    when(userRepository.findByUsername("targetuser")).thenReturn(Optional.of(targetUser));
    
    CursorPager<ArticleData> mockPager = createMockPager(Arrays.asList(testArticleData));
    when(articleQueryService.findUserFeedWithCursor(eq(targetUser), any(CursorPageParameter.class)))
        .thenReturn(mockPager);

    DataFetcherResult<ArticlesConnection> result = articleDatafetcher.userFeed(null, null, 10, null, mockDfe);

    assertThat(result, notNullValue());
    assertThat(result.getData(), notNullValue());
    verify(articleQueryService).findUserFeedWithCursor(eq(targetUser), any(CursorPageParameter.class));
  }

  @Test
  public void should_throw_exception_when_both_first_and_last_are_null_in_user_feed() {
    DgsDataFetchingEnvironment mockDfe = mock(DgsDataFetchingEnvironment.class);

    assertThrows(IllegalArgumentException.class, () -> {
      articleDatafetcher.userFeed(null, null, null, null, mockDfe);
    });
  }

  @Test
  public void should_get_user_articles_with_first_parameter() {
    DgsDataFetchingEnvironment mockDfe = mock(DgsDataFetchingEnvironment.class);
    when(mockDfe.getSource()).thenReturn(testProfile);
    
    CursorPager<ArticleData> mockPager = createMockPager(Arrays.asList(testArticleData));
    when(articleQueryService.findRecentArticlesWithCursor(isNull(), eq("targetuser"), isNull(), any(CursorPageParameter.class), any()))
        .thenReturn(mockPager);

    DataFetcherResult<ArticlesConnection> result = articleDatafetcher.userArticles(10, null, null, null, mockDfe);

    assertThat(result, notNullValue());
    assertThat(result.getData(), notNullValue());
    verify(articleQueryService).findRecentArticlesWithCursor(isNull(), eq("targetuser"), isNull(), any(CursorPageParameter.class), any());
  }

  @Test
  public void should_get_user_articles_with_last_parameter() {
    DgsDataFetchingEnvironment mockDfe = mock(DgsDataFetchingEnvironment.class);
    when(mockDfe.getSource()).thenReturn(testProfile);
    
    CursorPager<ArticleData> mockPager = createMockPager(Arrays.asList(testArticleData));
    when(articleQueryService.findRecentArticlesWithCursor(isNull(), eq("targetuser"), isNull(), any(CursorPageParameter.class), any()))
        .thenReturn(mockPager);

    DataFetcherResult<ArticlesConnection> result = articleDatafetcher.userArticles(null, null, 10, null, mockDfe);

    assertThat(result, notNullValue());
    assertThat(result.getData(), notNullValue());
    verify(articleQueryService).findRecentArticlesWithCursor(isNull(), eq("targetuser"), isNull(), any(CursorPageParameter.class), any());
  }

  @Test
  public void should_throw_exception_when_both_first_and_last_are_null_in_user_articles() {
    DgsDataFetchingEnvironment mockDfe = mock(DgsDataFetchingEnvironment.class);

    assertThrows(IllegalArgumentException.class, () -> {
      articleDatafetcher.userArticles(null, null, null, null, mockDfe);
    });
  }

  @Test
  public void should_get_user_favorites_with_first_parameter() {
    DgsDataFetchingEnvironment mockDfe = mock(DgsDataFetchingEnvironment.class);
    when(mockDfe.getSource()).thenReturn(testProfile);
    
    CursorPager<ArticleData> mockPager = createMockPager(Arrays.asList(testArticleData));
    when(articleQueryService.findRecentArticlesWithCursor(isNull(), isNull(), eq("targetuser"), any(CursorPageParameter.class), any()))
        .thenReturn(mockPager);

    DataFetcherResult<ArticlesConnection> result = articleDatafetcher.userFavorites(10, null, null, null, mockDfe);

    assertThat(result, notNullValue());
    assertThat(result.getData(), notNullValue());
    verify(articleQueryService).findRecentArticlesWithCursor(isNull(), isNull(), eq("targetuser"), any(CursorPageParameter.class), any());
  }

  @Test
  public void should_get_user_favorites_with_last_parameter() {
    DgsDataFetchingEnvironment mockDfe = mock(DgsDataFetchingEnvironment.class);
    when(mockDfe.getSource()).thenReturn(testProfile);
    
    CursorPager<ArticleData> mockPager = createMockPager(Arrays.asList(testArticleData));
    when(articleQueryService.findRecentArticlesWithCursor(isNull(), isNull(), eq("targetuser"), any(CursorPageParameter.class), any()))
        .thenReturn(mockPager);

    DataFetcherResult<ArticlesConnection> result = articleDatafetcher.userFavorites(null, null, 10, null, mockDfe);

    assertThat(result, notNullValue());
    assertThat(result.getData(), notNullValue());
    verify(articleQueryService).findRecentArticlesWithCursor(isNull(), isNull(), eq("targetuser"), any(CursorPageParameter.class), any());
  }

  @Test
  public void should_throw_exception_when_both_first_and_last_are_null_in_user_favorites() {
    DgsDataFetchingEnvironment mockDfe = mock(DgsDataFetchingEnvironment.class);

    assertThrows(IllegalArgumentException.class, () -> {
      articleDatafetcher.userFavorites(null, null, null, null, mockDfe);
    });
  }

  @Test
  public void should_get_articles_with_all_filters() {
    CursorPager<ArticleData> mockPager = createMockPager(Arrays.asList(testArticleData));
    when(articleQueryService.findRecentArticlesWithCursor(eq("tag1"), eq("testuser"), eq("favuser"), any(CursorPageParameter.class), any()))
        .thenReturn(mockPager);

    DataFetcherResult<ArticlesConnection> result = articleDatafetcher.getArticles(null, null, 10, null, "testuser", "favuser", "tag1", null);

    assertThat(result, notNullValue());
    assertThat(result.getData(), notNullValue());
    verify(articleQueryService).findRecentArticlesWithCursor(eq("tag1"), eq("testuser"), eq("favuser"), any(CursorPageParameter.class), any());
  }

  @Test
  public void should_get_articles_with_after_cursor() {
    CursorPager<ArticleData> mockPager = createMockPager(Arrays.asList(testArticleData));
    when(articleQueryService.findRecentArticlesWithCursor(anyString(), anyString(), anyString(), any(CursorPageParameter.class), any()))
        .thenReturn(mockPager);

    String afterCursor = String.valueOf(DateTime.now().getMillis());
    DataFetcherResult<ArticlesConnection> result = articleDatafetcher.getArticles(10, afterCursor, null, null, "testuser", "favuser", "tag1", null);

    assertThat(result, notNullValue());
    assertThat(result.getData(), notNullValue());
    verify(articleQueryService).findRecentArticlesWithCursor(eq("tag1"), eq("testuser"), eq("favuser"), any(CursorPageParameter.class), any());
  }

  private CursorPager<ArticleData> createMockPager(List<ArticleData> data) {
    return new CursorPager<>(data, Direction.NEXT, false);
  }
}
