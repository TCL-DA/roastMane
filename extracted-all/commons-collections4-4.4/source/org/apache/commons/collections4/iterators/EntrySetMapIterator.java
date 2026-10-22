package org.apache.commons.collections4.iterators;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.ResettableIterator;

public class EntrySetMapIterator<K, V> implements MapIterator<K, V>, ResettableIterator<K> {
   private final Map<K, V> map;
   private Iterator<Entry<K, V>> iterator;
   private Entry<K, V> last;
   private boolean canRemove = false;

   public EntrySetMapIterator(Map<K, V> map) {
      this.map = map;
      this.iterator = map.entrySet().iterator();
   }

   @Override
   public boolean hasNext() {
      return this.iterator.hasNext();
   }

   @Override
   public K next() {
      this.last = this.iterator.next();
      this.canRemove = true;
      return this.last.getKey();
   }

   @Override
   public void remove() {
      if (!this.canRemove) {
         throw new IllegalStateException("Iterator remove() can only be called once after next()");
      } else {
         this.iterator.remove();
         this.last = null;
         this.canRemove = false;
      }
   }

   @Override
   public K getKey() {
      if (this.last == null) {
         throw new IllegalStateException("Iterator getKey() can only be called after next() and before remove()");
      } else {
         return this.last.getKey();
      }
   }

   @Override
   public V getValue() {
      if (this.last == null) {
         throw new IllegalStateException("Iterator getValue() can only be called after next() and before remove()");
      } else {
         return this.last.getValue();
      }
   }

   @Override
   public V setValue(V value) {
      if (this.last == null) {
         throw new IllegalStateException("Iterator setValue() can only be called after next() and before remove()");
      } else {
         return this.last.setValue(value);
      }
   }

   @Override
   public void reset() {
      this.iterator = this.map.entrySet().iterator();
      this.last = null;
      this.canRemove = false;
   }

   @Override
   public String toString() {
      return this.last != null ? "MapIterator[" + this.getKey() + "=" + this.getValue() + "]" : "MapIterator[]";
   }
}
