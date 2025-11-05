package io.spring.application.data;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

public class ProfileDataTest {

  @Test
  public void should_create_profile_data_with_all_fields() {
    String id = "user-id";
    String username = "testuser";
    String bio = "Test bio";
    String image = "image.jpg";
    boolean following = true;

    ProfileData profileData = new ProfileData(id, username, bio, image, following);

    assertThat(profileData.getId()).isEqualTo(id);
    assertThat(profileData.getUsername()).isEqualTo(username);
    assertThat(profileData.getBio()).isEqualTo(bio);
    assertThat(profileData.getImage()).isEqualTo(image);
    assertThat(profileData.isFollowing()).isEqualTo(following);
  }

  @Test
  public void should_create_profile_data_with_no_args_constructor() {
    ProfileData profileData = new ProfileData();
    
    assertThat(profileData.getId()).isNull();
    assertThat(profileData.getUsername()).isNull();
    assertThat(profileData.getBio()).isNull();
    assertThat(profileData.getImage()).isNull();
    assertThat(profileData.isFollowing()).isFalse();
  }

  @Test
  public void should_set_and_get_all_fields() {
    ProfileData profileData = new ProfileData();
    String id = "test-id";
    String username = "testuser";
    String bio = "Test bio";
    String image = "test.jpg";
    boolean following = true;

    profileData.setId(id);
    profileData.setUsername(username);
    profileData.setBio(bio);
    profileData.setImage(image);
    profileData.setFollowing(following);

    assertThat(profileData.getId()).isEqualTo(id);
    assertThat(profileData.getUsername()).isEqualTo(username);
    assertThat(profileData.getBio()).isEqualTo(bio);
    assertThat(profileData.getImage()).isEqualTo(image);
    assertThat(profileData.isFollowing()).isEqualTo(following);
  }

  @Test
  public void should_handle_following_status() {
    ProfileData profileData = new ProfileData();
    
    profileData.setFollowing(true);
    assertThat(profileData.isFollowing()).isTrue();
    
    profileData.setFollowing(false);
    assertThat(profileData.isFollowing()).isFalse();
  }

  @Test
  public void should_handle_profile_image_operations() {
    ProfileData profileData = new ProfileData();
    String imageUrl = "https://example.com/avatar.jpg";
    
    profileData.setImage(imageUrl);
    
    assertThat(profileData.getImage()).isEqualTo(imageUrl);
    assertThat(profileData.getImage()).startsWith("https://");
  }

  @Test
  public void should_handle_null_values() {
    ProfileData profileData = new ProfileData(null, null, null, null, false);

    assertThat(profileData.getId()).isNull();
    assertThat(profileData.getUsername()).isNull();
    assertThat(profileData.getBio()).isNull();
    assertThat(profileData.getImage()).isNull();
    assertThat(profileData.isFollowing()).isFalse();
  }

  @Test
  public void should_handle_empty_strings() {
    ProfileData profileData = new ProfileData("", "", "", "", true);

    assertThat(profileData.getId()).isEmpty();
    assertThat(profileData.getUsername()).isEmpty();
    assertThat(profileData.getBio()).isEmpty();
    assertThat(profileData.getImage()).isEmpty();
    assertThat(profileData.isFollowing()).isTrue();
  }

  @Test
  public void should_handle_equals_and_hashcode() {
    ProfileData profileData1 = new ProfileData("id", "username", "bio", "image.jpg", true);
    ProfileData profileData2 = new ProfileData("id", "username", "bio", "image.jpg", true);

    assertThat(profileData1).isEqualTo(profileData2);
    assertThat(profileData1.hashCode()).isEqualTo(profileData2.hashCode());
  }

  @Test
  public void should_handle_equals_edge_cases() {
    ProfileData profileData = new ProfileData("id", "username", "bio", "image.jpg", true);
    
    assertThat(profileData).isEqualTo(profileData);
    assertThat(profileData).isNotEqualTo(null);
    assertThat(profileData).isNotEqualTo("not a ProfileData");
    
    ProfileData differentId = new ProfileData("different-id", "username", "bio", "image.jpg", true);
    assertThat(profileData).isNotEqualTo(differentId);
    
    ProfileData differentUsername = new ProfileData("id", "different-username", "bio", "image.jpg", true);
    assertThat(profileData).isNotEqualTo(differentUsername);
    
    ProfileData differentBio = new ProfileData("id", "username", "different-bio", "image.jpg", true);
    assertThat(profileData).isNotEqualTo(differentBio);
    
    ProfileData differentImage = new ProfileData("id", "username", "bio", "different-image.jpg", true);
    assertThat(profileData).isNotEqualTo(differentImage);
    
    ProfileData differentFollowing = new ProfileData("id", "username", "bio", "image.jpg", false);
    assertThat(profileData).isNotEqualTo(differentFollowing);
  }

  @Test
  public void should_handle_equals_with_null_fields() {
    ProfileData profileWithNulls = new ProfileData(null, null, null, null, false);
    ProfileData anotherWithNulls = new ProfileData(null, null, null, null, false);
    
    assertThat(profileWithNulls).isEqualTo(anotherWithNulls);
    assertThat(profileWithNulls.hashCode()).isEqualTo(anotherWithNulls.hashCode());
    
    ProfileData profileWithId = new ProfileData("id", null, null, null, false);
    assertThat(profileWithNulls).isNotEqualTo(profileWithId);
    
    ProfileData profileWithUsername = new ProfileData(null, "username", null, null, false);
    assertThat(profileWithNulls).isNotEqualTo(profileWithUsername);
    
    ProfileData profileWithBio = new ProfileData(null, null, "bio", null, false);
    assertThat(profileWithNulls).isNotEqualTo(profileWithBio);
    
    ProfileData profileWithImage = new ProfileData(null, null, null, "image.jpg", false);
    assertThat(profileWithNulls).isNotEqualTo(profileWithImage);
  }

  @Test
  public void should_handle_hashcode_consistency() {
    ProfileData profileData = new ProfileData("id", "username", "bio", "image.jpg", true);
    
    int hashCode1 = profileData.hashCode();
    int hashCode2 = profileData.hashCode();
    
    assertThat(hashCode1).isEqualTo(hashCode2);
    
    ProfileData identicalProfile = new ProfileData("id", "username", "bio", "image.jpg", true);
    assertThat(profileData.hashCode()).isEqualTo(identicalProfile.hashCode());
  }

  @Test
  public void should_handle_toString() {
    ProfileData profileData = new ProfileData("id", "testuser", "bio", "image.jpg", false);

    String toString = profileData.toString();

    assertThat(toString).contains("ProfileData");
    assertThat(toString).contains("testuser");
    assertThat(toString).contains("bio");
  }
}
