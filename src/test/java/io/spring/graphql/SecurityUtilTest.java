package io.spring.graphql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.spring.core.user.User;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtilTest {

  @AfterEach
  public void cleanup() {
    SecurityContextHolder.clearContext();
  }

  @Test
  public void should_return_current_user_when_authenticated() {
    User mockUser = mock(User.class);
    Authentication authentication = new UsernamePasswordAuthenticationToken(
        mockUser, null, AuthorityUtils.createAuthorityList("ROLE_USER"));
    
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);

    Optional<User> result = SecurityUtil.getCurrentUser();

    assertThat(result).isPresent();
    assertThat(result.get()).isEqualTo(mockUser);
  }

  @Test
  public void should_return_empty_when_anonymous_authentication() {
    AnonymousAuthenticationToken anonymousAuth = new AnonymousAuthenticationToken(
        "key", "anonymous", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));
    
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(anonymousAuth);
    SecurityContextHolder.setContext(securityContext);

    Optional<User> result = SecurityUtil.getCurrentUser();

    assertThat(result).isEmpty();
  }

  @Test
  public void should_return_empty_when_principal_is_null() {
    Authentication authentication = new UsernamePasswordAuthenticationToken(
        null, null, AuthorityUtils.createAuthorityList("ROLE_USER"));
    
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);

    Optional<User> result = SecurityUtil.getCurrentUser();

    assertThat(result).isEmpty();
  }

  @Test
  public void should_handle_null_authentication_gracefully() {
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(null);
    SecurityContextHolder.setContext(securityContext);

    try {
      Optional<User> result = SecurityUtil.getCurrentUser();
      assertThat(result).isEmpty();
    } catch (NullPointerException e) {
      assertThat(e.getMessage()).contains("authentication");
    }
  }

  @Test
  public void should_handle_no_security_context_gracefully() {
    SecurityContextHolder.clearContext();

    try {
      Optional<User> result = SecurityUtil.getCurrentUser();
      assertThat(result).isEmpty();
    } catch (NullPointerException e) {
      assertThat(e.getMessage()).contains("authentication");
    }
  }

  @Test
  public void should_handle_different_user_types() {
    User mockUser = mock(User.class);
    when(mockUser.getId()).thenReturn("user123");
    when(mockUser.getUsername()).thenReturn("testuser");
    
    Authentication authentication = new UsernamePasswordAuthenticationToken(
        mockUser, "password", AuthorityUtils.createAuthorityList("ROLE_USER"));
    
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);

    Optional<User> result = SecurityUtil.getCurrentUser();

    assertThat(result).isPresent();
    assertThat(result.get().getId()).isEqualTo("user123");
    assertThat(result.get().getUsername()).isEqualTo("testuser");
  }

  @Test
  public void should_handle_authentication_with_credentials() {
    User mockUser = mock(User.class);
    Authentication authentication = new UsernamePasswordAuthenticationToken(
        mockUser, "credentials", AuthorityUtils.createAuthorityList("ROLE_USER", "ROLE_ADMIN"));
    
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);

    Optional<User> result = SecurityUtil.getCurrentUser();

    assertThat(result).isPresent();
    assertThat(result.get()).isEqualTo(mockUser);
  }

  @Test
  public void should_handle_multiple_authorities() {
    User mockUser = mock(User.class);
    Authentication authentication = new UsernamePasswordAuthenticationToken(
        mockUser, null, AuthorityUtils.createAuthorityList("ROLE_USER", "ROLE_ADMIN", "ROLE_MODERATOR"));
    
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);

    Optional<User> result = SecurityUtil.getCurrentUser();

    assertThat(result).isPresent();
    assertThat(result.get()).isEqualTo(mockUser);
  }

  @Test
  public void should_handle_empty_authorities() {
    User mockUser = mock(User.class);
    Authentication authentication = new UsernamePasswordAuthenticationToken(
        mockUser, null, AuthorityUtils.NO_AUTHORITIES);
    
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);

    Optional<User> result = SecurityUtil.getCurrentUser();

    assertThat(result).isPresent();
    assertThat(result.get()).isEqualTo(mockUser);
  }

  @Test
  public void should_handle_concurrent_access() {
    User mockUser1 = mock(User.class);
    User mockUser2 = mock(User.class);
    
    Authentication auth1 = new UsernamePasswordAuthenticationToken(
        mockUser1, null, AuthorityUtils.createAuthorityList("ROLE_USER"));
    Authentication auth2 = new UsernamePasswordAuthenticationToken(
        mockUser2, null, AuthorityUtils.createAuthorityList("ROLE_USER"));
    
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(auth1);
    SecurityContextHolder.setContext(securityContext);

    Optional<User> result1 = SecurityUtil.getCurrentUser();
    assertThat(result1).isPresent();
    assertThat(result1.get()).isEqualTo(mockUser1);

    when(securityContext.getAuthentication()).thenReturn(auth2);
    Optional<User> result2 = SecurityUtil.getCurrentUser();
    assertThat(result2).isPresent();
    assertThat(result2.get()).isEqualTo(mockUser2);
  }
}
