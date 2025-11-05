package io.spring.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.spring.application.data.ArticleData;
import io.spring.application.data.ArticleDataList;
import io.spring.application.data.ProfileData;
import io.spring.core.user.User;
import io.spring.infrastructure.mybatis.readservice.ArticleReadService;
import io.spring.infrastructure.mybatis.readservice.ArticleFavoritesReadService;
import io.spring.infrastructure.mybatis.readservice.UserRelationshipQueryService;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ArticleQueryServiceEnhancedTest {

  @Mock
  private ArticleReadService articleReadService;

  @Mock
  private ArticleFavoritesReadService articleFavoritesReadService;

  @Mock
  private UserRelationshipQueryService userRelationshipQueryService;

  @InjectMocks
  private ArticleQueryService articleQueryService;

  @Test
  public void findRecentArticles_should_handle_null_parameters() {
    when(articleReadService.queryArticles(isNull(), isNull(), isNull(), any(Page.class)))
        .thenReturn(Collections.emptyList());
    when(articleReadService.countArticle(isNull(), isNull(), isNull()))
        .thenReturn(0);

    ArticleDataList result = articleQueryService.findRecentArticles(
        null, null, null, new Page(0, 20), null);

    assertThat(result).isNotNull();
    assertThat(result.getArticleDatas()).isEmpty();
    assertThat(result.getCount()).isEqualTo(0);
    verify(articleReadService).queryArticles(isNull(), isNull(), isNull(), any(Page.class));
    verify(articleReadService).countArticle(isNull(), isNull(), isNull());
  }

  @Test
  public void findRecentArticles_should_handle_empty_string_parameters() {
    when(articleReadService.queryArticles(eq(""), eq(""), eq(""), any(Page.class)))
        .thenReturn(Collections.emptyList());
    when(articleReadService.countArticle(eq(""), eq(""), eq("")))
        .thenReturn(0);

    ArticleDataList result = articleQueryService.findRecentArticles(
        "", "", "", new Page(0, 20), null);

    assertThat(result).isNotNull();
    assertThat(result.getArticleDatas()).isEmpty();
    assertThat(result.getCount()).isEqualTo(0);
  }

  @Test
  public void findRecentArticles_should_handle_whitespace_parameters() {
    when(articleReadService.queryArticles(eq("   "), eq("   "), eq("   "), any(Page.class)))
        .thenReturn(Collections.emptyList());
    when(articleReadService.countArticle(eq("   "), eq("   "), eq("   ")))
        .thenReturn(0);

    ArticleDataList result = articleQueryService.findRecentArticles(
        "   ", "   ", "   ", new Page(0, 20), null);

    assertThat(result).isNotNull();
    assertThat(result.getArticleDatas()).isEmpty();
    assertThat(result.getCount()).isEqualTo(0);
  }

  @Test
  public void findRecentArticles_should_handle_special_characters_in_parameters() {
    String specialTag = "java-spring@2023";
    String specialAuthor = "user_name-123";
    String specialFavoriter = "favoriter@domain.com";

    when(articleReadService.queryArticles(eq(specialTag), eq(specialAuthor), eq(specialFavoriter), any(Page.class)))
        .thenReturn(Collections.emptyList());
    when(articleReadService.countArticle(eq(specialTag), eq(specialAuthor), eq(specialFavoriter)))
        .thenReturn(0);

    ArticleDataList result = articleQueryService.findRecentArticles(
        specialTag, specialAuthor, specialFavoriter, new Page(0, 20), null);

    assertThat(result).isNotNull();
    assertThat(result.getArticleDatas()).isEmpty();
    assertThat(result.getCount()).isEqualTo(0);
  }

  @Test
  public void findRecentArticles_should_handle_unicode_parameters() {
    String unicodeTag = "编程";
    String unicodeAuthor = "用户名";
    String unicodeFavoriter = "收藏者";

    when(articleReadService.queryArticles(eq(unicodeTag), eq(unicodeAuthor), eq(unicodeFavoriter), any(Page.class)))
        .thenReturn(Collections.emptyList());
    when(articleReadService.countArticle(eq(unicodeTag), eq(unicodeAuthor), eq(unicodeFavoriter)))
        .thenReturn(0);

    ArticleDataList result = articleQueryService.findRecentArticles(
        unicodeTag, unicodeAuthor, unicodeFavoriter, new Page(0, 20), null);

    assertThat(result).isNotNull();
    assertThat(result.getArticleDatas()).isEmpty();
    assertThat(result.getCount()).isEqualTo(0);
  }

  @Test
  public void findRecentArticles_should_handle_large_page_size() {
    Page largePage = new Page(0, 1000);
    
    when(articleReadService.queryArticles(isNull(), isNull(), isNull(), eq(largePage)))
        .thenReturn(Collections.emptyList());
    when(articleReadService.countArticle(isNull(), isNull(), isNull()))
        .thenReturn(0);

    ArticleDataList result = articleQueryService.findRecentArticles(null, null, null, largePage, null);

    assertThat(result).isNotNull();
    assertThat(result.getArticleDatas()).isEmpty();
    assertThat(result.getCount()).isEqualTo(0);
  }

  @Test
  public void findRecentArticles_should_handle_zero_page_size() {
    Page zeroPage = new Page(0, 0);
    
    when(articleReadService.queryArticles(isNull(), isNull(), isNull(), eq(zeroPage)))
        .thenReturn(Collections.emptyList());
    when(articleReadService.countArticle(isNull(), isNull(), isNull()))
        .thenReturn(0);

    ArticleDataList result = articleQueryService.findRecentArticles(null, null, null, zeroPage, null);

    assertThat(result).isNotNull();
    assertThat(result.getArticleDatas()).isEmpty();
    assertThat(result.getCount()).isEqualTo(0);
  }

  @Test
  public void findRecentArticlesWithCursor_should_handle_null_user() {
    CursorPageParameter<DateTime> pageParam = new CursorPageParameter<>(null, 20, CursorPager.Direction.NEXT);
    
    when(articleReadService.findArticlesWithCursor(isNull(), isNull(), isNull(), eq(pageParam)))
        .thenReturn(Collections.emptyList());

    CursorPager<ArticleData> result = articleQueryService.findRecentArticlesWithCursor(
        null, null, null, pageParam, null);

    assertThat(result).isNotNull();
    assertThat(result.getData()).isEmpty();
    verify(articleReadService).findArticlesWithCursor(isNull(), isNull(), isNull(), eq(pageParam));
  }

  @Test
  public void findRecentArticlesWithCursor_should_handle_authenticated_user() {
    CursorPageParameter<DateTime> pageParam = new CursorPageParameter<>(null, 20, CursorPager.Direction.NEXT);
    User mockUser = new User("test@example.com", "testuser", "password", "bio", "image");
    
    when(articleReadService.findArticlesWithCursor(isNull(), isNull(), isNull(), eq(pageParam)))
        .thenReturn(Collections.emptyList());

    CursorPager<ArticleData> result = articleQueryService.findRecentArticlesWithCursor(
        null, null, null, pageParam, mockUser);

    assertThat(result).isNotNull();
    assertThat(result.getData()).isEmpty();
    verify(articleReadService).findArticlesWithCursor(isNull(), isNull(), isNull(), eq(pageParam));
  }

  @Test
  public void findUserFeedWithCursor_should_throw_exception_when_user_is_null() {
    CursorPageParameter<DateTime> pageParam = new CursorPageParameter<>(null, 20, CursorPager.Direction.NEXT);
    
    assertThrows(NullPointerException.class, () -> 
        articleQueryService.findUserFeedWithCursor(null, pageParam));
  }

  @Test
  public void findUserFeedWithCursor_should_handle_authenticated_user() {
    User mockUser = createMockUser();
    CursorPageParameter<DateTime> pageParam = new CursorPageParameter<>(null, 20, CursorPager.Direction.NEXT);
    
    when(userRelationshipQueryService.followedUsers(eq("user-id")))
        .thenReturn(Arrays.asList("followed-user-1"));
    when(articleReadService.findArticlesOfAuthorsWithCursor(any(List.class), eq(pageParam)))
        .thenReturn(Collections.emptyList());

    CursorPager<ArticleData> result = articleQueryService.findUserFeedWithCursor(mockUser, pageParam);

    assertThat(result).isNotNull();
    assertThat(result.getData()).isEmpty();
    verify(userRelationshipQueryService).followedUsers(eq("user-id"));
  }

  @Test
  public void findById_should_handle_null_user() {
    String articleId = "article-123";
    ArticleData mockArticleData = createMockArticleData();
    
    when(articleReadService.findById(eq(articleId)))
        .thenReturn(mockArticleData);

    Optional<ArticleData> result = articleQueryService.findById(articleId, null);

    assertThat(result).isPresent();
    assertThat(result.get()).isEqualTo(mockArticleData);
    verify(articleReadService).findById(eq(articleId));
  }

  @Test
  public void findById_should_handle_authenticated_user() {
    String articleId = "article-123";
    User mockUser = createMockUser();
    ArticleData mockArticleData = createMockArticleData();
    
    when(articleReadService.findById(eq(articleId)))
        .thenReturn(mockArticleData);
    when(articleFavoritesReadService.isUserFavorite(eq("user-id"), eq(articleId)))
        .thenReturn(false);
    when(articleFavoritesReadService.articleFavoriteCount(eq(articleId)))
        .thenReturn(0);
    when(userRelationshipQueryService.isUserFollowing(eq("user-id"), eq("user-id")))
        .thenReturn(false);

    Optional<ArticleData> result = articleQueryService.findById(articleId, mockUser);

    assertThat(result).isPresent();
    assertThat(result.get()).isEqualTo(mockArticleData);
    verify(articleReadService).findById(eq(articleId));
  }

  @Test
  public void findById_should_return_empty_when_article_not_found() {
    String articleId = "nonexistent-article";
    
    when(articleReadService.findById(eq(articleId)))
        .thenReturn(null);

    Optional<ArticleData> result = articleQueryService.findById(articleId, null);

    assertThat(result).isEmpty();
    verify(articleReadService).findById(eq(articleId));
  }

  @Test
  public void findBySlug_should_handle_null_user() {
    String slug = "test-article-slug";
    ArticleData mockArticleData = createMockArticleData();
    
    when(articleReadService.findBySlug(eq(slug)))
        .thenReturn(mockArticleData);

    Optional<ArticleData> result = articleQueryService.findBySlug(slug, null);

    assertThat(result).isPresent();
    assertThat(result.get()).isEqualTo(mockArticleData);
    verify(articleReadService).findBySlug(eq(slug));
  }

  @Test
  public void findBySlug_should_handle_authenticated_user() {
    String slug = "test-article-slug";
    User mockUser = createMockUser();
    ArticleData mockArticleData = createMockArticleData();
    
    when(articleReadService.findBySlug(eq(slug)))
        .thenReturn(mockArticleData);
    when(articleFavoritesReadService.isUserFavorite(eq("user-id"), eq("article-id")))
        .thenReturn(false);
    when(articleFavoritesReadService.articleFavoriteCount(eq("article-id")))
        .thenReturn(0);
    when(userRelationshipQueryService.isUserFollowing(eq("user-id"), eq("user-id")))
        .thenReturn(false);

    Optional<ArticleData> result = articleQueryService.findBySlug(slug, mockUser);

    assertThat(result).isPresent();
    assertThat(result.get()).isEqualTo(mockArticleData);
    verify(articleReadService).findBySlug(eq(slug));
  }

  @Test
  public void findBySlug_should_return_empty_when_article_not_found() {
    String slug = "nonexistent-slug";
    
    when(articleReadService.findBySlug(eq(slug)))
        .thenReturn(null);

    Optional<ArticleData> result = articleQueryService.findBySlug(slug, null);

    assertThat(result).isEmpty();
    verify(articleReadService).findBySlug(eq(slug));
  }

  @Test
  public void findBySlug_should_handle_special_characters_in_slug() {
    String specialSlug = "test-article-123_with@special.chars";
    ArticleData mockArticleData = createMockArticleData();
    
    when(articleReadService.findBySlug(eq(specialSlug)))
        .thenReturn(mockArticleData);

    Optional<ArticleData> result = articleQueryService.findBySlug(specialSlug, null);

    assertThat(result).isPresent();
    assertThat(result.get()).isEqualTo(mockArticleData);
  }

  @Test
  public void findBySlug_should_handle_unicode_in_slug() {
    String unicodeSlug = "测试文章-slug";
    ArticleData mockArticleData = createMockArticleData();
    
    when(articleReadService.findBySlug(eq(unicodeSlug)))
        .thenReturn(mockArticleData);

    Optional<ArticleData> result = articleQueryService.findBySlug(unicodeSlug, null);

    assertThat(result).isPresent();
    assertThat(result.get()).isEqualTo(mockArticleData);
  }

  private CursorPager<ArticleData> createMockCursorPager() {
    CursorPager<ArticleData> pager = mock(CursorPager.class);
    when(pager.getData()).thenReturn(Collections.emptyList());
    when(pager.getStartCursor()).thenReturn(null);
    when(pager.getEndCursor()).thenReturn(null);
    when(pager.hasPrevious()).thenReturn(false);
    when(pager.hasNext()).thenReturn(false);
    return pager;
  }

  private User createMockUser() {
    User user = mock(User.class);
    when(user.getId()).thenReturn("user-id");
    return user;
  }

  private ArticleData createMockArticleData() {
    ProfileData profileData = new ProfileData("user-id", "testuser", "Test Bio", "image.jpg", false);
    return new ArticleData(
        "article-id", "test-slug", "Test Title", "Test Description", "Test Body",
        false, 0, DateTime.now(), DateTime.now(), Arrays.asList("tag1"), profileData);
  }
}
