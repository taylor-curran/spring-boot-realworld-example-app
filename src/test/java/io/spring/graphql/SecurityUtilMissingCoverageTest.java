package io.spring.graphql;

import static org.assertj.core.api.Assertions.assertThat;

import io.spring.core.user.User;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtilMissingCoverageTest {

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void should_return_empty_when_authentication_is_anonymous() {
    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
    AnonymousAuthenticationToken anonymousAuth =
        new AnonymousAuthenticationToken(
            "anonymous",
            "anonymousUser",
            java.util.Collections.singletonList(new SimpleGrantedAuthority("ROLE_ANONYMOUS")));
    securityContext.setAuthentication(anonymousAuth);
    SecurityContextHolder.setContext(securityContext);

    Optional<User> result = SecurityUtil.getCurrentUser();

    assertThat(result).isEmpty();
  }

  @Test
  void should_return_empty_when_principal_is_null() {
    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
    Authentication authentication = org.mockito.Mockito.mock(Authentication.class);
    org.mockito.Mockito.when(authentication.getPrincipal()).thenReturn(null);
    securityContext.setAuthentication(authentication);
    SecurityContextHolder.setContext(securityContext);

    Optional<User> result = SecurityUtil.getCurrentUser();

    assertThat(result).isEmpty();
  }

  @Test
  void should_return_user_when_principal_is_user() {
    User user = new User("test@example.com", "testuser", "123", "", "");
    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
    Authentication authentication = org.mockito.Mockito.mock(Authentication.class);
    org.mockito.Mockito.when(authentication.getPrincipal()).thenReturn(user);
    securityContext.setAuthentication(authentication);
    SecurityContextHolder.setContext(securityContext);

    Optional<User> result = SecurityUtil.getCurrentUser();

    assertThat(result).isPresent();
    assertThat(result.get()).isEqualTo(user);
  }
}
