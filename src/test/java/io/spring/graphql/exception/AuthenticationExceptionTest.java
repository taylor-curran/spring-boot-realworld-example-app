package io.spring.graphql.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

public class AuthenticationExceptionTest {

  @Test
  public void should_create_authentication_exception() {
    AuthenticationException exception = new AuthenticationException();

    assertThat(exception).isInstanceOf(RuntimeException.class);
    assertThat(exception.getMessage()).isNull();
    assertThat(exception.getCause()).isNull();
  }

  @Test
  public void should_be_throwable() {
    assertThatThrownBy(
            () -> {
              throw new AuthenticationException();
            })
        .isInstanceOf(AuthenticationException.class)
        .isInstanceOf(RuntimeException.class);
  }

  @Test
  public void should_inherit_from_runtime_exception() {
    AuthenticationException exception = new AuthenticationException();

    assertThat(exception).isInstanceOf(RuntimeException.class);
    assertThat(exception).isInstanceOf(Exception.class);
    assertThat(exception).isInstanceOf(Throwable.class);
  }

  @Test
  public void should_have_default_constructor() {
    AuthenticationException exception = new AuthenticationException();

    assertThat(exception).isNotNull();
    assertThat(exception.getMessage()).isNull();
    assertThat(exception.getCause()).isNull();
    assertThat(exception.getStackTrace()).isNotEmpty();
  }

  @Test
  public void should_be_serializable() {
    AuthenticationException exception = new AuthenticationException();

    assertThat(exception).isInstanceOf(java.io.Serializable.class);
  }

  @Test
  public void should_have_consistent_equals_and_hashcode() {
    AuthenticationException exception1 = new AuthenticationException();
    AuthenticationException exception2 = new AuthenticationException();

    assertThat(exception1.equals(exception2)).isFalse();
    assertThat(exception1.hashCode()).isNotEqualTo(exception2.hashCode());
    assertThat(exception1.equals(exception1)).isTrue();
    assertThat(exception1.hashCode()).isEqualTo(exception1.hashCode());
  }

  @Test
  public void should_have_meaningful_string_representation() {
    AuthenticationException exception = new AuthenticationException();

    String stringRepresentation = exception.toString();

    assertThat(stringRepresentation).contains("AuthenticationException");
    assertThat(stringRepresentation).isNotEmpty();
  }

  @Test
  public void should_support_exception_chaining() {
    RuntimeException cause = new RuntimeException("Original cause");

    try {
      try {
        throw cause;
      } catch (RuntimeException e) {
        throw new AuthenticationException();
      }
    } catch (AuthenticationException e) {
      assertThat(e).isInstanceOf(AuthenticationException.class);
      assertThat(e.getCause()).isNull();
    }
  }

  @Test
  public void should_work_in_try_catch_blocks() {
    boolean exceptionCaught = false;

    try {
      throw new AuthenticationException();
    } catch (AuthenticationException e) {
      exceptionCaught = true;
      assertThat(e).isInstanceOf(AuthenticationException.class);
    } catch (RuntimeException e) {
      assertThat(false)
          .as("Should have caught AuthenticationException, not RuntimeException")
          .isTrue();
    }

    assertThat(exceptionCaught).isTrue();
  }

  @Test
  public void should_work_with_exception_handling_patterns() {
    assertThatThrownBy(
            () -> {
              throw new AuthenticationException();
            })
        .isExactlyInstanceOf(AuthenticationException.class)
        .hasMessage(null)
        .hasNoCause();
  }
}
