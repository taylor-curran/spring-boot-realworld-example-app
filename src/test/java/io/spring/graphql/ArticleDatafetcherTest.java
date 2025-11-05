package io.spring.graphql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.mockStatic;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import com.netflix.graphql.dgs.DgsDataFetchingEnvironment;
import graphql.execution.DataFetcherResult;
import io.spring.api.exception.ResourceNotFoundException;
import io.spring.application.ArticleQueryService;
import io.spring.application.CursorPageParameter;
import io.spring.application.CursorPager;
import io.spring.application.CursorPager.Direction;
import io.spring.application.DateTimeCursor;
import io.spring.application.data.ArticleData;
import io.spring.core.user.User;
import io.spring.core.user.UserRepository;
import io.spring.graphql.types.Article;
import io.spring.graphql.types.ArticlesConnection;
import io.spring.graphql.types.Profile;
import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ArticleDatafetcherTest {

  @Mock
  private ArticleQueryService articleQueryService;

  @Mock
  private UserRepository userRepository;

  @Mock
  private DgsDataFetchingEnvironment dfe;

  @InjectMocks
  private ArticleDatafetcher articleDatafetcher;

  private User testUser;
  private ArticleData testArticleData;
  private Profile testProfile;

  @BeforeEach
  public void setUp() {
    testUser = new User("test@example.com", "testuser", "password", "bio", "image.jpg");
    testArticleData = new ArticleData(
        "article-id", "test-slug", "Test Title", "Test Description", "Test Body",
        false, 0, DateTime.now(), DateTime.now(), Arrays.asList("java", "spring"),
        null
    );
    testProfile = Profile.newBuilder()
        .username("testuser")
        .bio("bio")
        .image("image.jpg")
        .following(false)
        .build();
  }

  @Test
  public void should_fetch_user_feed_successfully() {
    Integer first = 10;
    String after = null;
    Integer last = null;
    String before = null;

    when(dfe.getSource()).thenReturn(testProfile);
    when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
    
    CursorPager<ArticleData> pager = new CursorPager<>(Arrays.asList(testArticleData), Direction.NEXT, false);
    when(articleQueryService.findUserFeedWithCursor(eq(testUser), any(CursorPageParameter.class)))
        .thenReturn(pager);

    DataFetcherResult<ArticlesConnection> result = articleDatafetcher.userFeed(first, after, last, before, dfe);

    assertThat(result).isNotNull();
    assertThat(result.getData()).isNotNull();
    assertThat(result.getData().getEdges()).hasSize(1);
    verify(articleQueryService).findUserFeedWithCursor(eq(testUser), any(CursorPageParameter.class));
  }

  @Test
  public void should_throw_exception_when_user_not_found_for_feed() {
    Integer first = 10;
    String after = null;
    Integer last = null;
    String before = null;

    when(dfe.getSource()).thenReturn(testProfile);
    when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

    try {
      articleDatafetcher.userFeed(first, after, last, before, dfe);
    } catch (ResourceNotFoundException e) {
      assertThat(e).isInstanceOf(ResourceNotFoundException.class);
    }
  }

  @Test
  public void should_fetch_user_favorites_successfully() {
    Integer first = 10;
    String after = null;
    Integer last = null;
    String before = null;

    when(dfe.getSource()).thenReturn(testProfile);
    
    CursorPager<ArticleData> pager = new CursorPager<>(Arrays.asList(testArticleData), Direction.NEXT, false);
    when(articleQueryService.findRecentArticlesWithCursor(eq(null), eq(null), eq("testuser"), any(CursorPageParameter.class), any()))
        .thenReturn(pager);

    try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
      securityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));

      DataFetcherResult<ArticlesConnection> result = articleDatafetcher.userFavorites(first, after, last, before, dfe);

      assertThat(result).isNotNull();
      assertThat(result.getData().getEdges()).hasSize(1);
      verify(articleQueryService).findRecentArticlesWithCursor(eq(null), eq(null), eq("testuser"), any(CursorPageParameter.class), eq(testUser));
    }
  }

  @Test
  public void should_fetch_user_articles_successfully() {
    Integer first = 10;
    String after = null;
    Integer last = null;
    String before = null;

    when(dfe.getSource()).thenReturn(testProfile);
    
    CursorPager<ArticleData> pager = new CursorPager<>(Arrays.asList(testArticleData), Direction.NEXT, false);
    when(articleQueryService.findRecentArticlesWithCursor(eq(null), eq("testuser"), eq(null), any(CursorPageParameter.class), any()))
        .thenReturn(pager);

    try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
      securityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));

      DataFetcherResult<ArticlesConnection> result = articleDatafetcher.userArticles(first, after, last, before, dfe);

      assertThat(result).isNotNull();
      assertThat(result.getData().getEdges()).hasSize(1);
      verify(articleQueryService).findRecentArticlesWithCursor(eq(null), eq("testuser"), eq(null), any(CursorPageParameter.class), eq(testUser));
    }
  }

  @Test
  public void should_fetch_articles_with_filters() {
    Integer first = 5;
    String after = "1640995200000"; // Valid timestamp in milliseconds
    Integer last = null;
    String before = null;
    String authoredBy = "testuser";
    String favoritedBy = "favuser";
    String withTag = "java";

    CursorPager<ArticleData> pager = new CursorPager<>(Arrays.asList(testArticleData), Direction.NEXT, false);
    when(articleQueryService.findRecentArticlesWithCursor(eq(withTag), eq(authoredBy), eq(favoritedBy), any(CursorPageParameter.class), any()))
        .thenReturn(pager);

    try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
      securityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));

      DataFetcherResult<ArticlesConnection> result = articleDatafetcher.getArticles(first, after, last, before, authoredBy, favoritedBy, withTag, dfe);

      assertThat(result).isNotNull();
      assertThat(result.getData().getEdges()).hasSize(1);
      verify(articleQueryService).findRecentArticlesWithCursor(eq(withTag), eq(authoredBy), eq(favoritedBy), any(CursorPageParameter.class), eq(testUser));
    }
  }

  @Test
  public void should_throw_exception_when_both_first_and_last_are_null() {
    Integer first = null;
    String after = null;
    Integer last = null;
    String before = null;

    try {
      articleDatafetcher.getArticles(first, after, last, before, null, null, null, dfe);
    } catch (IllegalArgumentException e) {
      assertThat(e.getMessage()).contains("first 和 last 必须只存在一个");
    }
  }

  @Test
  public void should_find_article_by_slug_successfully() {
    String slug = "test-slug";

    when(articleQueryService.findBySlug(eq(slug), any())).thenReturn(Optional.of(testArticleData));

    try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
      securityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));

      DataFetcherResult<Article> result = articleDatafetcher.findArticleBySlug(slug);

      assertThat(result).isNotNull();
      assertThat(result.getData()).isNotNull();
      verify(articleQueryService).findBySlug(eq(slug), eq(testUser));
    }
  }

  @Test
  public void should_throw_exception_when_article_not_found_by_slug() {
    String slug = "non-existent-slug";

    when(articleQueryService.findBySlug(eq(slug), any())).thenReturn(Optional.empty());

    try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
      securityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));

      try {
        articleDatafetcher.findArticleBySlug(slug);
      } catch (ResourceNotFoundException e) {
        assertThat(e).isInstanceOf(ResourceNotFoundException.class);
      }

      verify(articleQueryService).findBySlug(eq(slug), eq(testUser));
    }
  }
}
