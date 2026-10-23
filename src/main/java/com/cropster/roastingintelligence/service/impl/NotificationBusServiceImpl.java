package com.cropster.roastingintelligence.service.impl;

import com.cropster.roastingintelligence.model.notification.Notification;
import com.cropster.roastingintelligence.model.notification.Subscription;
import com.cropster.roastingintelligence.service.NotificationBusService;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.SubmissionPublisher;
import java.util.concurrent.Flow.Subscriber;
import java.util.function.Consumer;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotificationBusServiceImpl implements NotificationBusService {
   private final Map<String, SubmissionPublisher<Notification>> busses = Collections.synchronizedMap(new HashMap<>());
   private final Map<String, Queue<Notification>> caches = Collections.synchronizedMap(new HashMap<>());
   private final Set<ChangeListener<Boolean>> listeners = Collections.synchronizedSet(new HashSet<>());

   @Override
   public void publish(Notification notification, String... topics) {
      Objects.requireNonNull(notification, "Null 'notification' parameter");
      Validate.noNullElements(topics, "Null element in 'topics' array at position %d", new Object[0]);
      NotificationBusService.validateNotEmpty(topics);
      Arrays.stream(topics).forEach(t -> this.publish(t, notification));
   }

   private void publish(final String topic, final Notification notification) {
      final BooleanBinding binding = notification.getBinding();
      if (binding != null && !binding.get()) {
         ChangeListener<Boolean> listener = new ChangeListener<Boolean>() {
            public void changed(ObservableValue<? extends Boolean> s, Boolean o, Boolean n) {
               if (n != null && n) {
                  NotificationBusServiceImpl.this.performPublish(topic, notification);
                  binding.removeListener(this);
                  NotificationBusServiceImpl.this.listeners.remove(this);
               }
            }
         };
         this.listeners.add(listener);
         binding.addListener(listener);
      } else {
         this.performPublish(topic, notification);
      }
   }

   private void performPublish(String topic, Notification notification) {
      Runnable prerequisite = notification.getPrerequisite();
      if (prerequisite != null && notification.isPrerequisiteOnSubmission()) {
         prerequisite.run();
      }

      SubmissionPublisher<Notification> bus = this.busses.computeIfAbsent(topic, this::createBus);
      Queue<Notification> cache = this.caches.get(topic);
      synchronized (cache) {
         cache.add(notification);
         bus.submit(notification);
      }
   }

   @Override
   public Subscription subscribe(String topic, Consumer<Notification> consumer) {
      Objects.requireNonNull(topic, "Null 'topic' parameter");
      Objects.requireNonNull(consumer, "Null 'consumer' parameter");
      Validate.notEmpty(topic, "Empty 'topic' parameter", new Object[0]);
      SubmissionPublisher<Notification> bus = this.busses.computeIfAbsent(topic, this::createBus);
      NotificationBusServiceImpl.BusSubscription subscription = new NotificationBusServiceImpl.BusSubscription(consumer);
      Queue<Notification> cache = this.caches.get(topic);
      synchronized (cache) {
         bus.subscribe(subscription);
         cache.forEach(consumer);
         return subscription;
      }
   }

   private SubmissionPublisher<Notification> createBus(String topic) {
      this.caches.put(topic, new CircularFifoQueue());
      return new SubmissionPublisher<>();
   }

   private static class BusSubscription implements Subscription, Subscriber<Notification> {
      private static final Logger LOGGER = LoggerFactory.getLogger(NotificationBusServiceImpl.BusSubscription.class);
      private final Consumer<Notification> consumer;
      private volatile boolean disposed = false;
      private java.util.concurrent.Flow.Subscription subscription;

      BusSubscription(Consumer<Notification> consumer) {
         this.consumer = consumer;
      }

      @Override
      public void dispose() {
         this.subscription.cancel();
         this.disposed = true;
      }

      @Override
      public boolean isDisposed() {
         return this.disposed;
      }

      @Override
      public void onComplete() {
         this.disposed = true;
      }

      @Override
      public void onError(Throwable throwable) {
         LOGGER.error("Error in notification bus subscription", throwable);
         this.disposed = true;
      }

      public void onNext(Notification notification) {
         this.consumer.accept(notification);
         this.subscription.request(1L);
      }

      @Override
      public void onSubscribe(java.util.concurrent.Flow.Subscription subscription) {
         this.subscription = subscription;
         subscription.request(1L);
      }
   }
}
