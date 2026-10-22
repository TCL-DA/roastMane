package com.segment.analytics.messages;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public abstract class MessageBuilder<T extends Message, V extends MessageBuilder> {
   private final Message.Type type;
   private String messageId;
   private Date sentAt;
   private Date timestamp;
   private Map<String, ?> context;
   private String anonymousId;
   private String userId;
   private Map<String, Object> integrations;

   MessageBuilder(Message.Type type) {
      this.type = type;
   }

   MessageBuilder(Message message) {
      this.type = message.type();
      this.context = message.context();
      this.anonymousId = message.anonymousId();
      this.userId = message.userId();
   }

   static boolean isNullOrEmpty(String string) {
      return string == null || string.trim().length() == 0;
   }

   /** @deprecated */
   public V messageId(UUID messageId) {
      if (messageId == null) {
         throw new NullPointerException("Null messageId");
      } else {
         this.messageId = messageId.toString();
         return this.self();
      }
   }

   public V messageId(String messageId) {
      if (isNullOrEmpty(messageId)) {
         throw new IllegalArgumentException("messageId cannot be null or empty.");
      } else {
         this.messageId = messageId;
         return this.self();
      }
   }

   public V sentAt(Date sentAt) {
      this.sentAt = sentAt;
      return this.self();
   }

   public V timestamp(Date timestamp) {
      if (timestamp == null) {
         throw new NullPointerException("Null timestamp");
      } else {
         this.timestamp = timestamp;
         return this.self();
      }
   }

   public V context(Map<String, ?> context) {
      if (context == null) {
         throw new NullPointerException("Null context");
      } else {
         this.context = ImmutableMap.copyOf(context);
         return this.self();
      }
   }

   /** @deprecated */
   public V anonymousId(UUID anonymousId) {
      if (anonymousId == null) {
         throw new NullPointerException("Null anonymousId");
      } else {
         this.anonymousId = anonymousId.toString();
         return this.self();
      }
   }

   public V anonymousId(String anonymousId) {
      this.anonymousId = anonymousId;
      return this.self();
   }

   public V userId(String userId) {
      this.userId = userId;
      return this.self();
   }

   public V enableIntegration(String key, boolean enable) {
      if (isNullOrEmpty(key)) {
         throw new IllegalArgumentException("Key cannot be null or empty.");
      } else {
         if (this.integrations == null) {
            this.integrations = new LinkedHashMap<>();
         }

         this.integrations.put(key, enable);
         return this.self();
      }
   }

   public V integrationOptions(String key, Map<String, ?> options) {
      if (isNullOrEmpty(key)) {
         throw new IllegalArgumentException("Key cannot be null or empty.");
      } else {
         if (this.integrations == null) {
            this.integrations = new LinkedHashMap<>();
         }

         this.integrations.put(key, ImmutableMap.copyOf(options));
         return this.self();
      }
   }

   protected abstract T realBuild(Message.Type var1, String var2, Date var3, Date var4, Map<String, ?> var5, String var6, String var7, Map<String, Object> var8);

   abstract V self();

   public T build() {
      if (isNullOrEmpty(this.anonymousId) && isNullOrEmpty(this.userId)) {
         throw new IllegalArgumentException("Either anonymousId or userId must be provided.");
      } else {
         Date timestamp = this.timestamp;
         if (timestamp == null) {
            timestamp = new Date();
         }

         Date sentAt = this.sentAt;
         String messageId = this.messageId;
         if (messageId == null) {
            messageId = UUID.randomUUID().toString();
         }

         Map<String, Object> integrations;
         if (this.integrations == null) {
            integrations = Collections.emptyMap();
         } else {
            integrations = ImmutableMap.copyOf(this.integrations);
         }

         return this.realBuild(this.type, messageId, sentAt, timestamp, this.context, this.anonymousId, this.userId, integrations);
      }
   }

   public Message.Type type() {
      return this.type;
   }
}
