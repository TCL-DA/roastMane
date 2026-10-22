package com.segment.analytics.messages;

import com.google.auto.value.AutoValue;
import com.segment.analytics.gson.AutoGson;
import java.util.Date;
import java.util.Map;

@AutoGson
@AutoValue
public abstract class AliasMessage implements Message {
   public static AliasMessage.Builder builder(String previousId) {
      return new AliasMessage.Builder(previousId);
   }

   public abstract String previousId();

   public AliasMessage.Builder toBuilder() {
      return new AliasMessage.Builder(this);
   }

   public static class Builder extends MessageBuilder<AliasMessage, AliasMessage.Builder> {
      private String previousId;

      private Builder(AliasMessage alias) {
         super(alias);
         this.previousId = alias.previousId();
      }

      private Builder(String previousId) {
         super(Message.Type.alias);
         if (isNullOrEmpty(previousId)) {
            throw new IllegalArgumentException("previousId cannot be null or empty.");
         } else {
            this.previousId = previousId;
         }
      }

      protected AliasMessage realBuild(
         Message.Type type,
         String messageId,
         Date sentAt,
         Date timestamp,
         Map<String, ?> context,
         String anonymousId,
         String userId,
         Map<String, Object> integrations
      ) {
         return new AutoValue_AliasMessage(type, messageId, sentAt, timestamp, context, anonymousId, userId, integrations, this.previousId);
      }

      AliasMessage.Builder self() {
         return this;
      }
   }
}
