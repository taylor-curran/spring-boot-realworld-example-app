package io.spring.graphql;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import io.spring.core.user.User;
import java.util.Arrays;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
public class SecurityUtilTest {

  @AfterEach
  public void cleanup() {
    SecurityContextHolder.clearContext();
  }

  @Test
  public void should_return_empty_for_anonymous_authentication() {
    SecurityContextHolder.getContext().setAuthentication(
        new AnonymousAuthenticationToken("key", "anonymous", Arrays.asList(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))));

    Optional<User> result = SecurityUtil.getCurrentUser();

    assertThat(result.isEmpty(), is(true));
  }

  @Test
  public void should_return_empty_for_null_authentication() {
    SecurityContextHolder.getContext().setAuthentication(null);

    Optional<User> result = SecurityUtil.getCurrentUser();

    assertThat(result.isEmpty(), is(true));
  }

  @Test
  public void should_return_user_for_valid_authentication() {
    User user = new User("test@example.com", "testuser", "password123", "Test bio", "test.jpg");
    TestingAuthenticationToken authentication = new TestingAuthenticationToken(user, null);
    SecurityContextHolder.getContext().setAuthentication(authentication);

    Optional<User> result = SecurityUtil.getCurrentUser();

    assertThat(result.isPresent(), is(true));
    assertThat(result.get().getEmail(), is(user.getEmail()));
  }

  @Test
  public void should_return_empty_for_anonymous_authentication_with_anonymous_principal() {
    SecurityContextHolder.getContext().setAuthentication(
        new AnonymousAuthenticationToken("key", "anonymous", Arrays.asList(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))));

    Optional<User> result = SecurityUtil.getCurrentUser();

    assertThat(result.isEmpty(), is(true));
  }
}
