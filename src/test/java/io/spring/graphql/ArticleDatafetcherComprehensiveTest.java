package io.spring.graphql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import com.netflix.graphql.dgs.DgsDataFetchingEnvironment;
import graphql.execution.DataFetcherResult;
import io.spring.application.ArticleQueryService;
import io.spring.application.CursorPageParameter;
import io.spring.application.CursorPager;
import io.spring.application.data.ArticleData;
import io.spring.core.user.User;
import io.spring.core.user.UserRepository;
import io.spring.graphql.types.ArticlesConnection;
import io.spring.graphql.types.Profile;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ArticleDatafetcherComprehensiveTest {

  @Mock private ArticleQueryService articleQueryService;

  @Mock private UserRepository userRepository;

  private ArticleDatafetcher articleDatafetcher;
  private User testUser;

  @BeforeEach
  public void setUp() {
    articleDatafetcher = new ArticleDatafetcher(articleQueryService, userRepository);
    testUser = new User("test@example.com", "testuser", "password", "bio", "image.jpg");
  }

  @Test
  public void userFavorites_should_handle_null_parameters() {
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
            eq(null), eq(null), eq("testuser"), any(CursorPageParameter.class), any(User.class)))
        .thenReturn(mockResult);

    try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
      securityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));

      DataFetcherResult<ArticlesConnection> result =
          articleDatafetcher.userFavorites(10, null, null, null, dfe);

      assertThat(result).isNotNull();
      assertThat(result.getData()).isNotNull();
      assertThat(result.getData().getEdges()).isEmpty();
    }
  }

  @Test
  public void userFavorites_should_handle_first_parameter_only() {
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
            eq(null), eq(null), eq("testuser"), any(CursorPageParameter.class), any(User.class)))
        .thenReturn(mockResult);

    try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
      securityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));

      DataFetcherResult<ArticlesConnection> result =
          articleDatafetcher.userFavorites(10, null, null, null, dfe);

      assertThat(result).isNotNull();
      assertThat(result.getData()).isNotNull();
      assertThat(result.getData().getEdges()).isEmpty();
    }
  }

  @Test
  public void userFavorites_should_handle_last_parameter_only() {
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
            eq(null), eq(null), eq("testuser"), any(CursorPageParameter.class), any(User.class)))
        .thenReturn(mockResult);

    try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
      securityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));

      DataFetcherResult<ArticlesConnection> result =
          articleDatafetcher.userFavorites(null, null, 10, null, dfe);

      assertThat(result).isNotNull();
      assertThat(result.getData()).isNotNull();
      assertThat(result.getData().getEdges()).isEmpty();
    }
  }

  @Test
  public void getFeed_should_handle_empty_result() {
    DgsDataFetchingEnvironment dfe = mock(DgsDataFetchingEnvironment.class);

    CursorPager<ArticleData> mockResult = mock(CursorPager.class);
    when(mockResult.getData()).thenReturn(Collections.emptyList());
    when(mockResult.getStartCursor()).thenReturn(null);
    when(mockResult.getEndCursor()).thenReturn(null);
    when(mockResult.hasPrevious()).thenReturn(false);
    when(mockResult.hasNext()).thenReturn(false);

    when(articleQueryService.findUserFeedWithCursor(
            any(User.class), any(CursorPageParameter.class)))
        .thenReturn(mockResult);

    try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
      securityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));

      DataFetcherResult<ArticlesConnection> result =
          articleDatafetcher.getFeed(10, null, null, null, dfe);

      assertThat(result).isNotNull();
      assertThat(result.getData()).isNotNull();
      assertThat(result.getData().getEdges()).isEmpty();
    }
  }

  @Test
  public void getFeed_should_handle_last_parameter() {
    DgsDataFetchingEnvironment dfe = mock(DgsDataFetchingEnvironment.class);

    CursorPager<ArticleData> mockResult = mock(CursorPager.class);
    when(mockResult.getData()).thenReturn(Collections.emptyList());
    when(mockResult.getStartCursor()).thenReturn(null);
    when(mockResult.getEndCursor()).thenReturn(null);
    when(mockResult.hasPrevious()).thenReturn(false);
    when(mockResult.hasNext()).thenReturn(false);

    when(articleQueryService.findUserFeedWithCursor(
            any(User.class), any(CursorPageParameter.class)))
        .thenReturn(mockResult);

    try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
      securityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));

      DataFetcherResult<ArticlesConnection> result =
          articleDatafetcher.getFeed(null, null, 10, "1640995200000", dfe);

      assertThat(result).isNotNull();
      assertThat(result.getData()).isNotNull();
      assertThat(result.getData().getEdges()).isEmpty();
    }
  }
}
