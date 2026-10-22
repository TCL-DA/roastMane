package org.jvnet.hk2.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.glassfish.hk2.api.messaging.Topic;
import org.glassfish.hk2.api.messaging.TopicDistributionService;
import org.glassfish.hk2.utilities.NamedImpl;

public class TopicImpl<T> implements Topic<T> {
   private final ServiceLocatorImpl locator;
   private final Type topicType;
   private final Set<Annotation> requiredQualifiers;

   TopicImpl(ServiceLocatorImpl locator, Type topicType, Set<Annotation> requiredQualifiers) {
      this.locator = locator;
      this.topicType = topicType;
      this.requiredQualifiers = Collections.unmodifiableSet(requiredQualifiers);
   }

   public void publish(T message) {
      if (message == null) {
         throw new IllegalArgumentException();
      } else {
         TopicDistributionService distributor = this.locator.getService(TopicDistributionService.class);
         if (distributor == null) {
            throw new IllegalStateException("There is no implementation of the TopicDistributionService to distribute the message");
         } else {
            distributor.distributeMessage(this, message);
         }
      }
   }

   public Topic<T> named(String name) {
      return this.qualifiedWith(new NamedImpl(name));
   }

   public <U> Topic<U> ofType(Type type) {
      return new TopicImpl(this.locator, type, this.requiredQualifiers);
   }

   public Topic<T> qualifiedWith(Annotation... qualifiers) {
      HashSet<Annotation> moreAnnotations = new HashSet<>(this.requiredQualifiers);

      for (Annotation qualifier : qualifiers) {
         moreAnnotations.add(qualifier);
      }

      return new TopicImpl<>(this.locator, this.topicType, moreAnnotations);
   }

   public Type getTopicType() {
      return this.topicType;
   }

   public Set<Annotation> getTopicQualifiers() {
      return this.requiredQualifiers;
   }
}
