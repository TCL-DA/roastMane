package com.segment.analytics.messages;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.util.Date;
import java.util.Map;

public interface Message {
   @Nonnull
   Message.Type type();

   @Nonnull
   String messageId();

   @Nullable
   Date sentAt();

   @Nonnull
   Date timestamp();

   @Nullable
   Map<String, ?> context();

   @Nullable
   String anonymousId();

   @Nullable
   String userId();

   @Nullable
   Map<String, Object> integrations();

   public static enum Type {
      identify,
      group,
      track,
      screen,
      page,
      alias;
   }
}
