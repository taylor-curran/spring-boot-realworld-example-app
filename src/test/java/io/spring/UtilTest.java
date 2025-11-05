package io.spring;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UtilTest {
    @Test
    public void testIsEmpty_withNull() {
        assertTrue(Util.isEmpty(null));
    }

    @Test
    public void testIsEmpty_withEmptyString() {
        assertTrue(Util.isEmpty(""));
    }

    @Test
    public void testIsEmpty_withNonEmptyString() {
        assertFalse(Util.isEmpty("test"));
    }
}
