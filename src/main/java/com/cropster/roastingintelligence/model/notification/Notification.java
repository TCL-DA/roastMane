package com.cropster.roastingintelligence.model.notification;

import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javafx.beans.binding.BooleanBinding;
import org.apache.commons.collections4.IterableUtils;

public class Notification {
   private final BooleanBinding binding;
   private final Date date;
   private final String message;
   private final Runnable prerequisite;
   private final boolean prerequisiteOnSubmission;
   private final String title;
   private final NotificationType type;
   private final Map<String, Object> properties;

   public static Notification.NotificationBuilder builder() {
      return builder(NotificationType.NONE);
   }

   public static Notification.NotificationBuilder builder(NotificationType type) {
      Objects.requireNonNull(type, "Null 'type' parameter");
      return new Notification.NotificationBuilder(type);
   }

   public static Notification of(String message) {
      return builder().withMessage(message).build();
   }

   public static Notification of(NotificationType type, String message) {
      return builder(type).withMessage(message).build();
   }

   public static Notification of(String title, String message) {
      return builder().withTitle(title).withMessage(message).build();
   }

   public static Notification of(NotificationType type, String title, String message) {
      return builder(type).withTitle(title).withMessage(message).build();
   }

   Notification(Notification.NotificationBuilder builder) {
      this.type = builder.type;
      this.binding = builder.binding;
      this.date = builder.date;
      this.message = builder.message;
      this.prerequisite = builder.prerequisite;
      this.prerequisiteOnSubmission = builder.prerequisiteOnSubmission;
      this.title = builder.title;
      this.properties = Map.copyOf(builder.properties);
   }

   public BooleanBinding getBinding() {
      return this.binding;
   }

   public Date getDate() {
      return this.date;
   }

   public String getMessage() {
      return this.message;
   }

   public Runnable getPrerequisite() {
      return this.prerequisite;
   }

   public boolean isPrerequisiteOnSubmission() {
      return this.prerequisiteOnSubmission;
   }

   public Map<String, Object> getProperties() {
      return this.properties;
   }

   public <T> T getProperty(Class<T> type, String key) {
      return type.cast(this.properties.get(key));
   }

   public <T> T getProperty(Class<T> type, String key, T defaultValue) {
      return type.cast(this.properties.getOrDefault(key, defaultValue));
   }

   public String getTitle() {
      return this.title;
   }

   public NotificationType getType() {
      return this.type;
   }

   @Override
   public String toString() {
      return MessageFormat.format(
         "Notification of type: {0}\n\t     Title: {1}\n\t   Message: {2}\n\t      Date: {3,date,short} {3,time,short}\n\tProperties: {4}",
         this.type,
         this.title,
         this.message,
         this.date,
         IterableUtils.toString(this.properties.keySet())
      );
   }

   public static class NotificationBuilder {
      private final Map<String, Object> properties = new HashMap<>();
      private final NotificationType type;
      private BooleanBinding binding;
      private Date date = new Date();
      private String message;
      private Runnable prerequisite;
      private boolean prerequisiteOnSubmission = false;
      private String title;

      NotificationBuilder(NotificationType type) {
         this.type = type;
      }

      public Notification build() {
         return new Notification(this);
      }

      public Notification.NotificationBuilder withBinding(BooleanBinding binding) {
         this.binding = binding;
         return this;
      }

      public Notification.NotificationBuilder withDate(Date date) {
         this.date = date;
         return this;
      }

      public Notification.NotificationBuilder withMessage(String message) {
         this.message = message;
         return this;
      }

      public Notification.NotificationBuilder withPrerequisite(Runnable prerequisite) {
         this.prerequisite = prerequisite;
         return this;
      }

      public Notification.NotificationBuilder withPrerequisiteOnSubmission(boolean onSubmission) {
         this.prerequisiteOnSubmission = onSubmission;
         return this;
      }

      public Notification.NotificationBuilder withProperty(String key, Object value) {
         this.properties.put(key, value);
         return this;
      }

      public Notification.NotificationBuilder withTitle(String title) {
         this.title = title;
         return this;
      }
   }
}
