package org.apache.logging.log4j.core.util.internal;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.Map.Entry;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.status.StatusLogger;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public final class InternalLoggerRegistry {
   private final Map<MessageFactory, Map<String, WeakReference<Logger>>> loggerRefByNameByMessageFactory = new WeakHashMap<>();
   private final ReadWriteLock lock = new ReentrantReadWriteLock();
   private final Lock readLock = this.lock.readLock();
   private final Lock writeLock = this.lock.writeLock();
   private final ReferenceQueue<Logger> staleLoggerRefs = new ReferenceQueue<>();

   private void expungeStaleEntries() {
      Reference<? extends Logger> loggerRef = this.staleLoggerRefs.poll();
      if (loggerRef != null) {
         this.writeLock.lock();

         try {
            while (this.staleLoggerRefs.poll() != null) {
            }

            Iterator<Entry<MessageFactory, Map<String, WeakReference<Logger>>>> loggerRefByNameByMessageFactoryEntryIt = this.loggerRefByNameByMessageFactory
               .entrySet()
               .iterator();

            while (loggerRefByNameByMessageFactoryEntryIt.hasNext()) {
               Entry<MessageFactory, Map<String, WeakReference<Logger>>> loggerRefByNameByMessageFactoryEntry = loggerRefByNameByMessageFactoryEntryIt.next();
               Map<String, WeakReference<Logger>> loggerRefByName = loggerRefByNameByMessageFactoryEntry.getValue();
               loggerRefByName.values().removeIf(weakRef -> weakRef.get() == null);
               if (loggerRefByName.isEmpty()) {
                  loggerRefByNameByMessageFactoryEntryIt.remove();
               }
            }
         } finally {
            this.writeLock.unlock();
         }
      }
   }

   @Nullable
   public Logger getLogger(final String name, final MessageFactory messageFactory) {
      Objects.requireNonNull(name, "name");
      Objects.requireNonNull(messageFactory, "messageFactory");
      this.expungeStaleEntries();
      this.readLock.lock();

      try {
         Map<String, WeakReference<Logger>> loggerRefByName = this.loggerRefByNameByMessageFactory.get(messageFactory);
         if (loggerRefByName != null) {
            WeakReference<Logger> loggerRef = loggerRefByName.get(name);
            if (loggerRef != null) {
               return loggerRef.get();
            }
         }

         return null;
      } finally {
         this.readLock.unlock();
      }
   }

   public Collection<Logger> getLoggers() {
      this.expungeStaleEntries();
      this.readLock.lock();

      Collection var1;
      try {
         var1 = this.loggerRefByNameByMessageFactory.values().stream().flatMap(loggerRefByName -> loggerRefByName.values().stream()).flatMap(loggerRef -> {
            Logger logger = loggerRef.get();
            return logger != null ? Stream.of(logger) : Stream.empty();
         }).collect(Collectors.toList());
      } finally {
         this.readLock.unlock();
      }

      return var1;
   }

   public boolean hasLogger(final String name, final MessageFactory messageFactory) {
      Objects.requireNonNull(name, "name");
      Objects.requireNonNull(messageFactory, "messageFactory");
      return this.getLogger(name, messageFactory) != null;
   }

   public boolean hasLogger(final String name, final Class<? extends MessageFactory> messageFactoryClass) {
      Objects.requireNonNull(name, "name");
      Objects.requireNonNull(messageFactoryClass, "messageFactoryClass");
      this.expungeStaleEntries();
      this.readLock.lock();

      boolean var3;
      try {
         var3 = this.loggerRefByNameByMessageFactory
            .entrySet()
            .stream()
            .filter(entry -> messageFactoryClass.equals(entry.getKey().getClass()))
            .anyMatch(entry -> entry.getValue().containsKey(name));
      } finally {
         this.readLock.unlock();
      }

      return var3;
   }

   public Logger computeIfAbsent(final String name, final MessageFactory messageFactory, final BiFunction<String, MessageFactory, Logger> loggerSupplier) {
      Objects.requireNonNull(name, "name");
      Objects.requireNonNull(messageFactory, "messageFactory");
      Objects.requireNonNull(loggerSupplier, "loggerSupplier");
      Logger logger = this.getLogger(name, messageFactory);
      if (logger != null) {
         return logger;
      } else {
         Logger newLogger = loggerSupplier.apply(name, messageFactory);
         String loggerName = newLogger.getName();
         MessageFactory loggerMessageFactory = newLogger.getMessageFactory();
         if (!loggerName.equals(name) || !loggerMessageFactory.equals(messageFactory)) {
            StatusLogger.getLogger()
               .error(
                  "Newly registered logger with name `{}` and message factory `{}`, is requested to be associated with a different name `{}` or message factory `{}`.\nEffectively the message factory of the logger will be used and the other one will be ignored.\nThis generally hints a problem at the logger context implementation.\nPlease report this using the Log4j project issue tracker.",
                  loggerName,
                  loggerMessageFactory,
                  name,
                  messageFactory
               );
         }

         this.writeLock.lock();

         Logger var10;
         try {
            Map<String, WeakReference<Logger>> loggerRefByName = this.loggerRefByNameByMessageFactory.get(messageFactory);
            if (loggerRefByName == null) {
               this.loggerRefByNameByMessageFactory.put(messageFactory, loggerRefByName = new HashMap<>());
            }

            WeakReference<Logger> loggerRef = loggerRefByName.get(name);
            if (loggerRef == null || (logger = loggerRef.get()) == null) {
               logger = newLogger;
               loggerRefByName.put(name, new WeakReference<>(newLogger, this.staleLoggerRefs));
            }

            var10 = logger;
         } finally {
            this.writeLock.unlock();
         }

         return var10;
      }
   }
}
