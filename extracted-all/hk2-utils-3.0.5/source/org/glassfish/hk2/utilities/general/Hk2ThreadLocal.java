package org.glassfish.hk2.utilities.general;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Hk2ThreadLocal<V> {
   private static final Object NULL = new Object();
   private final Map<Hk2ThreadLocal.Key, Object> locals = new ConcurrentHashMap<>();
   private final ReferenceQueue<Thread> queue = new ReferenceQueue<>();

   protected V initialValue() {
      return null;
   }

   public V get() {
      this.removeStaleEntries();
      Hk2ThreadLocal.Key key = newLookupKey();
      Object value = this.locals.get(key);
      if (value == null) {
         value = this.initialValue();
         this.locals.put(newStorageKey(this.queue), maskNull(value));
      } else {
         value = unmaskNull(value);
      }

      return (V)value;
   }

   public void set(V value) {
      Hk2ThreadLocal.Key key = newStorageKey(this.queue);
      this.locals.put(key, maskNull(value));
   }

   public void remove() {
      Hk2ThreadLocal.Key key = newLookupKey();
      this.locals.remove(key);
   }

   public void removeAll() {
      this.locals.clear();
   }

   public int getSize() {
      this.removeStaleEntries();
      return this.locals.size();
   }

   private void removeStaleEntries() {
      Object queued;
      while ((queued = this.queue.poll()) != null) {
         Hk2ThreadLocal.Key key = (Hk2ThreadLocal.Key)queued;
         this.locals.remove(key);
      }
   }

   private static Object maskNull(Object value) {
      return value == null ? NULL : value;
   }

   private static Object unmaskNull(Object value) {
      return value == NULL ? null : value;
   }

   private static Hk2ThreadLocal.Key newStorageKey(ReferenceQueue<Thread> queue) {
      return new Hk2ThreadLocal.Key(Thread.currentThread(), queue);
   }

   private static Hk2ThreadLocal.Key newLookupKey() {
      return new Hk2ThreadLocal.Key(Thread.currentThread(), null);
   }

   private static class Key extends WeakReference<Thread> {
      private final long threadId;
      private final int hash;

      private Key(Thread thread, ReferenceQueue<Thread> queue) {
         super(thread, queue);
         this.threadId = thread.getId();
         this.hash = thread.hashCode();
      }

      @Override
      public boolean equals(Object obj) {
         if (!(obj instanceof Hk2ThreadLocal.Key)) {
            return false;
         } else {
            Hk2ThreadLocal.Key other = (Hk2ThreadLocal.Key)obj;
            return other.threadId == this.threadId;
         }
      }

      @Override
      public int hashCode() {
         return this.hash;
      }
   }
}
