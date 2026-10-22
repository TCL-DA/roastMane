package nonapi.io.github.classgraph.concurrency;

import java.util.ArrayList;
import java.util.List;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import nonapi.io.github.classgraph.utils.LogNode;

public abstract class SingletonMap<K, V, E extends Exception> {
   private final ConcurrentMap<K, SingletonMap.SingletonHolder<V>> map = new ConcurrentHashMap<>();

   public abstract V newInstance(K var1, LogNode var2) throws E, InterruptedException;

   public V get(K key, LogNode log, SingletonMap.NewInstanceFactory<V, E> newInstanceFactory) throws E, InterruptedException, SingletonMap.NullSingletonException, SingletonMap.NewInstanceException {
      SingletonMap.SingletonHolder<V> singletonHolder = this.map.get(key);
      V instance = null;
      if (singletonHolder != null) {
         instance = singletonHolder.get();
      } else {
         SingletonMap.SingletonHolder<V> newSingletonHolder = new SingletonMap.SingletonHolder<>();
         SingletonMap.SingletonHolder<V> oldSingletonHolder = this.map.putIfAbsent(key, newSingletonHolder);
         if (oldSingletonHolder != null) {
            instance = oldSingletonHolder.get();
         } else {
            try {
               if (newInstanceFactory != null) {
                  instance = newInstanceFactory.newInstance();
               } else {
                  instance = this.newInstance(key, log);
               }
            } catch (Throwable var9) {
               newSingletonHolder.set(instance);
               throw new SingletonMap.NewInstanceException(key, var9);
            }

            newSingletonHolder.set(instance);
         }
      }

      if (instance == null) {
         throw new SingletonMap.NullSingletonException(key);
      } else {
         return instance;
      }
   }

   public V get(K key, LogNode log) throws E, InterruptedException, SingletonMap.NullSingletonException, SingletonMap.NewInstanceException {
      return this.get(key, log, null);
   }

   public List<V> values() throws InterruptedException {
      List<V> entries = new ArrayList(this.map.size());

      for (Entry<K, SingletonMap.SingletonHolder<V>> ent : this.map.entrySet()) {
         V entryValue = ent.getValue().get();
         if (entryValue != null) {
            entries.add(entryValue);
         }
      }

      return entries;
   }

   public boolean isEmpty() {
      return this.map.isEmpty();
   }

   public List<Entry<K, V>> entries() throws InterruptedException {
      List<Entry<K, V>> entries = new ArrayList<>(this.map.size());

      for (Entry<K, SingletonMap.SingletonHolder<V>> ent : this.map.entrySet()) {
         entries.add(new SimpleEntry<>(ent.getKey(), ent.getValue().get()));
      }

      return entries;
   }

   public V remove(K key) throws InterruptedException {
      SingletonMap.SingletonHolder<V> val = this.map.remove(key);
      return val == null ? null : val.get();
   }

   public void clear() {
      this.map.clear();
   }

   public static class NewInstanceException extends Exception {
      static final long serialVersionUID = 1L;

      public <K> NewInstanceException(K key, Throwable t) {
         super("newInstance threw an exception for key " + key + " : " + t, t);
      }
   }

   @FunctionalInterface
   public interface NewInstanceFactory<V, E extends Exception> {
      V newInstance() throws E, InterruptedException;
   }

   public static class NullSingletonException extends Exception {
      static final long serialVersionUID = 1L;

      public <K> NullSingletonException(K key) {
         super("newInstance returned null for key " + key);
      }
   }

   private static class SingletonHolder<V> {
      private volatile V singleton;
      private final CountDownLatch initialized = new CountDownLatch(1);

      private SingletonHolder() {
      }

      void set(V singleton) throws IllegalArgumentException {
         if (this.initialized.getCount() < 1L) {
            throw new IllegalArgumentException("Singleton already initialized");
         } else {
            this.singleton = singleton;
            this.initialized.countDown();
            if (this.initialized.getCount() != 0L) {
               throw new IllegalArgumentException("Singleton initialized more than once");
            }
         }
      }

      V get() throws InterruptedException {
         this.initialized.await();
         return this.singleton;
      }
   }
}
