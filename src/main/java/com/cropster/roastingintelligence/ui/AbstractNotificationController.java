package com.cropster.roastingintelligence.ui;

import com.cropster.roastingintelligence.model.notification.Notification;
import com.cropster.roastingintelligence.model.notification.Subscription;
import com.cropster.roastingintelligence.service.NotificationBusService;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import org.apache.commons.lang3.Validate;

public abstract class AbstractNotificationController {
   private final Map<String, Subscription> subscriptions = Collections.synchronizedMap(new HashMap<>());

   public Subscription getSubscription(String topic) {
      return this.subscriptions.get(topic);
   }

   public void subscribe(NotificationBusService service, String... topics) {
      Objects.requireNonNull(service, "Null 'service' parameter");
      Validate.noNullElements(topics, "Null element in 'topics' array at position %d", new Object[0]);
      NotificationBusService.validateNotEmpty(topics);
      Arrays.stream(topics).forEach(topic -> this.subscriptions.computeIfAbsent(topic, t -> service.subscribe(t, this.createConsumer(t))));
   }

   protected abstract Consumer<Notification> createConsumer(String var1);
}
