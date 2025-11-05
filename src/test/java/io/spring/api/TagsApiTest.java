package io.spring.api;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.when;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.spring.JacksonCustomizations;
import io.spring.api.security.WebSecurityConfig;
import io.spring.application.TagsQueryService;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(TagsApi.class)
@Import({WebSecurityConfig.class, JacksonCustomizations.class})
public class TagsApiTest extends TestWithCurrentUser {

  @MockBean private TagsQueryService tagsQueryService;

  @Autowired private MockMvc mvc;

  @Override
  @BeforeEach
  public void setUp() throws Exception {
    super.setUp();
    RestAssuredMockMvc.mockMvc(mvc);
  }

  @Test
  public void should_get_all_tags_success() throws Exception {
    List<String> tags = Arrays.asList("java", "spring", "testing", "api");
    
    when(tagsQueryService.allTags()).thenReturn(tags);

    given()
        .when()
        .get("/tags")
        .then()
        .statusCode(200)
        .body("tags.size()", equalTo(4))
        .body("tags[0]", equalTo("java"))
        .body("tags[1]", equalTo("spring"))
        .body("tags[2]", equalTo("testing"))
        .body("tags[3]", equalTo("api"));
  }

  @Test
  public void should_get_empty_tags_list() throws Exception {
    when(tagsQueryService.allTags()).thenReturn(Arrays.asList());

    given()
        .when()
        .get("/tags")
        .then()
        .statusCode(200)
        .body("tags.size()", equalTo(0));
  }

  @Test
  public void should_get_single_tag() throws Exception {
    List<String> tags = Arrays.asList("single");
    
    when(tagsQueryService.allTags()).thenReturn(tags);

    given()
        .when()
        .get("/tags")
        .then()
        .statusCode(200)
        .body("tags.size()", equalTo(1))
        .body("tags[0]", equalTo("single"));
  }

  @Test
  public void should_handle_tags_with_special_characters() throws Exception {
    List<String> tags = Arrays.asList("c++", "c#", "node.js", "spring-boot");
    
    when(tagsQueryService.allTags()).thenReturn(tags);

    given()
        .when()
        .get("/tags")
        .then()
        .statusCode(200)
        .body("tags.size()", equalTo(4))
        .body("tags[0]", equalTo("c++"))
        .body("tags[1]", equalTo("c#"))
        .body("tags[2]", equalTo("node.js"))
        .body("tags[3]", equalTo("spring-boot"));
  }

  @Test
  public void should_handle_large_number_of_tags() throws Exception {
    List<String> tags = Arrays.asList(
        "tag1", "tag2", "tag3", "tag4", "tag5",
        "tag6", "tag7", "tag8", "tag9", "tag10",
        "tag11", "tag12", "tag13", "tag14", "tag15"
    );
    
    when(tagsQueryService.allTags()).thenReturn(tags);

    given()
        .when()
        .get("/tags")
        .then()
        .statusCode(200)
        .body("tags.size()", equalTo(15));
  }

  @Test
  public void should_handle_tags_with_unicode_characters() throws Exception {
    List<String> tags = Arrays.asList("编程", "プログラミング", "программирование");
    
    when(tagsQueryService.allTags()).thenReturn(tags);

    given()
        .when()
        .get("/tags")
        .then()
        .statusCode(200)
        .body("tags.size()", equalTo(3))
        .body("tags[0]", equalTo("编程"))
        .body("tags[1]", equalTo("プログラミング"))
        .body("tags[2]", equalTo("программирование"));
  }

  @Test
  public void should_handle_tags_with_long_names() throws Exception {
    List<String> tags = Arrays.asList(
        "this-is-a-very-long-tag-name-that-should-be-handled-correctly",
        "another-extremely-long-tag-name-for-testing-purposes"
    );
    
    when(tagsQueryService.allTags()).thenReturn(tags);

    given()
        .when()
        .get("/tags")
        .then()
        .statusCode(200)
        .body("tags.size()", equalTo(2))
        .body("tags[0]", equalTo("this-is-a-very-long-tag-name-that-should-be-handled-correctly"))
        .body("tags[1]", equalTo("another-extremely-long-tag-name-for-testing-purposes"));
  }

  @Test
  public void should_handle_tags_with_numbers() throws Exception {
    List<String> tags = Arrays.asList("java8", "spring5", "junit5", "version2.0");
    
    when(tagsQueryService.allTags()).thenReturn(tags);

    given()
        .when()
        .get("/tags")
        .then()
        .statusCode(200)
        .body("tags.size()", equalTo(4))
        .body("tags[0]", equalTo("java8"))
        .body("tags[1]", equalTo("spring5"))
        .body("tags[2]", equalTo("junit5"))
        .body("tags[3]", equalTo("version2.0"));
  }

  @Test
  public void should_handle_mixed_case_tags() throws Exception {
    List<String> tags = Arrays.asList("JavaScript", "TypeScript", "HTML", "CSS");
    
    when(tagsQueryService.allTags()).thenReturn(tags);

    given()
        .when()
        .get("/tags")
        .then()
        .statusCode(200)
        .body("tags.size()", equalTo(4))
        .body("tags[0]", equalTo("JavaScript"))
        .body("tags[1]", equalTo("TypeScript"))
        .body("tags[2]", equalTo("HTML"))
        .body("tags[3]", equalTo("CSS"));
  }

  @Test
  public void should_handle_tags_with_spaces_and_underscores() throws Exception {
    List<String> tags = Arrays.asList("machine learning", "data_science", "artificial intelligence", "deep_learning");
    
    when(tagsQueryService.allTags()).thenReturn(tags);

    given()
        .when()
        .get("/tags")
        .then()
        .statusCode(200)
        .body("tags.size()", equalTo(4))
        .body("tags[0]", equalTo("machine learning"))
        .body("tags[1]", equalTo("data_science"))
        .body("tags[2]", equalTo("artificial intelligence"))
        .body("tags[3]", equalTo("deep_learning"));
  }
}
