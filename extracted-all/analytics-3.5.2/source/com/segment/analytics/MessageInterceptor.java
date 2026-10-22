package com.segment.analytics;

import com.segment.analytics.messages.AliasMessage;
import com.segment.analytics.messages.GroupMessage;
import com.segment.analytics.messages.IdentifyMessage;
import com.segment.analytics.messages.Message;
import com.segment.analytics.messages.PageMessage;
import com.segment.analytics.messages.ScreenMessage;
import com.segment.analytics.messages.TrackMessage;
import com.segment.analytics.messages.Message.Type;

public interface MessageInterceptor {
   Message intercept(Message var1);

   public abstract static class Typed implements MessageInterceptor {
      @Override
      public final Message intercept(Message message) {
         Type type = message.type();
         switch (type) {
            case alias:
               return this.alias((AliasMessage)message);
            case group:
               return this.group((GroupMessage)message);
            case identify:
               return this.identify((IdentifyMessage)message);
            case screen:
               return this.screen((ScreenMessage)message);
            case page:
               return this.page((PageMessage)message);
            case track:
               return this.track((TrackMessage)message);
            default:
               throw new IllegalArgumentException("Unknown payload type: " + type);
         }
      }

      abstract AliasMessage alias(AliasMessage var1);

      abstract GroupMessage group(GroupMessage var1);

      abstract IdentifyMessage identify(IdentifyMessage var1);

      abstract ScreenMessage screen(ScreenMessage var1);

      abstract PageMessage page(PageMessage var1);

      abstract TrackMessage track(TrackMessage var1);
   }
}
