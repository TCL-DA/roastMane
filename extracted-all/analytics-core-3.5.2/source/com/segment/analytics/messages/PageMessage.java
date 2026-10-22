package com.segment.analytics.messages;

import com.google.auto.value.AutoValue;
import com.segment.analytics.gson.AutoGson;
import jakarta.annotation.Nullable;
import java.util.Date;
import java.util.Map;

@AutoGson
@AutoValue
public abstract class PageMessage implements Message {
   public static PageMessage.Builder builder(String name) {
      return new PageMessage.Builder(name);
   }

   public abstract String name();

   @Nullable
   public abstract Map<String, ?> properties();

   @Nullable
   public abstract String category();

   public PageMessage.Builder toBuilder() {
      return new PageMessage.Builder(this);
   }

   public static class Builder extends MessageBuilder<PageMessage, PageMessage.Builder> {
      private String name;
      private Map<String, ?> properties;
      private String category;

      private Builder(PageMessage page) {
         super(page);
         this.name = page.name();
         this.properties = page.properties();
         this.category = page.category();
      }

      private Builder(String name) {
         super(Message.Type.page);
         if (isNullOrEmpty(name)) {
            throw new IllegalArgumentException("page name cannot be null or empty.");
         } else {
            this.name = name;
         }
      }

      public PageMessage.Builder properties(Map<String, ?> properties) {
         if (properties == null) {
            throw new NullPointerException("Null properties");
         } else {
            this.properties = ImmutableMap.copyOf(properties);
            return this;
         }
      }

      public PageMessage.Builder category(String category) {
         if (category == null) {
            throw new NullPointerException("Null category");
         } else {
            this.category = category;
            return this;
         }
      }

      PageMessage.Builder self() {
         return this;
      }

      protected PageMessage realBuild(
         Message.Type type,
         String messageId,
         Date sentAt,
         Date timestamp,
         Map<String, ?> context,
         String anonymousId,
         String userId,
         Map<String, Object> integrations
      ) {
         return new AutoValue_PageMessage(
            type, messageId, sentAt, timestamp, context, anonymousId, userId, integrations, this.name, this.properties, this.category
         );
      }
   }
}
