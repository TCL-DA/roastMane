package org.glassfish.hk2.utilities.general.internal;

import java.lang.ref.ReferenceQueue;
import java.util.LinkedList;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import org.glassfish.hk2.utilities.cache.CacheKeyFilter;
import org.glassfish.hk2.utilities.general.WeakHashLRU;

public class WeakHashLRUImpl<K> implements WeakHashLRU<K> {
   private static final Object VALUE = new Object();
   private final boolean isWeak;
   private final WeakHashMap<K, DoubleNode<K, Object>> byKey;
   private final ConcurrentHashMap<K, DoubleNode<K, Object>> byKeyNotWeak;
   private final ReferenceQueue<? super K> myQueue = new ReferenceQueue<>();
   private DoubleNode<K, Object> mru;
   private DoubleNode<K, Object> lru;

   public WeakHashLRUImpl(boolean isWeak) {
      this.isWeak = isWeak;
      if (isWeak) {
         this.byKey = new WeakHashMap<>();
         this.byKeyNotWeak = null;
      } else {
         this.byKey = null;
         this.byKeyNotWeak = new ConcurrentHashMap<>();
      }
   }

   private DoubleNode<K, Object> addToHead(K key) {
      DoubleNode<K, Object> added = new DoubleNode<>(key, VALUE, this.myQueue);
      if (this.mru == null) {
         this.mru = added;
         this.lru = added;
         return added;
      } else {
         added.setNext(this.mru);
         this.mru.setPrevious(added);
         this.mru = added;
         return added;
      }
   }

   private K remove(DoubleNode<K, Object> removeMe) {
      K retVal = removeMe.getWeakKey().get();
      if (removeMe.getNext() != null) {
         removeMe.getNext().setPrevious(removeMe.getPrevious());
      }

      if (removeMe.getPrevious() != null) {
         removeMe.getPrevious().setNext(removeMe.getNext());
      }

      if (removeMe == this.mru) {
         this.mru = removeMe.getNext();
      }

      if (removeMe == this.lru) {
         this.lru = removeMe.getPrevious();
      }

      removeMe.setNext(null);
      removeMe.setPrevious(null);
      return retVal;
   }

   @Override
   public synchronized void add(K key) {
      if (key == null) {
         throw new IllegalArgumentException("key may not be null");
      } else {
         DoubleNode<K, Object> existing;
         if (this.isWeak) {
            this.clearStale();
            existing = this.byKey.get(key);
         } else {
            existing = this.byKeyNotWeak.get(key);
         }

         if (existing != null) {
            this.remove(existing);
         }

         DoubleNode<K, Object> added = this.addToHead(key);
         if (this.isWeak) {
            this.byKey.put(key, added);
         } else {
            this.byKeyNotWeak.put(key, added);
         }
      }
   }

   @Override
   public boolean contains(K key) {
      if (this.isWeak) {
         synchronized (this) {
            this.clearStale();
            return this.byKey.containsKey(key);
         }
      } else {
         return this.byKeyNotWeak.containsKey(key);
      }
   }

   @Override
   public synchronized boolean remove(K key) {
      if (this.isWeak) {
         this.clearStale();
      }

      return this.removeNoClear(key);
   }

   private boolean removeNoClear(K key) {
      if (key == null) {
         return false;
      } else {
         DoubleNode<K, Object> removeMe;
         if (this.isWeak) {
            removeMe = this.byKey.remove(key);
         } else {
            removeMe = this.byKeyNotWeak.remove(key);
         }

         if (removeMe == null) {
            return false;
         } else {
            this.remove(removeMe);
            return true;
         }
      }
   }

   @Override
   public int size() {
      if (this.isWeak) {
         synchronized (this) {
            this.clearStale();
            return this.byKey.size();
         }
      } else {
         return this.byKeyNotWeak.size();
      }
   }

   @Override
   public synchronized K remove() {
      try {
         if (this.lru == null) {
            return null;
         } else {
            DoubleNode<K, Object> current = this.lru;

            while (current != null) {
               DoubleNode<K, Object> previous = current.getPrevious();
               K retVal = current.getWeakKey().get();
               if (retVal != null) {
                  this.removeNoClear(retVal);
                  return retVal;
               }

               this.remove(current);
               current = previous;
            }

            return null;
         }
      } finally {
         this.clearStale();
      }
   }

   @Override
   public synchronized void releaseMatching(CacheKeyFilter<K> filter) {
      if (filter != null) {
         if (this.isWeak) {
            this.clearStale();
         }

         LinkedList<K> removeMe = new LinkedList<>();

         for (DoubleNode<K, Object> current = this.mru; current != null; current = current.getNext()) {
            K key = current.getWeakKey().get();
            if (key != null && filter.matches(key)) {
               removeMe.add(key);
            }
         }

         for (K removeKey : removeMe) {
            this.removeNoClear(removeKey);
         }
      }
   }

   @Override
   public synchronized void clear() {
      if (this.isWeak) {
         this.clearStale();
         this.byKey.clear();
      } else {
         this.byKeyNotWeak.clear();
      }

      this.mru = null;
      this.lru = null;
   }

   @Override
   public synchronized void clearStaleReferences() {
      this.clearStale();
   }

   private void clearStale() {
      boolean goOn = false;

      while (this.myQueue.poll() != null) {
         goOn = true;
      }

      if (goOn) {
         DoubleNode<K, Object> current = this.mru;

         while (current != null) {
            DoubleNode<K, Object> next = current.getNext();
            if (current.getWeakKey().get() == null) {
               this.remove(current);
            }

            current = next;
         }
      }
   }

   @Override
   public synchronized String toString() {
      StringBuffer sb = new StringBuffer("WeakHashLRUImpl({");
      boolean first = true;

      for (DoubleNode<K, Object> current = this.mru; current != null; current = current.getNext()) {
         K key = current.getWeakKey().get();
         String keyString = key == null ? "null" : key.toString();
         if (first) {
            first = false;
            sb.append(keyString);
         } else {
            sb.append("," + keyString);
         }
      }

      sb.append("}," + System.identityHashCode(this) + ")");
      return sb.toString();
   }
}
