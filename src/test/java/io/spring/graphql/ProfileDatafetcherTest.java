package io.spring.graphql;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import graphql.execution.DataFetcherResult;
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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
public class ProfileDatafetcherTest {

  @Mock private ProfileQueryService profileQueryService;
  @Mock private DataFetchingEnvironment dataFetchingEnvironment;

  private ProfileDatafetcher profileDatafetcher;
  private User currentUser;
  private ProfileData profileData;

  @BeforeEach
  public void setUp() {
    profileDatafetcher = new ProfileDatafetcher(profileQueryService);
    currentUser = new User("current@example.com", "currentuser", "password123", "Current bio", "current.jpg");
    
    profileData = new ProfileData(
        "profile-id",
        "testuser",
        "Test bio",
        "test.jpg",
        false
    );
  }

  @AfterEach
  public void cleanup() {
    SecurityContextHolder.clearContext();
  }

  @Test
  public void should_query_profile_by_username_for_authenticated_user() {
    SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken(currentUser, null));
    
    when(dataFetchingEnvironment.getArgument("username")).thenReturn("testuser");
    when(profileQueryService.findByUsername(eq("testuser"), eq(currentUser)))
        .thenReturn(Optional.of(profileData));

    ProfilePayload result = profileDatafetcher.queryProfile("testuser", dataFetchingEnvironment);

