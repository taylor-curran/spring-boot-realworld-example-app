package io.spring.graphql;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.netflix.graphql.dgs.DgsDataFetchingEnvironment;
import graphql.schema.DataFetchingEnvironment;
import graphql.execution.DataFetcherResult;
import io.spring.application.CommentQueryService;
import io.spring.application.CursorPageParameter;
import io.spring.application.CursorPager;
import org.joda.time.DateTime;
import io.spring.application.data.ArticleData;
import io.spring.application.data.CommentData;
import io.spring.application.data.ProfileData;
import io.spring.core.user.User;
import io.spring.graphql.types.Article;
import io.spring.graphql.types.Comment;
import io.spring.graphql.types.CommentsConnection;
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
class CommentDatafetcherTest {

    @Mock
    private CommentQueryService commentQueryService;

    @Mock
    private DgsDataFetchingEnvironment dgsDataFetchingEnvironment;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private CommentDatafetcher commentDatafetcher;

    private User testUser;
    private CommentData testCommentData;
    private ArticleData testArticleData;
    private CursorPager<CommentData> testCursorPager;

    @BeforeEach
    void setUp() {
        testUser = new User("test@example.com", "testuser", "hashedpassword", "Test Bio", "avatar.jpg");
        SecurityContextHolder.setContext(securityContext);
        
        ProfileData profileData = new ProfileData("user1", "testuser", "Test Bio", "avatar.jpg", false);
        testCommentData = new CommentData(
            "comment1",
            "Test comment body",
            "article1",
            DateTime.now(),
            DateTime.now(),
            profileData
        );

        testArticleData = new ArticleData(
            "article1",
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
            Arrays.asList(testCommentData),
            CursorPager.Direction.NEXT,
            true
        );
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldTestCommentQueryServiceIntegration() {
        CursorPageParameter<DateTime> pageParam = new CursorPageParameter<>(
            null, 10, CursorPager.Direction.NEXT
        );
        
        when(commentQueryService.findByArticleIdWithCursor(
            eq("article1"), eq(testUser), eq(pageParam)))
            .thenReturn(testCursorPager);

        CursorPager<CommentData> result = commentQueryService.findByArticleIdWithCursor(
            "article1", testUser, pageParam
        );

        assertNotNull(result);
        assertEquals(1, result.getData().size());
        assertEquals("comment1", result.getData().get(0).getId());
        verify(commentQueryService).findByArticleIdWithCursor(
            eq("article1"), eq(testUser), eq(pageParam));
    }

    @Test
    void shouldTestCommentQueryServiceWithNullUser() {
        CursorPageParameter<DateTime> pageParam = new CursorPageParameter<>(
            null, 10, CursorPager.Direction.NEXT
        );
        
        when(commentQueryService.findByArticleIdWithCursor(
            eq("article1"), isNull(), eq(pageParam)))
            .thenReturn(testCursorPager);

        CursorPager<CommentData> result = commentQueryService.findByArticleIdWithCursor(
            "article1", null, pageParam
        );

        assertNotNull(result);
        assertEquals(1, result.getData().size());
        verify(commentQueryService).findByArticleIdWithCursor(
            eq("article1"), isNull(), eq(pageParam));
    }

    @Test
    void shouldTestCommentQueryServiceWithEmptyResults() {
        CursorPager<CommentData> emptyCursorPager = new CursorPager<>(
            Collections.emptyList(), CursorPager.Direction.NEXT, false
        );
        
        CursorPageParameter<DateTime> pageParam = new CursorPageParameter<>(
            null, 10, CursorPager.Direction.NEXT
        );
        
        when(commentQueryService.findByArticleIdWithCursor(
            eq("article1"), eq(testUser), eq(pageParam)))
            .thenReturn(emptyCursorPager);

        CursorPager<CommentData> result = commentQueryService.findByArticleIdWithCursor(
            "article1", testUser, pageParam
        );

        assertNotNull(result);
        assertTrue(result.getData().isEmpty());
        assertFalse(result.hasNext());
    }

    @Test
    void shouldGetCommentFromPayload() {
        when(dgsDataFetchingEnvironment.getLocalContext()).thenReturn(testCommentData);

        DataFetcherResult<Comment> result = commentDatafetcher.getComment(dgsDataFetchingEnvironment);

        assertNotNull(result);
        assertNotNull(result.getData());
        assertEquals("comment1", result.getData().getId());
        assertEquals("Test comment body", result.getData().getBody());
        assertNotNull(result.getLocalContext());
        Map<String, CommentData> localContext = (Map<String, CommentData>) result.getLocalContext();
        assertTrue(localContext.containsKey("comment1"));
        assertEquals(testCommentData, localContext.get("comment1"));
    }

    @Test
    void shouldGetArticleCommentsWithFirstParameter() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(testUser);
        
        Article article = Article.newBuilder().slug("test-slug").build();
        when(dgsDataFetchingEnvironment.getSource()).thenReturn(article);
        
        Map<String, ArticleData> contextMap = new HashMap<>();
        contextMap.put("test-slug", testArticleData);
        when(dgsDataFetchingEnvironment.getLocalContext()).thenReturn(contextMap);
        
        when(commentQueryService.findByArticleIdWithCursor(eq("article1"), eq(testUser), any(CursorPageParameter.class)))
            .thenReturn(testCursorPager);

        DataFetcherResult<CommentsConnection> result = commentDatafetcher.articleComments(10, null, null, null, dgsDataFetchingEnvironment);

        assertNotNull(result);
        assertNotNull(result.getData());
        assertEquals(1, result.getData().getEdges().size());
        assertEquals("comment1", result.getData().getEdges().get(0).getNode().getId());
        verify(commentQueryService).findByArticleIdWithCursor(eq("article1"), eq(testUser), any(CursorPageParameter.class));
    }

