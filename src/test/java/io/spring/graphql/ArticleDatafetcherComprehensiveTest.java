package io.spring.graphql;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.netflix.graphql.dgs.DgsDataFetchingEnvironment;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import io.spring.api.exception.ResourceNotFoundException;
import io.spring.application.ArticleQueryService;
import io.spring.application.CursorPageParameter;
import io.spring.application.CursorPager;
import io.spring.application.DateTimeCursor;
import io.spring.application.data.ArticleData;
import io.spring.application.data.CommentData;
import io.spring.application.data.ProfileData;
import io.spring.core.user.User;
import io.spring.core.user.UserRepository;
import io.spring.graphql.types.Article;
import io.spring.graphql.types.ArticlesConnection;
import io.spring.graphql.types.Profile;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ArticleDatafetcherComprehensiveTest {

    @Mock
    private ArticleQueryService articleQueryService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DgsDataFetchingEnvironment dgsDataFetchingEnvironment;

    @Mock
    private DataFetchingEnvironment dataFetchingEnvironment;

    @InjectMocks
    private ArticleDatafetcher articleDatafetcher;

    private User testUser;
    private ArticleData articleData;
    private CursorPager<ArticleData> cursorPager;
    private Profile profile;
    private CommentData commentData;
    private io.spring.core.article.Article coreArticle;

    @BeforeEach
    void setUp() {
        testUser = new User("test@example.com", "testuser", "password", "Test Bio", "test.jpg");
        
        ProfileData profileData = new ProfileData(testUser.getId(), "testuser", "Test Bio", "test.jpg", false);
        articleData = new ArticleData(
            "article-id",
            "test-slug",
            "Test Title",
            "Test Description",
            "Test Body",
            true,
            5,
            new DateTime(),
            new DateTime(),
            Arrays.asList("tag1", "tag2"),
            profileData
        );

        cursorPager = new CursorPager<>(
            Arrays.asList(articleData),
            CursorPager.Direction.NEXT,
            true
        );

        profile = Profile.newBuilder()
            .username("testuser")
            .bio("Test Bio")
            .image("test.jpg")
            .following(false)
            .build();

        commentData = new CommentData(
            "comment-id",
            "Test comment",
            "article-id",
            new DateTime(),
            new DateTime(),
            profileData
        );

        coreArticle = new io.spring.core.article.Article(
            "Test Title",
            "Test Description", 
            "Test Body",
            Arrays.asList("tag1", "tag2"),
            testUser.getId()
        );
    }

    @Test
    void getFeed_shouldReturnArticlesWithFirstParameter() {
        try (MockedStatic<SecurityUtil> securityUtilMock = mockStatic(SecurityUtil.class)) {
            securityUtilMock.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));
            when(articleQueryService.findUserFeedWithCursor(eq(testUser), any(CursorPageParameter.class)))
                .thenReturn(cursorPager);

            DataFetcherResult<ArticlesConnection> result = articleDatafetcher.getFeed(
                10, "1640995200000", null, null, dgsDataFetchingEnvironment);

            assertNotNull(result);
            assertNotNull(result.getData());
            assertEquals(1, result.getData().getEdges().size());
            assertEquals("test-slug", result.getData().getEdges().get(0).getNode().getSlug());
            verify(articleQueryService).findUserFeedWithCursor(eq(testUser), any(CursorPageParameter.class));
        }
    }

    @Test
    void getFeed_shouldReturnArticlesWithLastParameter() {
        try (MockedStatic<SecurityUtil> securityUtilMock = mockStatic(SecurityUtil.class)) {
            securityUtilMock.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));
            when(articleQueryService.findUserFeedWithCursor(eq(testUser), any(CursorPageParameter.class)))
                .thenReturn(cursorPager);

            DataFetcherResult<ArticlesConnection> result = articleDatafetcher.getFeed(
                null, null, 10, "1640995200000", dgsDataFetchingEnvironment);

            assertNotNull(result);
            assertNotNull(result.getData());
            assertEquals(1, result.getData().getEdges().size());
            verify(articleQueryService).findUserFeedWithCursor(eq(testUser), any(CursorPageParameter.class));
        }
    }

    @Test
    void getFeed_shouldThrowExceptionWhenBothFirstAndLastAreNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            articleDatafetcher.getFeed(null, null, null, null, dgsDataFetchingEnvironment);
        });
    }

    @Test
    void getFeed_shouldWorkWithAnonymousUser() {
        try (MockedStatic<SecurityUtil> securityUtilMock = mockStatic(SecurityUtil.class)) {
            securityUtilMock.when(SecurityUtil::getCurrentUser).thenReturn(Optional.empty());
            when(articleQueryService.findUserFeedWithCursor(isNull(), any(CursorPageParameter.class)))
                .thenReturn(cursorPager);

            DataFetcherResult<ArticlesConnection> result = articleDatafetcher.getFeed(
                10, null, null, null, dgsDataFetchingEnvironment);

            assertNotNull(result);
            verify(articleQueryService).findUserFeedWithCursor(isNull(), any(CursorPageParameter.class));
        }
    }

    @Test
    void userFeed_shouldReturnUserArticlesWithFirstParameter() {
        try (MockedStatic<SecurityUtil> securityUtilMock = mockStatic(SecurityUtil.class)) {
            when(dgsDataFetchingEnvironment.getSource()).thenReturn(profile);
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
            when(articleQueryService.findUserFeedWithCursor(eq(testUser), any(CursorPageParameter.class)))
                .thenReturn(cursorPager);

            DataFetcherResult<ArticlesConnection> result = articleDatafetcher.userFeed(
                10, "1640995200000", null, null, dgsDataFetchingEnvironment);

            assertNotNull(result);
            assertNotNull(result.getData());
            assertEquals(1, result.getData().getEdges().size());
            verify(userRepository).findByUsername("testuser");
            verify(articleQueryService).findUserFeedWithCursor(eq(testUser), any(CursorPageParameter.class));
        }
    }

    @Test
    void userFeed_shouldThrowResourceNotFoundExceptionWhenUserNotFound() {
        when(dgsDataFetchingEnvironment.getSource()).thenReturn(profile);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            articleDatafetcher.userFeed(10, null, null, null, dgsDataFetchingEnvironment);
        });
    }

    @Test
    void userFeed_shouldThrowExceptionWhenBothFirstAndLastAreNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            articleDatafetcher.userFeed(null, null, null, null, dgsDataFetchingEnvironment);
        });
    }

    @Test
    void userFavorites_shouldReturnFavoriteArticlesWithFirstParameter() {
        try (MockedStatic<SecurityUtil> securityUtilMock = mockStatic(SecurityUtil.class)) {
            securityUtilMock.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));
            when(dgsDataFetchingEnvironment.getSource()).thenReturn(profile);
            when(articleQueryService.findRecentArticlesWithCursor(
                isNull(), isNull(), eq("testuser"), any(CursorPageParameter.class), eq(testUser)))
                .thenReturn(cursorPager);

            DataFetcherResult<ArticlesConnection> result = articleDatafetcher.userFavorites(
                10, "1640995200000", null, null, dgsDataFetchingEnvironment);

            assertNotNull(result);
            assertNotNull(result.getData());
            assertEquals(1, result.getData().getEdges().size());
            verify(articleQueryService).findRecentArticlesWithCursor(
                isNull(), isNull(), eq("testuser"), any(CursorPageParameter.class), eq(testUser));
        }
    }

    @Test
    void userFavorites_shouldWorkWithLastParameter() {
        try (MockedStatic<SecurityUtil> securityUtilMock = mockStatic(SecurityUtil.class)) {
            securityUtilMock.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));
            when(dgsDataFetchingEnvironment.getSource()).thenReturn(profile);
            when(articleQueryService.findRecentArticlesWithCursor(
                isNull(), isNull(), eq("testuser"), any(CursorPageParameter.class), eq(testUser)))
                .thenReturn(cursorPager);

            DataFetcherResult<ArticlesConnection> result = articleDatafetcher.userFavorites(
                null, null, 10, "1640995200000", dgsDataFetchingEnvironment);

            assertNotNull(result);
            verify(articleQueryService).findRecentArticlesWithCursor(
                isNull(), isNull(), eq("testuser"), any(CursorPageParameter.class), eq(testUser));
        }
    }

    @Test
    void userArticles_shouldReturnUserArticlesWithFirstParameter() {
        try (MockedStatic<SecurityUtil> securityUtilMock = mockStatic(SecurityUtil.class)) {
            securityUtilMock.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));
            when(dgsDataFetchingEnvironment.getSource()).thenReturn(profile);
            when(articleQueryService.findRecentArticlesWithCursor(
                isNull(), eq("testuser"), isNull(), any(CursorPageParameter.class), eq(testUser)))
                .thenReturn(cursorPager);

            DataFetcherResult<ArticlesConnection> result = articleDatafetcher.userArticles(
                10, "1640995200000", null, null, dgsDataFetchingEnvironment);

            assertNotNull(result);
            assertNotNull(result.getData());
            assertEquals(1, result.getData().getEdges().size());
            verify(articleQueryService).findRecentArticlesWithCursor(
                isNull(), eq("testuser"), isNull(), any(CursorPageParameter.class), eq(testUser));
        }
    }

    @Test
    void userArticles_shouldWorkWithAnonymousUser() {
        try (MockedStatic<SecurityUtil> securityUtilMock = mockStatic(SecurityUtil.class)) {
            securityUtilMock.when(SecurityUtil::getCurrentUser).thenReturn(Optional.empty());
            when(dgsDataFetchingEnvironment.getSource()).thenReturn(profile);
            when(articleQueryService.findRecentArticlesWithCursor(
                isNull(), eq("testuser"), isNull(), any(CursorPageParameter.class), isNull()))
                .thenReturn(cursorPager);

            DataFetcherResult<ArticlesConnection> result = articleDatafetcher.userArticles(
                10, null, null, null, dgsDataFetchingEnvironment);

            assertNotNull(result);
            verify(articleQueryService).findRecentArticlesWithCursor(
                isNull(), eq("testuser"), isNull(), any(CursorPageParameter.class), isNull());
        }
    }

    @Test
    void getArticles_shouldReturnArticlesWithAllFilters() {
        try (MockedStatic<SecurityUtil> securityUtilMock = mockStatic(SecurityUtil.class)) {
            securityUtilMock.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));
            when(articleQueryService.findRecentArticlesWithCursor(
                eq("java"), eq("author"), eq("favoriter"), any(CursorPageParameter.class), eq(testUser)))
                .thenReturn(cursorPager);

            DataFetcherResult<ArticlesConnection> result = articleDatafetcher.getArticles(
                10, "1640995200000", null, null, "author", "favoriter", "java", dgsDataFetchingEnvironment);

            assertNotNull(result);
            assertNotNull(result.getData());
            assertEquals(1, result.getData().getEdges().size());
            verify(articleQueryService).findRecentArticlesWithCursor(
                eq("java"), eq("author"), eq("favoriter"), any(CursorPageParameter.class), eq(testUser));
        }
    }

    @Test
    void getArticles_shouldWorkWithLastParameter() {
        try (MockedStatic<SecurityUtil> securityUtilMock = mockStatic(SecurityUtil.class)) {
            securityUtilMock.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));
            when(articleQueryService.findRecentArticlesWithCursor(
                isNull(), isNull(), isNull(), any(CursorPageParameter.class), eq(testUser)))
                .thenReturn(cursorPager);

            DataFetcherResult<ArticlesConnection> result = articleDatafetcher.getArticles(
                null, null, 10, "1640995200000", null, null, null, dgsDataFetchingEnvironment);

            assertNotNull(result);
            verify(articleQueryService).findRecentArticlesWithCursor(
                isNull(), isNull(), isNull(), any(CursorPageParameter.class), eq(testUser));
        }
    }

    @Test
    void getArticle_shouldReturnArticleFromLocalContext() {
        try (MockedStatic<SecurityUtil> securityUtilMock = mockStatic(SecurityUtil.class)) {
            securityUtilMock.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));
            when(dataFetchingEnvironment.getLocalContext()).thenReturn(coreArticle);
            when(articleQueryService.findById(eq(coreArticle.getId()), eq(testUser)))
                .thenReturn(Optional.of(articleData));

            DataFetcherResult<Article> result = articleDatafetcher.getArticle(dataFetchingEnvironment);

            assertNotNull(result);
            assertNotNull(result.getData());
            assertEquals("test-slug", result.getData().getSlug());
            assertEquals("Test Title", result.getData().getTitle());
            verify(articleQueryService).findById(eq(coreArticle.getId()), eq(testUser));
        }
    }

    @Test
    void getArticle_shouldThrowResourceNotFoundExceptionWhenArticleNotFound() {
        try (MockedStatic<SecurityUtil> securityUtilMock = mockStatic(SecurityUtil.class)) {
            securityUtilMock.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));
            when(dataFetchingEnvironment.getLocalContext()).thenReturn(coreArticle);
            when(articleQueryService.findById(eq(coreArticle.getId()), eq(testUser)))
                .thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> {
                articleDatafetcher.getArticle(dataFetchingEnvironment);
            });
        }
    }

    @Test
    void getCommentArticle_shouldReturnArticleFromCommentData() {
        try (MockedStatic<SecurityUtil> securityUtilMock = mockStatic(SecurityUtil.class)) {
            securityUtilMock.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));
            when(dataFetchingEnvironment.getLocalContext()).thenReturn(commentData);
            when(articleQueryService.findById(eq(commentData.getArticleId()), eq(testUser)))
                .thenReturn(Optional.of(articleData));

            DataFetcherResult<Article> result = articleDatafetcher.getCommentArticle(dataFetchingEnvironment);

            assertNotNull(result);
            assertNotNull(result.getData());
            assertEquals("test-slug", result.getData().getSlug());
            verify(articleQueryService).findById(eq(commentData.getArticleId()), eq(testUser));
        }
    }

    @Test
    void getCommentArticle_shouldThrowResourceNotFoundExceptionWhenArticleNotFound() {
        try (MockedStatic<SecurityUtil> securityUtilMock = mockStatic(SecurityUtil.class)) {
            securityUtilMock.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));
            when(dataFetchingEnvironment.getLocalContext()).thenReturn(commentData);
            when(articleQueryService.findById(eq(commentData.getArticleId()), eq(testUser)))
                .thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> {
                articleDatafetcher.getCommentArticle(dataFetchingEnvironment);
            });
        }
    }

    @Test
    void findArticleBySlug_shouldReturnArticleBySlug() {
        try (MockedStatic<SecurityUtil> securityUtilMock = mockStatic(SecurityUtil.class)) {
            securityUtilMock.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));
            when(articleQueryService.findBySlug(eq("test-slug"), eq(testUser)))
                .thenReturn(Optional.of(articleData));

            DataFetcherResult<Article> result = articleDatafetcher.findArticleBySlug("test-slug");

            assertNotNull(result);
            assertNotNull(result.getData());
            assertEquals("test-slug", result.getData().getSlug());
            assertEquals("Test Title", result.getData().getTitle());
            verify(articleQueryService).findBySlug(eq("test-slug"), eq(testUser));
        }
    }

    @Test
    void findArticleBySlug_shouldThrowResourceNotFoundExceptionWhenArticleNotFound() {
        try (MockedStatic<SecurityUtil> securityUtilMock = mockStatic(SecurityUtil.class)) {
            securityUtilMock.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));
            when(articleQueryService.findBySlug(eq("nonexistent-slug"), eq(testUser)))
                .thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> {
                articleDatafetcher.findArticleBySlug("nonexistent-slug");
            });
        }
    }

    @Test
    void findArticleBySlug_shouldWorkWithAnonymousUser() {
        try (MockedStatic<SecurityUtil> securityUtilMock = mockStatic(SecurityUtil.class)) {
            securityUtilMock.when(SecurityUtil::getCurrentUser).thenReturn(Optional.empty());
            when(articleQueryService.findBySlug(eq("test-slug"), isNull()))
                .thenReturn(Optional.of(articleData));

            DataFetcherResult<Article> result = articleDatafetcher.findArticleBySlug("test-slug");

            assertNotNull(result);
            assertNotNull(result.getData());
            assertEquals("test-slug", result.getData().getSlug());
            verify(articleQueryService).findBySlug(eq("test-slug"), isNull());
        }
    }

    @Test
    void shouldHandleEmptyArticleList() {
        try (MockedStatic<SecurityUtil> securityUtilMock = mockStatic(SecurityUtil.class)) {
            securityUtilMock.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));
            
            CursorPager<ArticleData> emptyCursorPager = new CursorPager<>(
                Collections.emptyList(),
                CursorPager.Direction.NEXT,
                false
            );
            
            when(articleQueryService.findUserFeedWithCursor(eq(testUser), any(CursorPageParameter.class)))
                .thenReturn(emptyCursorPager);

            DataFetcherResult<ArticlesConnection> result = articleDatafetcher.getFeed(
                10, null, null, null, dgsDataFetchingEnvironment);

            assertNotNull(result);
            assertNotNull(result.getData());
            assertEquals(0, result.getData().getEdges().size());
            assertNotNull(result.getData().getPageInfo());
        }
    }

    @Test
    void shouldHandleNullCursors() {
        try (MockedStatic<SecurityUtil> securityUtilMock = mockStatic(SecurityUtil.class)) {
            securityUtilMock.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));
            when(articleQueryService.findUserFeedWithCursor(eq(testUser), any(CursorPageParameter.class)))
                .thenReturn(cursorPager);

            DataFetcherResult<ArticlesConnection> result = articleDatafetcher.getFeed(
                10, null, null, null, dgsDataFetchingEnvironment);

            assertNotNull(result);
            assertNotNull(result.getData());
            verify(articleQueryService).findUserFeedWithCursor(eq(testUser), any(CursorPageParameter.class));
        }
    }
}
