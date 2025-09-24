package io.spring.graphql;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.netflix.graphql.dgs.DgsDataFetchingEnvironment;
import graphql.schema.DataFetchingEnvironment;
import graphql.execution.DataFetcherResult;
import io.spring.api.exception.ResourceNotFoundException;
import io.spring.application.ArticleQueryService;
import io.spring.application.CursorPageParameter;
import io.spring.application.CursorPager;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.joda.time.DateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class ArticleDatafetcherTest {

    @Mock
    private ArticleQueryService articleQueryService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DgsDataFetchingEnvironment dgsDataFetchingEnvironment;

    @Mock
    private DataFetchingEnvironment dataFetchingEnvironment;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private ArticleDatafetcher articleDatafetcher;

    private User testUser;
    private ArticleData testArticleData;
    private CursorPager<ArticleData> testCursorPager;
    private Article testArticle;
    private CommentData testCommentData;

    @BeforeEach
    void setUp() {
        testUser = new User("test@example.com", "testuser", "hashedpassword", "Test Bio", "avatar.jpg");
        SecurityContextHolder.setContext(securityContext);
        
        ProfileData profileData = new ProfileData("user1", "testuser", "Test Bio", "avatar.jpg", false);
        testArticleData = new ArticleData(
            "article-1",
            "test-slug",
            "Test Title",
            "Test Description",
            "Test Body",
            false,
            5,
            DateTime.now(),
            DateTime.now(),
            Arrays.asList("java", "spring"),
            profileData
        );

        testCursorPager = new CursorPager<>(
            Arrays.asList(testArticleData),
            CursorPager.Direction.NEXT,
            true
        );
        
        testArticle = new Article("Test Title", "Test Description", "Test Body", Arrays.asList("java"), "testuser");
        testCommentData = new CommentData("comment1", "Test comment", "article1", DateTime.now(), DateTime.now(), profileData);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }



    @Test
    void shouldGetArticleFromPayload() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(testUser);
        Article article = new Article("Test Title", "Test Description", "Test Body", Arrays.asList("java"), "testuser");
        when(dataFetchingEnvironment.getLocalContext()).thenReturn(article);
        when(articleQueryService.findById(eq(article.getId()), eq(testUser)))
            .thenReturn(Optional.of(testArticleData));

        DataFetcherResult<io.spring.graphql.types.Article> result = articleDatafetcher.getArticle(dataFetchingEnvironment);

        assertNotNull(result);
        assertNotNull(result.getData());
        assertEquals("test-slug", result.getData().getSlug());
        verify(articleQueryService).findById(eq(article.getId()), eq(testUser));
    }

    @Test
    void shouldThrowExceptionWhenArticleNotFoundInGetArticle() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(testUser);
        Article article = new Article("Test Title", "Test Description", "Test Body", Arrays.asList("java"), "testuser");
        when(dataFetchingEnvironment.getLocalContext()).thenReturn(article);
        when(articleQueryService.findById(eq(article.getId()), eq(testUser)))
            .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            articleDatafetcher.getArticle(dataFetchingEnvironment);
        });
    }

    @Test
    void shouldGetCommentArticle() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(testUser);
        CommentData commentData = new CommentData("comment1", "Test comment", "article1", DateTime.now(), DateTime.now(), null);
        when(dataFetchingEnvironment.getLocalContext()).thenReturn(commentData);
        when(articleQueryService.findById(eq("article1"), eq(testUser)))
            .thenReturn(Optional.of(testArticleData));

        DataFetcherResult<io.spring.graphql.types.Article> result = articleDatafetcher.getCommentArticle(dataFetchingEnvironment);

        assertNotNull(result);
        assertNotNull(result.getData());
        assertEquals("test-slug", result.getData().getSlug());
        verify(articleQueryService).findById(eq("article1"), eq(testUser));
    }

    @Test
    void shouldFindArticleBySlug() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(testUser);
        when(articleQueryService.findBySlug(eq("test-slug"), eq(testUser)))
            .thenReturn(Optional.of(testArticleData));

        DataFetcherResult<io.spring.graphql.types.Article> result = articleDatafetcher.findArticleBySlug("test-slug");

        assertNotNull(result);
        assertNotNull(result.getData());
        assertEquals("test-slug", result.getData().getSlug());
        verify(articleQueryService).findBySlug(eq("test-slug"), eq(testUser));
    }

    @Test
    void shouldThrowExceptionWhenArticleNotFoundBySlug() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(testUser);
        when(articleQueryService.findBySlug(eq("nonexistent"), eq(testUser)))
            .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            articleDatafetcher.findArticleBySlug("nonexistent");
        });
    }

    @Test
    void shouldHandleNullCurrentUserInGetArticle() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(null);
        when(dataFetchingEnvironment.getLocalContext()).thenReturn(testArticle);
        when(articleQueryService.findById(eq(testArticle.getId()), isNull()))
            .thenReturn(Optional.of(testArticleData));

        DataFetcherResult<io.spring.graphql.types.Article> result = articleDatafetcher.getArticle(dataFetchingEnvironment);

        assertNotNull(result);
        assertNotNull(result.getData());
        assertEquals("test-slug", result.getData().getSlug());
        verify(articleQueryService).findById(eq(testArticle.getId()), isNull());
    }

    @Test
    void shouldGetFeedWithFirstParameter() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(testUser);
        when(articleQueryService.findUserFeedWithCursor(eq(testUser), any(CursorPageParameter.class)))
            .thenReturn(testCursorPager);

        DataFetcherResult<ArticlesConnection> result = articleDatafetcher.getFeed(10, null, null, null, dgsDataFetchingEnvironment);

        assertNotNull(result);
        assertNotNull(result.getData());
        assertEquals(1, result.getData().getEdges().size());
        assertEquals("test-slug", result.getData().getEdges().get(0).getNode().getSlug());
        verify(articleQueryService).findUserFeedWithCursor(eq(testUser), any(CursorPageParameter.class));
    }

    @Test
    void shouldGetFeedWithLastParameter() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(testUser);
        when(articleQueryService.findUserFeedWithCursor(eq(testUser), any(CursorPageParameter.class)))
            .thenReturn(testCursorPager);

        DataFetcherResult<ArticlesConnection> result = articleDatafetcher.getFeed(null, null, 10, "1672531200000", dgsDataFetchingEnvironment);

        assertNotNull(result);
        assertNotNull(result.getData());
        assertEquals(1, result.getData().getEdges().size());
        verify(articleQueryService).findUserFeedWithCursor(eq(testUser), any(CursorPageParameter.class));
    }

    @Test
    void shouldThrowExceptionWhenBothFirstAndLastAreNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            articleDatafetcher.getFeed(null, null, null, null, dgsDataFetchingEnvironment);
        });
    }

    @Test
    void shouldThrowExceptionWhenBothFirstAndLastAreProvided() {
        assertThrows(IllegalArgumentException.class, () -> {
            articleDatafetcher.getFeed(null, null, null, null, dgsDataFetchingEnvironment);
        });
    }

    @Test
    void shouldGetFeedWithNullCurrentUser() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(null);
        when(articleQueryService.findUserFeedWithCursor(isNull(), any(CursorPageParameter.class)))
            .thenReturn(testCursorPager);

        DataFetcherResult<ArticlesConnection> result = articleDatafetcher.getFeed(10, null, null, null, dgsDataFetchingEnvironment);

        assertNotNull(result);
        verify(articleQueryService).findUserFeedWithCursor(isNull(), any(CursorPageParameter.class));
    }

    @Test
    void shouldGetUserFeedWithFirstParameter() {
        Profile profile = Profile.newBuilder().username("testuser").build();
        when(dgsDataFetchingEnvironment.getSource()).thenReturn(profile);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(articleQueryService.findUserFeedWithCursor(eq(testUser), any(CursorPageParameter.class)))
            .thenReturn(testCursorPager);

        DataFetcherResult<ArticlesConnection> result = articleDatafetcher.userFeed(10, null, null, null, dgsDataFetchingEnvironment);

        assertNotNull(result);
        assertNotNull(result.getData());
        assertEquals(1, result.getData().getEdges().size());
        verify(userRepository).findByUsername("testuser");
        verify(articleQueryService).findUserFeedWithCursor(eq(testUser), any(CursorPageParameter.class));
    }

    @Test
    void shouldThrowExceptionWhenUserNotFoundInUserFeed() {
        Profile profile = Profile.newBuilder().username("nonexistent").build();
        when(dgsDataFetchingEnvironment.getSource()).thenReturn(profile);
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            articleDatafetcher.userFeed(10, null, null, null, dgsDataFetchingEnvironment);
        });
    }

    @Test
    void shouldGetUserFavoritesWithFirstParameter() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(testUser);
        Profile profile = Profile.newBuilder().username("testuser").build();
        when(dgsDataFetchingEnvironment.getSource()).thenReturn(profile);
        when(articleQueryService.findRecentArticlesWithCursor(isNull(), isNull(), eq("testuser"), any(CursorPageParameter.class), eq(testUser)))
            .thenReturn(testCursorPager);

        DataFetcherResult<ArticlesConnection> result = articleDatafetcher.userFavorites(10, null, null, null, dgsDataFetchingEnvironment);

        assertNotNull(result);
        assertNotNull(result.getData());
        assertEquals(1, result.getData().getEdges().size());
        verify(articleQueryService).findRecentArticlesWithCursor(isNull(), isNull(), eq("testuser"), any(CursorPageParameter.class), eq(testUser));
    }

    @Test
    void shouldGetUserFavoritesWithLastParameter() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(testUser);
        Profile profile = Profile.newBuilder().username("testuser").build();
        when(dgsDataFetchingEnvironment.getSource()).thenReturn(profile);
        when(articleQueryService.findRecentArticlesWithCursor(isNull(), isNull(), eq("testuser"), any(CursorPageParameter.class), eq(testUser)))
            .thenReturn(testCursorPager);

        DataFetcherResult<ArticlesConnection> result = articleDatafetcher.userFavorites(null, null, 10, "1672531200000", dgsDataFetchingEnvironment);

        assertNotNull(result);
        verify(articleQueryService).findRecentArticlesWithCursor(isNull(), isNull(), eq("testuser"), any(CursorPageParameter.class), eq(testUser));
    }

    @Test
    void shouldGetUserArticlesWithFirstParameter() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(testUser);
        Profile profile = Profile.newBuilder().username("testuser").build();
        when(dgsDataFetchingEnvironment.getSource()).thenReturn(profile);
        when(articleQueryService.findRecentArticlesWithCursor(isNull(), eq("testuser"), isNull(), any(CursorPageParameter.class), eq(testUser)))
            .thenReturn(testCursorPager);

        DataFetcherResult<ArticlesConnection> result = articleDatafetcher.userArticles(10, null, null, null, dgsDataFetchingEnvironment);

        assertNotNull(result);
        assertNotNull(result.getData());
        assertEquals(1, result.getData().getEdges().size());
        verify(articleQueryService).findRecentArticlesWithCursor(isNull(), eq("testuser"), isNull(), any(CursorPageParameter.class), eq(testUser));
    }

    @Test
    void shouldGetArticlesWithAllFilters() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(testUser);
        when(articleQueryService.findRecentArticlesWithCursor(eq("java"), eq("author"), eq("favoriter"), any(CursorPageParameter.class), eq(testUser)))
            .thenReturn(testCursorPager);

        DataFetcherResult<ArticlesConnection> result = articleDatafetcher.getArticles(10, null, null, null, "author", "favoriter", "java", dgsDataFetchingEnvironment);

        assertNotNull(result);
        assertNotNull(result.getData());
        assertEquals(1, result.getData().getEdges().size());
        verify(articleQueryService).findRecentArticlesWithCursor(eq("java"), eq("author"), eq("favoriter"), any(CursorPageParameter.class), eq(testUser));
    }

    @Test
    void shouldGetArticlesWithLastParameter() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(testUser);
        when(articleQueryService.findRecentArticlesWithCursor(isNull(), isNull(), isNull(), any(CursorPageParameter.class), eq(testUser)))
            .thenReturn(testCursorPager);

        DataFetcherResult<ArticlesConnection> result = articleDatafetcher.getArticles(null, null, 10, "1672531200000", null, null, null, dgsDataFetchingEnvironment);

        assertNotNull(result);
        verify(articleQueryService).findRecentArticlesWithCursor(isNull(), isNull(), isNull(), any(CursorPageParameter.class), eq(testUser));
    }

    @Test
    void shouldHandleEmptyArticlesList() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(testUser);
        CursorPager<ArticleData> emptyCursorPager = new CursorPager<>(
            Collections.emptyList(),
            CursorPager.Direction.NEXT,
            false
        );
        when(articleQueryService.findUserFeedWithCursor(eq(testUser), any(CursorPageParameter.class)))
            .thenReturn(emptyCursorPager);

        DataFetcherResult<ArticlesConnection> result = articleDatafetcher.getFeed(10, null, null, null, dgsDataFetchingEnvironment);

        assertNotNull(result);
        assertNotNull(result.getData());
        assertTrue(result.getData().getEdges().isEmpty());
        assertNotNull(result.getData().getPageInfo());
    }

    @Test
    void shouldHandleCursorParsing() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(testUser);
        when(articleQueryService.findUserFeedWithCursor(eq(testUser), any(CursorPageParameter.class)))
            .thenReturn(testCursorPager);

        DataFetcherResult<ArticlesConnection> result = articleDatafetcher.getFeed(10, "1672531200000", null, null, dgsDataFetchingEnvironment);

        assertNotNull(result);
        verify(articleQueryService).findUserFeedWithCursor(eq(testUser), any(CursorPageParameter.class));
    }

    @Test
    void shouldBuildCorrectPageInfo() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(testUser);
        
        CursorPager<ArticleData> pagerWithPagination = new CursorPager<>(
            Arrays.asList(testArticleData),
            CursorPager.Direction.NEXT,
            true
        );
        when(articleQueryService.findUserFeedWithCursor(eq(testUser), any(CursorPageParameter.class)))
            .thenReturn(pagerWithPagination);

        DataFetcherResult<ArticlesConnection> result = articleDatafetcher.getFeed(10, null, null, null, dgsDataFetchingEnvironment);

        assertNotNull(result);
        assertNotNull(result.getData().getPageInfo());
        assertNotNull(result.getData().getPageInfo().getEndCursor());
    }

    @Test
    void shouldBuildCorrectLocalContext() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(testUser);
        when(articleQueryService.findUserFeedWithCursor(eq(testUser), any(CursorPageParameter.class)))
            .thenReturn(testCursorPager);

        DataFetcherResult<ArticlesConnection> result = articleDatafetcher.getFeed(10, null, null, null, dgsDataFetchingEnvironment);

        assertNotNull(result);
        assertNotNull(result.getLocalContext());
        Map<String, ArticleData> localContext = (Map<String, ArticleData>) result.getLocalContext();
        assertTrue(localContext.containsKey("test-slug"));
        assertEquals(testArticleData, localContext.get("test-slug"));
    }
}
