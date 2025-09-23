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
import io.spring.graphql.exception.AuthenticationException;
import io.spring.api.exception.ResourceNotFoundException;
import io.spring.application.ProfileQueryService;
import io.spring.application.data.ProfileData;
import io.spring.core.user.UserRepository;
import io.spring.core.user.User;
import io.spring.graphql.types.ProfilePayload;
import java.util.Optional;
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
public class RelationMutationTest {

  @Mock private UserRepository userRepository;
  @Mock private ProfileQueryService profileQueryService;

  private RelationMutation relationMutation;
  private User currentUser;
  private User targetUser;
  private ProfileData profileData;

  @BeforeEach
  public void setUp() {
    relationMutation = new RelationMutation(userRepository, profileQueryService);
    currentUser = new User("current@example.com", "currentuser", "password123", "Current bio", "current.jpg");
    targetUser = new User("target@example.com", "targetuser", "password456", "Target bio", "target.jpg");
    
    profileData = new ProfileData(
        targetUser.getId(),
        targetUser.getUsername(),
        targetUser.getBio(),
        targetUser.getImage(),
        false
    );
  }

  @AfterEach
  public void cleanup() {
    SecurityContextHolder.clearContext();
  }

  @Test
  public void should_follow_user_successfully() {
    SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken(currentUser, null));
    
    when(userRepository.findByUsername(eq(targetUser.getUsername()))).thenReturn(Optional.of(targetUser));
    
    ProfileData followedProfileData = new ProfileData(
        targetUser.getId(),
        targetUser.getUsername(),
        targetUser.getBio(),
        targetUser.getImage(),
        true
    );
    when(profileQueryService.findByUsername(eq(targetUser.getUsername()), eq(currentUser)))
        .thenReturn(Optional.of(followedProfileData));

    ProfilePayload result = relationMutation.follow(targetUser.getUsername());

    assertThat(result, notNullValue());
    assertThat(result.getProfile(), notNullValue());
    verify(userRepository).findByUsername(eq(targetUser.getUsername()));
    verify(userRepository).saveRelation(any());
    verify(profileQueryService).findByUsername(eq(targetUser.getUsername()), eq(currentUser));
  }

  @Test
  public void should_unfollow_user_successfully() {
    SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken(currentUser, null));
    
    when(userRepository.findByUsername(eq(targetUser.getUsername()))).thenReturn(Optional.of(targetUser));
    when(userRepository.findRelation(eq(currentUser.getId()), eq(targetUser.getId()))).thenReturn(Optional.of(new io.spring.core.user.FollowRelation(currentUser.getId(), targetUser.getId())));
    
    ProfileData unfollowedProfileData = new ProfileData(
        targetUser.getId(),
        targetUser.getUsername(),
        targetUser.getBio(),
        targetUser.getImage(),
        false
    );
    when(profileQueryService.findByUsername(eq(targetUser.getUsername()), eq(currentUser)))
        .thenReturn(Optional.of(unfollowedProfileData));

    ProfilePayload result = relationMutation.unfollow(targetUser.getUsername());

    assertThat(result, notNullValue());
    assertThat(result.getProfile(), notNullValue());
    verify(userRepository).findByUsername(eq(targetUser.getUsername()));
    verify(userRepository).findRelation(eq(currentUser.getId()), eq(targetUser.getId()));
    verify(userRepository).removeRelation(any());
    verify(profileQueryService).findByUsername(eq(targetUser.getUsername()), eq(currentUser));
  }

  @Test
  public void should_throw_exception_when_following_without_authentication() {
    SecurityContextHolder.getContext().setAuthentication(
        new AnonymousAuthenticationToken("key", "anonymous", java.util.Arrays.asList(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))));

    assertThrows(AuthenticationException.class, () -> {
      relationMutation.follow(targetUser.getUsername());
    });
  }

  @Test
  public void should_throw_exception_when_unfollowing_without_authentication() {
    SecurityContextHolder.getContext().setAuthentication(
        new AnonymousAuthenticationToken("key", "anonymous", java.util.Arrays.asList(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))));

    assertThrows(AuthenticationException.class, () -> {
      relationMutation.unfollow(targetUser.getUsername());
    });
  }

  @Test
  public void should_throw_exception_when_target_user_not_found_for_follow() {
    SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken(currentUser, null));
    
    when(userRepository.findByUsername(eq("nonexistent"))).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> {
      relationMutation.follow("nonexistent");
    });
  }

  @Test
  public void should_throw_exception_when_target_user_not_found_for_unfollow() {
    SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken(currentUser, null));
    
    when(userRepository.findByUsername(eq("nonexistent"))).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> {
      relationMutation.unfollow("nonexistent");
    });
  }

  @Test
  public void should_throw_exception_when_relation_not_found_for_unfollow() {
    SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken(currentUser, null));
    
    when(userRepository.findByUsername(eq(targetUser.getUsername()))).thenReturn(Optional.of(targetUser));
    when(userRepository.findRelation(eq(currentUser.getId()), eq(targetUser.getId()))).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> {
      relationMutation.unfollow(targetUser.getUsername());
    });
  }
}
