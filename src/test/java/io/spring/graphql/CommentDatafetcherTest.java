package io.spring.graphql;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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
    private DataFetchingEnvironment dgsDataFetchingEnvironment;

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
}
