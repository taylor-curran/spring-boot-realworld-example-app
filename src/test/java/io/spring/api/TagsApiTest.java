package io.spring.api;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.when;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.spring.JacksonCustomizations;
import io.spring.api.security.WebSecurityConfig;
import io.spring.application.TagsQueryService;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest({TagsApi.class})
@Import({WebSecurityConfig.class, JacksonCustomizations.class})
public class TagsApiTest extends TestWithCurrentUser {
  @Autowired private MockMvc mvc;

  @MockBean private TagsQueryService tagsQueryService;

  @Override
  @BeforeEach
  public void setUp() throws Exception {
    super.setUp();
    RestAssuredMockMvc.mockMvc(mvc);
  }

  @Test
  void should_get_all_tags_successfully() throws Exception {
    List<String> mockTags = Arrays.asList("java", "spring", "testing", "api");
    when(tagsQueryService.allTags()).thenReturn(mockTags);

    RestAssuredMockMvc.when()
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
  void should_return_empty_tags_when_no_tags_exist() throws Exception {
    List<String> emptyTags = Collections.emptyList();
    when(tagsQueryService.allTags()).thenReturn(emptyTags);

    RestAssuredMockMvc.when()
        .get("/tags")
        .then()
        .statusCode(200)
        .body("tags.size()", equalTo(0));
  }

  @Test
  void should_handle_single_tag() throws Exception {
    List<String> singleTag = Arrays.asList("javascript");
    when(tagsQueryService.allTags()).thenReturn(singleTag);

    RestAssuredMockMvc.when()
        .get("/tags")
        .then()
        .statusCode(200)
        .body("tags.size()", equalTo(1))
        .body("tags[0]", equalTo("javascript"));
  }
}
