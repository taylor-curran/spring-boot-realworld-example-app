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
import io.spring.core.user.User;
import io.spring.graphql.SecurityUtil;
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
class ArticleMutationTest {

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
    private CreateArticleInput createArticleInput;
    private UpdateArticleInput updateArticleInput;

    @BeforeEach
    void setUp() {
        testUser = new User("test@example.com", "testuser", "hashedpassword", "Test Bio", "avatar.jpg");
        testArticle = new Article("Test Title", "Test Description", "Test Body", Arrays.asList("java", "spring"), testUser.getId());
        
        createArticleInput = CreateArticleInput.newBuilder()
            .title("Test Title")
            .description("Test Description")
            .body("Test Body")
            .tagList(Arrays.asList("java", "spring"))
            .build();
            
        updateArticleInput = UpdateArticleInput.newBuilder()
            .title("Updated Title")
            .description("Updated Description")
            .body("Updated Body")
            .build();
    }

    @Test
    void shouldCreateArticleSuccessfully() {
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));
            when(articleCommandService.createArticle(any(NewArticleParam.class), eq(testUser)))
                .thenReturn(testArticle);

            DataFetcherResult<ArticlePayload> result = articleMutation.createArticle(createArticleInput);

            assertNotNull(result);
            assertNotNull(result.getData());
            assertEquals(testArticle, result.getLocalContext());
            verify(articleCommandService).createArticle(any(NewArticleParam.class), eq(testUser));
        }
    }

    @Test
    void shouldCreateArticleWithEmptyTagList() {
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));
            when(articleCommandService.createArticle(any(NewArticleParam.class), eq(testUser)))
                .thenReturn(testArticle);

            CreateArticleInput inputWithNullTags = CreateArticleInput.newBuilder()
                .title("Test Title")
                .description("Test Description")
                .body("Test Body")
                .tagList(null)
                .build();

            DataFetcherResult<ArticlePayload> result = articleMutation.createArticle(inputWithNullTags);

            assertNotNull(result);
            verify(articleCommandService).createArticle(argThat(param -> 
                param.getTagList().equals(Collections.emptyList())), eq(testUser));
        }
    }

    @Test
    void shouldThrowAuthenticationExceptionWhenUserNotAuthenticated() {
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.empty());

            assertThrows(AuthenticationException.class, () -> {
                articleMutation.createArticle(createArticleInput);
            });
        }
    }

    @Test
    void shouldUpdateArticleSuccessfully() {
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));
            when(articleRepository.findBySlug("test-slug")).thenReturn(Optional.of(testArticle));
            when(articleCommandService.updateArticle(eq(testArticle), any(UpdateArticleParam.class)))
                .thenReturn(testArticle);

            DataFetcherResult<ArticlePayload> result = articleMutation.updateArticle("test-slug", updateArticleInput);

            assertNotNull(result);
            assertEquals(testArticle, result.getLocalContext());
            verify(articleRepository).findBySlug("test-slug");
            verify(articleCommandService).updateArticle(eq(testArticle), any(UpdateArticleParam.class));
        }
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenArticleNotFound() {
        when(articleRepository.findBySlug("nonexistent")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            articleMutation.updateArticle("nonexistent", updateArticleInput);
        });
    }

    @Test
    void shouldThrowNoAuthorizationExceptionWhenUserCannotWriteArticle() {
        User otherUser = new User("other@example.com", "otheruser", "password", "Bio", "image.jpg");
        Article otherUserArticle = new Article("Title", "Desc", "Body", Arrays.asList(), otherUser.getId());
        
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));
            when(articleRepository.findBySlug("other-slug")).thenReturn(Optional.of(otherUserArticle));

            assertThrows(NoAuthorizationException.class, () -> {
                articleMutation.updateArticle("other-slug", updateArticleInput);
            });
        }
    }

    @Test
    void shouldFavoriteArticleSuccessfully() {
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));
            when(articleRepository.findBySlug("test-slug")).thenReturn(Optional.of(testArticle));

            DataFetcherResult<ArticlePayload> result = articleMutation.favoriteArticle("test-slug");

            assertNotNull(result);
            assertEquals(testArticle, result.getLocalContext());
            verify(articleRepository).findBySlug("test-slug");
            verify(articleFavoriteRepository).save(any(ArticleFavorite.class));
        }
    }

    @Test
    void shouldUnfavoriteArticleSuccessfully() {
        ArticleFavorite existingFavorite = new ArticleFavorite(testArticle.getId(), testUser.getId());
        
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));
            when(articleRepository.findBySlug("test-slug")).thenReturn(Optional.of(testArticle));
            when(articleFavoriteRepository.find(testArticle.getId(), testUser.getId()))
                .thenReturn(Optional.of(existingFavorite));

            DataFetcherResult<ArticlePayload> result = articleMutation.unfavoriteArticle("test-slug");

            assertNotNull(result);
            assertEquals(testArticle, result.getLocalContext());
            verify(articleFavoriteRepository).remove(existingFavorite);
        }
    }

    @Test
    void shouldUnfavoriteArticleWhenNoFavoriteExists() {
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));
            when(articleRepository.findBySlug("test-slug")).thenReturn(Optional.of(testArticle));
            when(articleFavoriteRepository.find(testArticle.getId(), testUser.getId()))
                .thenReturn(Optional.empty());

            DataFetcherResult<ArticlePayload> result = articleMutation.unfavoriteArticle("test-slug");

            assertNotNull(result);
            assertEquals(testArticle, result.getLocalContext());
            verify(articleFavoriteRepository, never()).remove(any());
        }
    }

    @Test
    void shouldDeleteArticleSuccessfully() {
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));
            when(articleRepository.findBySlug("test-slug")).thenReturn(Optional.of(testArticle));

            DeletionStatus result = articleMutation.deleteArticle("test-slug");

            assertNotNull(result);
            assertTrue(result.getSuccess());
            verify(articleRepository).findBySlug("test-slug");
            verify(articleRepository).remove(testArticle);
        }
    }

    @Test
    void shouldThrowNoAuthorizationExceptionWhenUserCannotDeleteArticle() {
        User otherUser = new User("other@example.com", "otheruser", "password", "Bio", "image.jpg");
        Article otherUserArticle = new Article("Title", "Desc", "Body", Arrays.asList(), otherUser.getId());
        
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));
            when(articleRepository.findBySlug("other-slug")).thenReturn(Optional.of(otherUserArticle));

            assertThrows(NoAuthorizationException.class, () -> {
                articleMutation.deleteArticle("other-slug");
            });
        }
    }

    @Test
    void shouldThrowAuthenticationExceptionInFavoriteWhenNotAuthenticated() {
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.empty());

            assertThrows(AuthenticationException.class, () -> {
                articleMutation.favoriteArticle("test-slug");
            });
        }
    }

    @Test
    void shouldThrowResourceNotFoundExceptionInFavoriteWhenArticleNotFound() {
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));
            when(articleRepository.findBySlug("nonexistent")).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> {
                articleMutation.favoriteArticle("nonexistent");
            });
        }
    }
}
