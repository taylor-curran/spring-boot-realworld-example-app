package io.spring.graphql;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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
    private DataFetchingEnvironment dgsDataFetchingEnvironment;

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
}
