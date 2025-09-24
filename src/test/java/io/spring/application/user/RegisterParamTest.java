package io.spring.application.user;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class RegisterParamTest {

  @Test
  public void should_create_register_param_with_all_fields() {
    RegisterParam param = new RegisterParam("test@example.com", "testuser", "password123");

    assertThat(param.getEmail()).isEqualTo("test@example.com");
    assertThat(param.getUsername()).isEqualTo("testuser");
    assertThat(param.getPassword()).isEqualTo("password123");
  }

  @Test
  public void should_create_register_param_with_no_args_constructor() {
    RegisterParam param = new RegisterParam();

    assertThat(param.getEmail()).isNull();
    assertThat(param.getUsername()).isNull();
    assertThat(param.getPassword()).isNull();
  }

  @Test
  public void should_handle_special_characters_in_email() {
    RegisterParam param = new RegisterParam("test+special@example.com", "testuser", "password123");

    assertThat(param.getEmail()).isEqualTo("test+special@example.com");
  }

  @Test
  public void should_handle_special_characters_in_username() {
    RegisterParam param = new RegisterParam("test@example.com", "test_user-123", "password123");

    assertThat(param.getUsername()).isEqualTo("test_user-123");
  }

  @Test
  public void should_handle_special_characters_in_password() {
    RegisterParam param = new RegisterParam("test@example.com", "testuser", "p@ssw0rd!123");

    assertThat(param.getPassword()).isEqualTo("p@ssw0rd!123");
  }

  @Test
  public void should_handle_unicode_characters() {
    RegisterParam param = new RegisterParam("测试@example.com", "用户名", "密码123");

    assertThat(param.getEmail()).isEqualTo("测试@example.com");
    assertThat(param.getUsername()).isEqualTo("用户名");
    assertThat(param.getPassword()).isEqualTo("密码123");
  }

  @Test
  public void should_handle_long_values() {
    String longEmail = "very.long.email.address.that.might.exceed.normal.limits@example.com";
    String longUsername = "verylongusernamethatmightexceedtypicallimits";
    String longPassword = "verylongpasswordthatmightexceedtypicallimitsandcontainspecialcharacters123!@#";

    RegisterParam param = new RegisterParam(longEmail, longUsername, longPassword);

    assertThat(param.getEmail()).isEqualTo(longEmail);
    assertThat(param.getUsername()).isEqualTo(longUsername);
    assertThat(param.getPassword()).isEqualTo(longPassword);
  }

  @Test
  public void should_handle_minimum_length_values() {
    RegisterParam param = new RegisterParam("a@b.c", "u", "p");

    assertThat(param.getEmail()).isEqualTo("a@b.c");
    assertThat(param.getUsername()).isEqualTo("u");
    assertThat(param.getPassword()).isEqualTo("p");
  }

  @Test
  public void should_handle_empty_strings() {
    RegisterParam param = new RegisterParam("", "", "");

    assertThat(param.getEmail()).isEqualTo("");
    assertThat(param.getUsername()).isEqualTo("");
    assertThat(param.getPassword()).isEqualTo("");
  }

  @Test
  public void should_handle_null_values() {
    RegisterParam param = new RegisterParam(null, null, null);

    assertThat(param.getEmail()).isNull();
    assertThat(param.getUsername()).isNull();
    assertThat(param.getPassword()).isNull();
  }

  @Test
  public void should_handle_whitespace_values() {
    RegisterParam param = new RegisterParam("  test@example.com  ", "  testuser  ", "  password123  ");

    assertThat(param.getEmail()).isEqualTo("  test@example.com  ");
    assertThat(param.getUsername()).isEqualTo("  testuser  ");
    assertThat(param.getPassword()).isEqualTo("  password123  ");
  }

  @Test
  public void should_handle_mixed_case_values() {
    RegisterParam param = new RegisterParam("Test@Example.COM", "TestUser", "Password123");

    assertThat(param.getEmail()).isEqualTo("Test@Example.COM");
    assertThat(param.getUsername()).isEqualTo("TestUser");
    assertThat(param.getPassword()).isEqualTo("Password123");
  }

  @Test
  public void should_handle_numeric_values() {
    RegisterParam param = new RegisterParam("123@456.789", "user123", "pass456");

    assertThat(param.getEmail()).isEqualTo("123@456.789");
    assertThat(param.getUsername()).isEqualTo("user123");
    assertThat(param.getPassword()).isEqualTo("pass456");
  }

  @Test
  public void should_handle_domain_variations() {
    RegisterParam param = new RegisterParam("test@sub.domain.co.uk", "testuser", "password123");

    assertThat(param.getEmail()).isEqualTo("test@sub.domain.co.uk");
  }

  @Test
  public void should_handle_email_with_plus_addressing() {
    RegisterParam param = new RegisterParam("test+tag@example.com", "testuser", "password123");

    assertThat(param.getEmail()).isEqualTo("test+tag@example.com");
  }

  @Test
  public void should_handle_email_with_dots() {
    RegisterParam param = new RegisterParam("first.last@example.com", "testuser", "password123");

    assertThat(param.getEmail()).isEqualTo("first.last@example.com");
  }
}
