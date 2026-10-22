package com.segment.analytics.internal;

import com.segment.analytics.messages.Message;
import com.segment.analytics.messages.Message.Type;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.util.Date;
import java.util.Map;

class StopMessage implements Message {
   static final StopMessage STOP = new StopMessage();

   private StopMessage() {
   }

   @Nonnull
   public Type type() {
      throw new UnsupportedOperationException();
   }

   @Nonnull
   public String messageId() {
      throw new UnsupportedOperationException();
   }

   @Nullable
   public Date sentAt() {
      throw new UnsupportedOperationException();
   }

   @Nonnull
   public Date timestamp() {
      throw new UnsupportedOperationException();
   }

   @Nullable
   public Map<String, ?> context() {
      throw new UnsupportedOperationException();
   }

   @Nullable
   public String anonymousId() {
      throw new UnsupportedOperationException();
   }

   @Nullable
   public String userId() {
      throw new UnsupportedOperationException();
   }

   @Nullable
   public Map<String, Object> integrations() {
      throw new UnsupportedOperationException();
   }

   @Override
   public String toString() {
      return "StopMessage{}";
   }
}
