package com.segment.analytics;

import com.segment.analytics.messages.MessageBuilder;
import com.segment.analytics.messages.AliasMessage.Builder;
import com.segment.analytics.messages.Message.Type;

public interface MessageTransformer {
   boolean transform(MessageBuilder var1);

   public abstract static class Typed implements MessageTransformer {
      @Override
      public final boolean transform(MessageBuilder builder) {
         Type type = builder.type();
         switch (type) {
            case alias:
               return this.alias((Builder)builder);
            case group:
               return this.group((com.segment.analytics.messages.GroupMessage.Builder)builder);
            case identify:
               return this.identify((com.segment.analytics.messages.IdentifyMessage.Builder)builder);
            case screen:
               return this.screen((com.segment.analytics.messages.ScreenMessage.Builder)builder);
            case page:
               return this.page((com.segment.analytics.messages.PageMessage.Builder)builder);
            case track:
               return this.track((com.segment.analytics.messages.TrackMessage.Builder)builder);
            default:
               throw new IllegalArgumentException("Unknown payload type: " + type);
         }
      }

      abstract boolean alias(Builder var1);

      abstract boolean group(com.segment.analytics.messages.GroupMessage.Builder var1);

      abstract boolean identify(com.segment.analytics.messages.IdentifyMessage.Builder var1);

      abstract boolean screen(com.segment.analytics.messages.ScreenMessage.Builder var1);

      abstract boolean page(com.segment.analytics.messages.PageMessage.Builder var1);

      abstract boolean track(com.segment.analytics.messages.TrackMessage.Builder var1);
   }
}
