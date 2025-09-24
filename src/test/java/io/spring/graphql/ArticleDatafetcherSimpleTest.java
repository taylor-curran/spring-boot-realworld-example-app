package io.spring.graphql;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.netflix.graphql.dgs.DgsDataFetchingEnvironment;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import io.spring.api.exception.ResourceNotFoundException;
import io.spring.application.ArticleQueryService;
import io.spring.application.CursorPager;
import io.spring.application.CursorPageParameter;
import io.spring.application.DateTimeCursor;
import io.spring.application.data.ArticleData;
import io.spring.application.data.CommentData;
import io.spring.application.data.ProfileData;
import io.spring.core.user.User;
import io.spring.core.user.UserRepository;
import io.spring.graphql.types.Article;
import io.spring.graphql.types.ArticlesConnection;
import io.spring.graphql.types.Profile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ArticleDatafetcherSimpleTest {

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

    private User currentUser;
    private ArticleData articleData;
    private ProfileData profileData;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        currentUser = new User("user@example.com", "testuser", "password", "", "");
        profileData = new ProfileData("user-id", "testuser", "Test Bio", "image.jpg", false);
        articleData = new ArticleData(
            "article-id",
            "test-article", 
            "Test Article",
            "Test Description",
            "Test Body",
            false,
            0,
            new DateTime(),
            new DateTime(),
            new ArrayList<>(),
            profileData
        );
    }

    @Test
    void shouldThrowExceptionWhenBothFirstAndLastAreNullInGetFeed() {
        assertThrows(IllegalArgumentException.class, () -> {
            articleDatafetcher.getFeed(null, null, null, null, dgsDataFetchingEnvironment);
        });
    }

    @Test
    void shouldThrowExceptionWhenBothFirstAndLastAreNullInUserFeed() {
        assertThrows(IllegalArgumentException.class, () -> {
            articleDatafetcher.userFeed(null, null, null, null, dgsDataFetchingEnvironment);
        });
    }

    @Test
    void shouldThrowExceptionWhenBothFirstAndLastAreNullInUserFavorites() {
        assertThrows(IllegalArgumentException.class, () -> {
            articleDatafetcher.userFavorites(null, null, null, null, dgsDataFetchingEnvironment);
        });
    }

    @Test
    void shouldThrowExceptionWhenBothFirstAndLastAreNullInUserArticles() {
        assertThrows(IllegalArgumentException.class, () -> {
            articleDatafetcher.userArticles(null, null, null, null, dgsDataFetchingEnvironment);
        });
    }

    @Test
    void shouldThrowExceptionWhenBothFirstAndLastAreNullInGetArticles() {
        assertThrows(IllegalArgumentException.class, () -> {
            articleDatafetcher.getArticles(null, null, null, null, null, null, null, dgsDataFetchingEnvironment);
        });
    }

}
