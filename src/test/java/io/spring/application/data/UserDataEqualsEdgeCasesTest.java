package io.spring.application.data;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class UserDataEqualsEdgeCasesTest {

  @Test
  void should_handle_equals_with_same_instance() {
    UserData user = createSampleUserData();

    assertThat(user.equals(user)).isTrue();
    assertThat(user.hashCode()).isEqualTo(user.hashCode());
  }

  @Test
  void should_handle_equals_with_null_bio() {
    UserData user1 = new UserData("id", "user@example.com", "testuser", null, "image.jpg");
    UserData user2 = new UserData("id", "user@example.com", "testuser", null, "image.jpg");
    UserData user3 = new UserData("id", "user@example.com", "testuser", "bio", "image.jpg");

    assertThat(user1).isEqualTo(user2);
    assertThat(user1).isNotEqualTo(user3);
  }

  @Test
  void should_handle_equals_with_null_image() {
    UserData user1 = new UserData("id", "user@example.com", "testuser", "bio", null);
    UserData user2 = new UserData("id", "user@example.com", "testuser", "bio", null);
    UserData user3 = new UserData("id", "user@example.com", "testuser", "bio", "image.jpg");

    assertThat(user1).isEqualTo(user2);
    assertThat(user1).isNotEqualTo(user3);
  }

  @Test
  void should_handle_equals_with_different_emails() {
    UserData user1 = new UserData("id", "user1@example.com", "testuser", "bio", "image.jpg");
    UserData user2 = new UserData("id", "user2@example.com", "testuser", "bio", "image.jpg");

    assertThat(user1).isNotEqualTo(user2);
  }

  @Test
  void should_handle_equals_with_different_usernames() {
    UserData user1 = new UserData("id", "user@example.com", "testuser1", "bio", "image.jpg");
    UserData user2 = new UserData("id", "user@example.com", "testuser2", "bio", "image.jpg");

    assertThat(user1).isNotEqualTo(user2);
  }

  @Test
  void should_handle_equals_with_all_null_fields() {
    UserData user1 = new UserData(null, null, null, null, null);
    UserData user2 = new UserData(null, null, null, null, null);
    UserData user3 = new UserData("id", null, null, null, null);

    assertThat(user1).isEqualTo(user2);
    assertThat(user1).isNotEqualTo(user3);
  }

  private UserData createSampleUserData() {
    return new UserData("id", "user@example.com", "testuser", "Test Bio", "image.jpg");
  }
}
