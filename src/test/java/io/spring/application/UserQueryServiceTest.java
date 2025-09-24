package io.spring.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.spring.application.data.UserData;
import io.spring.infrastructure.mybatis.readservice.UserReadService;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserQueryServiceTest {

  @Mock private UserReadService userReadService;

  @InjectMocks private UserQueryService userQueryService;

  @Test
  public void should_find_user_by_id_when_user_exists() {
    String userId = "user123";
    UserData expectedUserData =
        new UserData(userId, "test@example.com", "testuser", "Test bio", "profile.jpg");

    when(userReadService.findById(userId)).thenReturn(expectedUserData);

    Optional<UserData> result = userQueryService.findById(userId);

    assertThat(result).isPresent();
    assertThat(result.get()).isEqualTo(expectedUserData);
    assertThat(result.get().getId()).isEqualTo(userId);
    assertThat(result.get().getEmail()).isEqualTo("test@example.com");
    assertThat(result.get().getUsername()).isEqualTo("testuser");
    verify(userReadService).findById(userId);
  }

  @Test
  public void should_return_empty_when_user_not_found() {
    String userId = "nonexistent";

    when(userReadService.findById(userId)).thenReturn(null);

    Optional<UserData> result = userQueryService.findById(userId);

    assertThat(result).isEmpty();
    verify(userReadService).findById(userId);
  }

  @Test
  public void should_handle_null_user_id() {
    when(userReadService.findById(null)).thenReturn(null);

    Optional<UserData> result = userQueryService.findById(null);

    assertThat(result).isEmpty();
    verify(userReadService).findById(null);
  }

  @Test
  public void should_handle_empty_user_id() {
    String emptyId = "";

    when(userReadService.findById(emptyId)).thenReturn(null);

    Optional<UserData> result = userQueryService.findById(emptyId);

    assertThat(result).isEmpty();
    verify(userReadService).findById(emptyId);
  }

  @Test
  public void should_handle_whitespace_user_id() {
    String whitespaceId = "   ";

    when(userReadService.findById(whitespaceId)).thenReturn(null);

    Optional<UserData> result = userQueryService.findById(whitespaceId);

    assertThat(result).isEmpty();
    verify(userReadService).findById(whitespaceId);
  }

  @Test
  public void should_handle_special_characters_in_user_id() {
    String specialId = "user-123_test@domain.com";
    UserData userData =
        new UserData(
            specialId, "special@example.com", "special_user", "Special bio", "special.jpg");

    when(userReadService.findById(specialId)).thenReturn(userData);

    Optional<UserData> result = userQueryService.findById(specialId);

    assertThat(result).isPresent();
    assertThat(result.get().getId()).isEqualTo(specialId);
    verify(userReadService).findById(specialId);
  }

  @Test
  public void should_handle_long_user_id() {
    String longId = "very-long-user-id-".repeat(10);
    UserData userData =
        new UserData(longId, "long@example.com", "longuser", "Long bio", "long.jpg");

    when(userReadService.findById(longId)).thenReturn(userData);

    Optional<UserData> result = userQueryService.findById(longId);

    assertThat(result).isPresent();
    assertThat(result.get().getId()).isEqualTo(longId);
    verify(userReadService).findById(longId);
  }

  @Test
  public void should_handle_unicode_user_id() {
    String unicodeId = "用户123";
    UserData userData =
        new UserData(
            unicodeId, "unicode@example.com", "unicodeuser", "Unicode bio 测试", "unicode.jpg");

    when(userReadService.findById(unicodeId)).thenReturn(userData);

    Optional<UserData> result = userQueryService.findById(unicodeId);

    assertThat(result).isPresent();
    assertThat(result.get().getId()).isEqualTo(unicodeId);
    assertThat(result.get().getBio()).isEqualTo("Unicode bio 测试");
    verify(userReadService).findById(unicodeId);
  }

  @Test
  public void should_handle_user_data_with_null_fields() {
    String userId = "user456";
    UserData userDataWithNulls = new UserData(userId, null, null, null, null);

    when(userReadService.findById(userId)).thenReturn(userDataWithNulls);

    Optional<UserData> result = userQueryService.findById(userId);

    assertThat(result).isPresent();
    assertThat(result.get().getId()).isEqualTo(userId);
    assertThat(result.get().getEmail()).isNull();
    assertThat(result.get().getUsername()).isNull();
    assertThat(result.get().getBio()).isNull();
    assertThat(result.get().getImage()).isNull();
    verify(userReadService).findById(userId);
  }

  @Test
  public void should_handle_user_data_with_empty_fields() {
    String userId = "user789";
    UserData userDataWithEmpties = new UserData(userId, "", "", "", "");

    when(userReadService.findById(userId)).thenReturn(userDataWithEmpties);

    Optional<UserData> result = userQueryService.findById(userId);

    assertThat(result).isPresent();
    assertThat(result.get().getId()).isEqualTo(userId);
    assertThat(result.get().getEmail()).isEqualTo("");
    assertThat(result.get().getUsername()).isEqualTo("");
    assertThat(result.get().getBio()).isEqualTo("");
    assertThat(result.get().getImage()).isEqualTo("");
    verify(userReadService).findById(userId);
  }

  @Test
  public void should_handle_multiple_consecutive_calls() {
    String userId1 = "user1";
    String userId2 = "user2";
    UserData userData1 = new UserData(userId1, "email1@test.com", "user1", "Bio1", "image1.jpg");
    UserData userData2 = new UserData(userId2, "email2@test.com", "user2", "Bio2", "image2.jpg");

    when(userReadService.findById(userId1)).thenReturn(userData1);
    when(userReadService.findById(userId2)).thenReturn(userData2);

    Optional<UserData> result1 = userQueryService.findById(userId1);
    Optional<UserData> result2 = userQueryService.findById(userId2);

    assertThat(result1).isPresent();
    assertThat(result1.get()).isEqualTo(userData1);
    assertThat(result2).isPresent();
    assertThat(result2.get()).isEqualTo(userData2);
    verify(userReadService).findById(userId1);
    verify(userReadService).findById(userId2);
  }

  @Test
  public void should_delegate_to_user_read_service() {
    String userId = "delegation-test";
    UserData mockUserData = mock(UserData.class);

    when(userReadService.findById(userId)).thenReturn(mockUserData);

    Optional<UserData> result = userQueryService.findById(userId);

    assertThat(result).isPresent();
    assertThat(result.get()).isEqualTo(mockUserData);
    verify(userReadService).findById(userId);
  }
}
