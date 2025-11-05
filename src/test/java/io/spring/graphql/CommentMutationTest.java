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
import io.spring.core.user.User;
import io.spring.graphql.SecurityUtil;
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
class CommentMutationTest {

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentQueryService commentQueryService;


    @InjectMocks
    private CommentMutation commentMutation;

    private User testUser;
    private User otherUser;
    private Article testArticle;
    private Comment testComment;
    private CommentData testCommentData;

    @BeforeEach
    void setUp() {
        testUser = new User("test@example.com", "testuser", "hashedpassword", "Test Bio", "avatar.jpg");
        otherUser = new User("other@example.com", "otheruser", "password", "Other Bio", "other.jpg");
        testArticle = new Article("Test Title", "Test Description", "Test Body", Arrays.asList("java"), "testuser");
        testComment = new Comment("Test comment body", testUser.getId(), testArticle.getId());
        
        ProfileData profileData = new ProfileData(testUser.getId(), "testuser", "Test Bio", "avatar.jpg", false);
        testCommentData = new CommentData(
            testComment.getId(),
            "Test comment body",
            testArticle.getId(),
            DateTime.now(),
            DateTime.now(),
            profileData
        );
        
    }

    @Test
    void shouldCreateCommentSuccessfully() {
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));
            when(articleRepository.findBySlug("test-slug")).thenReturn(Optional.of(testArticle));
            when(commentQueryService.findById(any(String.class), eq(testUser)))
                .thenReturn(Optional.of(testCommentData));

            DataFetcherResult<CommentPayload> result = commentMutation.createComment("test-slug", "Test comment body");

            assertNotNull(result);
            assertNotNull(result.getData());
            assertEquals(testCommentData, result.getLocalContext());
            verify(articleRepository).findBySlug("test-slug");
            verify(commentRepository).save(any(Comment.class));
            verify(commentQueryService).findById(any(String.class), eq(testUser));
        }
    }

    @Test
    void shouldThrowAuthenticationExceptionWhenUserNotAuthenticated() {
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.empty());

            assertThrows(AuthenticationException.class, () -> {
                commentMutation.createComment("test-slug", "Test comment body");
            });
        }
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenArticleNotFound() {
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));
            when(articleRepository.findBySlug("nonexistent")).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> {
                commentMutation.createComment("nonexistent", "Test comment body");
            });
        }
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenCommentDataNotFound() {
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));
            when(articleRepository.findBySlug("test-slug")).thenReturn(Optional.of(testArticle));
            when(commentQueryService.findById(any(String.class), eq(testUser)))
                .thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> {
                commentMutation.createComment("test-slug", "Test comment body");
            });
        }
    }

    @Test
    void shouldRemoveCommentSuccessfully() {
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));
            when(articleRepository.findBySlug("test-slug")).thenReturn(Optional.of(testArticle));
            when(commentRepository.findById(testArticle.getId(), testComment.getId()))
                .thenReturn(Optional.of(testComment));

            DeletionStatus result = commentMutation.removeComment("test-slug", testComment.getId());

            assertNotNull(result);
            assertTrue(result.getSuccess());
            verify(articleRepository).findBySlug("test-slug");
            verify(commentRepository).findById(testArticle.getId(), testComment.getId());
            verify(commentRepository).remove(testComment);
        }
    }

    @Test
    void shouldThrowAuthenticationExceptionWhenUserNotAuthenticatedForRemoval() {
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.empty());

            assertThrows(AuthenticationException.class, () -> {
                commentMutation.removeComment("test-slug", testComment.getId());
            });
        }
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenArticleNotFoundForRemoval() {
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));
            when(articleRepository.findBySlug("nonexistent")).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> {
                commentMutation.removeComment("nonexistent", testComment.getId());
            });
        }
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenCommentNotFound() {
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));
            when(articleRepository.findBySlug("test-slug")).thenReturn(Optional.of(testArticle));
            when(commentRepository.findById(testArticle.getId(), "nonexistent"))
                .thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> {
                commentMutation.removeComment("test-slug", "nonexistent");
            });
        }
    }

    @Test
    void shouldThrowNoAuthorizationExceptionWhenUserCannotWriteComment() {
        Comment otherUserComment = new Comment("Other comment", otherUser.getId(), testArticle.getId());
        
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));
            when(articleRepository.findBySlug("test-slug")).thenReturn(Optional.of(testArticle));
            when(commentRepository.findById(testArticle.getId(), otherUserComment.getId()))
                .thenReturn(Optional.of(otherUserComment));

            assertThrows(NoAuthorizationException.class, () -> {
                commentMutation.removeComment("test-slug", otherUserComment.getId());
            });
        }
    }

    @Test
    void shouldHandleCommentCreationWithLongBody() {
        String longBody = "This is a very long comment body that tests the system's ability to handle longer text content in comments.";
        
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));
            when(articleRepository.findBySlug("test-slug")).thenReturn(Optional.of(testArticle));
            when(commentQueryService.findById(any(String.class), eq(testUser)))
                .thenReturn(Optional.of(testCommentData));

            DataFetcherResult<CommentPayload> result = commentMutation.createComment("test-slug", longBody);

            assertNotNull(result);
            verify(commentRepository).save(argThat(comment -> 
                comment.getBody().equals(longBody) && 
                comment.getUserId().equals(testUser.getId()) &&
                comment.getArticleId().equals(testArticle.getId())
            ));
        }
    }

    @Test
    void shouldHandleEmptyCommentBody() {
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));
            when(articleRepository.findBySlug("test-slug")).thenReturn(Optional.of(testArticle));
            when(commentQueryService.findById(any(String.class), eq(testUser)))
                .thenReturn(Optional.of(testCommentData));

            DataFetcherResult<CommentPayload> result = commentMutation.createComment("test-slug", "");

            assertNotNull(result);
            verify(commentRepository).save(argThat(comment -> 
                comment.getBody().equals("") && 
                comment.getUserId().equals(testUser.getId())
            ));
        }
    }
}
