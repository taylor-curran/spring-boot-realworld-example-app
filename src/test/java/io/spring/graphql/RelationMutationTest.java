package io.spring.graphql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.spring.api.exception.ResourceNotFoundException;
import io.spring.application.ProfileQueryService;
import io.spring.application.data.ProfileData;
import io.spring.core.user.FollowRelation;
import io.spring.core.user.User;
import io.spring.core.user.UserRepository;
import io.spring.graphql.exception.AuthenticationException;
import io.spring.graphql.types.Profile;
import io.spring.graphql.types.ProfilePayload;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RelationMutationTest {

  @Mock private UserRepository userRepository;

  @Mock private ProfileQueryService profileQueryService;

  @InjectMocks private RelationMutation relationMutation;

  private User currentUser;
  private User targetUser;
  private ProfileData profileData;

  @BeforeEach
  public void setUp() {
    currentUser = new User("current@example.com", "currentuser", "password", "bio", "image.jpg");
    targetUser =
        new User("target@example.com", "targetuser", "password", "target bio", "target.jpg");
    profileData =
        new ProfileData(
            targetUser.getId(),
            targetUser.getUsername(),
            targetUser.getBio(),
            targetUser.getImage(),
            false);
  }

  @Test
  public void should_follow_user_successfully() {
    String username = "targetuser";

    when(userRepository.findByUsername(username)).thenReturn(Optional.of(targetUser));
    when(profileQueryService.findByUsername(username, currentUser))
        .thenReturn(Optional.of(profileData));

    try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
      securityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(currentUser));

      ProfilePayload result = relationMutation.follow(username);

      assertThat(result).isNotNull();
      assertThat(result.getProfile()).isNotNull();
      assertThat(result.getProfile().getUsername()).isEqualTo(username);
      verify(userRepository).saveRelation(any(FollowRelation.class));
      verify(profileQueryService).findByUsername(username, currentUser);
    }
  }

  @Test
  public void should_throw_authentication_exception_when_user_not_authenticated_for_follow() {
    String username = "targetuser";

    try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
      securityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.empty());

      try {
        relationMutation.follow(username);
      } catch (AuthenticationException e) {
        assertThat(e).isInstanceOf(AuthenticationException.class);
      }

      verify(userRepository, never()).saveRelation(any(FollowRelation.class));
    }
  }

  @Test
  public void should_throw_resource_not_found_when_target_user_not_exists_for_follow() {
    String username = "nonexistentuser";

    when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

    try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
      securityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(currentUser));

      try {
        relationMutation.follow(username);
      } catch (ResourceNotFoundException e) {
        assertThat(e).isInstanceOf(ResourceNotFoundException.class);
      }

      verify(userRepository, never()).saveRelation(any(FollowRelation.class));
    }
  }

  @Test
  public void should_unfollow_user_successfully() {
    String username = "targetuser";
    FollowRelation followRelation = new FollowRelation(currentUser.getId(), targetUser.getId());

    when(userRepository.findByUsername(username)).thenReturn(Optional.of(targetUser));
    when(userRepository.findRelation(currentUser.getId(), targetUser.getId()))
        .thenReturn(Optional.of(followRelation));
    when(profileQueryService.findByUsername(username, currentUser))
        .thenReturn(Optional.of(profileData));

    try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
      securityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(currentUser));

      ProfilePayload result = relationMutation.unfollow(username);

      assertThat(result).isNotNull();
      assertThat(result.getProfile()).isNotNull();
      assertThat(result.getProfile().getUsername()).isEqualTo(username);
      verify(userRepository).removeRelation(followRelation);
      verify(profileQueryService).findByUsername(username, currentUser);
    }
  }

  @Test
  public void should_throw_authentication_exception_when_user_not_authenticated_for_unfollow() {
    String username = "targetuser";

    try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
      securityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.empty());

      try {
        relationMutation.unfollow(username);
      } catch (AuthenticationException e) {
        assertThat(e).isInstanceOf(AuthenticationException.class);
      }

      verify(userRepository, never()).removeRelation(any(FollowRelation.class));
    }
  }

  @Test
  public void should_throw_resource_not_found_when_target_user_not_exists_for_unfollow() {
    String username = "nonexistentuser";

    when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

    try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
      securityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(currentUser));

      try {
        relationMutation.unfollow(username);
      } catch (ResourceNotFoundException e) {
        assertThat(e).isInstanceOf(ResourceNotFoundException.class);
      }

      verify(userRepository, never()).removeRelation(any(FollowRelation.class));
    }
  }

  @Test
  public void should_throw_resource_not_found_when_follow_relation_not_exists() {
    String username = "targetuser";

    when(userRepository.findByUsername(username)).thenReturn(Optional.of(targetUser));
    when(userRepository.findRelation(currentUser.getId(), targetUser.getId()))
        .thenReturn(Optional.empty());

    try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
      securityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(currentUser));

      try {
        relationMutation.unfollow(username);
      } catch (ResourceNotFoundException e) {
        assertThat(e).isInstanceOf(ResourceNotFoundException.class);
      }

      verify(userRepository, never()).removeRelation(any(FollowRelation.class));
    }
  }

  @Test
  public void should_build_profile_correctly() {
    String username = "targetuser";
    ProfileData profileDataWithFollowing =
        new ProfileData(
            targetUser.getId(),
            targetUser.getUsername(),
            targetUser.getBio(),
            targetUser.getImage(),
            true);

    when(userRepository.findByUsername(username)).thenReturn(Optional.of(targetUser));
    when(profileQueryService.findByUsername(username, currentUser))
        .thenReturn(Optional.of(profileDataWithFollowing));

    try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
      securityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(currentUser));

      ProfilePayload result = relationMutation.follow(username);

      assertThat(result).isNotNull();
      Profile profile = result.getProfile();
      assertThat(profile.getUsername()).isEqualTo(targetUser.getUsername());
      assertThat(profile.getBio()).isEqualTo(targetUser.getBio());
      assertThat(profile.getImage()).isEqualTo(targetUser.getImage());
      assertThat(profile.getFollowing()).isTrue();
    }
  }
}
