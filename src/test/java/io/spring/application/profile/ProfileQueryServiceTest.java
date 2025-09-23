package io.spring.application.profile;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import io.spring.application.ProfileQueryService;
import io.spring.application.data.ProfileData;
import io.spring.core.user.User;
import io.spring.core.user.UserRepository;
import io.spring.infrastructure.DbTestBase;
import io.spring.infrastructure.repository.MyBatisUserRepository;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

@Import({ProfileQueryService.class, MyBatisUserRepository.class})
public class ProfileQueryServiceTest extends DbTestBase {
  @Autowired private ProfileQueryService profileQueryService;
  @Autowired private UserRepository userRepository;

  @Test
  public void should_fetch_profile_success() {
    User currentUser = new User("a@test.com", "a", "123", "", "");
    User profileUser = new User("p@test.com", "p", "123", "", "");
    userRepository.save(profileUser);

    Optional<ProfileData> optional =
        profileQueryService.findByUsername(profileUser.getUsername(), currentUser);
    Assertions.assertTrue(optional.isPresent());
  }

  @Test
  public void should_return_empty_when_user_not_found() {
    User currentUser = new User("a@test.com", "a", "123", "", "");
    
    Optional<ProfileData> optional =
        profileQueryService.findByUsername("nonexistent", currentUser);
    
    assertThat(optional.isPresent(), is(false));
  }

  @Test
  public void should_fetch_profile_with_null_current_user() {
    User profileUser = new User("p@test.com", "p", "123", "Bio", "image.jpg");
    userRepository.save(profileUser);

    Optional<ProfileData> optional =
        profileQueryService.findByUsername(profileUser.getUsername(), null);
    
    assertThat(optional.isPresent(), is(true));
    ProfileData profileData = optional.get();
    assertThat(profileData.getUsername(), is("p"));
    assertThat(profileData.getBio(), is("Bio"));
    assertThat(profileData.getImage(), is("image.jpg"));
    assertThat(profileData.isFollowing(), is(false));
  }

  @Test
  public void should_fetch_profile_with_following_relationship() {
    User currentUser = new User("a@test.com", "a", "123", "", "");
    User profileUser = new User("p@test.com", "p", "123", "Bio", "image.jpg");
    userRepository.save(currentUser);
    userRepository.save(profileUser);
    
    userRepository.saveRelation(new io.spring.core.user.FollowRelation(currentUser.getId(), profileUser.getId()));

    Optional<ProfileData> optional =
        profileQueryService.findByUsername(profileUser.getUsername(), currentUser);
    
    assertThat(optional.isPresent(), is(true));
    ProfileData profileData = optional.get();
    assertThat(profileData.getUsername(), is("p"));
    assertThat(profileData.getBio(), is("Bio"));
    assertThat(profileData.getImage(), is("image.jpg"));
    assertThat(profileData.isFollowing(), is(true));
  }

  @Test
  public void should_fetch_profile_without_following_relationship() {
    User currentUser = new User("a@test.com", "a", "123", "", "");
    User profileUser = new User("p@test.com", "p", "123", "Bio", "image.jpg");
    userRepository.save(currentUser);
    userRepository.save(profileUser);

    Optional<ProfileData> optional =
        profileQueryService.findByUsername(profileUser.getUsername(), currentUser);
    
    assertThat(optional.isPresent(), is(true));
    ProfileData profileData = optional.get();
    assertThat(profileData.getUsername(), is("p"));
    assertThat(profileData.getBio(), is("Bio"));
    assertThat(profileData.getImage(), is("image.jpg"));
    assertThat(profileData.isFollowing(), is(false));
  }

  @Test
  public void should_return_empty_when_user_not_found_with_null_current_user() {
    Optional<ProfileData> optional =
        profileQueryService.findByUsername("nonexistent", null);
    
    assertThat(optional.isPresent(), is(false));
  }

  @Test
  public void should_handle_edge_cases_with_empty_bio_and_image() {
    User currentUser = new User("a@test.com", "a", "123", "", "");
    User profileUser = new User("p@test.com", "p", "123", "", "");
    userRepository.save(currentUser);
    userRepository.save(profileUser);

    Optional<ProfileData> optional =
        profileQueryService.findByUsername(profileUser.getUsername(), currentUser);
    
    assertThat(optional.isPresent(), is(true));
    ProfileData profileData = optional.get();
    assertThat(profileData.getUsername(), is("p"));
    assertThat(profileData.getBio(), is(""));
    assertThat(profileData.getImage(), is(""));
    assertThat(profileData.isFollowing(), is(false));
  }
}
