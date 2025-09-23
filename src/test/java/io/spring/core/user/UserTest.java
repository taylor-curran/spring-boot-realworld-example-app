package io.spring.core.user;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

public class UserTest {

  @Test
  public void should_create_user_with_all_fields() {
    User user = new User("test@example.com", "testuser", "password123", "Test bio", "http://example.com/image.jpg");
    
    assertThat(user.getId(), notNullValue());
    assertThat(user.getEmail(), is("test@example.com"));
    assertThat(user.getUsername(), is("testuser"));
    assertThat(user.getPassword(), is("password123"));
    assertThat(user.getBio(), is("Test bio"));
    assertThat(user.getImage(), is("http://example.com/image.jpg"));
  }

  @Test
  public void should_create_user_with_null_bio_and_image() {
    User user = new User("test@example.com", "testuser", "password123", null, null);
    
    assertThat(user.getId(), notNullValue());
    assertThat(user.getEmail(), is("test@example.com"));
    assertThat(user.getUsername(), is("testuser"));
    assertThat(user.getPassword(), is("password123"));
    assertThat(user.getBio(), is((String) null));
    assertThat(user.getImage(), is((String) null));
  }

  @Test
  public void should_update_all_fields_when_provided() {
    User user = new User("old@example.com", "olduser", "oldpass", "old bio", "old.jpg");
    
    user.update("new@example.com", "newuser", "newpass", "new bio", "new.jpg");
    
    assertThat(user.getEmail(), is("new@example.com"));
    assertThat(user.getUsername(), is("newuser"));
    assertThat(user.getPassword(), is("newpass"));
    assertThat(user.getBio(), is("new bio"));
    assertThat(user.getImage(), is("new.jpg"));
  }

  @Test
  public void should_not_update_email_when_empty() {
    User user = new User("test@example.com", "testuser", "password123", "bio", "image.jpg");
    
    user.update("", "newuser", "newpass", "new bio", "new.jpg");
    
    assertThat(user.getEmail(), is("test@example.com"));
    assertThat(user.getUsername(), is("newuser"));
    assertThat(user.getPassword(), is("newpass"));
    assertThat(user.getBio(), is("new bio"));
    assertThat(user.getImage(), is("new.jpg"));
  }

  @Test
  public void should_not_update_email_when_null() {
    User user = new User("test@example.com", "testuser", "password123", "bio", "image.jpg");
    
    user.update(null, "newuser", "newpass", "new bio", "new.jpg");
    
    assertThat(user.getEmail(), is("test@example.com"));
    assertThat(user.getUsername(), is("newuser"));
    assertThat(user.getPassword(), is("newpass"));
    assertThat(user.getBio(), is("new bio"));
    assertThat(user.getImage(), is("new.jpg"));
  }

  @Test
  public void should_not_update_username_when_empty() {
    User user = new User("test@example.com", "testuser", "password123", "bio", "image.jpg");
    
    user.update("new@example.com", "", "newpass", "new bio", "new.jpg");
    
    assertThat(user.getEmail(), is("new@example.com"));
    assertThat(user.getUsername(), is("testuser"));
    assertThat(user.getPassword(), is("newpass"));
    assertThat(user.getBio(), is("new bio"));
    assertThat(user.getImage(), is("new.jpg"));
  }

  @Test
  public void should_not_update_username_when_null() {
    User user = new User("test@example.com", "testuser", "password123", "bio", "image.jpg");
    
    user.update("new@example.com", null, "newpass", "new bio", "new.jpg");
    
    assertThat(user.getEmail(), is("new@example.com"));
    assertThat(user.getUsername(), is("testuser"));
    assertThat(user.getPassword(), is("newpass"));
    assertThat(user.getBio(), is("new bio"));
    assertThat(user.getImage(), is("new.jpg"));
  }

  @Test
  public void should_not_update_password_when_empty() {
    User user = new User("test@example.com", "testuser", "password123", "bio", "image.jpg");
    
    user.update("new@example.com", "newuser", "", "new bio", "new.jpg");
    
    assertThat(user.getEmail(), is("new@example.com"));
    assertThat(user.getUsername(), is("newuser"));
    assertThat(user.getPassword(), is("password123"));
    assertThat(user.getBio(), is("new bio"));
    assertThat(user.getImage(), is("new.jpg"));
  }

  @Test
  public void should_not_update_password_when_null() {
    User user = new User("test@example.com", "testuser", "password123", "bio", "image.jpg");
    
    user.update("new@example.com", "newuser", null, "new bio", "new.jpg");
    
    assertThat(user.getEmail(), is("new@example.com"));
    assertThat(user.getUsername(), is("newuser"));
    assertThat(user.getPassword(), is("password123"));
    assertThat(user.getBio(), is("new bio"));
    assertThat(user.getImage(), is("new.jpg"));
  }

  @Test
  public void should_not_update_bio_when_empty() {
    User user = new User("test@example.com", "testuser", "password123", "bio", "image.jpg");
    
    user.update("new@example.com", "newuser", "newpass", "", "new.jpg");
    
    assertThat(user.getEmail(), is("new@example.com"));
    assertThat(user.getUsername(), is("newuser"));
    assertThat(user.getPassword(), is("newpass"));
    assertThat(user.getBio(), is("bio"));
    assertThat(user.getImage(), is("new.jpg"));
  }

