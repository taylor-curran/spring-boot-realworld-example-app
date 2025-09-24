package io.spring;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class UtilTest {

    @Test
    void shouldCreateUtilInstance() {
        Util util = new Util();
        
        assertNotNull(util);
    }

    @Test
    void shouldReturnTrueForNullString() {
        boolean result = Util.isEmpty(null);
        
        assertTrue(result);
    }

    @Test
    void shouldReturnTrueForEmptyString() {
        boolean result = Util.isEmpty("");
        
        assertTrue(result);
    }

    @Test
    void shouldReturnFalseForNonEmptyString() {
        boolean result = Util.isEmpty("test");
        
        assertFalse(result);
    }

    @Test
    void shouldReturnFalseForWhitespaceString() {
        boolean result = Util.isEmpty("   ");
        
        assertFalse(result);
    }

    @Test
    void shouldReturnFalseForStringWithContent() {
        boolean result = Util.isEmpty("Hello World");
        
        assertFalse(result);
    }

    @Test
    void shouldReturnFalseForSpecialCharacters() {
        boolean result = Util.isEmpty("!@#$%^&*()");
        
        assertFalse(result);
    }

    @Test
    void shouldReturnFalseForUnicodeString() {
        boolean result = Util.isEmpty("测试 🌍");
        
        assertFalse(result);
    }

    @Test
    void shouldReturnFalseForSingleCharacter() {
        boolean result = Util.isEmpty("a");
        
        assertFalse(result);
    }

    @Test
    void shouldReturnFalseForNumericString() {
        boolean result = Util.isEmpty("123");
        
        assertFalse(result);
    }

    @Test
    void shouldReturnFalseForNewlineString() {
        boolean result = Util.isEmpty("\n");
        
        assertFalse(result);
    }

    @Test
    void shouldReturnFalseForTabString() {
        boolean result = Util.isEmpty("\t");
        
        assertFalse(result);
    }
}
