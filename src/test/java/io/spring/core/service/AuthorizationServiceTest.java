package io.spring.core.service;

import static org.junit.jupiter.api.Assertions.*;

import io.spring.core.article.Article;
import io.spring.core.comment.Comment;
import io.spring.core.user.User;
import java.util.List;
import org.junit.jupiter.api.Test;

class AuthorizationServiceTest {

    @Test
    void shouldCreateAuthorizationServiceInstance() {
        AuthorizationService service = new AuthorizationService();
        
        assertNotNull(service);
    }

    @Test
    void shouldAllowArticleOwnerToWriteArticle() {
        User user = new User("test@example.com", "testuser", "password", "bio", "image.jpg");
        Article article = new Article("Test Title", "Test desc", "Test body", List.of("tag1"), user.getId());

        boolean canWrite = AuthorizationService.canWriteArticle(user, article);

        assertTrue(canWrite);
    }

    @Test
    void shouldDenyNonOwnerToWriteArticle() {
        User user = new User("test@example.com", "testuser", "password", "bio", "image.jpg");
        User otherUser = new User("other@example.com", "otheruser", "password", "bio", "image.jpg");
        Article article = new Article("Test Title", "Test desc", "Test body", List.of("tag1"), otherUser.getId());

        boolean canWrite = AuthorizationService.canWriteArticle(user, article);

        assertFalse(canWrite);
    }

    @Test
    void shouldAllowArticleOwnerToWriteComment() {
        User user = new User("test@example.com", "testuser", "password", "bio", "image.jpg");
        Article article = new Article("Test Title", "Test desc", "Test body", List.of("tag1"), user.getId());
        Comment comment = new Comment("Test comment", "someuser", article.getId());

        boolean canWrite = AuthorizationService.canWriteComment(user, article, comment);

        assertTrue(canWrite);
    }

    @Test
    void shouldAllowCommentOwnerToWriteComment() {
        User user = new User("test@example.com", "testuser", "password", "bio", "image.jpg");
        User articleOwner = new User("owner@example.com", "owner", "password", "bio", "image.jpg");
        Article article = new Article("Test Title", "Test desc", "Test body", List.of("tag1"), articleOwner.getId());
        Comment comment = new Comment("Test comment", user.getId(), article.getId());

        boolean canWrite = AuthorizationService.canWriteComment(user, article, comment);

        assertTrue(canWrite);
    }

    @Test
    void shouldDenyNonOwnerToWriteComment() {
        User user = new User("test@example.com", "testuser", "password", "bio", "image.jpg");
        User articleOwner = new User("owner@example.com", "owner", "password", "bio", "image.jpg");
        User commentOwner = new User("commenter@example.com", "commenter", "password", "bio", "image.jpg");
        Article article = new Article("Test Title", "Test desc", "Test body", List.of("tag1"), articleOwner.getId());
        Comment comment = new Comment("Test comment", commentOwner.getId(), article.getId());

        boolean canWrite = AuthorizationService.canWriteComment(user, article, comment);

        assertFalse(canWrite);
    }

    @Test
    void shouldHandleNullUserIdInArticle() {
        User user = new User("test@example.com", "testuser", "password", "bio", "image.jpg");
        Article article = new Article("Test Title", "Test desc", "Test body", List.of("tag1"), null);

        boolean canWrite = AuthorizationService.canWriteArticle(user, article);

        assertFalse(canWrite);
    }

    @Test
    void shouldHandleNullUserIdInComment() {
        User user = new User("test@example.com", "testuser", "password", "bio", "image.jpg");
        User articleOwner = new User("owner@example.com", "owner", "password", "bio", "image.jpg");
        Article article = new Article("Test Title", "Test desc", "Test body", List.of("tag1"), articleOwner.getId());
        Comment comment = new Comment("Test comment", null, article.getId());

        boolean canWrite = AuthorizationService.canWriteComment(user, article, comment);

        assertFalse(canWrite);
    }

    @Test
    void shouldThrowNullPointerExceptionWhenUserIdIsNull() {
        User user = new User() {
            @Override
            public String getId() {
                return null;
            }
            
            @Override
            public String getEmail() {
                return "test@example.com";
            }
            
            @Override
            public String getUsername() {
                return "testuser";
            }
            
            @Override
            public String getPassword() {
                return "password";
            }
            
            @Override
            public String getBio() {
                return "bio";
            }
            
            @Override
            public String getImage() {
                return "image.jpg";
            }
        };
        Article article = new Article("Test Title", "Test desc", "Test body", List.of("tag1"), "someuser");

        assertThrows(NullPointerException.class, () -> {
            AuthorizationService.canWriteArticle(user, article);
        });
    }

    @Test
    void shouldHandleSameUserIdForArticleAndComment() {
        User user = new User("test@example.com", "testuser", "password", "bio", "image.jpg");
        Article article = new Article("Test Title", "Test desc", "Test body", List.of("tag1"), user.getId());
        Comment comment = new Comment("Test comment", user.getId(), article.getId());

        boolean canWrite = AuthorizationService.canWriteComment(user, article, comment);

        assertTrue(canWrite);
    }

    @Test
    void shouldHandleEmptyUserIds() {
        User user = new User("test@example.com", "testuser", "password", "bio", "image.jpg");
        Article article = new Article("Test Title", "Test desc", "Test body", List.of("tag1"), "");
        Comment comment = new Comment("Test comment", "", article.getId());

        boolean canWriteArticle = AuthorizationService.canWriteArticle(user, article);
        boolean canWriteComment = AuthorizationService.canWriteComment(user, article, comment);

        assertFalse(canWriteArticle);
        assertFalse(canWriteComment);
    }

    @Test
    void shouldHandleSpecialCharactersInUserIds() {
        User user = new User("test@example.com", "testuser", "password", "bio", "image.jpg");
        String specialUserId = "user-123_test@domain.com";
        
        User userWithSpecialId = new User() {
            @Override
            public String getId() {
                return specialUserId;
            }
        };
        
        Article article = new Article("Test Title", "Test desc", "Test body", List.of("tag1"), specialUserId);

        boolean canWrite = AuthorizationService.canWriteArticle(userWithSpecialId, article);

        assertTrue(canWrite);
    }

    @Test
    void shouldHandleCaseSensitiveUserIds() {
        User user = new User("test@example.com", "testuser", "password", "bio", "image.jpg");
        String userId = user.getId();
        String upperCaseUserId = userId.toUpperCase();
        
        Article article = new Article("Test Title", "Test desc", "Test body", List.of("tag1"), upperCaseUserId);

        boolean canWrite = AuthorizationService.canWriteArticle(user, article);

        assertFalse(canWrite);
    }

    @Test
    void shouldHandleUnicodeInUserIds() {
        String unicodeUserId = "用户123-测试";
        
        User user = new User() {
            @Override
            public String getId() {
                return unicodeUserId;
            }
        };
        
        Article article = new Article("Test Title", "Test desc", "Test body", List.of("tag1"), unicodeUserId);

        boolean canWrite = AuthorizationService.canWriteArticle(user, article);

        assertTrue(canWrite);
    }
}
