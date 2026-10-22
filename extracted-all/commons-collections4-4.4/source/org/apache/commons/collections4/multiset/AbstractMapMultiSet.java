package org.apache.commons.collections4.multiset;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.collections4.MultiSet;
import org.apache.commons.collections4.iterators.AbstractIteratorDecorator;

public abstract class AbstractMapMultiSet<E> extends AbstractMultiSet<E> {
   private transient Map<E, AbstractMapMultiSet.MutableInteger> map;
   private transient int size;
   private transient int modCount;

   protected AbstractMapMultiSet() {
   }

   protected AbstractMapMultiSet(Map<E, AbstractMapMultiSet.MutableInteger> map) {
      this.map = map;
   }

   protected Map<E, AbstractMapMultiSet.MutableInteger> getMap() {
      return this.map;
   }

   protected void setMap(Map<E, AbstractMapMultiSet.MutableInteger> map) {
      this.map = map;
   }

   @Override
   public int size() {
      return this.size;
   }

   @Override
   public boolean isEmpty() {
      return this.map.isEmpty();
   }

   @Override
   public int getCount(Object object) {
      AbstractMapMultiSet.MutableInteger count = this.map.get(object);
      return count != null ? count.value : 0;
   }

   @Override
   public boolean contains(Object object) {
      return this.map.containsKey(object);
   }

   @Override
   public Iterator<E> iterator() {
      return new AbstractMapMultiSet.MapBasedMultiSetIterator<>(this);
   }

   @Override
   public int add(E object, int occurrences) {
      if (occurrences < 0) {
         throw new IllegalArgumentException("Occurrences must not be negative.");
      } else {
         AbstractMapMultiSet.MutableInteger mut = this.map.get(object);
         int oldCount = mut != null ? mut.value : 0;
         if (occurrences > 0) {
            this.modCount++;
            this.size += occurrences;
            if (mut == null) {
               this.map.put(object, new AbstractMapMultiSet.MutableInteger(occurrences));
            } else {
               mut.value += occurrences;
            }
         }

         return oldCount;
      }
   }

   @Override
   public void clear() {
      this.modCount++;
      this.map.clear();
      this.size = 0;
   }

   @Override
   public int remove(Object object, int occurrences) {
      if (occurrences < 0) {
         throw new IllegalArgumentException("Occurrences must not be negative.");
      } else {
         AbstractMapMultiSet.MutableInteger mut = this.map.get(object);
         if (mut == null) {
            return 0;
         } else {
            int oldCount = mut.value;
            if (occurrences > 0) {
               this.modCount++;
               if (occurrences < mut.value) {
                  mut.value -= occurrences;
                  this.size -= occurrences;
               } else {
                  this.map.remove(object);
                  this.size = this.size - mut.value;
                  mut.value = 0;
               }
            }

            return oldCount;
         }
      }
   }

   @Override
   protected Iterator<E> createUniqueSetIterator() {
      return new AbstractMapMultiSet.UniqueSetIterator<>(this.getMap().keySet().iterator(), this);
   }

   @Override
   protected int uniqueElements() {
      return this.map.size();
   }

   @Override
   protected Iterator<MultiSet.Entry<E>> createEntrySetIterator() {
      return new AbstractMapMultiSet.EntrySetIterator<>(this.map.entrySet().iterator(), this);
   }

   @Override
   protected void doWriteObject(ObjectOutputStream out) throws IOException {
      out.writeInt(this.map.size());

      for (Map.Entry<E, AbstractMapMultiSet.MutableInteger> entry : this.map.entrySet()) {
         out.writeObject(entry.getKey());
         out.writeInt(entry.getValue().value);
      }
   }

   @Override
   protected void doReadObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
      int entrySize = in.readInt();

