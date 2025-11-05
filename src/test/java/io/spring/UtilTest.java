package io.spring;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UtilTest {

  @Test
  public void should_instantiate_util_class() {
    Util util = new Util();
    assertNotNull(util);
  }

  @Test
  public void should_check_empty_string() {
    assertTrue(Util.isEmpty(null));
    assertTrue(Util.isEmpty(""));
    assertFalse(Util.isEmpty("test"));
  }
}
