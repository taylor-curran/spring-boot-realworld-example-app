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
class MeDatafetcherSimpleTest {

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

    @InjectMocks
    private MeDatafetcher meDatafetcher;

    private io.spring.core.user.User testUser;
    private UserData userData;

    @BeforeEach
    void setUp() {
        testUser = new io.spring.core.user.User("test@example.com", "testuser", "password", "Test Bio", "test.jpg");
        userData = new UserData("user-id", "test@example.com", "testuser", "Test Bio", "test.jpg");
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void shouldReturnCurrentUserWhenAuthenticated() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(testUser);
        when(userQueryService.findById(testUser.getId())).thenReturn(Optional.of(userData));

        DataFetcherResult<io.spring.graphql.types.User> result = meDatafetcher.getMe("Bearer token123", dataFetchingEnvironment);

        assertNotNull(result);
        assertNotNull(result.getData());
        assertEquals(testUser, result.getLocalContext());
        verify(userQueryService).findById(testUser.getId());
    }

    @Test
    void shouldReturnNullWhenAnonymousAuthentication() {
        AnonymousAuthenticationToken anonymousAuth = mock(AnonymousAuthenticationToken.class);
        when(securityContext.getAuthentication()).thenReturn(anonymousAuth);

        DataFetcherResult<io.spring.graphql.types.User> result = meDatafetcher.getMe("Bearer token123", dataFetchingEnvironment);

        assertNull(result);
        verify(userQueryService, never()).findById(anyString());
    }

    @Test
    void shouldReturnNullWhenNoPrincipal() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(null);

        DataFetcherResult<io.spring.graphql.types.User> result = meDatafetcher.getMe("Bearer token123", dataFetchingEnvironment);

        assertNull(result);
        verify(userQueryService, never()).findById(anyString());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(testUser);
        when(userQueryService.findById(testUser.getId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            meDatafetcher.getMe("Bearer token123", dataFetchingEnvironment);
        });

        verify(userQueryService).findById(testUser.getId());
    }
}
