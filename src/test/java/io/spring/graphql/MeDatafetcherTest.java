package io.spring.graphql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import io.spring.api.exception.ResourceNotFoundException;
import io.spring.application.UserQueryService;
import io.spring.application.data.UserData;
import io.spring.core.service.JwtService;
import io.spring.graphql.types.User;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
public class MeDatafetcherTest {

  @Mock private UserQueryService userQueryService;

  @Mock private JwtService jwtService;

  @InjectMocks private MeDatafetcher meDatafetcher;

  @Mock private DataFetchingEnvironment dataFetchingEnvironment;

  @Mock private SecurityContext securityContext;

  @Mock private Authentication authentication;

  private io.spring.core.user.User testUser;
  private UserData testUserData;

  @BeforeEach
  public void setUp() {
    testUser =
        new io.spring.core.user.User(
            "test@example.com", "testuser", "password", "bio", "image.jpg");
    testUserData = new UserData("user123", "test@example.com", "testuser", "bio", "image.jpg");
  }

  @Test
  public void should_return_me_with_valid_authentication() {
    String authorization = "Bearer validtoken123";

    when(authentication.getPrincipal()).thenReturn(testUser);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(userQueryService.findById(testUser.getId())).thenReturn(Optional.of(testUserData));

    try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder =
        Mockito.mockStatic(SecurityContextHolder.class)) {
      mockedSecurityContextHolder
          .when(SecurityContextHolder::getContext)
          .thenReturn(securityContext);

      DataFetcherResult<User> result = meDatafetcher.getMe(authorization, dataFetchingEnvironment);

      assertThat(result).isNotNull();
      assertThat(result.getData()).isNotNull();
      assertThat(result.getData().getEmail()).isEqualTo("test@example.com");
      assertThat(result.getData().getUsername()).isEqualTo("testuser");
      assertThat(result.getData().getToken()).isEqualTo("validtoken123");
      assertThat(result.getLocalContext()).isEqualTo(testUser);
    }
  }

  @Test
  public void should_return_null_for_anonymous_authentication() {
    String authorization = "Bearer token123";
    AnonymousAuthenticationToken anonymousAuth = mock(AnonymousAuthenticationToken.class);

    when(securityContext.getAuthentication()).thenReturn(anonymousAuth);

    try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder =
        Mockito.mockStatic(SecurityContextHolder.class)) {
      mockedSecurityContextHolder
          .when(SecurityContextHolder::getContext)
          .thenReturn(securityContext);

      DataFetcherResult<User> result = meDatafetcher.getMe(authorization, dataFetchingEnvironment);

      assertThat(result).isNull();
    }
  }

  @Test
  public void should_return_null_for_null_principal() {
    String authorization = "Bearer token123";

    when(authentication.getPrincipal()).thenReturn(null);
    when(securityContext.getAuthentication()).thenReturn(authentication);

    try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder =
        Mockito.mockStatic(SecurityContextHolder.class)) {
      mockedSecurityContextHolder
          .when(SecurityContextHolder::getContext)
          .thenReturn(securityContext);

      DataFetcherResult<User> result = meDatafetcher.getMe(authorization, dataFetchingEnvironment);

      assertThat(result).isNull();
    }
  }

  @Test
  public void should_throw_exception_when_user_not_found() {
    String authorization = "Bearer validtoken123";

    when(authentication.getPrincipal()).thenReturn(testUser);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(userQueryService.findById(testUser.getId())).thenReturn(Optional.empty());

    try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder =
        Mockito.mockStatic(SecurityContextHolder.class)) {
      mockedSecurityContextHolder
          .when(SecurityContextHolder::getContext)
          .thenReturn(securityContext);

      assertThatThrownBy(() -> meDatafetcher.getMe(authorization, dataFetchingEnvironment))
          .isInstanceOf(ResourceNotFoundException.class);
    }
  }

  @Test
  public void should_handle_authorization_header_with_bearer_prefix() {
    String authorization = "Bearer token-with-special-chars_123";

    when(authentication.getPrincipal()).thenReturn(testUser);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(userQueryService.findById(testUser.getId())).thenReturn(Optional.of(testUserData));

    try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder =
        Mockito.mockStatic(SecurityContextHolder.class)) {
      mockedSecurityContextHolder
          .when(SecurityContextHolder::getContext)
          .thenReturn(securityContext);

      DataFetcherResult<User> result = meDatafetcher.getMe(authorization, dataFetchingEnvironment);

      assertThat(result.getData().getToken()).isEqualTo("token-with-special-chars_123");
    }
  }

  @Test
  public void should_get_user_payload_user() {
    when(dataFetchingEnvironment.getLocalContext()).thenReturn(testUser);
    when(jwtService.toToken(testUser)).thenReturn("generated-jwt-token");

    DataFetcherResult<User> result = meDatafetcher.getUserPayloadUser(dataFetchingEnvironment);

    assertThat(result).isNotNull();
    assertThat(result.getData()).isNotNull();
    assertThat(result.getData().getEmail()).isEqualTo(testUser.getEmail());
    assertThat(result.getData().getUsername()).isEqualTo(testUser.getUsername());
    assertThat(result.getData().getToken()).isEqualTo("generated-jwt-token");
    assertThat(result.getLocalContext()).isEqualTo(testUser);
  }

  @Test
  public void should_handle_user_with_empty_fields() {
    io.spring.core.user.User userWithEmptyFields = new io.spring.core.user.User("", "", "", "", "");
    UserData userDataWithEmptyFields = new UserData("user123", "", "", "", "");
    String authorization = "Bearer token123";

    when(authentication.getPrincipal()).thenReturn(userWithEmptyFields);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(userQueryService.findById(userWithEmptyFields.getId()))
        .thenReturn(Optional.of(userDataWithEmptyFields));

    try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder =
        Mockito.mockStatic(SecurityContextHolder.class)) {
      mockedSecurityContextHolder
          .when(SecurityContextHolder::getContext)
          .thenReturn(securityContext);

      DataFetcherResult<User> result = meDatafetcher.getMe(authorization, dataFetchingEnvironment);

      assertThat(result.getData().getEmail()).isEqualTo("");
      assertThat(result.getData().getUsername()).isEqualTo("");
      assertThat(result.getData().getToken()).isEqualTo("token123");
    }
  }
}
