package io.spring.graphql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mockStatic;

import com.netflix.graphql.dgs.DgsDataFetchingEnvironment;
import graphql.execution.DataFetcherResult;
import io.spring.application.ArticleQueryService;
import io.spring.application.CursorPageParameter;
import io.spring.application.CursorPager;
import io.spring.application.DateTimeCursor;
import io.spring.application.data.ArticleData;
import io.spring.core.user.User;
import io.spring.core.user.UserRepository;
import io.spring.graphql.SecurityUtil;
import io.spring.graphql.types.ArticlesConnection;
import io.spring.graphql.types.Profile;
import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ArticleDatafetcherSimpleTest {

  @Mock
  private ArticleQueryService articleQueryService;

  @Mock
  private UserRepository userRepository;

  private ArticleDatafetcher articleDatafetcher;
  private User testUser;

  @BeforeEach
  public void setUp() {
    articleDatafetcher = new ArticleDatafetcher(articleQueryService, userRepository);
    testUser = new User("test@example.com", "testuser", "password", "bio", "image.jpg");
  }

  @Test
  public void userArticles_should_handle_first_parameter_with_data() {
    DgsDataFetchingEnvironment dfe = mock(DgsDataFetchingEnvironment.class);
    Profile profile = Profile.newBuilder().username("testuser").build();
    when(dfe.getSource()).thenReturn(profile);

    ArticleData articleData1 = createMockArticleData("article1", "Test Article 1");
    ArticleData articleData2 = createMockArticleData("article2", "Test Article 2");

    CursorPager<ArticleData> mockResult = mock(CursorPager.class);
    when(mockResult.getData()).thenReturn(Arrays.asList(articleData1, articleData2));
    when(mockResult.getStartCursor()).thenReturn(new DateTimeCursor(DateTime.now()));
    when(mockResult.getEndCursor()).thenReturn(new DateTimeCursor(DateTime.now()));
    when(mockResult.hasPrevious()).thenReturn(false);
    when(mockResult.hasNext()).thenReturn(true);

    when(articleQueryService.findRecentArticlesWithCursor(
        eq(null), eq("testuser"), eq(null), any(CursorPageParameter.class), any(User.class)))
        .thenReturn(mockResult);

    try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
      securityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));

      DataFetcherResult<ArticlesConnection> result = articleDatafetcher.userArticles(10, null, null, null, dfe);

      assertThat(result).isNotNull();
      assertThat(result.getData()).isNotNull();
      assertThat(result.getData().getEdges()).hasSize(2);
      assertThat(result.getData().getPageInfo().isHasNextPage()).isTrue();
      assertThat((Map<String, Object>) result.getLocalContext()).containsKeys("article1", "article2");
    }
  }

  @Test
  public void getArticles_should_handle_filter_parameters_with_data() {
    DgsDataFetchingEnvironment dfe = mock(DgsDataFetchingEnvironment.class);

    ArticleData articleData = createMockArticleData("filtered-article", "Filtered Article");

    CursorPager<ArticleData> mockResult = mock(CursorPager.class);
    when(mockResult.getData()).thenReturn(Arrays.asList(articleData));
    when(mockResult.getStartCursor()).thenReturn(new DateTimeCursor(DateTime.now()));
    when(mockResult.getEndCursor()).thenReturn(new DateTimeCursor(DateTime.now()));
    when(mockResult.hasPrevious()).thenReturn(false);
    when(mockResult.hasNext()).thenReturn(false);

    when(articleQueryService.findRecentArticlesWithCursor(
        eq("java"), eq("author"), eq("favorited"), any(CursorPageParameter.class), any(User.class)))
        .thenReturn(mockResult);

    try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
      securityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));

      DataFetcherResult<ArticlesConnection> result = articleDatafetcher.getArticles(10, null, null, null, "author", "favorited", "java", dfe);

      assertThat(result).isNotNull();
      assertThat(result.getData()).isNotNull();
      assertThat(result.getData().getEdges()).hasSize(1);
      assertThat(result.getData().getEdges().get(0).getNode().getSlug()).isEqualTo("filtered-article");
      assertThat((Map<String, Object>) result.getLocalContext()).containsKey("filtered-article");
    }
  }

  @Test
  public void userFavorites_should_handle_first_parameter_with_data() {
    DgsDataFetchingEnvironment dfe = mock(DgsDataFetchingEnvironment.class);
    Profile profile = Profile.newBuilder().username("testuser").build();
    when(dfe.getSource()).thenReturn(profile);

    ArticleData favoriteArticle = createMockArticleData("favorite-article", "Favorite Article");

    CursorPager<ArticleData> mockResult = mock(CursorPager.class);
    when(mockResult.getData()).thenReturn(Arrays.asList(favoriteArticle));
    when(mockResult.getStartCursor()).thenReturn(new DateTimeCursor(DateTime.now()));
    when(mockResult.getEndCursor()).thenReturn(new DateTimeCursor(DateTime.now()));
    when(mockResult.hasPrevious()).thenReturn(false);
    when(mockResult.hasNext()).thenReturn(false);

    when(articleQueryService.findRecentArticlesWithCursor(
        eq(null), eq(null), eq("testuser"), any(CursorPageParameter.class), any(User.class)))
        .thenReturn(mockResult);

    try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
      securityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));

      DataFetcherResult<ArticlesConnection> result = articleDatafetcher.userFavorites(10, null, null, null, dfe);

      assertThat(result).isNotNull();
      assertThat(result.getData()).isNotNull();
      assertThat(result.getData().getEdges()).hasSize(1);
      assertThat(result.getData().getEdges().get(0).getNode().getSlug()).isEqualTo("favorite-article");
      assertThat((Map<String, Object>) result.getLocalContext()).containsKey("favorite-article");
    }
  }

  @Test
  public void getFeed_should_handle_first_parameter_with_data() {
    DgsDataFetchingEnvironment dfe = mock(DgsDataFetchingEnvironment.class);

    ArticleData feedArticle = createMockArticleData("feed-article", "Feed Article");

    CursorPager<ArticleData> mockResult = mock(CursorPager.class);
    when(mockResult.getData()).thenReturn(Arrays.asList(feedArticle));
    when(mockResult.getStartCursor()).thenReturn(new DateTimeCursor(DateTime.now()));
    when(mockResult.getEndCursor()).thenReturn(new DateTimeCursor(DateTime.now()));
    when(mockResult.hasPrevious()).thenReturn(false);
    when(mockResult.hasNext()).thenReturn(false);

    when(articleQueryService.findUserFeedWithCursor(any(User.class), any(CursorPageParameter.class)))
        .thenReturn(mockResult);

    try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
      securityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));

      DataFetcherResult<ArticlesConnection> result = articleDatafetcher.getFeed(10, null, null, null, dfe);

      assertThat(result).isNotNull();
      assertThat(result.getData()).isNotNull();
      assertThat(result.getData().getEdges()).hasSize(1);
      assertThat(result.getData().getEdges().get(0).getNode().getSlug()).isEqualTo("feed-article");
      assertThat((Map<String, Object>) result.getLocalContext()).containsKey("feed-article");
    }
  }

  @Test
  public void userFeed_should_handle_first_parameter_with_data() {
    DgsDataFetchingEnvironment dfe = mock(DgsDataFetchingEnvironment.class);
    Profile profile = Profile.newBuilder().username("targetuser").build();
    when(dfe.getSource()).thenReturn(profile);

    User targetUser = new User("target@example.com", "targetuser", "password", "bio", "image.jpg");
    when(userRepository.findByUsername("targetuser")).thenReturn(Optional.of(targetUser));

    ArticleData userFeedArticle = createMockArticleData("user-feed-article", "User Feed Article");

    CursorPager<ArticleData> mockResult = mock(CursorPager.class);
    when(mockResult.getData()).thenReturn(Arrays.asList(userFeedArticle));
    when(mockResult.getStartCursor()).thenReturn(new DateTimeCursor(DateTime.now()));
    when(mockResult.getEndCursor()).thenReturn(new DateTimeCursor(DateTime.now()));
    when(mockResult.hasPrevious()).thenReturn(false);
    when(mockResult.hasNext()).thenReturn(false);

    when(articleQueryService.findUserFeedWithCursor(eq(targetUser), any(CursorPageParameter.class)))
        .thenReturn(mockResult);

    DataFetcherResult<ArticlesConnection> result = articleDatafetcher.userFeed(10, null, null, null, dfe);

    assertThat(result).isNotNull();
    assertThat(result.getData()).isNotNull();
    assertThat(result.getData().getEdges()).hasSize(1);
    assertThat(result.getData().getEdges().get(0).getNode().getSlug()).isEqualTo("user-feed-article");
    assertThat((Map<String, Object>) result.getLocalContext()).containsKey("user-feed-article");
  }

  @Test
  public void userArticles_should_handle_last_parameter() {
    DgsDataFetchingEnvironment dfe = mock(DgsDataFetchingEnvironment.class);
    Profile profile = Profile.newBuilder().username("testuser").build();
    when(dfe.getSource()).thenReturn(profile);

    CursorPager<ArticleData> mockResult = mock(CursorPager.class);
    when(mockResult.getData()).thenReturn(Collections.emptyList());
    when(mockResult.getStartCursor()).thenReturn(null);
    when(mockResult.getEndCursor()).thenReturn(null);
    when(mockResult.hasPrevious()).thenReturn(false);
    when(mockResult.hasNext()).thenReturn(false);

    when(articleQueryService.findRecentArticlesWithCursor(
        eq(null), eq("testuser"), eq(null), any(CursorPageParameter.class), any(User.class)))
        .thenReturn(mockResult);

    try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
      securityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));

      DataFetcherResult<ArticlesConnection> result = articleDatafetcher.userArticles(null, null, 10, "1234567890", dfe);

      assertThat(result).isNotNull();
      assertThat(result.getData()).isNotNull();
      assertThat(result.getData().getEdges()).isEmpty();
    }
  }

  private ArticleData createMockArticleData(String slug, String title) {
    ArticleData articleData = mock(ArticleData.class);
    when(articleData.getSlug()).thenReturn(slug);
    when(articleData.getTitle()).thenReturn(title);
    when(articleData.getBody()).thenReturn("Test body");
    when(articleData.getDescription()).thenReturn("Test description");
    when(articleData.isFavorited()).thenReturn(false);
    when(articleData.getFavoritesCount()).thenReturn(0);
    when(articleData.getTagList()).thenReturn(Arrays.asList("test"));
    when(articleData.getCreatedAt()).thenReturn(DateTime.now());
    when(articleData.getUpdatedAt()).thenReturn(DateTime.now());
    when(articleData.getCursor()).thenReturn(new DateTimeCursor(DateTime.now()));
    return articleData;
  }
}
