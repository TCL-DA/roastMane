package org.apache.logging.slf4j;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.ThreadContext.ContextStack;
import org.apache.logging.log4j.status.StatusLogger;
import org.slf4j.spi.MDCAdapter;

public class Log4jMDCAdapter implements MDCAdapter {
   private static final Logger LOGGER = StatusLogger.getLogger();
   private final Log4jMDCAdapter.ThreadLocalMapOfStacks mapOfStacks = new Log4jMDCAdapter.ThreadLocalMapOfStacks();

   public void put(final String key, final String val) {
      ThreadContext.put(key, val);
   }

   public String get(final String key) {
      return ThreadContext.get(key);
   }

   public void remove(final String key) {
      ThreadContext.remove(key);
   }

   public void clear() {
      ThreadContext.clearMap();
   }

   public Map<String, String> getCopyOfContextMap() {
      return ThreadContext.getContext();
   }

   public void setContextMap(final Map<String, String> map) {
      ThreadContext.clearMap();
      ThreadContext.putAll(map);
   }

   public void pushByKey(final String key, final String value) {
      if (key == null) {
         ThreadContext.push(value);
      } else {
         String oldValue = this.mapOfStacks.peekByKey(key);
         if (!Objects.equals(ThreadContext.get(key), oldValue)) {
            LOGGER.warn("The key {} was used in both the string and stack-valued MDC.", key);
         }

         this.mapOfStacks.pushByKey(key, value);
         ThreadContext.put(key, value);
      }
   }

   public String popByKey(final String key) {
      if (key == null) {
         return ThreadContext.getDepth() > 0 ? ThreadContext.pop() : null;
      } else {
         String value = this.mapOfStacks.popByKey(key);
         if (!Objects.equals(ThreadContext.get(key), value)) {
            LOGGER.warn("The key {} was used in both the string and stack-valued MDC.", key);
         }

         ThreadContext.put(key, this.mapOfStacks.peekByKey(key));
         return value;
      }
   }

   public Deque<String> getCopyOfDequeByKey(final String key) {
      if (key == null) {
         ContextStack stack = ThreadContext.getImmutableStack();
         Deque<String> copy = new ArrayDeque<>(stack.size());
         stack.forEach(copy::push);
         return copy;
      } else {
         return this.mapOfStacks.getCopyOfDequeByKey(key);
      }
   }

   public void clearDequeByKey(final String key) {
      if (key == null) {
         ThreadContext.clearStack();
      } else {
         this.mapOfStacks.clearByKey(key);
         ThreadContext.put(key, null);
      }
   }

   private static class ThreadLocalMapOfStacks {
      private final ThreadLocal<Map<String, Deque<String>>> tlMapOfStacks = ThreadLocal.withInitial(HashMap::new);

      private ThreadLocalMapOfStacks() {
      }

      public void pushByKey(final String key, final String value) {
         this.tlMapOfStacks.get().computeIfAbsent(key, ignored -> new ArrayDeque<>()).push(value);
      }

      public String popByKey(final String key) {
         Deque<String> deque = this.tlMapOfStacks.get().get(key);
         return deque != null ? deque.poll() : null;
      }

      public Deque<String> getCopyOfDequeByKey(final String key) {
         Deque<String> deque = this.tlMapOfStacks.get().get(key);
         return deque != null ? new ArrayDeque<>(deque) : null;
      }

      public void clearByKey(final String key) {
         Deque<String> deque = this.tlMapOfStacks.get().get(key);
         if (deque != null) {
            deque.clear();
         }
      }

      public String peekByKey(final String key) {
         Deque<String> deque = this.tlMapOfStacks.get().get(key);
         return deque != null ? deque.peek() : null;
      }
   }
}
