package io.spring.graphql;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import graphql.schema.DataFetchingEnvironment;
import io.spring.api.exception.ResourceNotFoundException;
import io.spring.application.ProfileQueryService;
import io.spring.application.data.ArticleData;
import io.spring.application.data.CommentData;
import io.spring.application.data.ProfileData;
import io.spring.core.user.User;
import io.spring.graphql.types.Article;
import io.spring.graphql.types.Comment;
import io.spring.graphql.types.Profile;
import io.spring.graphql.types.ProfilePayload;
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
class ProfileDatafetcherTest {

    @Mock
    private ProfileQueryService profileQueryService;

    @Mock
    private DataFetchingEnvironment dataFetchingEnvironment;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private ProfileDatafetcher profileDatafetcher;

    private User testUser;
    private ProfileData testProfileData;

    @BeforeEach
    void setUp() {
        testUser = new User("test@example.com", "testuser", "hashedpassword", "Test Bio", "avatar.jpg");
        testProfileData = new ProfileData("user1", "testuser", "Test Bio", "avatar.jpg", false);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void shouldGetUserProfile() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(testUser);
        when(dataFetchingEnvironment.getLocalContext()).thenReturn(testUser);
        when(profileQueryService.findByUsername(eq("testuser"), eq(testUser)))
            .thenReturn(Optional.of(testProfileData));

        Profile result = profileDatafetcher.getUserProfile(dataFetchingEnvironment);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("Test Bio", result.getBio());
        assertEquals("avatar.jpg", result.getImage());
        assertFalse(result.getFollowing());
        verify(profileQueryService).findByUsername(eq("testuser"), eq(testUser));
    }

    @Test
    void shouldGetAuthor() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(testUser);
        
        Article article = Article.newBuilder().slug("test-slug").build();
        ArticleData articleData = new ArticleData(
            "article1", "test-slug", "Test Title", "Test Description", "Test Body",
            false, 5, DateTime.now(), DateTime.now(), null, testProfileData
        );
        Map<String, ArticleData> articleMap = new HashMap<>();
        articleMap.put("test-slug", articleData);
        
        when(dataFetchingEnvironment.getLocalContext()).thenReturn(articleMap);
        when(dataFetchingEnvironment.getSource()).thenReturn(article);
        when(profileQueryService.findByUsername(eq("testuser"), eq(testUser)))
            .thenReturn(Optional.of(testProfileData));

        Profile result = profileDatafetcher.getAuthor(dataFetchingEnvironment);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(profileQueryService).findByUsername(eq("testuser"), eq(testUser));
    }

    @Test
    void shouldGetCommentAuthor() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(testUser);
        
        Comment comment = Comment.newBuilder().id("comment1").build();
        CommentData commentData = new CommentData(
            "comment1", "Test comment", "article1", DateTime.now(), DateTime.now(), testProfileData
        );
        Map<String, CommentData> commentMap = new HashMap<>();
        commentMap.put("comment1", commentData);
        
        when(dataFetchingEnvironment.getSource()).thenReturn(comment);
        when(dataFetchingEnvironment.getLocalContext()).thenReturn(commentMap);
        when(profileQueryService.findByUsername(eq("testuser"), eq(testUser)))
            .thenReturn(Optional.of(testProfileData));

        Profile result = profileDatafetcher.getCommentAuthor(dataFetchingEnvironment);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(profileQueryService).findByUsername(eq("testuser"), eq(testUser));
    }

    @Test
    void shouldQueryProfileWithArgument() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(testUser);
        when(dataFetchingEnvironment.getArgument("username")).thenReturn("testuser");
        when(profileQueryService.findByUsername(eq("testuser"), eq(testUser)))
            .thenReturn(Optional.of(testProfileData));

        ProfilePayload result = profileDatafetcher.queryProfile("testuser", dataFetchingEnvironment);

        assertNotNull(result);
        assertNotNull(result.getProfile());
        assertEquals("testuser", result.getProfile().getUsername());
        verify(profileQueryService).findByUsername(eq("testuser"), eq(testUser));
    }

    @Test
    void shouldThrowExceptionWhenProfileNotFound() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(testUser);
        when(dataFetchingEnvironment.getLocalContext()).thenReturn(testUser);
        when(profileQueryService.findByUsername(eq("testuser"), eq(testUser)))
            .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            profileDatafetcher.getUserProfile(dataFetchingEnvironment);
        });
    }

    @Test
    void shouldHandleNullCurrentUser() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(null);
        when(dataFetchingEnvironment.getLocalContext()).thenReturn(testUser);
        when(profileQueryService.findByUsername(eq("testuser"), isNull()))
            .thenReturn(Optional.of(testProfileData));

        Profile result = profileDatafetcher.getUserProfile(dataFetchingEnvironment);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(profileQueryService).findByUsername(eq("testuser"), isNull());
    }

    @Test
    void shouldHandleFollowingProfile() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(testUser);
        ProfileData followingProfileData = new ProfileData("user2", "followeduser", "Followed Bio", "followed.jpg", true);
        when(dataFetchingEnvironment.getLocalContext()).thenReturn(testUser);
        when(profileQueryService.findByUsername(eq("testuser"), eq(testUser)))
            .thenReturn(Optional.of(followingProfileData));

        Profile result = profileDatafetcher.getUserProfile(dataFetchingEnvironment);

        assertNotNull(result);
        assertEquals("followeduser", result.getUsername());
        assertTrue(result.getFollowing());
    }

    @Test
    void shouldBuildProfileCorrectly() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(testUser);
        ProfileData detailedProfileData = new ProfileData("user1", "detaileduser", "Detailed Bio", "detailed.jpg", false);
        when(dataFetchingEnvironment.getLocalContext()).thenReturn(testUser);
        when(profileQueryService.findByUsername(eq("testuser"), eq(testUser)))
            .thenReturn(Optional.of(detailedProfileData));

        Profile result = profileDatafetcher.getUserProfile(dataFetchingEnvironment);

        assertNotNull(result);
        assertEquals("detaileduser", result.getUsername());
        assertEquals("Detailed Bio", result.getBio());
        assertEquals("detailed.jpg", result.getImage());
        assertFalse(result.getFollowing());
    }
}
