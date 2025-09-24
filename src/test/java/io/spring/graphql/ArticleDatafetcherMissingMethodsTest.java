package io.spring.graphql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

import com.netflix.graphql.dgs.DgsDataFetchingEnvironment;
import graphql.execution.DataFetcherResult;
import io.spring.application.ArticleQueryService;
import io.spring.application.CursorPageParameter;
import io.spring.application.CursorPager;
import io.spring.application.data.ArticleData;
import io.spring.application.data.ProfileData;
import io.spring.core.user.User;
import io.spring.core.user.UserRepository;
import io.spring.graphql.types.ArticlesConnection;
import io.spring.graphql.types.Profile;
import java.util.Arrays;
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
public class ArticleDatafetcherMissingMethodsTest {

  @Mock private ArticleQueryService articleQueryService;
  @Mock private UserRepository userRepository;
  @Mock private DgsDataFetchingEnvironment dgsDataFetchingEnvironment;
  @Mock private Profile mockProfile;

  private ArticleDatafetcher articleDatafetcher;
  private User testUser;
  private ArticleData testArticleData;

  @BeforeEach
  void setUp() {
    articleDatafetcher = new ArticleDatafetcher(articleQueryService, userRepository);
    testUser = new User("test@example.com", "testuser", "123", "", "");
    
    ProfileData profileData = new ProfileData("profile-id", "testuser", "bio", "image.jpg", false);
    testArticleData = new ArticleData(
        "article-id",
        "test-slug",
        "Test Title",
        "Test Description",
        "Test Body",
        false,
        0,
        DateTime.now(),
        DateTime.now(),
        Arrays.asList("java", "spring"),
        profileData);
  }

  @Test
  void userFeed_should_handle_first_parameter_with_after_cursor() {
    when(mockProfile.getUsername()).thenReturn("testuser");
    when(dgsDataFetchingEnvironment.getSource()).thenReturn(mockProfile);
    when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
    
    CursorPager<ArticleData> mockPager = new CursorPager<>(Arrays.asList(testArticleData), 
        io.spring.application.CursorPager.Direction.NEXT, false);
    when(articleQueryService.findUserFeedWithCursor(eq(testUser), any(CursorPageParameter.class)))
        .thenReturn(mockPager);

    DataFetcherResult<ArticlesConnection> result = articleDatafetcher.userFeed(10, "1234567890", null, null, dgsDataFetchingEnvironment);

    assertThat(result).isNotNull();
    assertThat(result.getData()).isNotNull();
    assertThat(result.getData().getEdges()).hasSize(1);
  }

  @Test
  void userFeed_should_handle_last_parameter_with_before_cursor() {
    when(mockProfile.getUsername()).thenReturn("testuser");
    when(dgsDataFetchingEnvironment.getSource()).thenReturn(mockProfile);
    when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
    
    CursorPager<ArticleData> mockPager = new CursorPager<>(Arrays.asList(testArticleData), 
        io.spring.application.CursorPager.Direction.PREV, false);
    when(articleQueryService.findUserFeedWithCursor(eq(testUser), any(CursorPageParameter.class)))
        .thenReturn(mockPager);

    DataFetcherResult<ArticlesConnection> result = articleDatafetcher.userFeed(null, null, 10, "1234567890", dgsDataFetchingEnvironment);

    assertThat(result).isNotNull();
    assertThat(result.getData()).isNotNull();
    assertThat(result.getData().getEdges()).hasSize(1);
  }

  @Test
  void getArticles_should_handle_first_parameter_with_filters() {
    try (MockedStatic<SecurityUtil> securityUtilMock = Mockito.mockStatic(SecurityUtil.class)) {
      securityUtilMock.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));
      
      CursorPager<ArticleData> mockPager = new CursorPager<>(Arrays.asList(testArticleData), 
          io.spring.application.CursorPager.Direction.NEXT, false);
      when(articleQueryService.findRecentArticlesWithCursor(eq("java"), eq("testuser"), eq("favorited-user"), 
          any(CursorPageParameter.class), eq(testUser)))
          .thenReturn(mockPager);

      DataFetcherResult<ArticlesConnection> result = articleDatafetcher.getArticles(
          10, "1234567890", null, null, "testuser", "favorited-user", "java", dgsDataFetchingEnvironment);