  @Test
  public void should_not_update_bio_when_null() {
    User user = new User("test@example.com", "testuser", "password123", "bio", "image.jpg");
    
    user.update("new@example.com", "newuser", "newpass", null, "new.jpg");
    
    assertThat(user.getEmail(), is("new@example.com"));
    assertThat(user.getUsername(), is("newuser"));
    assertThat(user.getPassword(), is("newpass"));
    assertThat(user.getBio(), is("bio"));
    assertThat(user.getImage(), is("new.jpg"));
  }

  @Test
  public void should_not_update_image_when_empty() {
    User user = new User("test@example.com", "testuser", "password123", "bio", "image.jpg");
    
    user.update("new@example.com", "newuser", "newpass", "new bio", "");
    
    assertThat(user.getEmail(), is("new@example.com"));
    assertThat(user.getUsername(), is("newuser"));
    assertThat(user.getPassword(), is("newpass"));
    assertThat(user.getBio(), is("new bio"));
    assertThat(user.getImage(), is("image.jpg"));
  }

  @Test
  public void should_not_update_image_when_null() {
    User user = new User("test@example.com", "testuser", "password123", "bio", "image.jpg");
    
    user.update("new@example.com", "newuser", "newpass", "new bio", null);
    
    assertThat(user.getEmail(), is("new@example.com"));
    assertThat(user.getUsername(), is("newuser"));
    assertThat(user.getPassword(), is("newpass"));
    assertThat(user.getBio(), is("new bio"));
    assertThat(user.getImage(), is("image.jpg"));
  }

  @Test
  public void should_update_only_non_empty_fields() {
    User user = new User("test@example.com", "testuser", "password123", "bio", "image.jpg");
    
    user.update("", "newuser", "", "new bio", "");
    
    assertThat(user.getEmail(), is("test@example.com"));
    assertThat(user.getUsername(), is("newuser"));
    assertThat(user.getPassword(), is("password123"));
    assertThat(user.getBio(), is("new bio"));
    assertThat(user.getImage(), is("image.jpg"));
  }

  @Test
  public void should_have_equals_based_on_id() {
    User user1 = new User("test1@example.com", "user1", "pass1", "bio1", "img1");
    User user2 = new User("test2@example.com", "user2", "pass2", "bio2", "img2");
    User user3 = new User("test1@example.com", "user1", "pass1", "bio1", "img1");
    
    assertThat(user1.equals(user1), is(true));
    assertThat(user1.equals(user2), is(false));
    assertThat(user1.equals(user3), is(false));
  }

  @Test
  public void should_have_consistent_hashcode_with_equals() {
    User user1 = new User("test@example.com", "testuser", "password123", "bio", "image.jpg");
    User user2 = new User("test@example.com", "testuser", "password123", "bio", "image.jpg");
    
    assertThat(user1.equals(user2), is(false));
    assertThat(user1.hashCode() == user2.hashCode(), is(false));
  }

  @Test
  public void should_handle_equals_with_null() {
    User user = new User("test@example.com", "testuser", "password123", "bio", "image.jpg");
    
    assertThat(user.equals(null), is(false));
  }

  @Test
  public void should_handle_equals_with_different_class() {
    User user = new User("test@example.com", "testuser", "password123", "bio", "image.jpg");
    String notAUser = "not a user";
    
    assertThat(user.equals(notAUser), is(false));
  }

  @Test
  public void should_handle_equals_with_same_instance() {
    User user = new User("test@example.com", "testuser", "password123", "bio", "image.jpg");
    
    assertThat(user.equals(user), is(true));
  }

  @Test
  public void should_handle_equals_with_different_ids() {
    User user1 = new User("test@example.com", "testuser", "password123", "bio", "image.jpg");
    User user2 = new User("test@example.com", "testuser", "password123", "bio", "image.jpg");
    
    assertThat(user1.getId().equals(user2.getId()), is(false));
    assertThat(user1.equals(user2), is(false));
  }

  @Test
  public void should_handle_equals_with_null_fields() {
    User user1 = new User("test@example.com", "testuser", "password123", null, null);
    User user2 = new User("test@example.com", "testuser", "password123", null, null);
    
    assertThat(user1.equals(user2), is(false));
  }

  @Test
  public void should_handle_hashcode_consistency() {
    User user = new User("test@example.com", "testuser", "password123", "bio", "image.jpg");
    
    int hashCode1 = user.hashCode();
    int hashCode2 = user.hashCode();
    
    assertThat(hashCode1, is(hashCode2));
  }

  @Test
  public void should_handle_hashcode_with_null_fields() {
    User user = new User("test@example.com", "testuser", "password123", null, null);
    
    int hashCode = user.hashCode();
    assertThat(hashCode, notNullValue());
  }

  @Test
  public void should_handle_equals_with_mixed_null_fields() {
    User user1 = new User("test@example.com", "testuser", "password123", "bio", null);
    User user2 = new User("test@example.com", "testuser", "password123", null, "image.jpg");
    
    assertThat(user1.equals(user2), is(false));
  }
}
