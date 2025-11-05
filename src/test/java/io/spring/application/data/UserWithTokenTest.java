package io.spring.application.data;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class UserWithTokenTest {

  @Test
  public void should_create_user_with_token_from_user_data() {
    UserData userData = new UserData(
        "user123",
        "test@example.com",
        "testuser",
        "Test bio content",
        "profile-image.jpg"
    );
    String token = "jwt-token-123";

    UserWithToken userWithToken = new UserWithToken(userData, token);

    assertThat(userWithToken.getEmail()).isEqualTo("test@example.com");
    assertThat(userWithToken.getUsername()).isEqualTo("testuser");
    assertThat(userWithToken.getBio()).isEqualTo("Test bio content");
    assertThat(userWithToken.getImage()).isEqualTo("profile-image.jpg");
    assertThat(userWithToken.getToken()).isEqualTo("jwt-token-123");
  }

  @Test
  public void should_handle_null_user_data_fields() {
    UserData userData = new UserData(
        "user123",
        null,
        null,
        null,
        null
    );
    String token = "jwt-token-456";

    UserWithToken userWithToken = new UserWithToken(userData, token);

    assertThat(userWithToken.getEmail()).isNull();
    assertThat(userWithToken.getUsername()).isNull();
    assertThat(userWithToken.getBio()).isNull();
    assertThat(userWithToken.getImage()).isNull();
    assertThat(userWithToken.getToken()).isEqualTo("jwt-token-456");
  }

  @Test
  public void should_handle_empty_user_data_fields() {
    UserData userData = new UserData(
        "user123",
        "",
        "",
        "",
        ""
    );
    String token = "";

    UserWithToken userWithToken = new UserWithToken(userData, token);

    assertThat(userWithToken.getEmail()).isEqualTo("");
    assertThat(userWithToken.getUsername()).isEqualTo("");
    assertThat(userWithToken.getBio()).isEqualTo("");
    assertThat(userWithToken.getImage()).isEqualTo("");
    assertThat(userWithToken.getToken()).isEqualTo("");
  }

  @Test
  public void should_handle_special_characters_in_fields() {
    UserData userData = new UserData(
        "user123",
        "special+email@example.com",
        "user_name-123",
        "Bio with émojis 🚀 and unicode: 测试内容",
        "image-with-special-chars!@#.jpg"
    );
    String token = "jwt.token.with.dots.and-dashes_123";

    UserWithToken userWithToken = new UserWithToken(userData, token);

    assertThat(userWithToken.getEmail()).isEqualTo("special+email@example.com");
    assertThat(userWithToken.getUsername()).isEqualTo("user_name-123");
    assertThat(userWithToken.getBio()).isEqualTo("Bio with émojis 🚀 and unicode: 测试内容");
    assertThat(userWithToken.getImage()).isEqualTo("image-with-special-chars!@#.jpg");
    assertThat(userWithToken.getToken()).isEqualTo("jwt.token.with.dots.and-dashes_123");
  }

  @Test
  public void should_handle_long_content() {
    String longEmail = "very.long.email.address.with.many.dots@very-long-domain-name.example.com";
    String longUsername = "very_long_username_".repeat(5);
    String longBio = "Very long bio content ".repeat(50);
    String longImage = "very-long-image-filename-".repeat(10) + ".jpg";
    String longToken = "very.long.jwt.token.".repeat(20);

    UserData userData = new UserData(
        "user123",
        longEmail,
        longUsername,
        longBio,
        longImage
    );

    UserWithToken userWithToken = new UserWithToken(userData, longToken);

    assertThat(userWithToken.getEmail()).isEqualTo(longEmail);
    assertThat(userWithToken.getUsername()).isEqualTo(longUsername);
    assertThat(userWithToken.getBio()).isEqualTo(longBio);
    assertThat(userWithToken.getImage()).isEqualTo(longImage);
    assertThat(userWithToken.getToken()).isEqualTo(longToken);
  }

  @Test
  public void should_handle_whitespace_content() {
    UserData userData = new UserData(
        "user123",
        "  email@example.com  ",
        "  username  ",
        "  bio content  ",
        "  image.jpg  "
    );
    String token = "  jwt-token  ";

    UserWithToken userWithToken = new UserWithToken(userData, token);

    assertThat(userWithToken.getEmail()).isEqualTo("  email@example.com  ");
    assertThat(userWithToken.getUsername()).isEqualTo("  username  ");
    assertThat(userWithToken.getBio()).isEqualTo("  bio content  ");
    assertThat(userWithToken.getImage()).isEqualTo("  image.jpg  ");
    assertThat(userWithToken.getToken()).isEqualTo("  jwt-token  ");
  }

  @Test
  public void should_handle_multiline_bio() {
    String multilineBio = "First line of bio\nSecond line of bio\n\nThird line after empty line";
    
    UserData userData = new UserData(
        "user123",
        "test@example.com",
        "testuser",
        multilineBio,
        "image.jpg"
    );
    String token = "jwt-token-123";

    UserWithToken userWithToken = new UserWithToken(userData, token);

    assertThat(userWithToken.getBio()).isEqualTo(multilineBio);
    assertThat(userWithToken.getToken()).isEqualTo("jwt-token-123");
  }

  @Test
  public void should_handle_null_token() {
    UserData userData = new UserData(
        "user123",
        "test@example.com",
        "testuser",
        "Test bio",
        "image.jpg"
    );

    UserWithToken userWithToken = new UserWithToken(userData, null);

    assertThat(userWithToken.getEmail()).isEqualTo("test@example.com");
    assertThat(userWithToken.getUsername()).isEqualTo("testuser");
    assertThat(userWithToken.getBio()).isEqualTo("Test bio");
    assertThat(userWithToken.getImage()).isEqualTo("image.jpg");
    assertThat(userWithToken.getToken()).isNull();
  }

  @Test
  public void should_handle_jwt_token_format() {
    UserData userData = new UserData(
        "user123",
        "test@example.com",
        "testuser",
        "Test bio",
        "image.jpg"
    );
    String jwtToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";

    UserWithToken userWithToken = new UserWithToken(userData, jwtToken);

    assertThat(userWithToken.getToken()).isEqualTo(jwtToken);
  }

  @Test
  public void should_preserve_user_data_integrity() {
    UserData originalUserData = new UserData(
        "user123",
        "original@example.com",
        "originaluser",
        "Original bio",
        "original.jpg"
    );
    String token = "token-123";

    UserWithToken userWithToken = new UserWithToken(originalUserData, token);

    assertThat(userWithToken.getEmail()).isEqualTo(originalUserData.getEmail());
    assertThat(userWithToken.getUsername()).isEqualTo(originalUserData.getUsername());
    assertThat(userWithToken.getBio()).isEqualTo(originalUserData.getBio());
    assertThat(userWithToken.getImage()).isEqualTo(originalUserData.getImage());
  }
}
