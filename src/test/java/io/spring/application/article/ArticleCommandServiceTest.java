package io.spring.application.article;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.spring.core.article.Article;
import io.spring.core.article.ArticleRepository;
import io.spring.core.user.User;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ArticleCommandServiceTest {

    @Mock
    private ArticleRepository articleRepository;

    @InjectMocks
    private ArticleCommandService articleCommandService;

    @Test
    void shouldCreateArticleSuccessfully() {
        User creator = new User("test@example.com", "testuser", "password", "bio", "image.jpg");
        NewArticleParam param = NewArticleParam.builder()
            .title("Test Article")
            .description("Test description")
            .body("Test body content")
            .tagList(List.of("java", "spring"))
            .build();

        Article result = articleCommandService.createArticle(param, creator);

        assertNotNull(result);
        assertEquals("Test Article", result.getTitle());
        assertEquals("Test description", result.getDescription());
        assertEquals("Test body content", result.getBody());
        assertEquals(creator.getId(), result.getUserId());
        assertEquals(2, result.getTags().size());
        verify(articleRepository).save(result);
    }

    @Test
    void shouldCreateArticleWithEmptyTagList() {
        User creator = new User("test@example.com", "testuser", "password", "bio", "image.jpg");
        NewArticleParam param = NewArticleParam.builder()
            .title("Test Article")
            .description("Test description")
            .body("Test body content")
            .tagList(List.of())
            .build();

        Article result = articleCommandService.createArticle(param, creator);

        assertNotNull(result);
        assertEquals("Test Article", result.getTitle());
        assertEquals(0, result.getTags().size());
        verify(articleRepository).save(result);
    }

    @Test
    void shouldCreateArticleWithNullTagList() {
        User creator = new User("test@example.com", "testuser", "password", "bio", "image.jpg");
        NewArticleParam param = NewArticleParam.builder()
            .title("Test Article")
            .description("Test description")
            .body("Test body content")
            .tagList(null)
            .build();

        assertThrows(NullPointerException.class, () -> {
            articleCommandService.createArticle(param, creator);
        });
    }

    @Test
    void shouldCreateArticleWithSpecialCharacters() {
        User creator = new User("test@example.com", "testuser", "password", "bio", "image.jpg");
        NewArticleParam param = NewArticleParam.builder()
            .title("Test Article with Special Chars!@#$")
            .description("Description with unicode: 测试 🌍")
            .body("Body with newlines\nand tabs\tand special chars")
            .tagList(List.of("test-tag", "special_chars"))
            .build();

        Article result = articleCommandService.createArticle(param, creator);

        assertNotNull(result);
        assertEquals("Test Article with Special Chars!@#$", result.getTitle());
        assertEquals("Description with unicode: 测试 🌍", result.getDescription());
        assertEquals("Body with newlines\nand tabs\tand special chars", result.getBody());
        verify(articleRepository).save(result);
    }

    @Test
    void shouldCreateArticleWithLongContent() {
        User creator = new User("test@example.com", "testuser", "password", "bio", "image.jpg");
        String longTitle = "Very Long Article Title ".repeat(10);
        String longDescription = "Very long description content ".repeat(50);
        String longBody = "Very long body content with multiple paragraphs ".repeat(100);
        
        NewArticleParam param = NewArticleParam.builder()
            .title(longTitle)
            .description(longDescription)
            .body(longBody)
            .tagList(List.of("long-content"))
            .build();

        Article result = articleCommandService.createArticle(param, creator);

        assertNotNull(result);
        assertEquals(longTitle, result.getTitle());
        assertEquals(longDescription, result.getDescription());
        assertEquals(longBody, result.getBody());
        verify(articleRepository).save(result);
    }

    @Test
    void shouldUpdateArticleSuccessfully() {
        User creator = new User("test@example.com", "testuser", "password", "bio", "image.jpg");
        Article existingArticle = new Article("Original Title", "Original desc", "Original body", List.of("tag1"), creator.getId());
        
        UpdateArticleParam param = new UpdateArticleParam("Updated Title", "Updated body content", "Updated description");

        Article result = articleCommandService.updateArticle(existingArticle, param);

        assertNotNull(result);
        assertEquals("Updated Title", result.getTitle());
        assertEquals("Updated description", result.getDescription());
        assertEquals("Updated body content", result.getBody());
        verify(articleRepository).save(existingArticle);
    }

    @Test
    void shouldUpdateArticleWithPartialData() {
        User creator = new User("test@example.com", "testuser", "password", "bio", "image.jpg");
        Article existingArticle = new Article("Original Title", "Original desc", "Original body", List.of("tag1"), creator.getId());
        
        UpdateArticleParam param = new UpdateArticleParam("Updated Title Only", null, null);

        Article result = articleCommandService.updateArticle(existingArticle, param);

        assertNotNull(result);
        assertEquals("Updated Title Only", result.getTitle());
        verify(articleRepository).save(existingArticle);
    }

    @Test
    void shouldUpdateArticleWithEmptyStrings() {
        User creator = new User("test@example.com", "testuser", "password", "bio", "image.jpg");
        Article existingArticle = new Article("Original Title", "Original desc", "Original body", List.of("tag1"), creator.getId());
        
        UpdateArticleParam param = new UpdateArticleParam("", "", "");

        Article result = articleCommandService.updateArticle(existingArticle, param);

        assertNotNull(result);
        assertEquals("Original Title", result.getTitle());
        assertEquals("Original desc", result.getDescription());
        assertEquals("Original body", result.getBody());
        verify(articleRepository).save(existingArticle);
    }

    @Test
    void shouldUpdateArticleWithSpecialCharacters() {
        User creator = new User("test@example.com", "testuser", "password", "bio", "image.jpg");
        Article existingArticle = new Article("Original Title", "Original desc", "Original body", List.of("tag1"), creator.getId());
        
        UpdateArticleParam param = new UpdateArticleParam("Updated Title with Special Chars!@#$", "Updated body with\nnewlines and\ttabs", "Updated description with unicode: 测试 🚀");

        Article result = articleCommandService.updateArticle(existingArticle, param);

        assertNotNull(result);
        assertEquals("Updated Title with Special Chars!@#$", result.getTitle());
        assertEquals("Updated description with unicode: 测试 🚀", result.getDescription());
        assertEquals("Updated body with\nnewlines and\ttabs", result.getBody());
        verify(articleRepository).save(existingArticle);
    }

    @Test
    void shouldHandleRepositoryInteraction() {
        User creator = new User("test@example.com", "testuser", "password", "bio", "image.jpg");
        NewArticleParam param = NewArticleParam.builder()
            .title("Test Article")
            .description("Test description")
            .body("Test body content")
            .tagList(List.of("test"))
            .build();

        articleCommandService.createArticle(param, creator);

        verify(articleRepository, times(1)).save(any(Article.class));
    }

    @Test
    void shouldHandleMultipleArticleCreations() {
        User creator = new User("test@example.com", "testuser", "password", "bio", "image.jpg");
        
        NewArticleParam param1 = NewArticleParam.builder()
            .title("First Article")
            .description("First description")
            .body("First body")
            .tagList(List.of("first"))
            .build();

        NewArticleParam param2 = NewArticleParam.builder()
            .title("Second Article")
            .description("Second description")
            .body("Second body")
            .tagList(List.of("second"))
            .build();

        Article article1 = articleCommandService.createArticle(param1, creator);
        Article article2 = articleCommandService.createArticle(param2, creator);

        assertNotEquals(article1.getId(), article2.getId());
        assertNotEquals(article1.getSlug(), article2.getSlug());
        verify(articleRepository, times(2)).save(any(Article.class));
    }
}
