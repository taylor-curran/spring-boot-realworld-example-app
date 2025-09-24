package io.spring.graphql;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import graphql.execution.DataFetcherResult;
import io.spring.api.exception.NoAuthorizationException;
import io.spring.api.exception.ResourceNotFoundException;
import io.spring.application.CommentQueryService;
import io.spring.application.data.CommentData;
import io.spring.application.data.ProfileData;
import io.spring.core.article.Article;
import io.spring.core.article.ArticleRepository;
import io.spring.core.comment.Comment;
import io.spring.core.comment.CommentRepository;
import io.spring.core.service.AuthorizationService;
import io.spring.core.user.User;
import io.spring.graphql.exception.AuthenticationException;
import io.spring.graphql.types.CommentPayload;
import io.spring.graphql.types.DeletionStatus;
import java.util.Arrays;
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
class CommentMutationSimpleTest {

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentQueryService commentQueryService;

    @InjectMocks
    private CommentMutation commentMutation;

    private User testUser;
    private Article testArticle;
    private Comment testComment;
    private CommentData commentData;

    @BeforeEach
    void setUp() {
        testUser = new User("test@example.com", "testuser", "password", "Test Bio", "test.jpg");
        testArticle = new Article("Test Title", "Test Description", "Test Body", Arrays.asList("tag1"), testUser.getId());
        testComment = new Comment("Test comment body", testUser.getId(), testArticle.getId());
        
        ProfileData profileData = new ProfileData(testUser.getId(), "testuser", "Test Bio", "test.jpg", false);
        commentData = new CommentData(
            testComment.getId(),
            "Test comment body",
            testArticle.getId(),
            new DateTime(),
            new DateTime(),
            profileData
        );
    }

    @Test
    void shouldCreateCommentSuccessfully() {
        try (MockedStatic<SecurityUtil> securityUtilMock = mockStatic(SecurityUtil.class)) {
            securityUtilMock.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));
            when(articleRepository.findBySlug("test-slug")).thenReturn(Optional.of(testArticle));
            when(commentQueryService.findById(any(), eq(testUser))).thenReturn(Optional.of(commentData));

            DataFetcherResult<CommentPayload> result = commentMutation.createComment("test-slug", "Test comment body");

            assertNotNull(result);
            assertNotNull(result.getData());
            assertEquals(commentData, result.getLocalContext());
            verify(commentRepository).save(any(Comment.class));
            verify(commentQueryService).findById(any(), eq(testUser));
        }
    }

    @Test
    void shouldThrowAuthenticationExceptionWhenUserNotAuthenticated() {
        try (MockedStatic<SecurityUtil> securityUtilMock = mockStatic(SecurityUtil.class)) {
            securityUtilMock.when(SecurityUtil::getCurrentUser).thenReturn(Optional.empty());

            assertThrows(AuthenticationException.class, () -> {
                commentMutation.createComment("test-slug", "Test comment body");
            });

            verify(commentRepository, never()).save(any());
        }
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenArticleNotFound() {
        try (MockedStatic<SecurityUtil> securityUtilMock = mockStatic(SecurityUtil.class)) {
            securityUtilMock.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));
            when(articleRepository.findBySlug("nonexistent-slug")).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> {
                commentMutation.createComment("nonexistent-slug", "Test comment body");
            });

            verify(commentRepository, never()).save(any());
        }
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenCommentDataNotFound() {
        try (MockedStatic<SecurityUtil> securityUtilMock = mockStatic(SecurityUtil.class)) {
            securityUtilMock.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));
            when(articleRepository.findBySlug("test-slug")).thenReturn(Optional.of(testArticle));
            when(commentQueryService.findById(any(), eq(testUser))).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> {
                commentMutation.createComment("test-slug", "Test comment body");
            });

            verify(commentRepository).save(any(Comment.class));
        }
    }

    @Test
    void shouldRemoveCommentSuccessfully() {
        try (MockedStatic<SecurityUtil> securityUtilMock = mockStatic(SecurityUtil.class);
             MockedStatic<AuthorizationService> authServiceMock = mockStatic(AuthorizationService.class)) {
            
            securityUtilMock.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));
            authServiceMock.when(() -> AuthorizationService.canWriteComment(testUser, testArticle, testComment)).thenReturn(true);
            
            when(articleRepository.findBySlug("test-slug")).thenReturn(Optional.of(testArticle));
            when(commentRepository.findById(testArticle.getId(), "comment-id")).thenReturn(Optional.of(testComment));

            DeletionStatus result = commentMutation.removeComment("test-slug", "comment-id");

            assertNotNull(result);
            assertTrue(result.getSuccess());
            verify(commentRepository).remove(testComment);
        }
    }

    @Test
    void shouldThrowNoAuthorizationExceptionWhenUserCannotWriteComment() {
        try (MockedStatic<SecurityUtil> securityUtilMock = mockStatic(SecurityUtil.class);
             MockedStatic<AuthorizationService> authServiceMock = mockStatic(AuthorizationService.class)) {
            
            securityUtilMock.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));
            authServiceMock.when(() -> AuthorizationService.canWriteComment(testUser, testArticle, testComment)).thenReturn(false);
            
            when(articleRepository.findBySlug("test-slug")).thenReturn(Optional.of(testArticle));
            when(commentRepository.findById(testArticle.getId(), "comment-id")).thenReturn(Optional.of(testComment));

            assertThrows(NoAuthorizationException.class, () -> {
                commentMutation.removeComment("test-slug", "comment-id");
            });

            verify(commentRepository, never()).remove(any());
        }
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenCommentNotFound() {
        try (MockedStatic<SecurityUtil> securityUtilMock = mockStatic(SecurityUtil.class)) {
            securityUtilMock.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));
            when(articleRepository.findBySlug("test-slug")).thenReturn(Optional.of(testArticle));
            when(commentRepository.findById(testArticle.getId(), "nonexistent-comment")).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> {
                commentMutation.removeComment("test-slug", "nonexistent-comment");
            });
        }
    }
}
