package io.spring.graphql;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.spring.core.user.User;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

class SecurityUtilTest {

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldReturnCurrentUserWhenAuthenticated() {
        User mockUser = new User("test@example.com", "testuser", "password", "bio", "image");
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            mockUser, null, java.util.List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        Optional<User> result = SecurityUtil.getCurrentUser();

        assertTrue(result.isPresent());
        assertEquals(mockUser, result.get());
        assertEquals("testuser", result.get().getUsername());
        assertEquals("test@example.com", result.get().getEmail());
    }

    @Test
    void shouldReturnEmptyWhenAnonymousAuthentication() {
        AnonymousAuthenticationToken anonymousAuth = new AnonymousAuthenticationToken(
            "anonymous", "anonymousUser", java.util.List.of(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))
        );
        
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(anonymousAuth);
        SecurityContextHolder.setContext(securityContext);

        Optional<User> result = SecurityUtil.getCurrentUser();

        assertFalse(result.isPresent());
    }

    @Test
    void shouldReturnEmptyWhenPrincipalIsNull() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(null);
        
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        Optional<User> result = SecurityUtil.getCurrentUser();

        assertFalse(result.isPresent());
    }

    @Test
    void shouldReturnEmptyWhenNoAuthentication() {
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);

        Optional<User> result = SecurityUtil.getCurrentUser();

        assertFalse(result.isPresent());
    }

    @Test
    void shouldReturnEmptyWhenNoSecurityContext() {
        SecurityContextHolder.clearContext();

        Optional<User> result = SecurityUtil.getCurrentUser();

        assertFalse(result.isPresent());
    }

    @Test
    void shouldHandleAuthenticationWithDifferentPrincipalType() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            "stringPrincipal", null, java.util.List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        assertThrows(ClassCastException.class, () -> {
            SecurityUtil.getCurrentUser();
        });
    }

    @Test
    void shouldHandleMultipleCallsConsistently() {
        User mockUser = new User("test@example.com", "testuser", "password", "bio", "image");
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            mockUser, null, java.util.List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        Optional<User> result1 = SecurityUtil.getCurrentUser();
        Optional<User> result2 = SecurityUtil.getCurrentUser();

        assertTrue(result1.isPresent());
        assertTrue(result2.isPresent());
        assertEquals(result1.get(), result2.get());
    }

    @Test
    void shouldReturnEmptyAfterClearingContext() {
        User mockUser = new User("test@example.com", "testuser", "password", "bio", "image");
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            mockUser, null, java.util.List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        Optional<User> resultBefore = SecurityUtil.getCurrentUser();
        assertTrue(resultBefore.isPresent());

        SecurityContextHolder.clearContext();

        Optional<User> resultAfter = SecurityUtil.getCurrentUser();
        assertFalse(resultAfter.isPresent());
    }
}