      for (int i = 0; i < entrySize; i++) {
         E obj = (E)in.readObject();
         int count = in.readInt();
         this.map.put(obj, new AbstractMapMultiSet.MutableInteger(count));
         this.size += count;
      }
   }

   @Override
   public Object[] toArray() {
      Object[] result = new Object[this.size()];
      int i = 0;

      for (Map.Entry<E, AbstractMapMultiSet.MutableInteger> entry : this.map.entrySet()) {
         E current = entry.getKey();
         AbstractMapMultiSet.MutableInteger count = entry.getValue();

         for (int index = count.value; index > 0; index--) {
            result[i++] = current;
         }
      }

      return result;
   }

   @Override
   public <T> T[] toArray(T[] array) {
      int size = this.size();
      if (array.length < size) {
         T[] unchecked = (T[])Array.newInstance(array.getClass().getComponentType(), size);
         array = unchecked;
      }

      int i = 0;

      for (Map.Entry<E, AbstractMapMultiSet.MutableInteger> entry : this.map.entrySet()) {
         E current = entry.getKey();
         AbstractMapMultiSet.MutableInteger count = entry.getValue();

         for (int index = count.value; index > 0; index--) {
            array[i++] = (T)current;
         }
      }

      while (i < array.length) {
         array[i++] = null;
      }

      return array;
   }

   @Override
   public boolean equals(Object object) {
      if (object == this) {
         return true;
      } else if (!(object instanceof MultiSet)) {
         return false;
      } else {
         MultiSet<?> other = (MultiSet<?>)object;
         if (other.size() != this.size()) {
            return false;
         } else {
            for (E element : this.map.keySet()) {
               if (other.getCount(element) != this.getCount(element)) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   @Override
   public int hashCode() {
      int total = 0;

      for (Map.Entry<E, AbstractMapMultiSet.MutableInteger> entry : this.map.entrySet()) {
         E element = entry.getKey();
         AbstractMapMultiSet.MutableInteger count = entry.getValue();
         total += (element == null ? 0 : element.hashCode()) ^ count.value;
      }

      return total;
   }

   protected static class EntrySetIterator<E> implements Iterator<MultiSet.Entry<E>> {
      protected final AbstractMapMultiSet<E> parent;
      protected final Iterator<Map.Entry<E, AbstractMapMultiSet.MutableInteger>> decorated;
      protected MultiSet.Entry<E> last = null;
      protected boolean canRemove = false;

      protected EntrySetIterator(Iterator<Map.Entry<E, AbstractMapMultiSet.MutableInteger>> iterator, AbstractMapMultiSet<E> parent) {
         this.decorated = iterator;
         this.parent = parent;
      }

      @Override
      public boolean hasNext() {
         return this.decorated.hasNext();
      }

      public MultiSet.Entry<E> next() {
         this.last = new AbstractMapMultiSet.MultiSetEntry<>(this.decorated.next());
         this.canRemove = true;
         return this.last;
      }

      @Override
      public void remove() {
         if (!this.canRemove) {
            throw new IllegalStateException("Iterator remove() can only be called once after next()");
         } else {
            this.decorated.remove();
            this.last = null;
            this.canRemove = false;
         }
      }
   }

   private static class MapBasedMultiSetIterator<E> implements Iterator<E> {
      private final AbstractMapMultiSet<E> parent;
      private final Iterator<Map.Entry<E, AbstractMapMultiSet.MutableInteger>> entryIterator;
      private Map.Entry<E, AbstractMapMultiSet.MutableInteger> current;
      private int itemCount;
      private final int mods;
      private boolean canRemove;

      public MapBasedMultiSetIterator(AbstractMapMultiSet<E> parent) {
         this.parent = parent;
         this.entryIterator = parent.map.entrySet().iterator();
         this.current = null;
         this.mods = parent.modCount;
         this.canRemove = false;
      }

      @Override
      public boolean hasNext() {
         return this.itemCount > 0 || this.entryIterator.hasNext();
      }

      @Override
      public E next() {
         if (this.parent.modCount != this.mods) {
            throw new ConcurrentModificationException();
         } else {
            if (this.itemCount == 0) {
               this.current = this.entryIterator.next();
               this.itemCount = this.current.getValue().value;
            }

            this.canRemove = true;
            this.itemCount--;
            return this.current.getKey();
         }
      }

      @Override
      public void remove() {
         if (this.parent.modCount != this.mods) {
            throw new ConcurrentModificationException();
         } else if (!this.canRemove) {
            throw new IllegalStateException();
         } else {
            AbstractMapMultiSet.MutableInteger mut = this.current.getValue();
            if (mut.value > 1) {
               mut.value--;
            } else {
               this.entryIterator.remove();
            }

            this.parent.size--;
            this.canRemove = false;
         }
      }
   }

   protected static class MultiSetEntry<E> extends AbstractMultiSet.AbstractEntry<E> {
      protected final Map.Entry<E, AbstractMapMultiSet.MutableInteger> parentEntry;

      protected MultiSetEntry(Map.Entry<E, AbstractMapMultiSet.MutableInteger> parentEntry) {
         this.parentEntry = parentEntry;
      }

      @Override
      public E getElement() {
         return this.parentEntry.getKey();
      }

      @Override
      public int getCount() {
         return this.parentEntry.getValue().value;
      }
   }

   protected static class MutableInteger {
      protected int value;

      MutableInteger(int value) {
         this.value = value;
      }

      @Override
      public boolean equals(Object obj) {
         return !(obj instanceof AbstractMapMultiSet.MutableInteger) ? false : ((AbstractMapMultiSet.MutableInteger)obj).value == this.value;
      }

      @Override
      public int hashCode() {
         return this.value;
      }
   }

   protected static class UniqueSetIterator<E> extends AbstractIteratorDecorator<E> {
      protected final AbstractMapMultiSet<E> parent;
      protected E lastElement = (E)null;
      protected boolean canRemove = false;

      protected UniqueSetIterator(Iterator<E> iterator, AbstractMapMultiSet<E> parent) {
         super(iterator);
         this.parent = parent;
      }

      @Override
      public E next() {
         this.lastElement = super.next();
         this.canRemove = true;
         return this.lastElement;
      }

      @Override
      public void remove() {
         if (!this.canRemove) {
            throw new IllegalStateException("Iterator remove() can only be called once after next()");
         } else {
            int count = this.parent.getCount(this.lastElement);
            super.remove();
            this.parent.remove(this.lastElement, count);
            this.lastElement = null;
            this.canRemove = false;
         }
      }
   }
}
