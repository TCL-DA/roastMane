package com.segment.analytics.messages;

import com.google.auto.value.AutoValue;
import com.segment.analytics.gson.AutoGson;
import jakarta.annotation.Nullable;
import java.util.Date;
import java.util.Map;

@AutoGson
@AutoValue
public abstract class GroupMessage implements Message {
   public static GroupMessage.Builder builder(String groupId) {
      return new GroupMessage.Builder(groupId);
   }

   public abstract String groupId();

   @Nullable
   public abstract Map<String, ?> traits();

   public GroupMessage.Builder toBuilder() {
      return new GroupMessage.Builder(this);
   }

   public static class Builder extends MessageBuilder<GroupMessage, GroupMessage.Builder> {
      private String groupId;
      private Map<String, ?> traits;

      private Builder(GroupMessage group) {
         super(group);
         this.groupId = group.groupId();
         this.traits = group.traits();
      }

      private Builder(String groupId) {
         super(Message.Type.group);
         if (isNullOrEmpty(groupId)) {
            throw new IllegalArgumentException("groupId cannot be null or empty.");
         } else {
            this.groupId = groupId;
         }
      }

      public GroupMessage.Builder traits(Map<String, ?> traits) {
         if (traits == null) {
            throw new NullPointerException("Null traits");
         } else {
            this.traits = ImmutableMap.copyOf(traits);
            return this;
         }
      }

      protected GroupMessage realBuild(
         Message.Type type,
         String messageId,
         Date sentAt,
         Date timestamp,
         Map<String, ?> context,
         String anonymousId,
         String userId,
         Map<String, Object> integrations
      ) {
         return new AutoValue_GroupMessage(type, messageId, sentAt, timestamp, context, anonymousId, userId, integrations, this.groupId, this.traits);
      }

      GroupMessage.Builder self() {
         return this;
      }
   }
}
