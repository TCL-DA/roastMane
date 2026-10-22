package com.segment.analytics.messages;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.util.Date;
import java.util.Map;

final class AutoValue_PageMessage extends PageMessage {
   private final Message.Type type;
   private final String messageId;
   private final Date sentAt;
   private final Date timestamp;
   private final Map<String, ?> context;
   private final String anonymousId;
   private final String userId;
   private final Map<String, Object> integrations;
   private final String name;
   private final Map<String, ?> properties;
   private final String category;

   AutoValue_PageMessage(
      Message.Type type,
      String messageId,
      @Nullable Date sentAt,
      Date timestamp,
      @Nullable Map<String, ?> context,
      @Nullable String anonymousId,
      @Nullable String userId,
      @Nullable Map<String, Object> integrations,
      String name,
      @Nullable Map<String, ?> properties,
      @Nullable String category
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
               if (name == null) {
                  throw new NullPointerException("Null name");
               } else {
                  this.name = name;
                  this.properties = properties;
                  this.category = category;
               }
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

   @Override
   public String name() {
      return this.name;
   }

   @Nullable
   @Override
   public Map<String, ?> properties() {
      return this.properties;
   }

   @Nullable
   @Override
   public String category() {
      return this.category;
   }

   @Override
   public String toString() {
      return "PageMessage{type="
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
         + ", name="
         + this.name
         + ", properties="
         + this.properties
         + ", category="
         + this.category
         + "}";
   }

   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof PageMessage)) {
         return false;
      } else {
         PageMessage that = (PageMessage)o;
         return this.type.equals(that.type())
            && this.messageId.equals(that.messageId())
            && (this.sentAt == null ? that.sentAt() == null : this.sentAt.equals(that.sentAt()))
            && this.timestamp.equals(that.timestamp())
            && (this.context == null ? that.context() == null : this.context.equals(that.context()))
            && (this.anonymousId == null ? that.anonymousId() == null : this.anonymousId.equals(that.anonymousId()))
            && (this.userId == null ? that.userId() == null : this.userId.equals(that.userId()))
            && (this.integrations == null ? that.integrations() == null : this.integrations.equals(that.integrations()))
            && this.name.equals(that.name())
            && (this.properties == null ? that.properties() == null : this.properties.equals(that.properties()))
            && (this.category == null ? that.category() == null : this.category.equals(that.category()));
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
      h$ ^= this.name.hashCode();
      h$ *= 1000003;
      h$ ^= this.properties == null ? 0 : this.properties.hashCode();
      h$ *= 1000003;
      return h$ ^ (this.category == null ? 0 : this.category.hashCode());
   }
}
