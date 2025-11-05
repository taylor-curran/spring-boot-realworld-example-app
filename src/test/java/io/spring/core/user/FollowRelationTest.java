package io.spring.core.user;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

public class FollowRelationTest {

  @Test
  public void should_create_follow_relation_with_constructor() {
    String userId = "user-123";
    String targetId = "target-456";
    
    FollowRelation relation = new FollowRelation(userId, targetId);
    
    assertThat(relation.getUserId(), is(userId));
    assertThat(relation.getTargetId(), is(targetId));
  }

  @Test
  public void should_create_follow_relation_with_no_args_constructor() {
    FollowRelation relation = new FollowRelation();
    
    assertThat(relation, notNullValue());
  }

  @Test
  public void should_set_and_get_user_id() {
    FollowRelation relation = new FollowRelation();
    String userId = "user-789";
    
    relation.setUserId(userId);
    
    assertThat(relation.getUserId(), is(userId));
  }

  @Test
  public void should_set_and_get_target_id() {
    FollowRelation relation = new FollowRelation();
    String targetId = "target-101";
    
    relation.setTargetId(targetId);
    
    assertThat(relation.getTargetId(), is(targetId));
  }

  @Test
  public void should_have_proper_equals_for_same_object() {
    FollowRelation relation = new FollowRelation("user-1", "target-1");
    
    assertThat(relation.equals(relation), is(true));
  }

  @Test
  public void should_have_proper_equals_for_equal_objects() {
    FollowRelation relation1 = new FollowRelation("user-1", "target-1");
    FollowRelation relation2 = new FollowRelation("user-1", "target-1");
    
    assertThat(relation1.equals(relation2), is(true));
    assertThat(relation2.equals(relation1), is(true));
  }

  @Test
  public void should_have_proper_equals_for_different_user_ids() {
    FollowRelation relation1 = new FollowRelation("user-1", "target-1");
    FollowRelation relation2 = new FollowRelation("user-2", "target-1");
    
    assertThat(relation1.equals(relation2), is(false));
  }

  @Test
  public void should_have_proper_equals_for_different_target_ids() {
    FollowRelation relation1 = new FollowRelation("user-1", "target-1");
    FollowRelation relation2 = new FollowRelation("user-1", "target-2");
    
    assertThat(relation1.equals(relation2), is(false));
  }

  @Test
  public void should_have_proper_equals_for_null_object() {
    FollowRelation relation = new FollowRelation("user-1", "target-1");
    
    assertThat(relation.equals(null), is(false));
  }

  @Test
  public void should_have_proper_equals_for_different_class() {
    FollowRelation relation = new FollowRelation("user-1", "target-1");
    String differentObject = "not a follow relation";
    
    assertThat(relation.equals(differentObject), is(false));
  }

  @Test
  public void should_have_proper_equals_for_null_user_id() {
    FollowRelation relation1 = new FollowRelation(null, "target-1");
    FollowRelation relation2 = new FollowRelation(null, "target-1");
    FollowRelation relation3 = new FollowRelation("user-1", "target-1");
    
    assertThat(relation1.equals(relation2), is(true));
    assertThat(relation1.equals(relation3), is(false));
    assertThat(relation3.equals(relation1), is(false));
  }

  @Test
  public void should_have_proper_equals_for_null_target_id() {
    FollowRelation relation1 = new FollowRelation("user-1", null);
    FollowRelation relation2 = new FollowRelation("user-1", null);
    FollowRelation relation3 = new FollowRelation("user-1", "target-1");
    
    assertThat(relation1.equals(relation2), is(true));
    assertThat(relation1.equals(relation3), is(false));
    assertThat(relation3.equals(relation1), is(false));
  }

  @Test
  public void should_have_consistent_hash_code_for_equal_objects() {
    FollowRelation relation1 = new FollowRelation("user-1", "target-1");
    FollowRelation relation2 = new FollowRelation("user-1", "target-1");
    
    assertThat(relation1.hashCode(), equalTo(relation2.hashCode()));
  }

  @Test
  public void should_have_different_hash_code_for_different_objects() {
    FollowRelation relation1 = new FollowRelation("user-1", "target-1");
    FollowRelation relation2 = new FollowRelation("user-2", "target-1");
    FollowRelation relation3 = new FollowRelation("user-1", "target-2");
    
    assertThat(relation1.hashCode(), not(equalTo(relation2.hashCode())));
    assertThat(relation1.hashCode(), not(equalTo(relation3.hashCode())));
  }

  @Test
  public void should_handle_hash_code_with_null_values() {
    FollowRelation relation1 = new FollowRelation(null, "target-1");
    FollowRelation relation2 = new FollowRelation("user-1", null);
    FollowRelation relation3 = new FollowRelation(null, null);
    
    assertThat(relation1.hashCode(), notNullValue());
    assertThat(relation2.hashCode(), notNullValue());
    assertThat(relation3.hashCode(), notNullValue());
  }

  @Test
  public void should_have_proper_to_string() {
    FollowRelation relation = new FollowRelation("user-123", "target-456");
    
    String toString = relation.toString();
    
    assertThat(toString, notNullValue());
    assertThat(toString.contains("user-123"), is(true));
    assertThat(toString.contains("target-456"), is(true));
    assertThat(toString.contains("FollowRelation"), is(true));
  }

  @Test
  public void should_handle_to_string_with_null_values() {
    FollowRelation relation1 = new FollowRelation(null, "target-1");
    FollowRelation relation2 = new FollowRelation("user-1", null);
    FollowRelation relation3 = new FollowRelation(null, null);
    
    assertThat(relation1.toString(), notNullValue());
    assertThat(relation2.toString(), notNullValue());
    assertThat(relation3.toString(), notNullValue());
  }
}
