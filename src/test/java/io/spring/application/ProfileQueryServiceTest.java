package io.spring.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.spring.application.data.ProfileData;
import io.spring.application.data.UserData;
import io.spring.core.user.User;
import io.spring.infrastructure.mybatis.readservice.UserReadService;
import io.spring.infrastructure.mybatis.readservice.UserRelationshipQueryService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ProfileQueryServiceTest {

  @Mock private UserReadService userReadService;

  @Mock private UserRelationshipQueryService userRelationshipQueryService;

  @InjectMocks private ProfileQueryService profileQueryService;

  private User testUser;
  private UserData targetUserData;

  @BeforeEach
  public void setUp() {
    testUser = new User("test@example.com", "testuser", "password", "Test bio", "test.jpg");
    targetUserData =
        new UserData("target-id", "target@example.com", "targetuser", "Target bio", "target.jpg");
  }

  @Test
  public void should_find_profile_by_username_when_user_exists() {
    String username = "targetuser";
    User currentUser = testUser;

    when(userReadService.findByUsername(username)).thenReturn(targetUserData);
    when(userRelationshipQueryService.isUserFollowing(currentUser.getId(), targetUserData.getId()))
        .thenReturn(false);

    Optional<ProfileData> result = profileQueryService.findByUsername(username, currentUser);

    assertThat(result).isPresent();
    ProfileData profileData = result.get();
    assertThat(profileData.getUsername()).isEqualTo("targetuser");
    assertThat(profileData.getBio()).isEqualTo("Target bio");
    assertThat(profileData.getImage()).isEqualTo("target.jpg");
    assertThat(profileData.isFollowing()).isFalse();

    verify(userReadService).findByUsername(username);
    verify(userRelationshipQueryService)
        .isUserFollowing(currentUser.getId(), targetUserData.getId());
  }

  @Test
  public void should_return_empty_when_user_not_found() {
    String username = "nonexistent";
    User currentUser = testUser;

    when(userReadService.findByUsername(username)).thenReturn(null);

    Optional<ProfileData> result = profileQueryService.findByUsername(username, currentUser);

    assertThat(result).isEmpty();
    verify(userReadService).findByUsername(username);
  }

  @Test
  public void should_show_following_true_when_relation_exists() {
    String username = "targetuser";
    User currentUser = testUser;

    when(userReadService.findByUsername(username)).thenReturn(targetUserData);
    when(userRelationshipQueryService.isUserFollowing(currentUser.getId(), targetUserData.getId()))
        .thenReturn(true);

    Optional<ProfileData> result = profileQueryService.findByUsername(username, currentUser);

    assertThat(result).isPresent();
    ProfileData profileData = result.get();
    assertThat(profileData.isFollowing()).isTrue();

    verify(userReadService).findByUsername(username);
    verify(userRelationshipQueryService)
        .isUserFollowing(currentUser.getId(), targetUserData.getId());
  }

  @Test
  public void should_handle_null_current_user() {
    String username = "targetuser";
    User currentUser = null;

    when(userReadService.findByUsername(username)).thenReturn(targetUserData);

    Optional<ProfileData> result = profileQueryService.findByUsername(username, currentUser);

    assertThat(result).isPresent();
    ProfileData profileData = result.get();
    assertThat(profileData.getUsername()).isEqualTo("targetuser");
    assertThat(profileData.isFollowing()).isFalse();

    verify(userReadService).findByUsername(username);
  }

  @Test
  public void should_handle_same_user_profile_lookup() {
    String username = "testuser";
    User currentUser = testUser;
    UserData currentUserData =
        new UserData(testUser.getId(), "test@example.com", "testuser", "Test bio", "test.jpg");

    when(userReadService.findByUsername(username)).thenReturn(currentUserData);
    when(userRelationshipQueryService.isUserFollowing(currentUser.getId(), currentUserData.getId()))
        .thenReturn(false);

    Optional<ProfileData> result = profileQueryService.findByUsername(username, currentUser);

    assertThat(result).isPresent();
    ProfileData profileData = result.get();
    assertThat(profileData.getUsername()).isEqualTo("testuser");
    assertThat(profileData.isFollowing()).isFalse();

    verify(userReadService).findByUsername(username);
  }
}
