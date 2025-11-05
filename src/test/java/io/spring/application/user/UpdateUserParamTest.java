package io.spring.application.user;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

import org.junit.jupiter.api.Test;

public class UpdateUserParamTest {

  @Test
  public void should_create_update_user_param_with_builder() {
    UpdateUserParam param = UpdateUserParam.builder()
        .email("test@example.com")
        .password("newpassword")
        .username("newusername")
        .bio("New bio")
        .image("new-image.jpg")
        .build();

    assertThat(param.getEmail(), is("test@example.com"));
    assertThat(param.getPassword(), is("newpassword"));
    assertThat(param.getUsername(), is("newusername"));
    assertThat(param.getBio(), is("New bio"));
    assertThat(param.getImage(), is("new-image.jpg"));
  }

  @Test
  public void should_create_update_user_param_with_default_values() {
    UpdateUserParam param = UpdateUserParam.builder().build();

    assertThat(param.getEmail(), is(""));
    assertThat(param.getPassword(), is(""));
    assertThat(param.getUsername(), is(""));
    assertThat(param.getBio(), is(""));
    assertThat(param.getImage(), is(""));
  }

  @Test
  public void should_create_update_user_param_with_partial_values() {
    UpdateUserParam param = UpdateUserParam.builder()
        .email("partial@example.com")
        .username("partialuser")
        .build();

    assertThat(param.getEmail(), is("partial@example.com"));
    assertThat(param.getPassword(), is(""));
    assertThat(param.getUsername(), is("partialuser"));
    assertThat(param.getBio(), is(""));
    assertThat(param.getImage(), is(""));
  }

  @Test
  public void should_test_builder_toString_method() {
    UpdateUserParam.UpdateUserParamBuilder builder = UpdateUserParam.builder()
        .email("test@example.com")
        .password("password123")
        .username("testuser")
        .bio("Test bio")
        .image("test.jpg");

    String toStringResult = builder.toString();
    
    assertThat(toStringResult, notNullValue());
    assertThat(toStringResult, containsString("UpdateUserParamBuilder"));
    assertThat(toStringResult, containsString("email$value=test@example.com"));
    assertThat(toStringResult, containsString("username$value=testuser"));
    assertThat(toStringResult, containsString("bio$value=Test bio"));
    assertThat(toStringResult, containsString("image$value=test.jpg"));
  }

  @Test
  public void should_test_builder_toString_with_empty_values() {
    UpdateUserParam.UpdateUserParamBuilder builder = UpdateUserParam.builder();

    String toStringResult = builder.toString();
    
    assertThat(toStringResult, notNullValue());
    assertThat(toStringResult, containsString("UpdateUserParamBuilder"));
  }

  @Test
  public void should_test_builder_individual_setters() {
    UpdateUserParam.UpdateUserParamBuilder builder = UpdateUserParam.builder();
    
    builder.email("individual@example.com");
    builder.password("individualpass");
    builder.username("individualuser");
    builder.bio("Individual bio");
    builder.image("individual.jpg");
    
    UpdateUserParam param = builder.build();
    
    assertThat(param.getEmail(), is("individual@example.com"));
    assertThat(param.getPassword(), is("individualpass"));
    assertThat(param.getUsername(), is("individualuser"));
    assertThat(param.getBio(), is("Individual bio"));
    assertThat(param.getImage(), is("individual.jpg"));
  }

  @Test
  public void should_test_no_args_constructor() {
    UpdateUserParam param = new UpdateUserParam();
    
    assertThat(param.getEmail(), is(""));
    assertThat(param.getPassword(), is(""));
    assertThat(param.getUsername(), is(""));
    assertThat(param.getBio(), is(""));
    assertThat(param.getImage(), is(""));
  }

  @Test
  public void should_test_all_args_constructor() {
    UpdateUserParam param = new UpdateUserParam(
        "allargs@example.com",
        "allargspass",
        "allargsuser",
        "All args bio",
        "allargs.jpg"
    );
    
    assertThat(param.getEmail(), is("allargs@example.com"));
    assertThat(param.getPassword(), is("allargspass"));
    assertThat(param.getUsername(), is("allargsuser"));
    assertThat(param.getBio(), is("All args bio"));
    assertThat(param.getImage(), is("allargs.jpg"));
  }

  @Test
  public void should_handle_null_values_in_builder() {
    UpdateUserParam param = UpdateUserParam.builder()
        .email(null)
        .password(null)
        .username(null)
        .bio(null)
        .image(null)
        .build();

    assertThat(param.getEmail(), is((String) null));
    assertThat(param.getPassword(), is((String) null));
    assertThat(param.getUsername(), is((String) null));
    assertThat(param.getBio(), is((String) null));
    assertThat(param.getImage(), is((String) null));
  }

  @Test
  public void should_test_builder_method_chaining() {
    UpdateUserParam param = UpdateUserParam.builder()
        .email("chain@example.com")
        .password("chainpass")
        .username("chainuser")
        .bio("Chain bio")
        .image("chain.jpg")
        .build();

    assertThat(param.getEmail(), is("chain@example.com"));
    assertThat(param.getPassword(), is("chainpass"));
    assertThat(param.getUsername(), is("chainuser"));
    assertThat(param.getBio(), is("Chain bio"));
    assertThat(param.getImage(), is("chain.jpg"));
  }
}
