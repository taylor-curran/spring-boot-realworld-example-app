package io.spring.graphql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
import java.util.Arrays;
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
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ProfileDatafetcherTest {

  @Mock
  private ProfileQueryService profileQueryService;

  @InjectMocks
  private ProfileDatafetcher profileDatafetcher;

  @Mock
  private DataFetchingEnvironment dataFetchingEnvironment;

  private User testUser;
  private ProfileData testProfileData;

  @BeforeEach
  public void setUp() {
    testUser = new User("test@example.com", "testuser", "password", "bio", "image.jpg");
    testProfileData = new ProfileData("user123", "testuser", "Test bio", "image.jpg", false);
  }

  @Test
  public void should_get_user_profile() {
    when(dataFetchingEnvironment.getLocalContext()).thenReturn(testUser);
    when(profileQueryService.findByUsername(eq("testuser"), any())).thenReturn(Optional.of(testProfileData));

    try (MockedStatic<SecurityUtil> mockedSecurityUtil = Mockito.mockStatic(SecurityUtil.class)) {
      mockedSecurityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));

      Profile result = profileDatafetcher.getUserProfile(dataFetchingEnvironment);

      assertThat(result).isNotNull();
      assertThat(result.getUsername()).isEqualTo("testuser");
      assertThat(result.getBio()).isEqualTo("Test bio");
      assertThat(result.getImage()).isEqualTo("image.jpg");
      assertThat(result.getFollowing()).isFalse();
    }
  }

  @Test
  public void should_get_article_author() {
    Article article = Article.newBuilder().slug("test-article").build();
    ProfileData authorProfileData = new ProfileData("author123", "author", "Author bio", "author.jpg", true);
    ArticleData articleData = new ArticleData("article123", "test-article", "Test Article", "Description", "Body", 
        false, 0, DateTime.now(), DateTime.now(), Arrays.asList("tag1"), authorProfileData);
    
    Map<String, ArticleData> articleMap = new HashMap<>();
    articleMap.put("test-article", articleData);

    when(dataFetchingEnvironment.getLocalContext()).thenReturn(articleMap);
    when(dataFetchingEnvironment.getSource()).thenReturn(article);
    when(profileQueryService.findByUsername(eq("author"), any())).thenReturn(Optional.of(authorProfileData));

    try (MockedStatic<SecurityUtil> mockedSecurityUtil = Mockito.mockStatic(SecurityUtil.class)) {
      mockedSecurityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.empty());

      Profile result = profileDatafetcher.getAuthor(dataFetchingEnvironment);

      assertThat(result).isNotNull();
      assertThat(result.getUsername()).isEqualTo("author");
      assertThat(result.getBio()).isEqualTo("Author bio");
      assertThat(result.getImage()).isEqualTo("author.jpg");
      assertThat(result.getFollowing()).isTrue();
    }
  }

  @Test
  public void should_get_comment_author() {
    Comment comment = Comment.newBuilder().id("comment123").build();
    ProfileData commentAuthorProfileData = new ProfileData("commenter123", "commenter", "Commenter bio", "commenter.jpg", false);
    CommentData commentData = new CommentData("comment123", "Comment body", "article123", DateTime.now(), DateTime.now(), commentAuthorProfileData);
    
    Map<String, CommentData> commentMap = new HashMap<>();
    commentMap.put("comment123", commentData);

    when(dataFetchingEnvironment.getLocalContext()).thenReturn(commentMap);
    when(dataFetchingEnvironment.getSource()).thenReturn(comment);
    when(profileQueryService.findByUsername(eq("commenter"), any())).thenReturn(Optional.of(commentAuthorProfileData));

    try (MockedStatic<SecurityUtil> mockedSecurityUtil = Mockito.mockStatic(SecurityUtil.class)) {
      mockedSecurityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));

      Profile result = profileDatafetcher.getCommentAuthor(dataFetchingEnvironment);

      assertThat(result).isNotNull();
      assertThat(result.getUsername()).isEqualTo("commenter");
      assertThat(result.getBio()).isEqualTo("Commenter bio");
      assertThat(result.getImage()).isEqualTo("commenter.jpg");
      assertThat(result.getFollowing()).isFalse();
    }
  }

  @Test
  public void should_query_profile_by_username() {
    when(dataFetchingEnvironment.getArgument("username")).thenReturn("targetuser");
    when(profileQueryService.findByUsername(eq("targetuser"), any())).thenReturn(Optional.of(testProfileData));

    try (MockedStatic<SecurityUtil> mockedSecurityUtil = Mockito.mockStatic(SecurityUtil.class)) {
      mockedSecurityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));

      ProfilePayload result = profileDatafetcher.queryProfile("targetuser", dataFetchingEnvironment);

      assertThat(result).isNotNull();
      assertThat(result.getProfile()).isNotNull();
      assertThat(result.getProfile().getUsername()).isEqualTo("testuser");
      assertThat(result.getProfile().getBio()).isEqualTo("Test bio");
    }
  }

  @Test
  public void should_throw_exception_when_profile_not_found() {
    when(dataFetchingEnvironment.getArgument("username")).thenReturn("nonexistent");
    when(profileQueryService.findByUsername(eq("nonexistent"), any())).thenReturn(Optional.empty());

    try (MockedStatic<SecurityUtil> mockedSecurityUtil = Mockito.mockStatic(SecurityUtil.class)) {
      mockedSecurityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));

      assertThatThrownBy(() -> profileDatafetcher.queryProfile("nonexistent", dataFetchingEnvironment))
          .isInstanceOf(ResourceNotFoundException.class);
    }
  }

  @Test
  public void should_handle_null_current_user() {
    when(profileQueryService.findByUsername(eq("testuser"), eq(null))).thenReturn(Optional.of(testProfileData));

    try (MockedStatic<SecurityUtil> mockedSecurityUtil = Mockito.mockStatic(SecurityUtil.class)) {
      mockedSecurityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.empty());

      when(dataFetchingEnvironment.getLocalContext()).thenReturn(testUser);
      Profile result = profileDatafetcher.getUserProfile(dataFetchingEnvironment);

      assertThat(result).isNotNull();
      assertThat(result.getUsername()).isEqualTo("testuser");
    }
  }

  @Test
  public void should_handle_profile_with_following_true() {
    ProfileData followingProfileData = new ProfileData("user123", "testuser", "Test bio", "image.jpg", true);
    when(dataFetchingEnvironment.getLocalContext()).thenReturn(testUser);
    when(profileQueryService.findByUsername(eq("testuser"), any())).thenReturn(Optional.of(followingProfileData));

    try (MockedStatic<SecurityUtil> mockedSecurityUtil = Mockito.mockStatic(SecurityUtil.class)) {
      mockedSecurityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));

      Profile result = profileDatafetcher.getUserProfile(dataFetchingEnvironment);

      assertThat(result.getFollowing()).isTrue();
    }
  }

  @Test
  public void should_handle_empty_profile_fields() {
    ProfileData emptyProfileData = new ProfileData("user123", "", "", "", false);
    when(dataFetchingEnvironment.getLocalContext()).thenReturn(testUser);
    when(profileQueryService.findByUsername(eq("testuser"), any())).thenReturn(Optional.of(emptyProfileData));

    try (MockedStatic<SecurityUtil> mockedSecurityUtil = Mockito.mockStatic(SecurityUtil.class)) {
      mockedSecurityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));

      Profile result = profileDatafetcher.getUserProfile(dataFetchingEnvironment);

      assertThat(result.getUsername()).isEqualTo("");
      assertThat(result.getBio()).isEqualTo("");
      assertThat(result.getImage()).isEqualTo("");
    }
  }
}
