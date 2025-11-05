package io.spring.application;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import io.spring.application.data.ArticleData;
import io.spring.application.data.ArticleDataList;
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
import java.util.Optional;
import java.util.Set;
import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ArticleQueryServiceTest {

  @Mock private ArticleReadService articleReadService;
  @Mock private UserRelationshipQueryService userRelationshipQueryService;
  @Mock private ArticleFavoritesReadService articleFavoritesReadService;

  private ArticleQueryService articleQueryService;
  private User testUser;
  private ArticleData testArticleData;
  private ProfileData testProfileData;

  @BeforeEach
  public void setUp() {
    articleQueryService = new ArticleQueryService(articleReadService, userRelationshipQueryService, articleFavoritesReadService);
    testUser = new User("test@example.com", "testuser", "password123", "Test bio", "test.jpg");
    testProfileData = new ProfileData("profile-id", "testuser", "Test bio", "test.jpg", false);
    testArticleData = new ArticleData("article-id", "test-slug", "Test Title", "Test Description", "Test Body", 
        false, 0, new DateTime(), new DateTime(), Arrays.asList("tag1", "tag2"), testProfileData);
  }

  @Test
  public void should_find_article_by_slug_successfully() {
    when(articleReadService.findBySlug("test-slug")).thenReturn(testArticleData);

    Optional<ArticleData> result = articleQueryService.findBySlug("test-slug", null);

    assertThat(result.isPresent(), is(true));
    assertThat(result.get().getSlug(), is("test-slug"));
    assertThat(result.get().getTitle(), is("Test Title"));
  }

  @Test
  public void should_find_article_by_slug_with_user_context() {
    when(articleReadService.findBySlug("test-slug")).thenReturn(testArticleData);
    when(articleFavoritesReadService.isUserFavorite(testUser.getId(), "article-id")).thenReturn(true);
    when(articleFavoritesReadService.articleFavoriteCount("article-id")).thenReturn(5);
    when(userRelationshipQueryService.isUserFollowing(testUser.getId(), "profile-id")).thenReturn(true);

    Optional<ArticleData> result = articleQueryService.findBySlug("test-slug", testUser);

    assertThat(result.isPresent(), is(true));
    assertThat(result.get().isFavorited(), is(true));
    assertThat(result.get().getFavoritesCount(), is(5));
    assertThat(result.get().getProfileData().isFollowing(), is(true));
  }

  @Test
  public void should_return_empty_when_article_not_found_by_slug() {
    when(articleReadService.findBySlug("nonexistent-slug")).thenReturn(null);

    Optional<ArticleData> result = articleQueryService.findBySlug("nonexistent-slug", testUser);

    assertThat(result.isPresent(), is(false));
  }

  @Test
  public void should_find_user_feed_with_cursor_successfully() {
    List<String> followedUsers = Arrays.asList("user1", "user2");
    List<ArticleData> articles = Arrays.asList(testArticleData);
    CursorPageParameter<DateTime> pageParam = new CursorPageParameter<>(new DateTime(), 10, CursorPager.Direction.NEXT);

    when(userRelationshipQueryService.followedUsers(testUser.getId())).thenReturn(followedUsers);
    when(articleReadService.findArticlesOfAuthorsWithCursor(followedUsers, pageParam)).thenReturn(articles);
    when(articleFavoritesReadService.articlesFavoriteCount(anyList())).thenReturn(Arrays.asList(new ArticleFavoriteCount("article-id", 3)));
    when(articleFavoritesReadService.userFavorites(anyList(), eq(testUser))).thenReturn(new HashSet<>());
    when(userRelationshipQueryService.followingAuthors(eq(testUser.getId()), anyList())).thenReturn(new HashSet<>());

    CursorPager<ArticleData> result = articleQueryService.findUserFeedWithCursor(testUser, pageParam);

    assertThat(result, notNullValue());
    assertThat(result.getData().size(), is(1));
    assertThat(result.getData().get(0).getId(), is("article-id"));
  }

  @Test
  public void should_return_empty_feed_when_user_follows_no_one() {
    CursorPageParameter<DateTime> pageParam = new CursorPageParameter<>(new DateTime(), 10, CursorPager.Direction.NEXT);

    when(userRelationshipQueryService.followedUsers(testUser.getId())).thenReturn(Collections.emptyList());

    CursorPager<ArticleData> result = articleQueryService.findUserFeedWithCursor(testUser, pageParam);

    assertThat(result, notNullValue());
    assertThat(result.getData().isEmpty(), is(true));
    assertThat(result.hasNext(), is(false));
    assertThat(result.hasPrevious(), is(false));
  }

  @Test
  public void should_handle_feed_with_extra_articles() {
    List<String> followedUsers = Arrays.asList("user1", "user2");
    List<ArticleData> articles = new ArrayList<>(Arrays.asList(testArticleData, testArticleData, testArticleData));
    CursorPageParameter<DateTime> pageParam = new CursorPageParameter<>(new DateTime(), 2, CursorPager.Direction.NEXT);

    when(userRelationshipQueryService.followedUsers(testUser.getId())).thenReturn(followedUsers);
    when(articleReadService.findArticlesOfAuthorsWithCursor(followedUsers, pageParam)).thenReturn(articles);
    when(articleFavoritesReadService.articlesFavoriteCount(anyList())).thenReturn(Arrays.asList(new ArticleFavoriteCount("article-id", 3)));
    when(articleFavoritesReadService.userFavorites(anyList(), eq(testUser))).thenReturn(new HashSet<>());
    when(userRelationshipQueryService.followingAuthors(eq(testUser.getId()), anyList())).thenReturn(new HashSet<>());

    CursorPager<ArticleData> result = articleQueryService.findUserFeedWithCursor(testUser, pageParam);

    assertThat(result, notNullValue());
    assertThat(result.getData().size(), is(2));
    assertThat(result.hasNext(), is(true));
  }

  @Test
  public void should_handle_feed_with_prev_direction() {
    List<String> followedUsers = Arrays.asList("user1", "user2");
    List<ArticleData> articles = new ArrayList<>(Arrays.asList(testArticleData));
    CursorPageParameter<DateTime> pageParam = new CursorPageParameter<>(new DateTime(), 10, CursorPager.Direction.PREV);

    when(userRelationshipQueryService.followedUsers(testUser.getId())).thenReturn(followedUsers);
    when(articleReadService.findArticlesOfAuthorsWithCursor(followedUsers, pageParam)).thenReturn(articles);
    when(articleFavoritesReadService.articlesFavoriteCount(anyList())).thenReturn(Arrays.asList(new ArticleFavoriteCount("article-id", 3)));
    when(articleFavoritesReadService.userFavorites(anyList(), eq(testUser))).thenReturn(new HashSet<>());
    when(userRelationshipQueryService.followingAuthors(eq(testUser.getId()), anyList())).thenReturn(new HashSet<>());

    CursorPager<ArticleData> result = articleQueryService.findUserFeedWithCursor(testUser, pageParam);

    assertThat(result, notNullValue());
    assertThat(result.getData().size(), is(1));
    assertThat(result.hasPrevious(), is(false));
    assertThat(result.hasNext(), is(false));
  }

  @Test
  public void should_find_by_id_successfully() {
    when(articleReadService.findById("article-id")).thenReturn(testArticleData);

    Optional<ArticleData> result = articleQueryService.findById("article-id", null);

    assertThat(result.isPresent(), is(true));
    assertThat(result.get().getId(), is("article-id"));
  }

  @Test
  public void should_find_by_id_with_user_context() {
    when(articleReadService.findById("article-id")).thenReturn(testArticleData);
    when(articleFavoritesReadService.isUserFavorite(testUser.getId(), "article-id")).thenReturn(false);
    when(articleFavoritesReadService.articleFavoriteCount("article-id")).thenReturn(2);
    when(userRelationshipQueryService.isUserFollowing(testUser.getId(), "profile-id")).thenReturn(false);

    Optional<ArticleData> result = articleQueryService.findById("article-id", testUser);

    assertThat(result.isPresent(), is(true));
    assertThat(result.get().isFavorited(), is(false));
    assertThat(result.get().getFavoritesCount(), is(2));
    assertThat(result.get().getProfileData().isFollowing(), is(false));
  }

  @Test
  public void should_return_empty_when_article_not_found_by_id() {
    when(articleReadService.findById("nonexistent-id")).thenReturn(null);

    Optional<ArticleData> result = articleQueryService.findById("nonexistent-id", testUser);

    assertThat(result.isPresent(), is(false));
  }

  @Test
  public void should_find_recent_articles_with_cursor() {
    List<String> articleIds = Arrays.asList("article-id");
    List<ArticleData> articles = Arrays.asList(testArticleData);
    CursorPageParameter<DateTime> pageParam = new CursorPageParameter<>(new DateTime(), 10, CursorPager.Direction.NEXT);

    when(articleReadService.findArticlesWithCursor("tag", "author", "favorited", pageParam)).thenReturn(articleIds);
    when(articleReadService.findArticles(articleIds)).thenReturn(articles);
    when(articleFavoritesReadService.articlesFavoriteCount(anyList())).thenReturn(Arrays.asList(new ArticleFavoriteCount("article-id", 1)));
    when(articleFavoritesReadService.userFavorites(anyList(), eq(testUser))).thenReturn(new HashSet<>());
    when(userRelationshipQueryService.followingAuthors(eq(testUser.getId()), anyList())).thenReturn(new HashSet<>());

    CursorPager<ArticleData> result = articleQueryService.findRecentArticlesWithCursor("tag", "author", "favorited", pageParam, testUser);

    assertThat(result, notNullValue());
    assertThat(result.getData().size(), is(1));
  }

  @Test
  public void should_return_empty_when_no_recent_articles_found() {
    CursorPageParameter<DateTime> pageParam = new CursorPageParameter<>(new DateTime(), 10, CursorPager.Direction.NEXT);

    when(articleReadService.findArticlesWithCursor("tag", "author", "favorited", pageParam)).thenReturn(Collections.emptyList());

    CursorPager<ArticleData> result = articleQueryService.findRecentArticlesWithCursor("tag", "author", "favorited", pageParam, testUser);

    assertThat(result, notNullValue());
    assertThat(result.getData().isEmpty(), is(true));
    assertThat(result.hasNext(), is(false));
    assertThat(result.hasPrevious(), is(false));
  }

  @Test
  public void should_find_recent_articles_with_pagination() {
    List<String> articleIds = Arrays.asList("article-id");
    List<ArticleData> articles = Arrays.asList(testArticleData);
    Page page = new Page(0, 10);

    when(articleReadService.queryArticles("tag", "author", "favorited", page)).thenReturn(articleIds);
    when(articleReadService.countArticle("tag", "author", "favorited")).thenReturn(1);
    when(articleReadService.findArticles(articleIds)).thenReturn(articles);
    when(articleFavoritesReadService.articlesFavoriteCount(anyList())).thenReturn(Arrays.asList(new ArticleFavoriteCount("article-id", 1)));

    ArticleDataList result = articleQueryService.findRecentArticles("tag", "author", "favorited", page, null);

    assertThat(result, notNullValue());
    assertThat(result.getArticleDatas().size(), is(1));
    assertThat(result.getCount(), is(1));
  }

  @Test
  public void should_find_user_feed_with_pagination() {
    List<String> followedUsers = Arrays.asList("user1", "user2");
    List<ArticleData> articles = Arrays.asList(testArticleData);
    Page page = new Page(0, 10);

    when(userRelationshipQueryService.followedUsers(testUser.getId())).thenReturn(followedUsers);
    when(articleReadService.findArticlesOfAuthors(followedUsers, page)).thenReturn(articles);
    when(articleReadService.countFeedSize(followedUsers)).thenReturn(1);
    when(articleFavoritesReadService.articlesFavoriteCount(anyList())).thenReturn(Arrays.asList(new ArticleFavoriteCount("article-id", 1)));
    when(articleFavoritesReadService.userFavorites(anyList(), eq(testUser))).thenReturn(new HashSet<>());
    when(userRelationshipQueryService.followingAuthors(eq(testUser.getId()), anyList())).thenReturn(new HashSet<>());

    ArticleDataList result = articleQueryService.findUserFeed(testUser, page);

    assertThat(result, notNullValue());
    assertThat(result.getArticleDatas().size(), is(1));
    assertThat(result.getCount(), is(1));
  }

  @Test
  public void should_return_empty_user_feed_when_follows_no_one() {
    Page page = new Page(0, 10);

    when(userRelationshipQueryService.followedUsers(testUser.getId())).thenReturn(Collections.emptyList());

    ArticleDataList result = articleQueryService.findUserFeed(testUser, page);

    assertThat(result, notNullValue());
    assertThat(result.getArticleDatas().isEmpty(), is(true));
    assertThat(result.getCount(), is(0));
  }

  @Test
  public void should_set_favorite_status_correctly() {
    when(articleReadService.findById("article-id")).thenReturn(testArticleData);
    when(articleFavoritesReadService.isUserFavorite(testUser.getId(), "article-id")).thenReturn(true);
    when(articleFavoritesReadService.articleFavoriteCount("article-id")).thenReturn(5);
    when(userRelationshipQueryService.isUserFollowing(testUser.getId(), "profile-id")).thenReturn(false);

    Optional<ArticleData> result = articleQueryService.findById("article-id", testUser);

    assertThat(result.isPresent(), is(true));
    assertThat(result.get().isFavorited(), is(true));
    assertThat(result.get().getFavoritesCount(), is(5));
  }

  @Test
  public void should_set_following_status_correctly() {
    Set<String> followingAuthors = new HashSet<>(Arrays.asList("profile-id"));

    when(articleReadService.findById("article-id")).thenReturn(testArticleData);
    when(articleFavoritesReadService.isUserFavorite(testUser.getId(), "article-id")).thenReturn(false);
    when(articleFavoritesReadService.articleFavoriteCount("article-id")).thenReturn(3);
    when(userRelationshipQueryService.isUserFollowing(testUser.getId(), "profile-id")).thenReturn(true);

    Optional<ArticleData> result = articleQueryService.findById("article-id", testUser);

    assertThat(result.isPresent(), is(true));
    assertThat(result.get().getProfileData().isFollowing(), is(true));
  }

  @Test
  public void should_handle_null_user_in_find_recent_articles() {
    List<String> articleIds = Arrays.asList("article-id");
    List<ArticleData> articles = Arrays.asList(testArticleData);
    CursorPageParameter<DateTime> pageParam = new CursorPageParameter<>(new DateTime(), 10, CursorPager.Direction.NEXT);

    when(articleReadService.findArticlesWithCursor("tag", "author", "favorited", pageParam)).thenReturn(articleIds);
    when(articleReadService.findArticles(articleIds)).thenReturn(articles);
    when(articleFavoritesReadService.articlesFavoriteCount(anyList())).thenReturn(Arrays.asList(new ArticleFavoriteCount("article-id", 1)));

    CursorPager<ArticleData> result = articleQueryService.findRecentArticlesWithCursor("tag", "author", "favorited", pageParam, null);

    assertThat(result, notNullValue());
    assertThat(result.getData().size(), is(1));
  }

  @Test
  public void should_handle_reverse_order_for_prev_direction() {
    List<String> followedUsers = Arrays.asList("user1", "user2");
    ArticleData article1 = new ArticleData("article-1", "slug-1", "Title 1", "Desc 1", "Body 1", 
        false, 0, new DateTime(), new DateTime(), Arrays.asList("tag1"), testProfileData);
    ArticleData article2 = new ArticleData("article-2", "slug-2", "Title 2", "Desc 2", "Body 2", 
        false, 0, new DateTime(), new DateTime(), Arrays.asList("tag2"), testProfileData);
    List<ArticleData> articles = new ArrayList<>(Arrays.asList(article1, article2));
    CursorPageParameter<DateTime> pageParam = new CursorPageParameter<>(new DateTime(), 10, CursorPager.Direction.PREV);

    when(userRelationshipQueryService.followedUsers(testUser.getId())).thenReturn(followedUsers);
    when(articleReadService.findArticlesOfAuthorsWithCursor(followedUsers, pageParam)).thenReturn(articles);
    when(articleFavoritesReadService.articlesFavoriteCount(anyList())).thenReturn(Arrays.asList(
        new ArticleFavoriteCount("article-1", 1), new ArticleFavoriteCount("article-2", 2)));
    when(articleFavoritesReadService.userFavorites(anyList(), eq(testUser))).thenReturn(new HashSet<>());
    when(userRelationshipQueryService.followingAuthors(eq(testUser.getId()), anyList())).thenReturn(new HashSet<>());

    CursorPager<ArticleData> result = articleQueryService.findUserFeedWithCursor(testUser, pageParam);

    assertThat(result, notNullValue());
    assertThat(result.getData().size(), is(2));
  }

  @Test
  public void should_handle_empty_article_ids_in_recent_articles_with_cursor() {
    CursorPageParameter<DateTime> pageParam = new CursorPageParameter<>(new DateTime(), 10, CursorPager.Direction.NEXT);

    when(articleReadService.findArticlesWithCursor("tag", "author", "favorited", pageParam)).thenReturn(new ArrayList<>());

    CursorPager<ArticleData> result = articleQueryService.findRecentArticlesWithCursor("tag", "author", "favorited", pageParam, testUser);

    assertThat(result, notNullValue());
    assertThat(result.getData().isEmpty(), is(true));
    assertThat(result.hasNext(), is(false));
    assertThat(result.hasPrevious(), is(false));
  }

  @Test
  public void should_handle_pagination_logic_in_recent_articles_with_cursor() {
    List<String> articleIds = new ArrayList<>(Arrays.asList("article-1", "article-2", "article-3"));
    List<ArticleData> articles = Arrays.asList(testArticleData, testArticleData);
    CursorPageParameter<DateTime> pageParam = new CursorPageParameter<>(new DateTime(), 2, CursorPager.Direction.NEXT);

    when(articleReadService.findArticlesWithCursor("tag", "author", "favorited", pageParam)).thenReturn(articleIds);
    when(articleReadService.findArticles(articleIds)).thenReturn(articles);
    when(articleFavoritesReadService.articlesFavoriteCount(anyList())).thenReturn(Arrays.asList(new ArticleFavoriteCount("article-id", 1)));
    when(articleFavoritesReadService.userFavorites(anyList(), eq(testUser))).thenReturn(new HashSet<>());
    when(userRelationshipQueryService.followingAuthors(eq(testUser.getId()), anyList())).thenReturn(new HashSet<>());

    CursorPager<ArticleData> result = articleQueryService.findRecentArticlesWithCursor("tag", "author", "favorited", pageParam, testUser);

    assertThat(result, notNullValue());
    assertThat(result.getData().size(), is(2));
    assertThat(result.hasNext(), is(true));
  }
}
