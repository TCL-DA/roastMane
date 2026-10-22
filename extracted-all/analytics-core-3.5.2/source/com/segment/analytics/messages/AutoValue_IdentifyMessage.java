package com.segment.analytics.messages;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.util.Date;
import java.util.Map;

final class AutoValue_IdentifyMessage extends IdentifyMessage {
   private final Message.Type type;
   private final String messageId;
   private final Date sentAt;
   private final Date timestamp;
   private final Map<String, ?> context;
   private final String anonymousId;
   private final String userId;
   private final Map<String, Object> integrations;
   private final Map<String, ?> traits;

   AutoValue_IdentifyMessage(
      Message.Type type,
      String messageId,
      @Nullable Date sentAt,
      Date timestamp,
      @Nullable Map<String, ?> context,
      @Nullable String anonymousId,
      @Nullable String userId,
      @Nullable Map<String, Object> integrations,
      @Nullable Map<String, ?> traits
   ) {
      if (type == null) {
         throw new NullPointerException("Null type");
      } else {
         this.type = type;
         if (messageId == null) {
            throw new NullPointerException("Null messageId");
         } else {
            this.messageId = messageId;
            this.sentAt = sentAt;
            if (timestamp == null) {
               throw new NullPointerException("Null timestamp");
            } else {
               this.timestamp = timestamp;
               this.context = context;
               this.anonymousId = anonymousId;
               this.userId = userId;
               this.integrations = integrations;
               this.traits = traits;
            }
         }
      }
   }

   @Nonnull
   @Override
   public Message.Type type() {
      return this.type;
   }

   @Nonnull
   @Override
   public String messageId() {
      return this.messageId;
   }

   @Nullable
   @Override
   public Date sentAt() {
      return this.sentAt;
   }

   @Nonnull
   @Override
   public Date timestamp() {
      return this.timestamp;
   }

   @Nullable
   @Override
   public Map<String, ?> context() {
      return this.context;
   }

   @Nullable
   @Override
   public String anonymousId() {
      return this.anonymousId;
   }

   @Nullable
   @Override
   public String userId() {
      return this.userId;
   }

   @Nullable
   @Override
   public Map<String, Object> integrations() {
      return this.integrations;
   }

   @Nullable
   @Override
   public Map<String, ?> traits() {
      return this.traits;
   }

   @Override
   public String toString() {
      return "IdentifyMessage{type="
         + this.type
         + ", messageId="
         + this.messageId
         + ", sentAt="
         + this.sentAt
         + ", timestamp="
         + this.timestamp
         + ", context="
         + this.context
         + ", anonymousId="
         + this.anonymousId
         + ", userId="
         + this.userId
         + ", integrations="
         + this.integrations
         + ", traits="
         + this.traits
         + "}";
   }

   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof IdentifyMessage)) {
         return false;
      } else {
         IdentifyMessage that = (IdentifyMessage)o;
         return this.type.equals(that.type())
            && this.messageId.equals(that.messageId())
            && (this.sentAt == null ? that.sentAt() == null : this.sentAt.equals(that.sentAt()))
            && this.timestamp.equals(that.timestamp())
            && (this.context == null ? that.context() == null : this.context.equals(that.context()))
            && (this.anonymousId == null ? that.anonymousId() == null : this.anonymousId.equals(that.anonymousId()))
            && (this.userId == null ? that.userId() == null : this.userId.equals(that.userId()))
            && (this.integrations == null ? that.integrations() == null : this.integrations.equals(that.integrations()))
            && (this.traits == null ? that.traits() == null : this.traits.equals(that.traits()));
      }
   }

   @Override
   public int hashCode() {
      int h$ = 1;
      h$ *= 1000003;
      h$ ^= this.type.hashCode();
      h$ *= 1000003;
      h$ ^= this.messageId.hashCode();
      h$ *= 1000003;
      h$ ^= this.sentAt == null ? 0 : this.sentAt.hashCode();
      h$ *= 1000003;
      h$ ^= this.timestamp.hashCode();
      h$ *= 1000003;
      h$ ^= this.context == null ? 0 : this.context.hashCode();
      h$ *= 1000003;
      h$ ^= this.anonymousId == null ? 0 : this.anonymousId.hashCode();
      h$ *= 1000003;
      h$ ^= this.userId == null ? 0 : this.userId.hashCode();
      h$ *= 1000003;
      h$ ^= this.integrations == null ? 0 : this.integrations.hashCode();
      h$ *= 1000003;
      return h$ ^ (this.traits == null ? 0 : this.traits.hashCode());
   }
}
