package org.apache.logging.log4j.spi;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;
import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.message.ParameterizedMessageFactory;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class LoggerRegistry<T extends ExtendedLogger> {
   private final Map<String, Map<MessageFactory, T>> loggerByMessageFactoryByName = new HashMap<>();
   private final ReadWriteLock lock = new ReentrantReadWriteLock();
   private final Lock readLock = this.lock.readLock();
   private final Lock writeLock = this.lock.writeLock();

   public LoggerRegistry() {
   }

   @Deprecated
   public LoggerRegistry(@Nullable final LoggerRegistry.MapFactory<T> mapFactory) {
      this();
   }

   @Deprecated
   @Nullable
   public T getLogger(final String name) {
      Objects.requireNonNull(name, "name");
      return this.getLogger(name, null);
   }

   public T getLogger(final String name, final MessageFactory messageFactory) {
      Objects.requireNonNull(name, "name");
      this.readLock.lock();

      ExtendedLogger var5;
      try {
         Map<MessageFactory, T> loggerByMessageFactory = this.loggerByMessageFactoryByName.get(name);
         MessageFactory effectiveMessageFactory = (MessageFactory)(messageFactory != null ? messageFactory : ParameterizedMessageFactory.INSTANCE);
         var5 = loggerByMessageFactory == null ? null : loggerByMessageFactory.get(effectiveMessageFactory);
      } finally {
         this.readLock.unlock();
      }

      return (T)var5;
   }

   public Collection<T> getLoggers() {
      this.readLock.lock();

      Collection var1;
      try {
         var1 = this.loggerByMessageFactoryByName
            .values()
            .stream()
            .flatMap(loggerByMessageFactory -> loggerByMessageFactory.values().stream())
            .collect(Collectors.toList());
      } finally {
         this.readLock.unlock();
      }

      return var1;
   }

   public Collection<T> getLoggers(final Collection<T> destination) {
      Objects.requireNonNull(destination, "destination");
      destination.addAll(this.getLoggers());
      return destination;
   }

   @Deprecated
   public boolean hasLogger(final String name) {
      Objects.requireNonNull(name, "name");
      T logger = this.getLogger(name);
      return logger != null;
   }

   public boolean hasLogger(final String name, final MessageFactory messageFactory) {
      Objects.requireNonNull(name, "name");
      T logger = this.getLogger(name, messageFactory);
      return logger != null;
   }

   public boolean hasLogger(final String name, final Class<? extends MessageFactory> messageFactoryClass) {
      Objects.requireNonNull(name, "name");
      Objects.requireNonNull(messageFactoryClass, "messageFactoryClass");
      this.readLock.lock();

      boolean var3;
      try {
         var3 = this.loggerByMessageFactoryByName
            .getOrDefault(name, Collections.emptyMap())
            .keySet()
            .stream()
            .anyMatch(messageFactory -> messageFactoryClass.equals(messageFactory.getClass()));
      } finally {
         this.readLock.unlock();
      }

      return var3;
   }

   public void putIfAbsent(final String name, @Nullable final MessageFactory messageFactory, final T logger) {
      Objects.requireNonNull(name, "name");
      Objects.requireNonNull(logger, "logger");
      this.writeLock.lock();

      try {
         MessageFactory effectiveMessageFactory = (MessageFactory)(messageFactory != null ? messageFactory : ParameterizedMessageFactory.INSTANCE);
         this.loggerByMessageFactoryByName.computeIfAbsent(name, this::createLoggerRefByMessageFactoryMap).putIfAbsent(effectiveMessageFactory, logger);
         if (!name.equals(logger.getName()) || !effectiveMessageFactory.equals(logger.getMessageFactory())) {
            this.loggerByMessageFactoryByName
               .computeIfAbsent(logger.getName(), this::createLoggerRefByMessageFactoryMap)
               .putIfAbsent(logger.getMessageFactory(), logger);
         }
      } finally {
         this.writeLock.unlock();
      }
   }

   private Map<MessageFactory, T> createLoggerRefByMessageFactoryMap(final String ignored) {
      return new WeakHashMap<>();
   }

   @Deprecated
   public static class ConcurrentMapFactory<T extends ExtendedLogger> implements LoggerRegistry.MapFactory<T> {
      @Override
      public Map<String, T> createInnerMap() {
         return new ConcurrentHashMap<>();
      }

      @Override
      public Map<String, Map<String, T>> createOuterMap() {
         return new ConcurrentHashMap<>();
      }

      @Override
      public void putIfAbsent(final Map<String, T> innerMap, final String name, final T logger) {
         innerMap.putIfAbsent(name, logger);
      }
   }

   @Deprecated
   public interface MapFactory<T extends ExtendedLogger> {
      Map<String, T> createInnerMap();

      Map<String, Map<String, T>> createOuterMap();

      void putIfAbsent(Map<String, T> innerMap, String name, T logger);
   }

   @Deprecated
   public static class WeakMapFactory<T extends ExtendedLogger> implements LoggerRegistry.MapFactory<T> {
      @Override
      public Map<String, T> createInnerMap() {
         return new WeakHashMap<>();
      }

      @Override
      public Map<String, Map<String, T>> createOuterMap() {
         return new WeakHashMap<>();
      }

      @Override
      public void putIfAbsent(final Map<String, T> innerMap, final String name, final T logger) {
         innerMap.put(name, logger);
      }
   }
}
