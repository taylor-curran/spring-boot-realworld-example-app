package io.spring.application.data;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

public class UserDataTest {

  @Test
  public void should_create_user_data_with_all_fields() {
    String id = "user-id";
    String email = "test@example.com";
    String username = "testuser";
    String bio = "Test bio";
    String image = "image.jpg";

    UserData userData = new UserData(id, email, username, bio, image);

    assertThat(userData.getId()).isEqualTo(id);
    assertThat(userData.getEmail()).isEqualTo(email);
    assertThat(userData.getUsername()).isEqualTo(username);
    assertThat(userData.getBio()).isEqualTo(bio);
    assertThat(userData.getImage()).isEqualTo(image);
  }

  @Test
  public void should_create_user_data_with_no_args_constructor() {
    UserData userData = new UserData();
    
    assertThat(userData.getId()).isNull();
    assertThat(userData.getEmail()).isNull();
    assertThat(userData.getUsername()).isNull();
    assertThat(userData.getBio()).isNull();
    assertThat(userData.getImage()).isNull();
  }

  @Test
  public void should_set_and_get_all_fields() {
    UserData userData = new UserData();
    String id = "test-id";
    String email = "test@example.com";
    String username = "testuser";
    String bio = "Test bio";
    String image = "test.jpg";

    userData.setId(id);
    userData.setEmail(email);
    userData.setUsername(username);
    userData.setBio(bio);
    userData.setImage(image);

    assertThat(userData.getId()).isEqualTo(id);
    assertThat(userData.getEmail()).isEqualTo(email);
    assertThat(userData.getUsername()).isEqualTo(username);
    assertThat(userData.getBio()).isEqualTo(bio);
    assertThat(userData.getImage()).isEqualTo(image);
  }

  @Test
  public void should_handle_email_validation_format() {
    UserData userData = new UserData();
    String email = "test@example.com";
    
    userData.setEmail(email);
    
    assertThat(userData.getEmail()).isEqualTo(email);
    assertThat(userData.getEmail()).contains("@");
    assertThat(userData.getEmail()).contains(".");
  }

  @Test
  public void should_handle_username_operations() {
    UserData userData = new UserData();
    String username = "testuser123";
    
    userData.setUsername(username);
    
    assertThat(userData.getUsername()).isEqualTo(username);
    assertThat(userData.getUsername()).hasSize(11);
  }

  @Test
  public void should_handle_null_values() {
    UserData userData = new UserData(null, null, null, null, null);

    assertThat(userData.getId()).isNull();
    assertThat(userData.getEmail()).isNull();
    assertThat(userData.getUsername()).isNull();
    assertThat(userData.getBio()).isNull();
    assertThat(userData.getImage()).isNull();
  }

  @Test
  public void should_handle_empty_strings() {
    UserData userData = new UserData("", "", "", "", "");

    assertThat(userData.getId()).isEmpty();
    assertThat(userData.getEmail()).isEmpty();
    assertThat(userData.getUsername()).isEmpty();
    assertThat(userData.getBio()).isEmpty();
    assertThat(userData.getImage()).isEmpty();
  }

  @Test
  public void should_handle_equals_and_hashcode() {
    UserData userData1 = new UserData("id", "test@example.com", "testuser", "bio", "image.jpg");
    UserData userData2 = new UserData("id", "test@example.com", "testuser", "bio", "image.jpg");

    assertThat(userData1).isEqualTo(userData2);
    assertThat(userData1.hashCode()).isEqualTo(userData2.hashCode());
  }

  @Test
  public void should_handle_toString() {
    UserData userData = new UserData("id", "test@example.com", "testuser", "bio", "image.jpg");

    String toString = userData.toString();

    assertThat(toString).contains("UserData");
    assertThat(toString).contains("testuser");
    assertThat(toString).contains("test@example.com");
  }
}
