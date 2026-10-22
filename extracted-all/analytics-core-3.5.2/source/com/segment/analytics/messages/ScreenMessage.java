package com.segment.analytics.messages;

import com.google.auto.value.AutoValue;
import com.segment.analytics.gson.AutoGson;
import jakarta.annotation.Nullable;
import java.util.Date;
import java.util.Map;

@AutoGson
@AutoValue
public abstract class ScreenMessage implements Message {
   public static ScreenMessage.Builder builder(String name) {
      return new ScreenMessage.Builder(name);
   }

   public abstract String name();

   @Nullable
   public abstract Map<String, ?> properties();

   public ScreenMessage.Builder toBuilder() {
      return new ScreenMessage.Builder(this);
   }

   public static class Builder extends MessageBuilder<ScreenMessage, ScreenMessage.Builder> {
      private String name;
      private Map<String, ?> properties;

      private Builder(ScreenMessage screen) {
         super(screen);
         this.name = screen.name();
         this.properties = screen.properties();
      }

      private Builder(String name) {
         super(Message.Type.screen);
         if (isNullOrEmpty(name)) {
            throw new IllegalArgumentException("screen name cannot be null or empty.");
         } else {
            this.name = name;
         }
      }

      public ScreenMessage.Builder properties(Map<String, ?> properties) {
         if (properties == null) {
            throw new NullPointerException("Null properties");
         } else {
            this.properties = ImmutableMap.copyOf(properties);
            return this;
         }
      }

      ScreenMessage.Builder self() {
         return this;
      }

      protected ScreenMessage realBuild(
         Message.Type type,
         String messageId,
         Date sentAt,
         Date timestamp,
         Map<String, ?> context,
         String anonymousId,
         String userId,
         Map<String, Object> integrations
      ) {
         return new AutoValue_ScreenMessage(type, messageId, sentAt, timestamp, context, anonymousId, userId, integrations, this.name, this.properties);
      }
   }
}
