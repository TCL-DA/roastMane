package org.apache.commons.collections4.bag;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import org.apache.commons.collections4.Bag;
import org.apache.commons.collections4.set.UnmodifiableSet;

public abstract class AbstractMapBag<E> implements Bag<E> {
   private transient Map<E, AbstractMapBag.MutableInteger> map;
   private int size;
   private transient int modCount;
   private transient Set<E> uniqueSet;

   protected AbstractMapBag() {
   }

   protected AbstractMapBag(Map<E, AbstractMapBag.MutableInteger> map) {
      this.map = map;
   }

   protected Map<E, AbstractMapBag.MutableInteger> getMap() {
      return this.map;
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
      AbstractMapBag.MutableInteger count = this.map.get(object);
      return count != null ? count.value : 0;
   }

   @Override
   public boolean contains(Object object) {
      return this.map.containsKey(object);
   }

   @Override
   public boolean containsAll(Collection<?> coll) {
      return coll instanceof Bag ? this.containsAll((Bag<?>)coll) : this.containsAll(new HashBag<>(coll));
   }

   boolean containsAll(Bag<?> other) {
      for (Object current : other.uniqueSet()) {
         if (this.getCount(current) < other.getCount(current)) {
            return false;
         }
      }

      return true;
   }

   @Override
   public Iterator<E> iterator() {
      return new AbstractMapBag.BagIterator<>(this);
   }

   @Override
   public boolean add(E object) {
      return this.add(object, 1);
   }

   @Override
   public boolean add(E object, int nCopies) {
      this.modCount++;
      if (nCopies > 0) {
         AbstractMapBag.MutableInteger mut = this.map.get(object);
         this.size += nCopies;
         if (mut == null) {
            this.map.put(object, new AbstractMapBag.MutableInteger(nCopies));
            return true;
         } else {
            mut.value += nCopies;
            return false;
         }
      } else {
         return false;
      }
   }

   @Override
   public boolean addAll(Collection<? extends E> coll) {
      boolean changed = false;
      Iterator<? extends E> i = coll.iterator();

      while (i.hasNext()) {
         boolean added = this.add((E)i.next());
         changed = changed || added;
      }

      return changed;
   }

   @Override
   public void clear() {
      this.modCount++;
      this.map.clear();
      this.size = 0;
   }

   @Override
   public boolean remove(Object object) {
      AbstractMapBag.MutableInteger mut = this.map.get(object);
      if (mut == null) {
         return false;
      } else {
         this.modCount++;
         this.map.remove(object);
         this.size = this.size - mut.value;
         return true;
      }
   }

   @Override
   public boolean remove(Object object, int nCopies) {
      AbstractMapBag.MutableInteger mut = this.map.get(object);
      if (mut == null) {
         return false;
      } else if (nCopies <= 0) {
         return false;
      } else {
         this.modCount++;
         if (nCopies < mut.value) {
            mut.value -= nCopies;
            this.size -= nCopies;
         } else {
            this.map.remove(object);
            this.size = this.size - mut.value;
         }

         return true;
      }
   }

   @Override
   public boolean removeAll(Collection<?> coll) {
      boolean result = false;
      if (coll != null) {
         Iterator<?> i = coll.iterator();

         while (i.hasNext()) {
            boolean changed = this.remove(i.next(), 1);
            result = result || changed;
         }
      }

      return result;
   }

   @Override
   public boolean retainAll(Collection<?> coll) {
      return coll instanceof Bag ? this.retainAll((Bag<?>)coll) : this.retainAll(new HashBag<>(coll));
   }

   boolean retainAll(Bag<?> other) {
      boolean result = false;
      Bag<E> excess = new HashBag<>();

      for (E current : this.uniqueSet()) {
         int myCount = this.getCount(current);
         int otherCount = other.getCount(current);
         if (1 <= otherCount && otherCount <= myCount) {
            excess.add(current, myCount - otherCount);
         } else {
            excess.add(current, myCount);
         }
      }

      if (!excess.isEmpty()) {
         result = this.removeAll(excess);
      }

      return result;
   }

   @Override
   public Object[] toArray() {
      Object[] result = new Object[this.size()];
      int i = 0;

      for (E current : this.map.keySet()) {
         for (int index = this.getCount(current); index > 0; index--) {
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

      for (E current : this.map.keySet()) {
         for (int index = this.getCount(current); index > 0; index--) {
            array[i++] = (T)current;
         }
      }

      while (i < array.length) {
         array[i++] = null;
      }

      return array;
   }

   @Override
   public Set<E> uniqueSet() {
      if (this.uniqueSet == null) {
         this.uniqueSet = UnmodifiableSet.unmodifiableSet(this.map.keySet());
      }

      return this.uniqueSet;
   }

   protected void doWriteObject(ObjectOutputStream out) throws IOException {
      out.writeInt(this.map.size());

      for (Entry<E, AbstractMapBag.MutableInteger> entry : this.map.entrySet()) {
         out.writeObject(entry.getKey());
         out.writeInt(entry.getValue().value);
      }
   }

   protected void doReadObject(Map<E, AbstractMapBag.MutableInteger> map, ObjectInputStream in) throws IOException, ClassNotFoundException {
      this.map = map;
      int entrySize = in.readInt();

      for (int i = 0; i < entrySize; i++) {
         E obj = (E)in.readObject();
         int count = in.readInt();
         map.put(obj, new AbstractMapBag.MutableInteger(count));
         this.size += count;
      }
   }

   @Override
   public boolean equals(Object object) {
      if (object == this) {
         return true;
      } else if (!(object instanceof Bag)) {
         return false;
      } else {
         Bag<?> other = (Bag<?>)object;
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

      for (Entry<E, AbstractMapBag.MutableInteger> entry : this.map.entrySet()) {
         E element = entry.getKey();
         AbstractMapBag.MutableInteger count = entry.getValue();
         total += (element == null ? 0 : element.hashCode()) ^ count.value;
      }

      return total;
   }

   @Override
   public String toString() {
      if (this.size() == 0) {
         return "[]";
      } else {
         StringBuilder buf = new StringBuilder();
         buf.append('[');
         Iterator<E> it = this.uniqueSet().iterator();

         while (it.hasNext()) {
            Object current = it.next();
            int count = this.getCount(current);
            buf.append(count);
            buf.append(':');
            buf.append(current);
            if (it.hasNext()) {
               buf.append(',');
            }
         }

         buf.append(']');
         return buf.toString();
      }
   }

   static class BagIterator<E> implements Iterator<E> {
      private final AbstractMapBag<E> parent;
      private final Iterator<Entry<E, AbstractMapBag.MutableInteger>> entryIterator;
      private Entry<E, AbstractMapBag.MutableInteger> current;
      private int itemCount;
      private final int mods;
      private boolean canRemove;

      public BagIterator(AbstractMapBag<E> parent) {
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
            AbstractMapBag.MutableInteger mut = this.current.getValue();
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

   protected static class MutableInteger {
      protected int value;

      MutableInteger(int value) {
         this.value = value;
      }

      @Override
      public boolean equals(Object obj) {
         return !(obj instanceof AbstractMapBag.MutableInteger) ? false : ((AbstractMapBag.MutableInteger)obj).value == this.value;
      }

      @Override
      public int hashCode() {
         return this.value;
      }
   }
}
