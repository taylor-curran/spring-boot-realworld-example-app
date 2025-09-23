package io.spring.graphql;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import graphql.execution.DataFetcherResult;
import io.spring.api.exception.InvalidAuthenticationException;
import io.spring.api.exception.ResourceNotFoundException;
import graphql.schema.DataFetchingEnvironment;
import io.spring.application.UserQueryService;
import io.spring.application.data.UserData;
import io.spring.core.service.JwtService;
import io.spring.core.user.User;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
public class MeDatafetcherTest {

  @Mock private UserQueryService userQueryService;
  @Mock private JwtService jwtService;
  @Mock private DataFetchingEnvironment dataFetchingEnvironment;

  private MeDatafetcher meDatafetcher;
  private User currentUser;
  private UserData userData;

  @BeforeEach
  public void setUp() {
    meDatafetcher = new MeDatafetcher(userQueryService, jwtService);
    currentUser = new User("current@example.com", "currentuser", "password123", "Current bio", "current.jpg");
    
    userData = new UserData(
        currentUser.getId(),
        currentUser.getEmail(),
        currentUser.getUsername(),
        currentUser.getBio(),
        currentUser.getImage()
    );
  }

  @AfterEach
  public void cleanup() {
    SecurityContextHolder.clearContext();
  }

  @Test
  public void should_get_current_user_data_when_authenticated() {
    SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken(currentUser, null));
    
    when(userQueryService.findById(eq(currentUser.getId())))
        .thenReturn(Optional.of(userData));

    DataFetcherResult<io.spring.graphql.types.User> result = meDatafetcher.getMe("Bearer token123", dataFetchingEnvironment);

    assertThat(result, notNullValue());
    assertThat(result.getData(), notNullValue());
    assertThat(result.getData().getEmail(), is(currentUser.getEmail()));
    assertThat(result.getData().getUsername(), is(currentUser.getUsername()));
    verify(userQueryService).findById(eq(currentUser.getId()));
  }

  @Test
  public void should_return_null_when_not_authenticated() {
    SecurityContextHolder.getContext().setAuthentication(
        new AnonymousAuthenticationToken("key", "anonymous", java.util.Arrays.asList(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))));

    DataFetcherResult<io.spring.graphql.types.User> result = meDatafetcher.getMe("Bearer token123", dataFetchingEnvironment);

    assertThat(result, is((DataFetcherResult<io.spring.graphql.types.User>) null));
  }

  @Test
  public void should_return_null_when_no_authentication_context() {
    SecurityContextHolder.getContext().setAuthentication(null);

    DataFetcherResult<io.spring.graphql.types.User> result = meDatafetcher.getMe("Bearer token123", dataFetchingEnvironment);

    assertThat(result, is((DataFetcherResult<io.spring.graphql.types.User>) null));
  }

  @Test
  public void should_throw_exception_when_user_not_found_in_database() {
    SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken(currentUser, null));
    
    when(userQueryService.findById(eq(currentUser.getId())))
        .thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> {
      meDatafetcher.getMe("Bearer token123", dataFetchingEnvironment);
    });
  }

  @Test
  public void should_handle_authentication_with_null_principal() {
    TestingAuthenticationToken authWithNullPrincipal = new TestingAuthenticationToken(null, null);
    SecurityContextHolder.getContext().setAuthentication(authWithNullPrincipal);

    DataFetcherResult<io.spring.graphql.types.User> result = meDatafetcher.getMe("Bearer token123", dataFetchingEnvironment);

    assertThat(result, is((DataFetcherResult<io.spring.graphql.types.User>) null));
  }

  @Test
  public void should_handle_authorization_header_parsing() {
    SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken(currentUser, null));
    
    when(userQueryService.findById(eq(currentUser.getId())))
        .thenReturn(Optional.of(userData));

    DataFetcherResult<io.spring.graphql.types.User> result = meDatafetcher.getMe("Bearer mytoken123", dataFetchingEnvironment);

    assertThat(result, notNullValue());
    assertThat(result.getData(), notNullValue());
    assertThat(result.getData().getToken(), is("mytoken123"));
    verify(userQueryService).findById(eq(currentUser.getId()));
  }

  @Test
  public void should_get_user_payload_user_from_local_context() {
    String expectedToken = "generated.jwt.token";
    when(dataFetchingEnvironment.getLocalContext()).thenReturn(currentUser);
    when(jwtService.toToken(eq(currentUser))).thenReturn(expectedToken);

    DataFetcherResult<io.spring.graphql.types.User> result = meDatafetcher.getUserPayloadUser(dataFetchingEnvironment);

    assertThat(result, notNullValue());
    assertThat(result.getData(), notNullValue());
    assertThat(result.getData().getEmail(), is(currentUser.getEmail()));
    assertThat(result.getData().getUsername(), is(currentUser.getUsername()));
    assertThat(result.getData().getToken(), is(expectedToken));
    assertThat(result.getLocalContext(), is(currentUser));
    verify(jwtService).toToken(eq(currentUser));
  }

  @Test
  public void should_handle_user_payload_user_with_different_user_data() {
    User differentUser = new User("different@example.com", "differentuser", "password456", "Different bio", "different.jpg");
    String expectedToken = "different.jwt.token";
    
    when(dataFetchingEnvironment.getLocalContext()).thenReturn(differentUser);
    when(jwtService.toToken(eq(differentUser))).thenReturn(expectedToken);

    DataFetcherResult<io.spring.graphql.types.User> result = meDatafetcher.getUserPayloadUser(dataFetchingEnvironment);

    assertThat(result, notNullValue());
    assertThat(result.getData(), notNullValue());
    assertThat(result.getData().getEmail(), is(differentUser.getEmail()));
    assertThat(result.getData().getUsername(), is(differentUser.getUsername()));
    assertThat(result.getData().getToken(), is(expectedToken));
    assertThat(result.getLocalContext(), is(differentUser));
    verify(jwtService).toToken(eq(differentUser));
  }

  @Test
  public void should_handle_user_payload_user_with_empty_bio_and_image() {
    User userWithEmptyFields = new User("empty@example.com", "emptyuser", "password789", "", "");
    String expectedToken = "empty.jwt.token";
    
    when(dataFetchingEnvironment.getLocalContext()).thenReturn(userWithEmptyFields);
    when(jwtService.toToken(eq(userWithEmptyFields))).thenReturn(expectedToken);

    DataFetcherResult<io.spring.graphql.types.User> result = meDatafetcher.getUserPayloadUser(dataFetchingEnvironment);

    assertThat(result, notNullValue());
    assertThat(result.getData(), notNullValue());
    assertThat(result.getData().getEmail(), is(userWithEmptyFields.getEmail()));
    assertThat(result.getData().getUsername(), is(userWithEmptyFields.getUsername()));
    assertThat(result.getData().getToken(), is(expectedToken));
    assertThat(result.getLocalContext(), is(userWithEmptyFields));
    verify(jwtService).toToken(eq(userWithEmptyFields));
  }
}