    @Test
    void shouldGetArticleCommentsWithLastParameter() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(testUser);
        
        Article article = Article.newBuilder().slug("test-slug").build();
        when(dgsDataFetchingEnvironment.getSource()).thenReturn(article);
        
        Map<String, ArticleData> contextMap = new HashMap<>();
        contextMap.put("test-slug", testArticleData);
        when(dgsDataFetchingEnvironment.getLocalContext()).thenReturn(contextMap);
        
        when(commentQueryService.findByArticleIdWithCursor(eq("article1"), eq(testUser), any(CursorPageParameter.class)))
            .thenReturn(testCursorPager);

        DataFetcherResult<CommentsConnection> result = commentDatafetcher.articleComments(null, null, 10, "1672531200000", dgsDataFetchingEnvironment);

        assertNotNull(result);
        assertNotNull(result.getData());
        assertEquals(1, result.getData().getEdges().size());
        verify(commentQueryService).findByArticleIdWithCursor(eq("article1"), eq(testUser), any(CursorPageParameter.class));
    }

    @Test
    void shouldThrowExceptionWhenBothFirstAndLastAreNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            commentDatafetcher.articleComments(null, null, null, null, dgsDataFetchingEnvironment);
        });
    }

    @Test
    void shouldThrowExceptionWhenBothFirstAndLastAreProvided() {
        assertThrows(IllegalArgumentException.class, () -> {
            commentDatafetcher.articleComments(null, null, null, null, dgsDataFetchingEnvironment);
        });
    }

    @Test
    void shouldGetArticleCommentsWithNullCurrentUser() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(null);
        
        Article article = Article.newBuilder().slug("test-slug").build();
        when(dgsDataFetchingEnvironment.getSource()).thenReturn(article);
        
        Map<String, ArticleData> contextMap = new HashMap<>();
        contextMap.put("test-slug", testArticleData);
        when(dgsDataFetchingEnvironment.getLocalContext()).thenReturn(contextMap);
        
        when(commentQueryService.findByArticleIdWithCursor(eq("article1"), isNull(), any(CursorPageParameter.class)))
            .thenReturn(testCursorPager);

        DataFetcherResult<CommentsConnection> result = commentDatafetcher.articleComments(10, null, null, null, dgsDataFetchingEnvironment);

        assertNotNull(result);
        verify(commentQueryService).findByArticleIdWithCursor(eq("article1"), isNull(), any(CursorPageParameter.class));
    }

    @Test
    void shouldHandleEmptyCommentsList() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(testUser);
        
        Article article = Article.newBuilder().slug("test-slug").build();
        when(dgsDataFetchingEnvironment.getSource()).thenReturn(article);
        
        Map<String, ArticleData> contextMap = new HashMap<>();
        contextMap.put("test-slug", testArticleData);
        when(dgsDataFetchingEnvironment.getLocalContext()).thenReturn(contextMap);
        
        CursorPager<CommentData> emptyCursorPager = new CursorPager<>(
            Collections.emptyList(),
            CursorPager.Direction.NEXT,
            false
        );
        when(commentQueryService.findByArticleIdWithCursor(eq("article1"), eq(testUser), any(CursorPageParameter.class)))
            .thenReturn(emptyCursorPager);

        DataFetcherResult<CommentsConnection> result = commentDatafetcher.articleComments(10, null, null, null, dgsDataFetchingEnvironment);

        assertNotNull(result);
        assertNotNull(result.getData());
        assertTrue(result.getData().getEdges().isEmpty());
        assertNotNull(result.getData().getPageInfo());
    }

    @Test
    void shouldHandleCursorParsing() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(testUser);
        
        Article article = Article.newBuilder().slug("test-slug").build();
        when(dgsDataFetchingEnvironment.getSource()).thenReturn(article);
        
        Map<String, ArticleData> contextMap = new HashMap<>();
        contextMap.put("test-slug", testArticleData);
        when(dgsDataFetchingEnvironment.getLocalContext()).thenReturn(contextMap);
        
        when(commentQueryService.findByArticleIdWithCursor(eq("article1"), eq(testUser), any(CursorPageParameter.class)))
            .thenReturn(testCursorPager);

        DataFetcherResult<CommentsConnection> result = commentDatafetcher.articleComments(10, "1672531200000", null, null, dgsDataFetchingEnvironment);

        assertNotNull(result);
        verify(commentQueryService).findByArticleIdWithCursor(eq("article1"), eq(testUser), any(CursorPageParameter.class));
    }

    @Test
    void shouldBuildCorrectPageInfo() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(testUser);
        
        Article article = Article.newBuilder().slug("test-slug").build();
        when(dgsDataFetchingEnvironment.getSource()).thenReturn(article);
        
        Map<String, ArticleData> contextMap = new HashMap<>();
        contextMap.put("test-slug", testArticleData);
        when(dgsDataFetchingEnvironment.getLocalContext()).thenReturn(contextMap);
        
        CursorPager<CommentData> pagerWithPagination = new CursorPager<>(
            Arrays.asList(testCommentData),
            CursorPager.Direction.NEXT,
            true
        );
        when(commentQueryService.findByArticleIdWithCursor(eq("article1"), eq(testUser), any(CursorPageParameter.class)))
            .thenReturn(pagerWithPagination);

        DataFetcherResult<CommentsConnection> result = commentDatafetcher.articleComments(10, null, null, null, dgsDataFetchingEnvironment);

        assertNotNull(result);
        assertNotNull(result.getData().getPageInfo());
        assertNotNull(result.getData().getPageInfo().getEndCursor());
    }

    @Test
    void shouldBuildCorrectLocalContext() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(testUser);
        
        Article article = Article.newBuilder().slug("test-slug").build();
        when(dgsDataFetchingEnvironment.getSource()).thenReturn(article);
        
        Map<String, ArticleData> contextMap = new HashMap<>();
        contextMap.put("test-slug", testArticleData);
        when(dgsDataFetchingEnvironment.getLocalContext()).thenReturn(contextMap);
        
        when(commentQueryService.findByArticleIdWithCursor(eq("article1"), eq(testUser), any(CursorPageParameter.class)))
            .thenReturn(testCursorPager);

        DataFetcherResult<CommentsConnection> result = commentDatafetcher.articleComments(10, null, null, null, dgsDataFetchingEnvironment);

        assertNotNull(result);
        assertNotNull(result.getLocalContext());
        Map<String, CommentData> localContext = (Map<String, CommentData>) result.getLocalContext();
        assertTrue(localContext.containsKey("comment1"));
        assertEquals(testCommentData, localContext.get("comment1"));
    }

    @Test
    void shouldHandleMultipleComments() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(testUser);
        
        ProfileData profileData = new ProfileData("user2", "testuser2", "Test Bio 2", "avatar2.jpg", false);
        CommentData secondComment = new CommentData(
            "comment2",
            "Second comment body",
            "article1",
            DateTime.now(),
            DateTime.now(),
            profileData
        );
        
        CursorPager<CommentData> multipleCommentsPager = new CursorPager<>(
            Arrays.asList(testCommentData, secondComment),
            CursorPager.Direction.NEXT,
            false
        );
        
        Article article = Article.newBuilder().slug("test-slug").build();
        when(dgsDataFetchingEnvironment.getSource()).thenReturn(article);
        
        Map<String, ArticleData> contextMap = new HashMap<>();
        contextMap.put("test-slug", testArticleData);
        when(dgsDataFetchingEnvironment.getLocalContext()).thenReturn(contextMap);
        
        when(commentQueryService.findByArticleIdWithCursor(eq("article1"), eq(testUser), any(CursorPageParameter.class)))
            .thenReturn(multipleCommentsPager);

        DataFetcherResult<CommentsConnection> result = commentDatafetcher.articleComments(10, null, null, null, dgsDataFetchingEnvironment);

        assertNotNull(result);
        assertNotNull(result.getData());
        assertEquals(2, result.getData().getEdges().size());
        assertEquals("comment1", result.getData().getEdges().get(0).getNode().getId());
        assertEquals("comment2", result.getData().getEdges().get(1).getNode().getId());
        
        Map<String, CommentData> localContext = (Map<String, CommentData>) result.getLocalContext();
        assertTrue(localContext.containsKey("comment1"));
        assertTrue(localContext.containsKey("comment2"));
    }
}
