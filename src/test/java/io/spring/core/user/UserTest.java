package io.spring.core.user;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class UserTest {

  @Test
  public void should_create_user_with_valid_parameters() {
    String email = "test@example.com";
    String username = "testuser";
    String password = "password123";
    String bio = "Test bio";
    String image = "test.jpg";

    User user = new User(email, username, password, bio, image);

    assertThat(user.getEmail()).isEqualTo(email);
    assertThat(user.getUsername()).isEqualTo(username);
    assertThat(user.getPassword()).isEqualTo(password);
    assertThat(user.getBio()).isEqualTo(bio);
    assertThat(user.getImage()).isEqualTo(image);
    assertThat(user.getId()).isNotNull();
  }

  @Test
  public void should_create_user_with_minimal_parameters() {
    String email = "test@example.com";
    String username = "testuser";
    String password = "password123";

    User user = new User(email, username, password, "", "");

    assertThat(user.getEmail()).isEqualTo(email);
    assertThat(user.getUsername()).isEqualTo(username);
    assertThat(user.getPassword()).isEqualTo(password);
    assertThat(user.getBio()).isEqualTo("");
    assertThat(user.getImage()).isEqualTo("");
    assertThat(user.getId()).isNotNull();
  }

  @Test
  public void should_update_user_profile() {
    User user = new User("test@example.com", "testuser", "password", "old bio", "old.jpg");

    user.update("new@example.com", "newuser", "newpassword", "new bio", "new.jpg");

    assertThat(user.getEmail()).isEqualTo("new@example.com");
    assertThat(user.getUsername()).isEqualTo("newuser");
    assertThat(user.getPassword()).isEqualTo("newpassword");
    assertThat(user.getBio()).isEqualTo("new bio");
    assertThat(user.getImage()).isEqualTo("new.jpg");
  }

  @Test
  public void should_not_update_user_with_null_values() {
    User user = new User("test@example.com", "testuser", "password", "bio", "image.jpg");

    user.update(null, null, null, null, null);

    assertThat(user.getEmail()).isEqualTo("test@example.com");
    assertThat(user.getUsername()).isEqualTo("testuser");
    assertThat(user.getPassword()).isEqualTo("password");
    assertThat(user.getBio()).isEqualTo("bio");
    assertThat(user.getImage()).isEqualTo("image.jpg");
  }

  @Test
  public void should_not_update_user_with_empty_values() {
    User user = new User("test@example.com", "testuser", "password", "bio", "image.jpg");

    user.update("", "", "", "", "");

    assertThat(user.getEmail()).isEqualTo("test@example.com");
    assertThat(user.getUsername()).isEqualTo("testuser");
    assertThat(user.getPassword()).isEqualTo("password");
    assertThat(user.getBio()).isEqualTo("bio");
    assertThat(user.getImage()).isEqualTo("image.jpg");
  }

  @Test
  public void should_handle_long_bio() {
    String longBio =
        "This is a very long bio that contains a lot of text to test how the user entity handles longer biographical information that users might want to include in their profiles.";
    User user = new User("test@example.com", "testuser", "password", longBio, "image.jpg");

    assertThat(user.getBio()).isEqualTo(longBio);
  }

  @Test
  public void should_handle_special_characters_in_fields() {
    String email = "test+special@example.com";
    String username = "user_name-123";
    String bio = "Bio with special chars: !@#$%^&*()";
    String image = "path/to/image-file_123.jpg";

    User user = new User(email, username, "password", bio, image);

    assertThat(user.getEmail()).isEqualTo(email);
    assertThat(user.getUsername()).isEqualTo(username);
    assertThat(user.getBio()).isEqualTo(bio);
    assertThat(user.getImage()).isEqualTo(image);
  }

  @Test
  public void should_handle_unicode_characters() {
    String username = "用户名";
    String bio = "Biografía en español with 中文 characters";

    User user = new User("test@example.com", username, "password", bio, "image.jpg");

    assertThat(user.getUsername()).isEqualTo(username);
    assertThat(user.getBio()).isEqualTo(bio);
  }

  @Test
  public void should_test_equals_and_hashcode() {
    User user1 = new User("test@example.com", "testuser", "password", "bio", "image.jpg");
    User user2 = new User("test@example.com", "testuser", "password", "bio", "image.jpg");
    User user3 = new User("different@example.com", "testuser", "password", "bio", "image.jpg");

    assertThat(user1).isNotEqualTo(user2); // Different IDs
    assertThat(user1).isNotEqualTo(user3);
    assertThat(user1).isNotEqualTo(null);
    assertThat(user1).isNotEqualTo("string");
    assertThat(user1).isEqualTo(user1);
  }

  @Test
  public void should_test_toString() {
    User user = new User("test@example.com", "testuser", "password", "bio", "image.jpg");

    String toString = user.toString();

    assertThat(toString).isNotNull();
    assertThat(toString).startsWith("io.spring.core.user.User@");
  }

  @Test
  public void should_generate_unique_ids() {
    User user1 = new User("test1@example.com", "user1", "password", "bio", "image.jpg");
    User user2 = new User("test2@example.com", "user2", "password", "bio", "image.jpg");

    assertThat(user1.getId()).isNotEqualTo(user2.getId());
    assertThat(user1.getId()).isNotNull();
    assertThat(user2.getId()).isNotNull();
  }
}
