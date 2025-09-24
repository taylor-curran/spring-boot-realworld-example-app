package io.spring.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import io.spring.application.data.ArticleData;
import io.spring.application.data.ArticleFavoriteCount;
import io.spring.application.data.ProfileData;
import io.spring.core.user.User;
import io.spring.infrastructure.mybatis.readservice.ArticleFavoritesReadService;
import io.spring.infrastructure.mybatis.readservice.ArticleReadService;
import io.spring.infrastructure.mybatis.readservice.UserRelationshipQueryService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ArticleQueryServiceMissingCoverageTest {

  @Mock private ArticleReadService articleReadService;
  @Mock private UserRelationshipQueryService userRelationshipQueryService;
  @Mock private ArticleFavoritesReadService articleFavoritesReadService;

  private ArticleQueryService articleQueryService;
  private User testUser;
  private ArticleData testArticleData;

  @BeforeEach
  void setUp() {
    articleQueryService =
        new ArticleQueryService(
            articleReadService, userRelationshipQueryService, articleFavoritesReadService);
    testUser = new User("test@example.com", "testuser", "123", "", "");

    ProfileData profileData = new ProfileData("profile-id", "testuser", "bio", "image.jpg", false);
    testArticleData =
        new ArticleData(
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
  void findUserFeedWithCursor_should_handle_next_direction_with_cursor() {
    DateTime cursorTime = DateTime.now().minusHours(1);
    CursorPageParameter pageParam =
        new CursorPageParameter(new DateTimeCursor(cursorTime), 10, CursorPager.Direction.NEXT);

    List<ArticleData> articles = new ArrayList<>(Arrays.asList(testArticleData));
    when(userRelationshipQueryService.followedUsers(testUser.getId()))
        .thenReturn(Arrays.asList("author1", "author2"));
    when(articleReadService.findArticlesOfAuthorsWithCursor(
            any(List.class), any(CursorPageParameter.class)))
        .thenReturn(articles);
    when(articleFavoritesReadService.articlesFavoriteCount(any(List.class)))
        .thenReturn(Arrays.asList(new ArticleFavoriteCount("article-id", 5)));
    when(userRelationshipQueryService.followingAuthors(any(String.class), any(List.class)))
        .thenReturn(new HashSet<>());
    when(articleFavoritesReadService.userFavorites(any(List.class), any(User.class)))
        .thenReturn(new HashSet<>());

    CursorPager<ArticleData> result =
        articleQueryService.findUserFeedWithCursor(testUser, pageParam);

    assertThat(result).isNotNull();
    assertThat(result.getData()).hasSize(1);
  }

  @Test
  void findUserFeedWithCursor_should_handle_prev_direction_with_cursor() {
    DateTime cursorTime = DateTime.now().minusHours(1);
    CursorPageParameter pageParam =
        new CursorPageParameter(new DateTimeCursor(cursorTime), 10, CursorPager.Direction.PREV);

    List<ArticleData> articles = new ArrayList<>(Arrays.asList(testArticleData));
    when(userRelationshipQueryService.followedUsers(testUser.getId()))
        .thenReturn(Arrays.asList("author1", "author2"));
    when(articleReadService.findArticlesOfAuthorsWithCursor(
            any(List.class), any(CursorPageParameter.class)))
        .thenReturn(articles);
    when(articleFavoritesReadService.articlesFavoriteCount(any(List.class)))
        .thenReturn(Arrays.asList(new ArticleFavoriteCount("article-id", 5)));
    when(userRelationshipQueryService.followingAuthors(any(String.class), any(List.class)))
        .thenReturn(new HashSet<>());
    when(articleFavoritesReadService.userFavorites(any(List.class), any(User.class)))
        .thenReturn(new HashSet<>());

    CursorPager<ArticleData> result =
        articleQueryService.findUserFeedWithCursor(testUser, pageParam);

    assertThat(result).isNotNull();
    assertThat(result.getData()).hasSize(1);
  }

  @Test
  void findUserFeedWithCursor_should_handle_empty_followed_users() {
    CursorPageParameter pageParam = new CursorPageParameter(null, 10, CursorPager.Direction.NEXT);

    when(userRelationshipQueryService.followedUsers(testUser.getId()))
        .thenReturn(Collections.emptyList());

    CursorPager<ArticleData> result =
        articleQueryService.findUserFeedWithCursor(testUser, pageParam);

    assertThat(result).isNotNull();
    assertThat(result.getData()).isEmpty();
    assertThat(result.hasNext()).isFalse();
  }

  @Test
  void findRecentArticlesWithCursor_should_handle_next_direction_with_all_filters() {
    DateTime cursorTime = DateTime.now().minusHours(1);
    CursorPageParameter pageParam =
        new CursorPageParameter(new DateTimeCursor(cursorTime), 10, CursorPager.Direction.NEXT);

    List<String> articleIds = Arrays.asList("article-1", "article-2");
    List<ArticleData> articles = Arrays.asList(testArticleData);
    when(articleReadService.findArticlesWithCursor(
            eq("java"), eq("author"), eq("favorited"), any(CursorPageParameter.class)))
        .thenReturn(articleIds);
    when(articleReadService.findArticles(articleIds)).thenReturn(articles);
    when(articleFavoritesReadService.articlesFavoriteCount(any(List.class)))
        .thenReturn(Arrays.asList(new ArticleFavoriteCount("article-id", 5)));
    when(userRelationshipQueryService.followingAuthors(any(String.class), any(List.class)))
        .thenReturn(new HashSet<>());
    when(articleFavoritesReadService.userFavorites(any(List.class), any(User.class)))
        .thenReturn(new HashSet<>());

    CursorPager<ArticleData> result =
        articleQueryService.findRecentArticlesWithCursor(
            "java", "author", "favorited", pageParam, testUser);

    assertThat(result).isNotNull();
    assertThat(result.getData()).hasSize(1);
  }

  @Test
  void findRecentArticlesWithCursor_should_handle_prev_direction_with_filters() {
    DateTime cursorTime = DateTime.now().minusHours(1);
    CursorPageParameter pageParam =
        new CursorPageParameter(new DateTimeCursor(cursorTime), 10, CursorPager.Direction.PREV);

    List<String> articleIds = Arrays.asList("article-1", "article-2");
    List<ArticleData> articles = Arrays.asList(testArticleData);
    when(articleReadService.findArticlesWithCursor(
            eq("spring"), eq("testuser"), eq(null), any(CursorPageParameter.class)))
        .thenReturn(articleIds);
    when(articleReadService.findArticles(articleIds)).thenReturn(articles);
    when(articleFavoritesReadService.articlesFavoriteCount(any(List.class)))
        .thenReturn(Arrays.asList(new ArticleFavoriteCount("article-id", 5)));
    when(userRelationshipQueryService.followingAuthors(any(String.class), any(List.class)))
        .thenReturn(new HashSet<>());
    when(articleFavoritesReadService.userFavorites(any(List.class), any(User.class)))
        .thenReturn(new HashSet<>());

    CursorPager<ArticleData> result =
        articleQueryService.findRecentArticlesWithCursor(
            "spring", "testuser", null, pageParam, testUser);

    assertThat(result).isNotNull();
    assertThat(result.getData()).hasSize(1);
  }

  @Test
  void findRecentArticlesWithCursor_should_handle_empty_results() {
    CursorPageParameter pageParam = new CursorPageParameter(null, 10, CursorPager.Direction.NEXT);

    when(articleReadService.findArticlesWithCursor(
            any(), any(), any(), any(CursorPageParameter.class)))
        .thenReturn(Collections.emptyList());

    CursorPager<ArticleData> result =
        articleQueryService.findRecentArticlesWithCursor(null, null, null, pageParam, testUser);

    assertThat(result).isNotNull();
    assertThat(result.getData()).isEmpty();
    assertThat(result.hasNext()).isFalse();
  }

  @Test
  void findRecentArticlesWithCursor_should_handle_hasExtra_true_case() {
    DateTime cursorTime = DateTime.now().minusHours(1);
    CursorPageParameter pageParam =
        new CursorPageParameter(new DateTimeCursor(cursorTime), 2, CursorPager.Direction.NEXT);

    List<String> articleIds = new ArrayList<>(Arrays.asList("article-1", "article-2", "article-3"));
    List<ArticleData> articles = Arrays.asList(testArticleData, testArticleData);
    when(articleReadService.findArticlesWithCursor(
            any(), any(), any(), any(CursorPageParameter.class)))
        .thenReturn(articleIds);
    when(articleReadService.findArticles(any(List.class))).thenReturn(articles);
    when(articleFavoritesReadService.articlesFavoriteCount(any(List.class)))
        .thenReturn(Arrays.asList(new ArticleFavoriteCount("article-id", 5)));
    when(userRelationshipQueryService.followingAuthors(any(String.class), any(List.class)))
        .thenReturn(new HashSet<>());
    when(articleFavoritesReadService.userFavorites(any(List.class), any(User.class)))
        .thenReturn(new HashSet<>());

    CursorPager<ArticleData> result =
        articleQueryService.findRecentArticlesWithCursor(null, null, null, pageParam, testUser);

    assertThat(result).isNotNull();
    assertThat(result.hasNext()).isTrue();
  }

  @Test
  void findUserFeedWithCursor_should_handle_hasExtra_true_case() {
    DateTime cursorTime = DateTime.now().minusHours(1);
    CursorPageParameter pageParam =
        new CursorPageParameter(new DateTimeCursor(cursorTime), 2, CursorPager.Direction.NEXT);

    List<ArticleData> articles =
        new ArrayList<>(Arrays.asList(testArticleData, testArticleData, testArticleData));
    when(userRelationshipQueryService.followedUsers(testUser.getId()))
        .thenReturn(Arrays.asList("author1", "author2"));
    when(articleReadService.findArticlesOfAuthorsWithCursor(
            any(List.class), any(CursorPageParameter.class)))
        .thenReturn(articles);
    when(articleFavoritesReadService.articlesFavoriteCount(any(List.class)))
        .thenReturn(Arrays.asList(new ArticleFavoriteCount("article-id", 5)));
    when(userRelationshipQueryService.followingAuthors(any(String.class), any(List.class)))
        .thenReturn(new HashSet<>());
    when(articleFavoritesReadService.userFavorites(any(List.class), any(User.class)))
        .thenReturn(new HashSet<>());

    CursorPager<ArticleData> result =
        articleQueryService.findUserFeedWithCursor(testUser, pageParam);

    assertThat(result).isNotNull();
    assertThat(result.hasNext()).isTrue();
  }
}
