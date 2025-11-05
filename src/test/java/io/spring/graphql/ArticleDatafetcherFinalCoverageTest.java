package io.spring.graphql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.netflix.graphql.dgs.DgsDataFetchingEnvironment;
import graphql.execution.DataFetcherResult;
import io.spring.application.ArticleQueryService;
import io.spring.application.CursorPageParameter;
import io.spring.application.CursorPager;
import io.spring.application.DateTimeCursor;
import io.spring.application.data.ArticleData;
import io.spring.application.data.ProfileData;
import io.spring.core.user.User;
import io.spring.core.user.UserRepository;
import io.spring.graphql.types.ArticlesConnection;
import io.spring.graphql.types.Profile;
import java.util.Collections;
import java.util.Optional;
import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ArticleDatafetcherFinalCoverageTest {

  @Mock private ArticleQueryService articleQueryService;
  @Mock private UserRepository userRepository;
  @Mock private DgsDataFetchingEnvironment dfe;

  private ArticleDatafetcher articleDatafetcher;

  @BeforeEach
  void setUp() {
    articleDatafetcher = new ArticleDatafetcher(articleQueryService, userRepository);
  }

  @Test
  void should_throw_exception_when_both_first_and_last_are_null_in_getFeed() {
    assertThatThrownBy(() -> articleDatafetcher.getFeed(null, null, null, null, dfe))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("first 和 last 必须只存在一个");
  }

  @Test
  void should_throw_exception_when_both_first_and_last_are_null_in_userFeed() {
    assertThatThrownBy(() -> articleDatafetcher.userFeed(null, null, null, null, dfe))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("first 和 last 必须只存在一个");
  }

  @Test
  void should_throw_exception_when_both_first_and_last_are_null_in_userFavorites() {
    assertThatThrownBy(() -> articleDatafetcher.userFavorites(null, null, null, null, dfe))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("first 和 last 必须只存在一个");
  }

  @Test
  void should_throw_exception_when_both_first_and_last_are_null_in_userArticles() {
    assertThatThrownBy(() -> articleDatafetcher.userArticles(null, null, null, null, dfe))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("first 和 last 必须只存在一个");
  }

  @Test
  void should_throw_exception_when_both_first_and_last_are_null_in_getArticles() {
    assertThatThrownBy(() -> articleDatafetcher.getArticles(null, null, null, null, null, null, null, dfe))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("first 和 last 必须只存在一个");
  }

  @Test
  void should_handle_getFeed_with_last_parameter_and_before_cursor() {
    try (MockedStatic<SecurityUtil> securityUtilMock = Mockito.mockStatic(SecurityUtil.class)) {
      User currentUser = new User("user@example.com", "testuser", "123", "", "");
      securityUtilMock.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(currentUser));

      CursorPager<ArticleData> mockPager = createMockPager();
      when(articleQueryService.findUserFeedWithCursor(eq(currentUser), any(CursorPageParameter.class)))
          .thenReturn(mockPager);

      DataFetcherResult<ArticlesConnection> result = articleDatafetcher.getFeed(
          null, null, 5, "1672531200000", dfe);

      assertThat(result).isNotNull();
      assertThat(result.getData()).isNotNull();
      assertThat(result.getData().getEdges()).hasSize(1);
    }
  }

  @Test
  void should_handle_userFeed_with_last_parameter_and_before_cursor() {
    Profile profile = Profile.newBuilder().username("testuser").build();
    when(dfe.getSource()).thenReturn(profile);

    User targetUser = new User("user@example.com", "testuser", "123", "", "");
    when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(targetUser));

    CursorPager<ArticleData> mockPager = createMockPager();
    when(articleQueryService.findUserFeedWithCursor(eq(targetUser), any(CursorPageParameter.class)))
        .thenReturn(mockPager);

    DataFetcherResult<ArticlesConnection> result = articleDatafetcher.userFeed(
        null, null, 5, "1672531200000", dfe);

    assertThat(result).isNotNull();
    assertThat(result.getData()).isNotNull();
    assertThat(result.getData().getEdges()).hasSize(1);
  }

  @Test
  void should_handle_userFavorites_with_last_parameter_and_before_cursor() {
    try (MockedStatic<SecurityUtil> securityUtilMock = Mockito.mockStatic(SecurityUtil.class)) {
      User currentUser = new User("user@example.com", "currentuser", "123", "", "");
      securityUtilMock.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(currentUser));

      Profile profile = Profile.newBuilder().username("testuser").build();
      when(dfe.getSource()).thenReturn(profile);

      CursorPager<ArticleData> mockPager = createMockPager();
      when(articleQueryService.findRecentArticlesWithCursor(
              eq(null), eq(null), eq("testuser"), any(CursorPageParameter.class), eq(currentUser)))
          .thenReturn(mockPager);

      DataFetcherResult<ArticlesConnection> result = articleDatafetcher.userFavorites(
          null, null, 5, "1672531200000", dfe);

      assertThat(result).isNotNull();
      assertThat(result.getData()).isNotNull();
      assertThat(result.getData().getEdges()).hasSize(1);
    }
  }

  @Test
  void should_handle_userArticles_with_last_parameter_and_before_cursor() {
    try (MockedStatic<SecurityUtil> securityUtilMock = Mockito.mockStatic(SecurityUtil.class)) {
      User currentUser = new User("user@example.com", "currentuser", "123", "", "");
      securityUtilMock.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(currentUser));

      Profile profile = Profile.newBuilder().username("testuser").build();
      when(dfe.getSource()).thenReturn(profile);

      CursorPager<ArticleData> mockPager = createMockPager();
      when(articleQueryService.findRecentArticlesWithCursor(
              eq(null), eq("testuser"), eq(null), any(CursorPageParameter.class), eq(currentUser)))
          .thenReturn(mockPager);

      DataFetcherResult<ArticlesConnection> result = articleDatafetcher.userArticles(
          null, null, 5, "1672531200000", dfe);

      assertThat(result).isNotNull();
      assertThat(result.getData()).isNotNull();
      assertThat(result.getData().getEdges()).hasSize(1);
    }
  }

  @Test
  void should_handle_getArticles_with_last_parameter_and_before_cursor() {
    try (MockedStatic<SecurityUtil> securityUtilMock = Mockito.mockStatic(SecurityUtil.class)) {
      User currentUser = new User("user@example.com", "currentuser", "123", "", "");
      securityUtilMock.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(currentUser));

      CursorPager<ArticleData> mockPager = createMockPager();
      when(articleQueryService.findRecentArticlesWithCursor(
              eq("tag"), eq("author"), eq("favorited"), any(CursorPageParameter.class), eq(currentUser)))
          .thenReturn(mockPager);

      DataFetcherResult<ArticlesConnection> result = articleDatafetcher.getArticles(
          null, null, 5, "1672531200000", "author", "favorited", "tag", dfe);

      assertThat(result).isNotNull();
      assertThat(result.getData()).isNotNull();
      assertThat(result.getData().getEdges()).hasSize(1);
    }
  }

  private CursorPager<ArticleData> createMockPager() {
    ProfileData profileData = new ProfileData(
        "author-id",
        "testauthor",
        "Test Bio",
        "",
        false);

    ArticleData articleData = new ArticleData(
        "article-id",
        "test-slug",
        "Test Title",
        "Test Description",
        "Test Body",
        false,
        0,
        DateTime.now(),
        DateTime.now(),
        Collections.singletonList("tag1"),
        profileData);

    return new CursorPager<>(
        Collections.singletonList(articleData),
        CursorPager.Direction.PREV,
        false);
  }
}
