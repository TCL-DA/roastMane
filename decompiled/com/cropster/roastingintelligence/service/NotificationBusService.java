package com.cropster.roastingintelligence.service;

import com.cropster.roastingintelligence.model.notification.Notification;
import com.cropster.roastingintelligence.model.notification.Subscription;
import java.util.function.Consumer;

public interface NotificationBusService {
   static void validateNotEmpty(String[] topics) {
      if (topics.length == 0) {
         throw new IllegalArgumentException("Empty 'topics' array");
      } else {
         for (int i = 0; i < topics.length; i++) {
            if (topics[i].isEmpty()) {
               throw new IllegalArgumentException("Empty element in 'topics' array at position " + i);
            }
         }
      }
   }

   void publish(Notification var1, String... var2);

   Subscription subscribe(String var1, Consumer<Notification> var2);
}
