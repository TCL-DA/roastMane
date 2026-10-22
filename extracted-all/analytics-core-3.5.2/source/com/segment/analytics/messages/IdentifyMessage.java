package com.segment.analytics.messages;

import com.google.auto.value.AutoValue;
import com.segment.analytics.gson.AutoGson;
import jakarta.annotation.Nullable;
import java.util.Date;
import java.util.Map;

@AutoGson
@AutoValue
public abstract class IdentifyMessage implements Message {
   public static IdentifyMessage.Builder builder() {
      return new IdentifyMessage.Builder();
   }

   @Nullable
   public abstract Map<String, ?> traits();

   public IdentifyMessage.Builder toBuilder() {
      return new IdentifyMessage.Builder(this);
   }

   public static class Builder extends MessageBuilder<IdentifyMessage, IdentifyMessage.Builder> {
      private Map<String, ?> traits;

      private Builder(IdentifyMessage identify) {
         super(identify);
         this.traits = identify.traits();
      }

      private Builder() {
         super(Message.Type.identify);
      }

      public IdentifyMessage.Builder traits(Map<String, ?> traits) {
         if (traits == null) {
            throw new NullPointerException("Null traits");
         } else {
            this.traits = ImmutableMap.copyOf(traits);
            return this;
         }
      }

      protected IdentifyMessage realBuild(
         Message.Type type,
         String messageId,
         Date sentAt,
         Date timestamp,
         Map<String, ?> context,
         String anonymousId,
         String userId,
         Map<String, Object> integrations
      ) {
         if (userId == null && this.traits == null) {
            throw new IllegalStateException("Either userId or traits must be provided.");
         } else {
            return new AutoValue_IdentifyMessage(type, messageId, sentAt, timestamp, context, anonymousId, userId, integrations, this.traits);
         }
      }

      IdentifyMessage.Builder self() {
         return this;
      }
   }
}
