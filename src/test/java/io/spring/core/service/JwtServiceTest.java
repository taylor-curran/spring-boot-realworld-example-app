package io.spring.core.service;

import static org.junit.jupiter.api.Assertions.*;

import io.spring.core.user.User;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class JwtServiceTest {

    @Test
    void shouldDefineToTokenMethod() {
        JwtService jwtService = new JwtService() {
            @Override
            public String toToken(User user) {
                return "mock-token-" + user.getId();
            }

            @Override
            public Optional<String> getSubFromToken(String token) {
                return Optional.of("mock-subject");
            }
        };

        User user = new User("test@example.com", "testuser", "password", "bio", "image.jpg");
        String token = jwtService.toToken(user);

        assertNotNull(token);
        assertTrue(token.startsWith("mock-token-"));
        assertTrue(token.contains(user.getId()));
    }

    @Test
    void shouldDefineGetSubFromTokenMethod() {
        JwtService jwtService = new JwtService() {
            @Override
            public String toToken(User user) {
                return "mock-token";
            }

            @Override
            public Optional<String> getSubFromToken(String token) {
                if (token != null && token.startsWith("valid-")) {
                    return Optional.of("user123");
                }
                return Optional.empty();
            }
        };

        Optional<String> validResult = jwtService.getSubFromToken("valid-token");
        Optional<String> invalidResult = jwtService.getSubFromToken("invalid-token");

        assertTrue(validResult.isPresent());
        assertEquals("user123", validResult.get());
        assertFalse(invalidResult.isPresent());
    }

    @Test
    void shouldHandleNullUserInToToken() {
        JwtService jwtService = new JwtService() {
            @Override
            public String toToken(User user) {
                if (user == null) {
                    return null;
                }
                return "token-" + user.getId();
            }

            @Override
            public Optional<String> getSubFromToken(String token) {
                return Optional.empty();
            }
        };

        String token = jwtService.toToken(null);

        assertNull(token);
    }

    @Test
    void shouldHandleNullTokenInGetSubFromToken() {
        JwtService jwtService = new JwtService() {
            @Override
            public String toToken(User user) {
                return "mock-token";
            }

            @Override
            public Optional<String> getSubFromToken(String token) {
                if (token == null) {
                    return Optional.empty();
                }
                return Optional.of("subject");
            }
        };

        Optional<String> result = jwtService.getSubFromToken(null);

        assertFalse(result.isPresent());
    }

    @Test
    void shouldHandleEmptyTokenInGetSubFromToken() {
        JwtService jwtService = new JwtService() {
            @Override
            public String toToken(User user) {
                return "mock-token";
            }

            @Override
            public Optional<String> getSubFromToken(String token) {
                if (token == null || token.isEmpty()) {
                    return Optional.empty();
                }
                return Optional.of("subject");
            }
        };

        Optional<String> result = jwtService.getSubFromToken("");

        assertFalse(result.isPresent());
    }

    @Test
    void shouldHandleUserWithNullId() {
        JwtService jwtService = new JwtService() {
            @Override
            public String toToken(User user) {
                if (user == null || user.getId() == null) {
                    return null;
                }
                return "token-" + user.getId();
            }

            @Override
            public Optional<String> getSubFromToken(String token) {
                return Optional.empty();
            }
        };

        User userWithNullId = new User() {
            @Override
            public String getId() {
                return null;
            }
        };

        String token = jwtService.toToken(userWithNullId);

        assertNull(token);
    }

    @Test
    void shouldHandleUserWithEmptyId() {
        JwtService jwtService = new JwtService() {
            @Override
            public String toToken(User user) {
                if (user == null || user.getId() == null || user.getId().isEmpty()) {
                    return null;
                }
                return "token-" + user.getId();
            }

            @Override
            public Optional<String> getSubFromToken(String token) {
                return Optional.empty();
            }
        };

        User userWithEmptyId = new User() {
            @Override
            public String getId() {
                return "";
            }
        };

        String token = jwtService.toToken(userWithEmptyId);

        assertNull(token);
    }

    @Test
    void shouldHandleSpecialCharactersInToken() {
        JwtService jwtService = new JwtService() {
            @Override
            public String toToken(User user) {
                return "token-" + user.getId();
            }

            @Override
            public Optional<String> getSubFromToken(String token) {
                if (token != null && token.contains("special-chars!@#$%")) {
                    return Optional.of("special-user");
                }
                return Optional.empty();
            }
        };

        Optional<String> result = jwtService.getSubFromToken("special-chars!@#$%");

        assertTrue(result.isPresent());
        assertEquals("special-user", result.get());
    }

    @Test
    void shouldHandleUnicodeInToken() {
        JwtService jwtService = new JwtService() {
            @Override
            public String toToken(User user) {
                return "token-" + user.getId();
            }

            @Override
            public Optional<String> getSubFromToken(String token) {
                if (token != null && token.contains("测试")) {
                    return Optional.of("unicode-user");
                }
                return Optional.empty();
            }
        };

        Optional<String> result = jwtService.getSubFromToken("token-测试-unicode");

        assertTrue(result.isPresent());
        assertEquals("unicode-user", result.get());
    }

    @Test
    void shouldReturnOptionalEmpty() {
        JwtService jwtService = new JwtService() {
            @Override
            public String toToken(User user) {
                return "mock-token";
            }

            @Override
            public Optional<String> getSubFromToken(String token) {
                return Optional.empty();
            }
        };

        Optional<String> result = jwtService.getSubFromToken("any-token");

        assertNotNull(result);
        assertFalse(result.isPresent());
    }
}