    assertThat(result, notNullValue());
    assertThat(result.getProfile(), notNullValue());
    assertThat(result.getProfile().getUsername(), is("testuser"));
    verify(profileQueryService).findByUsername(eq("testuser"), eq(currentUser));
  }

  @Test
  public void should_query_profile_by_username_for_anonymous_user() {
    SecurityContextHolder.getContext().setAuthentication(
        new AnonymousAuthenticationToken("key", "anonymous", java.util.Arrays.asList(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))));
    
    when(dataFetchingEnvironment.getArgument("username")).thenReturn("testuser");
    when(profileQueryService.findByUsername(eq("testuser"), eq(null)))
        .thenReturn(Optional.of(profileData));

    ProfilePayload result = profileDatafetcher.queryProfile("testuser", dataFetchingEnvironment);

    assertThat(result, notNullValue());
    assertThat(result.getProfile(), notNullValue());
    assertThat(result.getProfile().getUsername(), is("testuser"));
    verify(profileQueryService).findByUsername(eq("testuser"), eq(null));
  }

  @Test
  public void should_throw_exception_when_profile_not_found() {
    SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken(currentUser, null));
    
    when(dataFetchingEnvironment.getArgument("username")).thenReturn("nonexistent");
    when(profileQueryService.findByUsername(eq("nonexistent"), eq(currentUser)))
        .thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> {
      profileDatafetcher.queryProfile("nonexistent", dataFetchingEnvironment);
    });
  }

  @Test
  public void should_handle_no_authentication_context() {
    SecurityContextHolder.clearContext();
    
    when(dataFetchingEnvironment.getArgument("username")).thenReturn("testuser");
    when(profileQueryService.findByUsername(eq("testuser"), eq(null)))
        .thenReturn(Optional.of(profileData));

    ProfilePayload result = profileDatafetcher.queryProfile("testuser", dataFetchingEnvironment);

    assertThat(result, notNullValue());
    assertThat(result.getProfile(), notNullValue());
    assertThat(result.getProfile().getUsername(), is("testuser"));
    verify(profileQueryService).findByUsername(eq("testuser"), eq(null));
  }

  @Test
  public void should_query_profile_with_following_status_true() {
    SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken(currentUser, null));
    
    ProfileData followingProfileData = new ProfileData(
        "profile-id",
        "testuser",
        "Test bio",
        "test.jpg",
        true
    );
    
    when(dataFetchingEnvironment.getArgument("username")).thenReturn("testuser");
    when(profileQueryService.findByUsername(eq("testuser"), eq(currentUser)))
        .thenReturn(Optional.of(followingProfileData));

    ProfilePayload result = profileDatafetcher.queryProfile("testuser", dataFetchingEnvironment);

    assertThat(result, notNullValue());
    assertThat(result.getProfile(), notNullValue());
    assertThat(result.getProfile().getFollowing(), is(true));
    verify(profileQueryService).findByUsername(eq("testuser"), eq(currentUser));
  }

  @Test
  public void should_handle_profile_with_null_bio() {
    SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken(currentUser, null));
    
    ProfileData profileWithNullBio = new ProfileData(
        "profile-id",
        "testuser",
        null,
        "test.jpg",
        false
    );
    
    when(dataFetchingEnvironment.getArgument("username")).thenReturn("testuser");
    when(profileQueryService.findByUsername(eq("testuser"), eq(currentUser)))
        .thenReturn(Optional.of(profileWithNullBio));

    ProfilePayload result = profileDatafetcher.queryProfile("testuser", dataFetchingEnvironment);

    assertThat(result, notNullValue());
    assertThat(result.getProfile(), notNullValue());
    assertThat(result.getProfile().getBio(), is((String) null));
    verify(profileQueryService).findByUsername(eq("testuser"), eq(currentUser));
  }

  @Test
  public void should_get_user_profile_from_context() {
    SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken(currentUser, null));
    
    when(dataFetchingEnvironment.getLocalContext()).thenReturn(currentUser);
    when(profileQueryService.findByUsername(eq("currentuser"), eq(currentUser)))
        .thenReturn(Optional.of(profileData));

    Profile result = profileDatafetcher.getUserProfile(dataFetchingEnvironment);

    assertThat(result, notNullValue());
    assertThat(result.getUsername(), is("testuser"));
    verify(profileQueryService).findByUsername(eq("currentuser"), eq(currentUser));
  }

  @Test
  public void should_get_article_author_profile() {
    SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken(currentUser, null));
    
    Article article = Article.newBuilder().slug("test-article").build();
    DateTime now = DateTime.now();
    ArticleData articleData = new ArticleData("article-id", "test-article", "Test Title", "Test Description", 
        "Test Body", false, 0, now, now, null, profileData);
    
    Map<String, ArticleData> articleMap = new HashMap<>();
    articleMap.put("test-article", articleData);
    
    when(dataFetchingEnvironment.getLocalContext()).thenReturn(articleMap);
    when(dataFetchingEnvironment.getSource()).thenReturn(article);
    when(profileQueryService.findByUsername(eq("testuser"), eq(currentUser)))
        .thenReturn(Optional.of(profileData));

    Profile result = profileDatafetcher.getAuthor(dataFetchingEnvironment);

    assertThat(result, notNullValue());
    assertThat(result.getUsername(), is("testuser"));
    verify(profileQueryService).findByUsername(eq("testuser"), eq(currentUser));
  }

  @Test
  public void should_get_comment_author_profile() {
    SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken(currentUser, null));
    
    Comment comment = Comment.newBuilder().id("comment-id").build();
    DateTime now = DateTime.now();
    CommentData commentData = new CommentData("comment-id", "Test comment", "article-id", now, now, profileData);
    
    Map<String, CommentData> commentMap = new HashMap<>();
    commentMap.put("comment-id", commentData);
    
    when(dataFetchingEnvironment.getLocalContext()).thenReturn(commentMap);
    when(dataFetchingEnvironment.getSource()).thenReturn(comment);
    when(profileQueryService.findByUsername(eq("testuser"), eq(currentUser)))
        .thenReturn(Optional.of(profileData));

    Profile result = profileDatafetcher.getCommentAuthor(dataFetchingEnvironment);

    assertThat(result, notNullValue());
    assertThat(result.getUsername(), is("testuser"));
    verify(profileQueryService).findByUsername(eq("testuser"), eq(currentUser));
  }
}
