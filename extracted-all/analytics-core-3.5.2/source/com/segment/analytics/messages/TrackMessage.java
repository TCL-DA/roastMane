package com.segment.analytics.messages;

import com.google.auto.value.AutoValue;
import com.segment.analytics.gson.AutoGson;
import jakarta.annotation.Nullable;
import java.util.Date;
import java.util.Map;

@AutoGson
@AutoValue
public abstract class TrackMessage implements Message {
   public static TrackMessage.Builder builder(String event) {
      return new TrackMessage.Builder(event);
   }

   public abstract String event();

   @Nullable
   public abstract Map<String, ?> properties();

   public TrackMessage.Builder toBuilder() {
      return new TrackMessage.Builder(this);
   }

   public static class Builder extends MessageBuilder<TrackMessage, TrackMessage.Builder> {
      private String event;
      private Map<String, ?> properties;

      private Builder(TrackMessage track) {
         super(track);
         this.event = track.event();
         this.properties = track.properties();
      }

      private Builder(String event) {
         super(Message.Type.track);
         if (isNullOrEmpty(event)) {
            throw new IllegalArgumentException("event cannot be null or empty.");
         } else {
            this.event = event;
         }
      }

      public TrackMessage.Builder properties(Map<String, ?> properties) {
         if (properties == null) {
            throw new NullPointerException("Null properties");
         } else {
            this.properties = ImmutableMap.copyOf(properties);
            return this;
         }
      }

      TrackMessage.Builder self() {
         return this;
      }

      protected TrackMessage realBuild(
         Message.Type type,
         String messageId,
         Date sentAt,
         Date timestamp,
         Map<String, ?> context,
         String anonymousId,
         String userId,
         Map<String, Object> integrations
      ) {
         return new AutoValue_TrackMessage(type, messageId, sentAt, timestamp, context, anonymousId, userId, integrations, this.event, this.properties);
      }
   }
}
