package io.spring.application.data;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class ProfileDataEqualsEdgeCasesTest {

  @Test
  void should_handle_equals_with_same_instance() {
    ProfileData profile = createSampleProfileData();
    
    assertThat(profile.equals(profile)).isTrue();
    assertThat(profile.hashCode()).isEqualTo(profile.hashCode());
  }

  @Test
  void should_handle_equals_with_null_bio() {
    ProfileData profile1 = new ProfileData("id", "testuser", null, "image.jpg", false);
    ProfileData profile2 = new ProfileData("id", "testuser", null, "image.jpg", false);
    ProfileData profile3 = new ProfileData("id", "testuser", "bio", "image.jpg", false);
    
    assertThat(profile1).isEqualTo(profile2);
    assertThat(profile1).isNotEqualTo(profile3);
  }

  @Test
  void should_handle_equals_with_null_image() {
    ProfileData profile1 = new ProfileData("id", "testuser", "bio", null, false);
    ProfileData profile2 = new ProfileData("id", "testuser", "bio", null, false);
    ProfileData profile3 = new ProfileData("id", "testuser", "bio", "image.jpg", false);
    
    assertThat(profile1).isEqualTo(profile2);
    assertThat(profile1).isNotEqualTo(profile3);
  }

  @Test
  void should_handle_equals_with_different_following_status() {
    ProfileData profile1 = new ProfileData("id", "testuser", "bio", "image.jpg", true);
    ProfileData profile2 = new ProfileData("id", "testuser", "bio", "image.jpg", false);
    
    assertThat(profile1).isNotEqualTo(profile2);
  }

  @Test
  void should_handle_equals_with_different_usernames() {
    ProfileData profile1 = new ProfileData("id", "testuser1", "bio", "image.jpg", false);
    ProfileData profile2 = new ProfileData("id", "testuser2", "bio", "image.jpg", false);
    
    assertThat(profile1).isNotEqualTo(profile2);
  }

  @Test
  void should_handle_equals_with_all_null_fields() {
    ProfileData profile1 = new ProfileData(null, null, null, null, false);
    ProfileData profile2 = new ProfileData(null, null, null, null, false);
    ProfileData profile3 = new ProfileData("id", null, null, null, false);
    
    assertThat(profile1).isEqualTo(profile2);
    assertThat(profile1).isNotEqualTo(profile3);
  }

  private ProfileData createSampleProfileData() {
    return new ProfileData("id", "testuser", "Test Bio", "image.jpg", false);
  }
}
