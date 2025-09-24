package io.spring.graphql;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import graphql.execution.DataFetcherResult;
import io.spring.api.exception.NoAuthorizationException;
import io.spring.api.exception.ResourceNotFoundException;
import io.spring.application.article.ArticleCommandService;
import io.spring.application.article.NewArticleParam;
import io.spring.application.article.UpdateArticleParam;
import io.spring.core.article.Article;
import io.spring.core.article.ArticleRepository;
import io.spring.core.favorite.ArticleFavorite;
import io.spring.core.favorite.ArticleFavoriteRepository;
import io.spring.core.service.AuthorizationService;
import io.spring.core.user.User;
import io.spring.graphql.exception.AuthenticationException;
import io.spring.graphql.types.ArticlePayload;
import io.spring.graphql.types.CreateArticleInput;
import io.spring.graphql.types.DeletionStatus;
import io.spring.graphql.types.UpdateArticleInput;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ArticleMutationSimpleTest {

    @Mock
    private ArticleCommandService articleCommandService;

    @Mock
    private ArticleFavoriteRepository articleFavoriteRepository;

    @Mock
    private ArticleRepository articleRepository;

    @InjectMocks
    private ArticleMutation articleMutation;

    private User testUser;
    private Article testArticle;

    @BeforeEach
    void setUp() {
        testUser = new User("test@example.com", "testuser", "password", "Test Bio", "test.jpg");
        testArticle = new Article("Test Title", "Test Description", "Test Body", Arrays.asList("tag1", "tag2"), testUser.getId());
    }

    @Test
    void shouldCreateArticleSuccessfully() {
        try (MockedStatic<SecurityUtil> securityUtilMock = mockStatic(SecurityUtil.class)) {
            securityUtilMock.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));

            CreateArticleInput input = CreateArticleInput.newBuilder()
                .title("New Article")
                .description("New Description")
                .body("New Body")
                .tagList(Arrays.asList("java", "spring"))
                .build();

            when(articleCommandService.createArticle(any(NewArticleParam.class), eq(testUser)))
                .thenReturn(testArticle);

            DataFetcherResult<ArticlePayload> result = articleMutation.createArticle(input);

            assertNotNull(result);
            assertNotNull(result.getData());
            assertEquals(testArticle, result.getLocalContext());
            verify(articleCommandService).createArticle(any(NewArticleParam.class), eq(testUser));
        }
    }

    @Test
    void shouldThrowAuthenticationExceptionWhenUserNotAuthenticated() {
        try (MockedStatic<SecurityUtil> securityUtilMock = mockStatic(SecurityUtil.class)) {
            securityUtilMock.when(SecurityUtil::getCurrentUser).thenReturn(Optional.empty());

            CreateArticleInput input = CreateArticleInput.newBuilder()
                .title("New Article")
                .description("New Description")
                .body("New Body")
                .build();

            assertThrows(AuthenticationException.class, () -> {
                articleMutation.createArticle(input);
            });

            verify(articleCommandService, never()).createArticle(any(), any());
        }
    }

    @Test
    void shouldUpdateArticleSuccessfully() {
        try (MockedStatic<SecurityUtil> securityUtilMock = mockStatic(SecurityUtil.class);
             MockedStatic<AuthorizationService> authServiceMock = mockStatic(AuthorizationService.class)) {
            
            securityUtilMock.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));
            authServiceMock.when(() -> AuthorizationService.canWriteArticle(testUser, testArticle)).thenReturn(true);

            when(articleRepository.findBySlug("test-slug")).thenReturn(Optional.of(testArticle));
            when(articleCommandService.updateArticle(eq(testArticle), any(UpdateArticleParam.class)))
                .thenReturn(testArticle);

            UpdateArticleInput input = UpdateArticleInput.newBuilder()
                .title("Updated Title")
                .description("Updated Description")
                .body("Updated Body")
                .build();

            DataFetcherResult<ArticlePayload> result = articleMutation.updateArticle("test-slug", input);

            assertNotNull(result);
            assertNotNull(result.getData());
            assertEquals(testArticle, result.getLocalContext());
            verify(articleCommandService).updateArticle(eq(testArticle), any(UpdateArticleParam.class));
        }
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenArticleNotFound() {
        try (MockedStatic<SecurityUtil> securityUtilMock = mockStatic(SecurityUtil.class)) {
            securityUtilMock.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));
            when(articleRepository.findBySlug("nonexistent-slug")).thenReturn(Optional.empty());

            UpdateArticleInput input = UpdateArticleInput.newBuilder()
                .title("Updated Title")
                .build();

            assertThrows(ResourceNotFoundException.class, () -> {
                articleMutation.updateArticle("nonexistent-slug", input);
            });
        }
    }

    @Test
    void shouldThrowNoAuthorizationExceptionWhenUserCannotWriteArticle() {
        try (MockedStatic<SecurityUtil> securityUtilMock = mockStatic(SecurityUtil.class);
             MockedStatic<AuthorizationService> authServiceMock = mockStatic(AuthorizationService.class)) {
            
            securityUtilMock.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));
            authServiceMock.when(() -> AuthorizationService.canWriteArticle(testUser, testArticle)).thenReturn(false);

            when(articleRepository.findBySlug("test-slug")).thenReturn(Optional.of(testArticle));

            UpdateArticleInput input = UpdateArticleInput.newBuilder()
                .title("Updated Title")
                .build();

            assertThrows(NoAuthorizationException.class, () -> {
                articleMutation.updateArticle("test-slug", input);
            });

            verify(articleCommandService, never()).updateArticle(any(), any());
        }
    }

    @Test
    void shouldFavoriteArticleSuccessfully() {
        try (MockedStatic<SecurityUtil> securityUtilMock = mockStatic(SecurityUtil.class)) {
            securityUtilMock.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));
            when(articleRepository.findBySlug("test-slug")).thenReturn(Optional.of(testArticle));

            DataFetcherResult<ArticlePayload> result = articleMutation.favoriteArticle("test-slug");

            assertNotNull(result);
            assertNotNull(result.getData());
            assertEquals(testArticle, result.getLocalContext());
            verify(articleFavoriteRepository).save(any(ArticleFavorite.class));
        }
    }

    @Test
    void shouldUnfavoriteArticleSuccessfully() {
        try (MockedStatic<SecurityUtil> securityUtilMock = mockStatic(SecurityUtil.class)) {
            securityUtilMock.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));
            when(articleRepository.findBySlug("test-slug")).thenReturn(Optional.of(testArticle));
            
            ArticleFavorite favorite = new ArticleFavorite(testArticle.getId(), testUser.getId());
            when(articleFavoriteRepository.find(testArticle.getId(), testUser.getId()))
                .thenReturn(Optional.of(favorite));

            DataFetcherResult<ArticlePayload> result = articleMutation.unfavoriteArticle("test-slug");

            assertNotNull(result);
            assertNotNull(result.getData());
            assertEquals(testArticle, result.getLocalContext());
            verify(articleFavoriteRepository).remove(favorite);
        }
    }

    @Test
    void shouldDeleteArticleSuccessfully() {
        try (MockedStatic<SecurityUtil> securityUtilMock = mockStatic(SecurityUtil.class);
             MockedStatic<AuthorizationService> authServiceMock = mockStatic(AuthorizationService.class)) {
            
            securityUtilMock.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));
            authServiceMock.when(() -> AuthorizationService.canWriteArticle(testUser, testArticle)).thenReturn(true);
            when(articleRepository.findBySlug("test-slug")).thenReturn(Optional.of(testArticle));

            DeletionStatus result = articleMutation.deleteArticle("test-slug");

            assertNotNull(result);
            assertTrue(result.getSuccess());
            verify(articleRepository).remove(testArticle);
        }
    }
}
