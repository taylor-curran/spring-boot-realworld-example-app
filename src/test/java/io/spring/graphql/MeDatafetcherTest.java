package io.spring.graphql;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import io.spring.api.exception.ResourceNotFoundException;
import io.spring.application.UserQueryService;
import io.spring.application.data.UserData;
import io.spring.core.service.JwtService;
import io.spring.core.user.User;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class MeDatafetcherTest {

    @Mock
    private UserQueryService userQueryService;

    @Mock
    private JwtService jwtService;

    @Mock
    private DataFetchingEnvironment dataFetchingEnvironment;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private AnonymousAuthenticationToken anonymousAuthentication;

    @InjectMocks
    private MeDatafetcher meDatafetcher;

    private User testUser;
    private UserData testUserData;

    @BeforeEach
    void setUp() {
        testUser = new User("test@example.com", "testuser", "hashedpassword", "Test Bio", "avatar.jpg");
        testUserData = new UserData(testUser.getId(), "test@example.com", "testuser", "Test Bio", "avatar.jpg");
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void shouldGetMeSuccessfully() {
        String authHeader = "Bearer valid-jwt-token";
        
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(testUser);
        when(userQueryService.findById(testUser.getId())).thenReturn(Optional.of(testUserData));

        DataFetcherResult<io.spring.graphql.types.User> result = meDatafetcher.getMe(authHeader, dataFetchingEnvironment);

        assertNotNull(result);
        assertNotNull(result.getData());
        assertEquals("test@example.com", result.getData().getEmail());
        assertEquals("testuser", result.getData().getUsername());
        assertEquals("valid-jwt-token", result.getData().getToken());
        assertEquals(testUser, result.getLocalContext());
        verify(userQueryService).findById(testUser.getId());
    }

    @Test
    void shouldReturnNullWhenUserIsAnonymous() {
        when(securityContext.getAuthentication()).thenReturn(anonymousAuthentication);

        DataFetcherResult<io.spring.graphql.types.User> result = meDatafetcher.getMe("Bearer token", dataFetchingEnvironment);

        assertNull(result);
        verify(userQueryService, never()).findById(any());
    }

    @Test
    void shouldReturnNullWhenAuthenticationPrincipalIsNull() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(null);

        DataFetcherResult<io.spring.graphql.types.User> result = meDatafetcher.getMe("Bearer token", dataFetchingEnvironment);

        assertNull(result);
        verify(userQueryService, never()).findById(any());
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenUserDataNotFound() {
        String authHeader = "Bearer valid-jwt-token";
        
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(testUser);
        when(userQueryService.findById(testUser.getId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            meDatafetcher.getMe(authHeader, dataFetchingEnvironment);
        });
    }

    @Test
    void shouldHandleAuthorizationHeaderWithoutBearer() {
        String authHeader = "invalid-format-token";
        
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(testUser);
        when(userQueryService.findById(testUser.getId())).thenReturn(Optional.of(testUserData));

        assertThrows(ArrayIndexOutOfBoundsException.class, () -> {
            meDatafetcher.getMe(authHeader, dataFetchingEnvironment);
        });
    }

    @Test
    void shouldGetUserPayloadUserSuccessfully() {
        String expectedToken = "generated-jwt-token";
        
        when(dataFetchingEnvironment.getLocalContext()).thenReturn(testUser);
        when(jwtService.toToken(testUser)).thenReturn(expectedToken);

        DataFetcherResult<io.spring.graphql.types.User> result = meDatafetcher.getUserPayloadUser(dataFetchingEnvironment);

        assertNotNull(result);
        assertNotNull(result.getData());
        assertEquals("test@example.com", result.getData().getEmail());
        assertEquals("testuser", result.getData().getUsername());
        assertEquals(expectedToken, result.getData().getToken());
        assertEquals(testUser, result.getLocalContext());
        verify(jwtService).toToken(testUser);
    }

    @Test
    void shouldHandleUserPayloadUserWithNullLocalContext() {
        when(dataFetchingEnvironment.getLocalContext()).thenReturn(null);

        assertThrows(NullPointerException.class, () -> {
            meDatafetcher.getUserPayloadUser(dataFetchingEnvironment);
        });
    }

    @Test
    void shouldHandleUserWithEmptyBio() {
        User userWithEmptyBio = new User("test@example.com", "testuser", "password", "", "avatar.jpg");
        UserData userDataWithEmptyBio = new UserData(userWithEmptyBio.getId(), "test@example.com", "testuser", "", "avatar.jpg");
        String authHeader = "Bearer valid-jwt-token";
        
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userWithEmptyBio);
        when(userQueryService.findById(userWithEmptyBio.getId())).thenReturn(Optional.of(userDataWithEmptyBio));

        DataFetcherResult<io.spring.graphql.types.User> result = meDatafetcher.getMe(authHeader, dataFetchingEnvironment);

        assertNotNull(result);
        assertEquals("testuser", result.getData().getUsername());
        assertEquals("test@example.com", result.getData().getEmail());
    }

    @Test
    void shouldHandleUserWithNullImage() {
        User userWithNullImage = new User("test@example.com", "testuser", "password", "Bio", null);
        UserData userDataWithNullImage = new UserData(userWithNullImage.getId(), "test@example.com", "testuser", "Bio", null);
        String authHeader = "Bearer valid-jwt-token";
        
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userWithNullImage);
        when(userQueryService.findById(userWithNullImage.getId())).thenReturn(Optional.of(userDataWithNullImage));

        DataFetcherResult<io.spring.graphql.types.User> result = meDatafetcher.getMe(authHeader, dataFetchingEnvironment);

        assertNotNull(result);
        assertEquals("testuser", result.getData().getUsername());
        assertEquals("test@example.com", result.getData().getEmail());
    }
}
