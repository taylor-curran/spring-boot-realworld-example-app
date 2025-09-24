package io.spring.application.user;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class UpdateUserParamTest {

  @Test
  public void should_create_update_user_param_with_all_fields() {
    UpdateUserParam param = new UpdateUserParam(
        "test@example.com",
        "newpassword",
        "newusername",
        "New bio content",
        "new-image.jpg"
    );

    assertThat(param.getEmail()).isEqualTo("test@example.com");
    assertThat(param.getPassword()).isEqualTo("newpassword");
    assertThat(param.getUsername()).isEqualTo("newusername");
    assertThat(param.getBio()).isEqualTo("New bio content");
    assertThat(param.getImage()).isEqualTo("new-image.jpg");
  }

  @Test
  public void should_create_update_user_param_with_no_args_constructor() {
    UpdateUserParam param = new UpdateUserParam();

    assertThat(param.getEmail()).isEqualTo("");
    assertThat(param.getPassword()).isEqualTo("");
    assertThat(param.getUsername()).isEqualTo("");
    assertThat(param.getBio()).isEqualTo("");
    assertThat(param.getImage()).isEqualTo("");
  }

  @Test
  public void should_create_update_user_param_with_builder() {
    UpdateUserParam param = UpdateUserParam.builder()
        .email("builder@example.com")
        .password("builderpassword")
        .username("builderusername")
        .bio("Builder bio")
        .image("builder-image.png")
        .build();

    assertThat(param.getEmail()).isEqualTo("builder@example.com");
    assertThat(param.getPassword()).isEqualTo("builderpassword");
    assertThat(param.getUsername()).isEqualTo("builderusername");
    assertThat(param.getBio()).isEqualTo("Builder bio");
    assertThat(param.getImage()).isEqualTo("builder-image.png");
  }

  @Test
  public void should_use_default_values_with_builder() {
    UpdateUserParam param = UpdateUserParam.builder().build();

    assertThat(param.getEmail()).isEqualTo("");
    assertThat(param.getPassword()).isEqualTo("");
    assertThat(param.getUsername()).isEqualTo("");
    assertThat(param.getBio()).isEqualTo("");
    assertThat(param.getImage()).isEqualTo("");
  }

  @Test
  public void should_handle_partial_builder_updates() {
    UpdateUserParam param = UpdateUserParam.builder()
        .email("partial@example.com")
        .username("partialuser")
        .build();

    assertThat(param.getEmail()).isEqualTo("partial@example.com");
    assertThat(param.getPassword()).isEqualTo("");
    assertThat(param.getUsername()).isEqualTo("partialuser");
    assertThat(param.getBio()).isEqualTo("");
    assertThat(param.getImage()).isEqualTo("");
  }

  @Test
  public void should_handle_null_values_in_constructor() {
    UpdateUserParam param = new UpdateUserParam(null, null, null, null, null);

    assertThat(param.getEmail()).isNull();
    assertThat(param.getPassword()).isNull();
    assertThat(param.getUsername()).isNull();
    assertThat(param.getBio()).isNull();
    assertThat(param.getImage()).isNull();
  }

  @Test
  public void should_handle_empty_strings() {
    UpdateUserParam param = new UpdateUserParam("", "", "", "", "");

    assertThat(param.getEmail()).isEqualTo("");
    assertThat(param.getPassword()).isEqualTo("");
    assertThat(param.getUsername()).isEqualTo("");
    assertThat(param.getBio()).isEqualTo("");
    assertThat(param.getImage()).isEqualTo("");
  }

  @Test
  public void should_handle_special_characters() {
    UpdateUserParam param = new UpdateUserParam(
        "special+email@example.com",
        "p@ssw0rd!@#$%^&*()",
        "user_name-123",
        "Bio with émojis 🚀 and unicode: 测试内容",
        "image-with-special-chars!@#.jpg"
    );

    assertThat(param.getEmail()).isEqualTo("special+email@example.com");
    assertThat(param.getPassword()).isEqualTo("p@ssw0rd!@#$%^&*()");
    assertThat(param.getUsername()).isEqualTo("user_name-123");
    assertThat(param.getBio()).isEqualTo("Bio with émojis 🚀 and unicode: 测试内容");
    assertThat(param.getImage()).isEqualTo("image-with-special-chars!@#.jpg");
  }

  @Test
  public void should_handle_long_content() {
    String longEmail = "very.long.email.address.with.many.dots@very-long-domain-name.example.com";
    String longPassword = "very_long_password_".repeat(10);
    String longUsername = "very_long_username_".repeat(5);
    String longBio = "Very long bio content ".repeat(50);
    String longImage = "very-long-image-filename-".repeat(10) + ".jpg";

    UpdateUserParam param = new UpdateUserParam(longEmail, longPassword, longUsername, longBio, longImage);

    assertThat(param.getEmail()).isEqualTo(longEmail);
    assertThat(param.getPassword()).isEqualTo(longPassword);
    assertThat(param.getUsername()).isEqualTo(longUsername);
    assertThat(param.getBio()).isEqualTo(longBio);
    assertThat(param.getImage()).isEqualTo(longImage);
  }

  @Test
  public void should_handle_whitespace_content() {
    UpdateUserParam param = new UpdateUserParam(
        "  email@example.com  ",
        "  password  ",
        "  username  ",
        "  bio content  ",
        "  image.jpg  "
    );

    assertThat(param.getEmail()).isEqualTo("  email@example.com  ");
    assertThat(param.getPassword()).isEqualTo("  password  ");
    assertThat(param.getUsername()).isEqualTo("  username  ");
    assertThat(param.getBio()).isEqualTo("  bio content  ");
    assertThat(param.getImage()).isEqualTo("  image.jpg  ");
  }

  @Test
  public void should_handle_multiline_bio() {
    String multilineBio = "First line of bio\nSecond line of bio\n\nThird line after empty line";
    
    UpdateUserParam param = new UpdateUserParam(
        "test@example.com",
        "password",
        "username",
        multilineBio,
        "image.jpg"
    );

    assertThat(param.getBio()).isEqualTo(multilineBio);
  }

  @Test
  public void should_handle_builder_with_null_values() {
    UpdateUserParam param = UpdateUserParam.builder()
        .email(null)
        .password(null)
        .username(null)
        .bio(null)
        .image(null)
        .build();

    assertThat(param.getEmail()).isNull();
    assertThat(param.getPassword()).isNull();
    assertThat(param.getUsername()).isNull();
    assertThat(param.getBio()).isNull();
    assertThat(param.getImage()).isNull();
  }
}
