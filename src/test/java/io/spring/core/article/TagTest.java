package io.spring.core.article;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class TagTest {

    @Test
    void shouldCreateTagWithValidName() {
        Tag tag = new Tag("java");

        assertEquals("java", tag.getName());
        assertNotNull(tag.getId());
        assertTrue(tag.getId().length() > 0);
    }

    @Test
    void shouldCreateTagWithEmptyName() {
        Tag tag = new Tag("");

        assertEquals("", tag.getName());
        assertNotNull(tag.getId());
    }

    @Test
    void shouldCreateTagWithNullName() {
        Tag tag = new Tag(null);

        assertNull(tag.getName());
        assertNotNull(tag.getId());
    }

    @Test
    void shouldCreateTagWithSpecialCharacters() {
        String tagName = "c++/c#-programming!@#$%";
        Tag tag = new Tag(tagName);

        assertEquals(tagName, tag.getName());
        assertNotNull(tag.getId());
    }

    @Test
    void shouldCreateTagWithUnicodeCharacters() {
        String tagName = "编程-プログラミング-🚀";
        Tag tag = new Tag(tagName);

        assertEquals(tagName, tag.getName());
        assertNotNull(tag.getId());
    }

    @Test
    void shouldCreateTagWithLongName() {
        String longName = "very-long-tag-name-".repeat(50);
        Tag tag = new Tag(longName);

        assertEquals(longName, tag.getName());
        assertNotNull(tag.getId());
    }

    @Test
    void shouldCreateTagWithWhitespace() {
        String tagName = "  tag with spaces  ";
        Tag tag = new Tag(tagName);

        assertEquals(tagName, tag.getName());
        assertNotNull(tag.getId());
    }

    @Test
    void shouldCreateTagWithNewlines() {
        String tagName = "tag\nwith\nnewlines";
        Tag tag = new Tag(tagName);

        assertEquals(tagName, tag.getName());
        assertNotNull(tag.getId());
    }

    @Test
    void shouldGenerateUniqueIds() {
        Tag tag1 = new Tag("java");
        Tag tag2 = new Tag("java");

        assertNotEquals(tag1.getId(), tag2.getId());
        assertEquals(tag1.getName(), tag2.getName());
    }

    @Test
    void shouldCreateWithNoArgsConstructor() {
        Tag tag = new Tag();

        assertNull(tag.getId());
        assertNull(tag.getName());
    }

    @Test
    void shouldSetNameAfterCreation() {
        Tag tag = new Tag();
        tag.setName("javascript");

        assertEquals("javascript", tag.getName());
        assertNull(tag.getId());
    }

    @Test
    void shouldSetIdAfterCreation() {
        Tag tag = new Tag();
        tag.setId("custom-id-123");

        assertEquals("custom-id-123", tag.getId());
        assertNull(tag.getName());
    }

    @Test
    void shouldOverrideExistingValues() {
        Tag tag = new Tag("oldName");
        String originalId = tag.getId();
        
        tag.setName("newName");
        tag.setId("newId");

        assertEquals("newName", tag.getName());
        assertEquals("newId", tag.getId());
        assertNotEquals(originalId, tag.getId());
    }

    @Test
    void shouldHandleEqualsBasedOnName() {
        Tag tag1 = new Tag("java");
        Tag tag2 = new Tag("java");
        Tag tag3 = new Tag("python");

        assertEquals(tag1, tag2);
        assertEquals(tag1.hashCode(), tag2.hashCode());
        assertNotEquals(tag1, tag3);
        assertNotEquals(tag1.hashCode(), tag3.hashCode());
    }

    @Test
    void shouldHandleEqualsWithNullNames() {
        Tag tag1 = new Tag(null);
        Tag tag2 = new Tag(null);
        Tag tag3 = new Tag("java");

        assertEquals(tag1, tag2);
        assertEquals(tag1.hashCode(), tag2.hashCode());
        assertNotEquals(tag1, tag3);
    }

    @Test
    void shouldHandleEqualsWithEmptyNames() {
        Tag tag1 = new Tag("");
        Tag tag2 = new Tag("");
        Tag tag3 = new Tag("java");

        assertEquals(tag1, tag2);
        assertEquals(tag1.hashCode(), tag2.hashCode());
        assertNotEquals(tag1, tag3);
    }

    @Test
    void shouldIgnoreIdInEquals() {
        Tag tag1 = new Tag("java");
        Tag tag2 = new Tag("java");
        
        tag1.setId("id1");
        tag2.setId("id2");

        assertEquals(tag1, tag2);
        assertEquals(tag1.hashCode(), tag2.hashCode());
    }

    @Test
    void shouldNotEqualNull() {
        Tag tag = new Tag("java");

        assertNotEquals(tag, null);
    }

    @Test
    void shouldNotEqualDifferentClass() {
        Tag tag = new Tag("java");
        String notATag = "not a tag";

        assertNotEquals(tag, notATag);
    }

    @Test
    void shouldEqualSelf() {
        Tag tag = new Tag("java");

        assertEquals(tag, tag);
        assertEquals(tag.hashCode(), tag.hashCode());
    }

    @Test
    void shouldHandleToString() {
        Tag tag = new Tag("java");

        String toString = tag.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("Tag"));
        assertTrue(toString.contains("java"));
    }

    @Test
    void shouldHandleToStringWithNullName() {
        Tag tag = new Tag(null);

        String toString = tag.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("Tag"));
    }

    @Test
    void shouldHandleToStringWithEmptyName() {
        Tag tag = new Tag("");

        String toString = tag.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("Tag"));
    }

    @Test
    void shouldHandleCaseSensitiveNames() {
        Tag tag1 = new Tag("Java");
        Tag tag2 = new Tag("java");
        Tag tag3 = new Tag("JAVA");

        assertNotEquals(tag1, tag2);
        assertNotEquals(tag2, tag3);
        assertNotEquals(tag1, tag3);
    }

    @Test
    void shouldHandleWhitespaceInEquality() {
        Tag tag1 = new Tag("java");
        Tag tag2 = new Tag(" java ");
        Tag tag3 = new Tag("java ");

        assertNotEquals(tag1, tag2);
        assertNotEquals(tag1, tag3);
        assertNotEquals(tag2, tag3);
    }
}