      assertThat(result).isNotNull();
      assertThat(result.getData()).isNotNull();
      assertThat(result.getData().getEdges()).hasSize(1);
    }
  }

  @Test
  void getArticles_should_handle_last_parameter_with_before_cursor() {
    try (MockedStatic<SecurityUtil> securityUtilMock = Mockito.mockStatic(SecurityUtil.class)) {
      securityUtilMock.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));
      
      CursorPager<ArticleData> mockPager = new CursorPager<>(Arrays.asList(testArticleData), 
          io.spring.application.CursorPager.Direction.PREV, false);
      when(articleQueryService.findRecentArticlesWithCursor(isNull(), isNull(), isNull(), 
          any(CursorPageParameter.class), eq(testUser)))
          .thenReturn(mockPager);

      DataFetcherResult<ArticlesConnection> result = articleDatafetcher.getArticles(
          null, null, 10, "1234567890", null, null, null, dgsDataFetchingEnvironment);

      assertThat(result).isNotNull();
      assertThat(result.getData()).isNotNull();
      assertThat(result.getData().getEdges()).hasSize(1);
    }
  }

  @Test
  void userFavorites_should_handle_first_parameter_edge_case() {
    try (MockedStatic<SecurityUtil> securityUtilMock = Mockito.mockStatic(SecurityUtil.class)) {
      securityUtilMock.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));
      
      when(mockProfile.getUsername()).thenReturn("testuser");
      when(dgsDataFetchingEnvironment.getSource()).thenReturn(mockProfile);
      
      CursorPager<ArticleData> mockPager = new CursorPager<>(Arrays.asList(testArticleData), 
          io.spring.application.CursorPager.Direction.NEXT, false);
      when(articleQueryService.findRecentArticlesWithCursor(isNull(), isNull(), eq("testuser"), 
          any(CursorPageParameter.class), eq(testUser)))
          .thenReturn(mockPager);

      DataFetcherResult<ArticlesConnection> result = articleDatafetcher.userFavorites(10, "1234567890", null, null, dgsDataFetchingEnvironment);

      assertThat(result).isNotNull();
      assertThat(result.getData()).isNotNull();
      assertThat(result.getData().getEdges()).hasSize(1);
    }
  }

  @Test
  void userFavorites_should_handle_last_parameter_edge_case() {
    try (MockedStatic<SecurityUtil> securityUtilMock = Mockito.mockStatic(SecurityUtil.class)) {
      securityUtilMock.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));
      
      when(mockProfile.getUsername()).thenReturn("testuser");
      when(dgsDataFetchingEnvironment.getSource()).thenReturn(mockProfile);
      
      CursorPager<ArticleData> mockPager = new CursorPager<>(Arrays.asList(testArticleData), 
          io.spring.application.CursorPager.Direction.PREV, false);
      when(articleQueryService.findRecentArticlesWithCursor(isNull(), isNull(), eq("testuser"), 
          any(CursorPageParameter.class), eq(testUser)))
          .thenReturn(mockPager);

      DataFetcherResult<ArticlesConnection> result = articleDatafetcher.userFavorites(null, null, 5, "1234567890", dgsDataFetchingEnvironment);

      assertThat(result).isNotNull();
      assertThat(result.getData()).isNotNull();
      assertThat(result.getData().getEdges()).hasSize(1);
    }
  }

  @Test
  void userArticles_should_handle_first_parameter_edge_case() {
    try (MockedStatic<SecurityUtil> securityUtilMock = Mockito.mockStatic(SecurityUtil.class)) {
      securityUtilMock.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));
      
      when(mockProfile.getUsername()).thenReturn("testuser");
      when(dgsDataFetchingEnvironment.getSource()).thenReturn(mockProfile);
      
      CursorPager<ArticleData> mockPager = new CursorPager<>(Arrays.asList(testArticleData), 
          io.spring.application.CursorPager.Direction.NEXT, false);
      when(articleQueryService.findRecentArticlesWithCursor(isNull(), eq("testuser"), isNull(), 
          any(CursorPageParameter.class), eq(testUser)))
          .thenReturn(mockPager);

      DataFetcherResult<ArticlesConnection> result = articleDatafetcher.userArticles(10, "1234567890", null, null, dgsDataFetchingEnvironment);

      assertThat(result).isNotNull();
      assertThat(result.getData()).isNotNull();
      assertThat(result.getData().getEdges()).hasSize(1);
    }
  }

  @Test
  void userArticles_should_handle_last_parameter_edge_case() {
    try (MockedStatic<SecurityUtil> securityUtilMock = Mockito.mockStatic(SecurityUtil.class)) {
      securityUtilMock.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));
      
      when(mockProfile.getUsername()).thenReturn("testuser");
      when(dgsDataFetchingEnvironment.getSource()).thenReturn(mockProfile);
      
      CursorPager<ArticleData> mockPager = new CursorPager<>(Arrays.asList(testArticleData), 
          io.spring.application.CursorPager.Direction.PREV, false);
      when(articleQueryService.findRecentArticlesWithCursor(isNull(), eq("testuser"), isNull(), 
          any(CursorPageParameter.class), eq(testUser)))
          .thenReturn(mockPager);

      DataFetcherResult<ArticlesConnection> result = articleDatafetcher.userArticles(null, null, 5, "1234567890", dgsDataFetchingEnvironment);

      assertThat(result).isNotNull();
      assertThat(result.getData()).isNotNull();
      assertThat(result.getData().getEdges()).hasSize(1);
    }
  }
}
