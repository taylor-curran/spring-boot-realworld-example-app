package io.spring.application.user;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.spring.core.user.User;
import io.spring.core.user.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldCreateUserSuccessfully() {
        RegisterParam param = new RegisterParam("test@example.com", "testuser", "password123");

        when(passwordEncoder.encode("password123")).thenReturn("encoded_password");

        User result = userService.createUser(param);

        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        assertEquals("testuser", result.getUsername());
        assertEquals("encoded_password", result.getPassword());
        assertEquals("", result.getBio());
        verify(userRepository).save(result);
        verify(passwordEncoder).encode("password123");
    }

    @Test
    void shouldCreateUserWithSpecialCharactersInEmail() {
        RegisterParam param = new RegisterParam("test+user@example.com", "testuser", "password123");

        when(passwordEncoder.encode("password123")).thenReturn("encoded_password");

        User result = userService.createUser(param);

        assertNotNull(result);
        assertEquals("test+user@example.com", result.getEmail());
        verify(userRepository).save(result);
    }

    @Test
    void shouldCreateUserWithSpecialCharactersInUsername() {
        RegisterParam param = new RegisterParam("test@example.com", "test_user-123", "password123");

        when(passwordEncoder.encode("password123")).thenReturn("encoded_password");

        User result = userService.createUser(param);

        assertNotNull(result);
        assertEquals("test_user-123", result.getUsername());
        verify(userRepository).save(result);
    }

    @Test
    void shouldCreateUserWithComplexPassword() {
        RegisterParam param = new RegisterParam("test@example.com", "testuser", "Complex!Password@123#$%");

        when(passwordEncoder.encode("Complex!Password@123#$%")).thenReturn("complex_encoded_password");

        User result = userService.createUser(param);

        assertNotNull(result);
        assertEquals("complex_encoded_password", result.getPassword());
        verify(passwordEncoder).encode("Complex!Password@123#$%");
    }

    @Test
    void shouldCreateUserWithUnicodeCharacters() {
        RegisterParam param = new RegisterParam("测试@example.com", "用户名", "密码123");

        when(passwordEncoder.encode("密码123")).thenReturn("unicode_encoded_password");

        User result = userService.createUser(param);

        assertNotNull(result);
        assertEquals("测试@example.com", result.getEmail());
        assertEquals("用户名", result.getUsername());
        assertEquals("unicode_encoded_password", result.getPassword());
        verify(userRepository).save(result);
    }

    @Test
    void shouldUpdateUserSuccessfully() {
        User existingUser = new User("old@example.com", "olduser", "oldpassword", "old bio", "old.jpg");
        UpdateUserParam param = UpdateUserParam.builder()
            .email("new@example.com")
            .username("newuser")
            .password("newpassword")
            .bio("new bio")
            .image("new.jpg")
            .build();

        UpdateUserCommand command = new UpdateUserCommand(existingUser, param);

        userService.updateUser(command);

        assertEquals("new@example.com", existingUser.getEmail());
        assertEquals("newuser", existingUser.getUsername());
        assertEquals("newpassword", existingUser.getPassword());
        assertEquals("new bio", existingUser.getBio());
        assertEquals("new.jpg", existingUser.getImage());
        verify(userRepository).save(existingUser);
    }

    @Test
    void shouldUpdateUserWithPartialData() {
        User existingUser = new User("old@example.com", "olduser", "oldpassword", "old bio", "old.jpg");
        UpdateUserParam param = UpdateUserParam.builder()
            .email("new@example.com")
            .build();

        UpdateUserCommand command = new UpdateUserCommand(existingUser, param);

        userService.updateUser(command);

        assertEquals("new@example.com", existingUser.getEmail());
        assertEquals("olduser", existingUser.getUsername());
        assertEquals("oldpassword", existingUser.getPassword());
        assertEquals("old bio", existingUser.getBio());
        assertEquals("old.jpg", existingUser.getImage());
        verify(userRepository).save(existingUser);
    }

    @Test
    void shouldUpdateUserWithEmptyStrings() {
        User existingUser = new User("old@example.com", "olduser", "oldpassword", "old bio", "old.jpg");
        UpdateUserParam param = new UpdateUserParam("", "", "", "", "");

        UpdateUserCommand command = new UpdateUserCommand(existingUser, param);

        userService.updateUser(command);

        assertEquals("old@example.com", existingUser.getEmail());
        assertEquals("olduser", existingUser.getUsername());
        assertEquals("oldpassword", existingUser.getPassword());
        assertEquals("old bio", existingUser.getBio());
        assertEquals("old.jpg", existingUser.getImage());
        verify(userRepository).save(existingUser);
    }

    @Test
    void shouldHandleRepositoryInteraction() {
        RegisterParam param = new RegisterParam("test@example.com", "testuser", "password123");

        when(passwordEncoder.encode("password123")).thenReturn("encoded_password");

        userService.createUser(param);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void shouldCreateMultipleUsers() {
        RegisterParam param1 = new RegisterParam("user1@example.com", "user1", "password1");
        RegisterParam param2 = new RegisterParam("user2@example.com", "user2", "password2");

        when(passwordEncoder.encode("password1")).thenReturn("encoded_password1");
        when(passwordEncoder.encode("password2")).thenReturn("encoded_password2");

        User user1 = userService.createUser(param1);
        User user2 = userService.createUser(param2);

        assertNotEquals(user1.getId(), user2.getId());
        assertEquals("user1@example.com", user1.getEmail());
        assertEquals("user2@example.com", user2.getEmail());
        verify(userRepository, times(2)).save(any(User.class));
    }
}
