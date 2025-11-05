package io.spring.api.security;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.spring.core.service.JwtService;
import io.spring.core.user.User;
import io.spring.core.user.UserRepository;
import java.io.IOException;
import java.util.Optional;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class JwtTokenFilterTest {

  @Mock private UserRepository userRepository;
  @Mock private JwtService jwtService;
  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;
  @Mock private FilterChain filterChain;
  @Mock private SecurityContext securityContext;
  @Mock private Authentication existingAuthentication;

  @InjectMocks private JwtTokenFilter jwtTokenFilter;

  private User testUser;

  @BeforeEach
  public void setUp() {
    testUser = new User("test@example.com", "testuser", "password", "bio", "image");
    ReflectionTestUtils.setField(testUser, "id", "user-123");
    SecurityContextHolder.setContext(securityContext);
  }

  @Test
  public void should_authenticate_user_with_valid_token() throws ServletException, IOException {
    String validToken = "valid.jwt.token";
    String authHeader = "Bearer " + validToken;
    String userId = "user-123";

    when(request.getHeader("Authorization")).thenReturn(authHeader);
    when(jwtService.getSubFromToken(validToken)).thenReturn(Optional.of(userId));
    when(securityContext.getAuthentication()).thenReturn(null);
    when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

    jwtTokenFilter.doFilterInternal(request, response, filterChain);

    verify(securityContext).setAuthentication(any(UsernamePasswordAuthenticationToken.class));
    verify(filterChain).doFilter(request, response);
  }

  @Test
  public void should_not_authenticate_when_authorization_header_is_null() throws ServletException, IOException {
    when(request.getHeader("Authorization")).thenReturn(null);

    jwtTokenFilter.doFilterInternal(request, response, filterChain);

    verify(jwtService, never()).getSubFromToken(any());
    verify(securityContext, never()).setAuthentication(any());
    verify(filterChain).doFilter(request, response);
  }

  @Test
  public void should_not_authenticate_when_authorization_header_is_malformed() throws ServletException, IOException {
    String malformedHeader = "Bearer";

    when(request.getHeader("Authorization")).thenReturn(malformedHeader);

    jwtTokenFilter.doFilterInternal(request, response, filterChain);

    verify(jwtService, never()).getSubFromToken(any());
    verify(securityContext, never()).setAuthentication(any());
    verify(filterChain).doFilter(request, response);
  }

  @Test
  public void should_not_authenticate_when_authorization_header_has_no_space() throws ServletException, IOException {
    String headerWithoutSpace = "BearerToken";

    when(request.getHeader("Authorization")).thenReturn(headerWithoutSpace);

    jwtTokenFilter.doFilterInternal(request, response, filterChain);

    verify(jwtService, never()).getSubFromToken(any());
    verify(securityContext, never()).setAuthentication(any());
    verify(filterChain).doFilter(request, response);
  }

  @Test
  public void should_not_authenticate_when_token_is_invalid() throws ServletException, IOException {
    String invalidToken = "invalid.jwt.token";
    String authHeader = "Bearer " + invalidToken;

    when(request.getHeader("Authorization")).thenReturn(authHeader);
    when(jwtService.getSubFromToken(invalidToken)).thenReturn(Optional.empty());

    jwtTokenFilter.doFilterInternal(request, response, filterChain);

    verify(securityContext, never()).setAuthentication(any());
    verify(filterChain).doFilter(request, response);
  }

  @Test
  public void should_not_authenticate_when_user_not_found() throws ServletException, IOException {
    String validToken = "valid.jwt.token";
    String authHeader = "Bearer " + validToken;
    String userId = "nonexistent-user";

    when(request.getHeader("Authorization")).thenReturn(authHeader);
    when(jwtService.getSubFromToken(validToken)).thenReturn(Optional.of(userId));
    when(securityContext.getAuthentication()).thenReturn(null);
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    jwtTokenFilter.doFilterInternal(request, response, filterChain);

    verify(securityContext, never()).setAuthentication(any());
    verify(filterChain).doFilter(request, response);
  }

  @Test
  public void should_not_authenticate_when_user_already_authenticated() throws ServletException, IOException {
    String validToken = "valid.jwt.token";
    String authHeader = "Bearer " + validToken;
    String userId = "user-123";

    when(request.getHeader("Authorization")).thenReturn(authHeader);
    when(jwtService.getSubFromToken(validToken)).thenReturn(Optional.of(userId));
    when(securityContext.getAuthentication()).thenReturn(existingAuthentication);

    jwtTokenFilter.doFilterInternal(request, response, filterChain);

    verify(userRepository, never()).findById(any());
    verify(securityContext, never()).setAuthentication(any());
    verify(filterChain).doFilter(request, response);
  }

  @Test
  public void should_handle_empty_authorization_header() throws ServletException, IOException {
    when(request.getHeader("Authorization")).thenReturn("");

    jwtTokenFilter.doFilterInternal(request, response, filterChain);

    verify(jwtService, never()).getSubFromToken(any());
    verify(securityContext, never()).setAuthentication(any());
    verify(filterChain).doFilter(request, response);
  }

  @Test
  public void should_handle_authorization_header_with_only_spaces() throws ServletException, IOException {
    when(request.getHeader("Authorization")).thenReturn("   ");

    jwtTokenFilter.doFilterInternal(request, response, filterChain);

    verify(jwtService, never()).getSubFromToken(any());
    verify(securityContext, never()).setAuthentication(any());
    verify(filterChain).doFilter(request, response);
  }

  @Test
  public void should_handle_authorization_header_with_multiple_spaces() throws ServletException, IOException {
    String authHeader = "Bearer   valid.jwt.token";

    when(request.getHeader("Authorization")).thenReturn(authHeader);
    when(jwtService.getSubFromToken("")).thenReturn(Optional.empty());

    jwtTokenFilter.doFilterInternal(request, response, filterChain);

    verify(jwtService).getSubFromToken("");
    verify(securityContext, never()).setAuthentication(any());
    verify(filterChain).doFilter(request, response);
  }

  @Test
  public void should_handle_authorization_header_with_extra_parts() throws ServletException, IOException {
    String validToken = "valid.jwt.token";
    String authHeader = "Bearer " + validToken + " extra";
    String userId = "user-123";

    when(request.getHeader("Authorization")).thenReturn(authHeader);
    when(jwtService.getSubFromToken(validToken)).thenReturn(Optional.of(userId));
    when(securityContext.getAuthentication()).thenReturn(null);
    when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

    jwtTokenFilter.doFilterInternal(request, response, filterChain);

    verify(securityContext).setAuthentication(any(UsernamePasswordAuthenticationToken.class));
    verify(filterChain).doFilter(request, response);
  }

  @Test
  public void should_handle_null_token_from_split() throws ServletException, IOException {
    String authHeader = "Bearer ";

    when(request.getHeader("Authorization")).thenReturn(authHeader);

    jwtTokenFilter.doFilterInternal(request, response, filterChain);

    verify(jwtService, never()).getSubFromToken(any());
    verify(securityContext, never()).setAuthentication(any());
    verify(filterChain).doFilter(request, response);
  }
}
