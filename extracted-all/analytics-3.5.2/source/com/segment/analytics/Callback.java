package com.segment.analytics;

import com.segment.analytics.messages.Message;

public interface Callback {
   void success(Message var1);

   void failure(Message var1, Throwable var2);
}
