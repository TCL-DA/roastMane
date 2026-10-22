package org.glassfish.hk2.utilities.general.internal;

import java.lang.ref.ReferenceQueue;
import java.util.LinkedList;
import java.util.WeakHashMap;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import org.glassfish.hk2.utilities.cache.CacheKeyFilter;
import org.glassfish.hk2.utilities.general.WeakHashClock;

public class WeakHashClockImpl<K, V> implements WeakHashClock<K, V> {
   private final boolean isWeak;
   private final ConcurrentHashMap<K, DoubleNode<K, V>> byKeyNotWeak;
   private final WeakHashMap<K, DoubleNode<K, V>> byKey;
   private final ReferenceQueue<? super K> myQueue = new ReferenceQueue<>();
   private DoubleNode<K, V> head;
   private DoubleNode<K, V> tail;
   private DoubleNode<K, V> dot;

   public WeakHashClockImpl(boolean isWeak) {
      this.isWeak = isWeak;
      if (isWeak) {
         this.byKey = new WeakHashMap<>();
         this.byKeyNotWeak = null;
      } else {
         this.byKeyNotWeak = new ConcurrentHashMap<>();
         this.byKey = null;
      }
   }

   private DoubleNode<K, V> addBeforeDot(K key, V value) {
      DoubleNode<K, V> toAdd = new DoubleNode<>(key, value, this.myQueue);
      if (this.dot == null) {
         this.head = toAdd;
         this.tail = toAdd;
         this.dot = toAdd;
         return toAdd;
      } else if (this.dot.getPrevious() == null) {
         this.dot.setPrevious(toAdd);
         toAdd.setNext(this.dot);
         this.head = toAdd;
         return toAdd;
      } else {
         toAdd.setNext(this.dot);
         toAdd.setPrevious(this.dot.getPrevious());
         this.dot.getPrevious().setNext(toAdd);
         this.dot.setPrevious(toAdd);
         return toAdd;
      }
   }

   private void removeFromDLL(DoubleNode<K, V> removeMe) {
      if (removeMe.getPrevious() != null) {
         removeMe.getPrevious().setNext(removeMe.getNext());
      }

      if (removeMe.getNext() != null) {
         removeMe.getNext().setPrevious(removeMe.getPrevious());
      }

      if (removeMe == this.head) {
         this.head = removeMe.getNext();
      }

      if (removeMe == this.tail) {
         this.tail = removeMe.getPrevious();
      }

      if (removeMe == this.dot) {
         this.dot = removeMe.getNext();
         if (this.dot == null) {
            this.dot = this.head;
         }
      }

      removeMe.setNext(null);
      removeMe.setPrevious(null);
   }

   @Override
   public void put(K key, V value) {
      if (key != null && value != null) {
         synchronized (this) {
            if (this.isWeak) {
               this.removeStale();
            }

            DoubleNode<K, V> addMe = this.addBeforeDot(key, value);
            if (this.isWeak) {
               this.byKey.put(key, addMe);
            } else {
               this.byKeyNotWeak.put(key, addMe);
            }
         }
      } else {
         throw new IllegalArgumentException("key " + key + " or value " + value + " is null");
      }
   }

   @Override
   public V get(K key) {
      if (key == null) {
         return null;
      } else {
         DoubleNode<K, V> node;
         if (this.isWeak) {
            synchronized (this) {
               this.removeStale();
               node = this.byKey.get(key);
            }
         } else {
            node = this.byKeyNotWeak.get(key);
         }

         return node == null ? null : node.getValue();
      }
   }

   @Override
   public V remove(K key) {
      if (key == null) {
         return null;
      } else {
         synchronized (this) {
            DoubleNode<K, V> node;
            if (this.isWeak) {
               this.removeStale();
               node = this.byKey.remove(key);
            } else {
               node = this.byKeyNotWeak.remove(key);
            }

            if (node == null) {
               return null;
            } else {
               this.removeFromDLL(node);
               return node.getValue();
            }
         }
      }
   }

   @Override
   public synchronized void releaseMatching(CacheKeyFilter<K> filter) {
      if (filter != null) {
         if (this.isWeak) {
            this.removeStale();
         }

         LinkedList<K> removeMe = new LinkedList<>();

         for (DoubleNode<K, V> current = this.head; current != null; current = current.getNext()) {
            K key = current.getWeakKey().get();
            if (key != null && filter.matches(key)) {
               removeMe.add(key);
            }
         }

         for (K removeKey : removeMe) {
            this.remove(removeKey);
         }
      }
   }

   @Override
   public int size() {
      if (this.isWeak) {
         synchronized (this) {
            this.removeStale();
            return this.byKey.size();
         }
      } else {
         return this.byKeyNotWeak.size();
      }
   }

   private DoubleNode<K, V> moveDot() {
      if (this.dot == null) {
         return null;
      } else {
         DoubleNode<K, V> returnSource = this.dot;
         this.dot = returnSource.getNext();
         if (this.dot == null) {
            this.dot = this.head;
         }

         return returnSource;
      }
   }

   private DoubleNode<K, V> moveDotNoWeak() {
      DoubleNode<K, V> original = this.moveDot();
      DoubleNode<K, V> retVal = original;
      if (original == null) {
         return null;
      } else {
         K key;
         while ((key = retVal.getWeakKey().get()) == null) {
            retVal = this.moveDot();
            if (retVal == null) {
               return null;
            }

            if (retVal == original) {
               return null;
            }
         }

         retVal.setHardenedKey(key);
         return retVal;
      }
   }

   @Override
   public synchronized Entry<K, V> next() {
      DoubleNode<K, V> hardenedNode = this.moveDotNoWeak();
      if (hardenedNode == null) {
         return null;
      } else {
         Entry var4;
         try {
            final K key = hardenedNode.getHardenedKey();
            final V value = hardenedNode.getValue();
            var4 = new Entry<K, V>() {
               @Override
               public K getKey() {
                  return key;
               }

               @Override
               public V getValue() {
                  return value;
               }

               @Override
               public V setValue(V valuex) {
                  throw new AssertionError("not implemented");
               }
            };
         } finally {
            hardenedNode.setHardenedKey(null);
            this.removeStale();
         }

         return var4;
      }
   }

   @Override
   public synchronized void clear() {
      if (this.isWeak) {
         this.byKey.clear();
      } else {
         this.byKeyNotWeak.clear();
      }

      this.head = this.tail = this.dot = null;
   }

   @Override
   public synchronized void clearStaleReferences() {
      this.removeStale();
   }

   private void removeStale() {
      boolean goOn = false;

      while (this.myQueue.poll() != null) {
         goOn = true;
      }

      if (goOn) {
         DoubleNode<K, V> current = this.head;

         while (current != null) {
            DoubleNode<K, V> next = current.getNext();
            if (current.getWeakKey().get() == null) {
               this.removeFromDLL(current);
            }

            current = next;
         }
      }
   }

   @Override
   public boolean hasWeakKeys() {
      return this.isWeak;
   }

   @Override
   public synchronized String toString() {
      StringBuffer sb = new StringBuffer("WeakHashClockImpl({");
      boolean first = true;
      DoubleNode<K, V> current = this.dot;
      if (current != null) {
         do {
            K key = current.getWeakKey().get();
            String keyString = key == null ? "null" : key.toString();
            if (first) {
               first = false;
               sb.append(keyString);
            } else {
               sb.append("," + keyString);
            }

            current = current.getNext();
            if (current == null) {
               current = this.head;
            }
         } while (current != this.dot);
      }

      sb.append("}," + System.identityHashCode(this) + ")");
      return sb.toString();
   }
}
