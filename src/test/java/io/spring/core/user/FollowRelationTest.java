package io.spring.core.user;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class FollowRelationTest {

  @Test
  public void should_create_follow_relation_with_constructor() {
    String userId = "user-123";
    String targetId = "target-456";
    
    FollowRelation followRelation = new FollowRelation(userId, targetId);
    
    assertThat(followRelation.getUserId()).isEqualTo(userId);
    assertThat(followRelation.getTargetId()).isEqualTo(targetId);
  }

  @Test
  public void should_create_follow_relation_with_no_args_constructor() {
    FollowRelation followRelation = new FollowRelation();
    
    assertThat(followRelation.getUserId()).isNull();
    assertThat(followRelation.getTargetId()).isNull();
  }

  @Test
  public void should_set_and_get_user_id() {
    FollowRelation followRelation = new FollowRelation();
    String userId = "user-789";
    
    followRelation.setUserId(userId);
    
    assertThat(followRelation.getUserId()).isEqualTo(userId);
  }

  @Test
  public void should_set_and_get_target_id() {
    FollowRelation followRelation = new FollowRelation();
    String targetId = "target-101";
    
    followRelation.setTargetId(targetId);
    
    assertThat(followRelation.getTargetId()).isEqualTo(targetId);
  }

  @Test
  public void should_handle_null_user_id() {
    FollowRelation followRelation = new FollowRelation(null, "target-123");
    
    assertThat(followRelation.getUserId()).isNull();
    assertThat(followRelation.getTargetId()).isEqualTo("target-123");
  }

  @Test
  public void should_handle_null_target_id() {
    FollowRelation followRelation = new FollowRelation("user-123", null);
    
    assertThat(followRelation.getUserId()).isEqualTo("user-123");
    assertThat(followRelation.getTargetId()).isNull();
  }

  @Test
  public void should_handle_both_null_ids() {
    FollowRelation followRelation = new FollowRelation(null, null);
    
    assertThat(followRelation.getUserId()).isNull();
    assertThat(followRelation.getTargetId()).isNull();
  }

  @Test
  public void should_handle_equals_and_hashcode() {
    FollowRelation relation1 = new FollowRelation("user-123", "target-456");
    FollowRelation relation2 = new FollowRelation("user-123", "target-456");
    FollowRelation relation3 = new FollowRelation("user-789", "target-456");
    
    assertThat(relation1).isEqualTo(relation2);
    assertThat(relation1).isNotEqualTo(relation3);
    assertThat(relation1.hashCode()).isEqualTo(relation2.hashCode());
    assertThat(relation1.hashCode()).isNotEqualTo(relation3.hashCode());
  }

  @Test
  public void should_handle_equals_with_different_types() {
    FollowRelation relation = new FollowRelation("user-123", "target-456");
    
    assertThat(relation.equals(null)).isFalse();
    assertThat(relation.equals("not a relation")).isFalse();
    assertThat(relation.equals(new Object())).isFalse();
    assertThat(relation.equals(relation)).isTrue();
  }

  @Test
  public void should_handle_equals_with_null_fields() {
    FollowRelation relation1 = new FollowRelation(null, null);
    FollowRelation relation2 = new FollowRelation(null, null);
    FollowRelation relation3 = new FollowRelation("user-123", null);
    FollowRelation relation4 = new FollowRelation(null, "target-456");
    
    assertThat(relation1).isEqualTo(relation2);
    assertThat(relation1).isNotEqualTo(relation3);
    assertThat(relation1).isNotEqualTo(relation4);
    assertThat(relation1.hashCode()).isEqualTo(relation2.hashCode());
  }

  @Test
  public void should_handle_equals_with_partial_null_fields() {
    FollowRelation relation1 = new FollowRelation("user-123", null);
    FollowRelation relation2 = new FollowRelation("user-123", null);
    FollowRelation relation3 = new FollowRelation("user-123", "target-456");
    
    assertThat(relation1).isEqualTo(relation2);
    assertThat(relation1).isNotEqualTo(relation3);
    assertThat(relation1.hashCode()).isEqualTo(relation2.hashCode());
  }

  @Test
  public void should_handle_toString() {
    FollowRelation relation = new FollowRelation("user-123", "target-456");
    
    String toString = relation.toString();
    
    assertThat(toString).contains("FollowRelation");
    assertThat(toString).contains("user-123");
    assertThat(toString).contains("target-456");
  }

  @Test
  public void should_handle_toString_with_null_values() {
    FollowRelation relation = new FollowRelation(null, null);
    
    String toString = relation.toString();
    
    assertThat(toString).contains("FollowRelation");
    assertThat(toString).contains("null");
  }

  @Test
  public void should_handle_can_equal_method() {
    FollowRelation relation = new FollowRelation("user-123", "target-456");
    
    assertThat(relation.canEqual(relation)).isTrue();
    assertThat(relation.canEqual(new FollowRelation())).isTrue();
    assertThat(relation.canEqual("not a relation")).isFalse();
    assertThat(relation.canEqual(null)).isFalse();
  }

  @Test
  public void should_handle_hash_code_consistency() {
    FollowRelation relation1 = new FollowRelation("user-123", "target-456");
    FollowRelation relation2 = new FollowRelation("user-123", "target-456");
    
    assertThat(relation1.hashCode()).isEqualTo(relation2.hashCode());
    
    int hash1 = relation1.hashCode();
    int hash2 = relation1.hashCode();
    assertThat(hash1).isEqualTo(hash2);
  }

  @Test
  public void should_handle_different_user_ids() {
    FollowRelation relation1 = new FollowRelation("user-123", "target-456");
    FollowRelation relation2 = new FollowRelation("user-789", "target-456");
    
    assertThat(relation1).isNotEqualTo(relation2);
    assertThat(relation1.hashCode()).isNotEqualTo(relation2.hashCode());
  }

  @Test
  public void should_handle_different_target_ids() {
    FollowRelation relation1 = new FollowRelation("user-123", "target-456");
    FollowRelation relation2 = new FollowRelation("user-123", "target-789");
    
    assertThat(relation1).isNotEqualTo(relation2);
    assertThat(relation1.hashCode()).isNotEqualTo(relation2.hashCode());
  }

  @Test
  public void should_handle_empty_string_ids() {
    FollowRelation relation = new FollowRelation("", "");
    
    assertThat(relation.getUserId()).isEqualTo("");
    assertThat(relation.getTargetId()).isEqualTo("");
  }

  @Test
  public void should_handle_special_characters_in_ids() {
    String specialUserId = "user-123!@#$%^&*()";
    String specialTargetId = "target-456<>?:{}[]";
    
    FollowRelation relation = new FollowRelation(specialUserId, specialTargetId);
    
    assertThat(relation.getUserId()).isEqualTo(specialUserId);
    assertThat(relation.getTargetId()).isEqualTo(specialTargetId);
  }

  @Test
  public void should_handle_unicode_in_ids() {
    String unicodeUserId = "用户-123";
    String unicodeTargetId = "目标-456";
    
    FollowRelation relation = new FollowRelation(unicodeUserId, unicodeTargetId);
    
    assertThat(relation.getUserId()).isEqualTo(unicodeUserId);
    assertThat(relation.getTargetId()).isEqualTo(unicodeTargetId);
  }

  @Test
  public void should_handle_very_long_ids() {
    String longUserId = "user-" + "a".repeat(1000);
    String longTargetId = "target-" + "b".repeat(1000);
    
    FollowRelation relation = new FollowRelation(longUserId, longTargetId);
    
    assertThat(relation.getUserId()).isEqualTo(longUserId);
    assertThat(relation.getTargetId()).isEqualTo(longTargetId);
  }
}
