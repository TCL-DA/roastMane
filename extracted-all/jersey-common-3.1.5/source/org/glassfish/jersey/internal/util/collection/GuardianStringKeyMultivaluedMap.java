package org.glassfish.jersey.internal.util.collection;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Consumer;

public class GuardianStringKeyMultivaluedMap<V> implements MultivaluedMap<String, V> {
   private final MultivaluedMap<String, V> inner;
   private final Map<String, Boolean> guards = new HashMap<>();

   private static boolean isMutable(Object mutable) {
      return !String.class.isInstance(mutable) && !MediaType.class.isInstance(mutable);
   }

   public GuardianStringKeyMultivaluedMap(MultivaluedMap<String, V> inner) {
      this.inner = inner;
   }

   public void putSingle(String key, V value) {
      this.observe(key);
      this.inner.putSingle(key, value);
   }

   public void add(String key, V value) {
      this.observe(key);
      this.inner.add(key, value);
   }

   public V getFirst(String key) {
      V first = (V)this.inner.getFirst(key);
      if (isMutable(key)) {
         this.observe(key);
      }

      return first;
   }

   public void addAll(String key, V... newValues) {
      this.observe(key);
      this.inner.addAll(key, newValues);
   }

   public void addAll(String key, List<V> valueList) {
      this.observe(key);
      this.inner.addAll(key, valueList);
   }

   public void addFirst(String key, V value) {
      this.observe(key);
      this.inner.addFirst(key, value);
   }

   public boolean equalsIgnoreValueOrder(MultivaluedMap<String, V> otherMap) {
      return this.inner.equalsIgnoreValueOrder(otherMap);
   }

   public int size() {
      return this.inner.size();
   }

   public boolean isEmpty() {
      return this.inner.isEmpty();
   }

   public boolean containsKey(Object key) {
      return this.inner.containsKey(key);
   }

   public boolean containsValue(Object value) {
      return this.inner.containsValue(value);
   }

   public List<V> get(Object key) {
      List<V> innerList = (List<V>)this.inner.get(key);
      if (innerList != null) {
         for (Entry<String, Boolean> guard : this.guards.entrySet()) {
            if (guard.getKey().equals(key)) {
               return new GuardianStringKeyMultivaluedMap.GuardianList<>(innerList, guard);
            }
         }
      }

      return innerList;
   }

   public List<V> put(String key, List<V> value) {
      this.observe(key);
      return (List<V>)this.inner.put(key, value);
   }

   public List<V> remove(Object key) {
      if (key != null) {
         this.observe(key.toString());
      }

      return (List<V>)this.inner.remove(key);
   }

   public void putAll(Map<? extends String, ? extends List<V>> m) {
      for (String key : m.keySet()) {
         this.observe(key);
      }

      this.inner.putAll(m);
   }

   public void clear() {
      this.observeAll();
      this.inner.clear();
   }

   public Set<String> keySet() {
      return this.inner.keySet();
   }

   public Collection<List<V>> values() {
      this.observeAll();
      return this.inner.values();
   }

   public Set<Entry<String, List<V>>> entrySet() {
      this.observeAll();
      return this.inner.entrySet();
   }

   public void setGuard(String key) {
      this.guards.put(key, false);
   }

   public Set<String> getGuards() {
      return this.guards.keySet();
   }

   public boolean isObservedAndReset(String key) {
      Boolean observed = this.guards.get(key);
      if (observed != null) {
         this.guards.put(key, false);
      }

      return observed != null && observed;
   }

   private void observe(String key) {
      for (Entry<String, Boolean> guard : this.guards.entrySet()) {
         if (guard.getKey().equals(key)) {
            guard.setValue(true);
            break;
         }
      }
   }

   private void observeAll() {
      for (Entry<String, Boolean> guard : this.guards.entrySet()) {
         guard.setValue(true);
      }
   }

   @Override
   public String toString() {
      return this.inner.toString();
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         GuardianStringKeyMultivaluedMap<?> that = (GuardianStringKeyMultivaluedMap<?>)o;
         return this.inner.equals(that.inner) && this.guards.equals(that.guards);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.inner, this.guards);
   }

   private static class GuardianIterator<V> extends GuardianStringKeyMultivaluedMap.MutableGuardian<V> implements Iterator<V> {
      protected final Iterator<V> guarded;

      public GuardianIterator(Iterator<V> guarded, Entry<String, Boolean> guard) {
         super(guard);
         this.guarded = guarded;
      }

      @Override
      public boolean hasNext() {
         return this.guarded.hasNext();
      }

      @Override
      public V next() {
         return this.guardMutable(this.guarded.next());
      }

      @Override
      public void remove() {
         this.guard.setValue(true);
         this.guarded.remove();
      }

      @Override
      public void forEachRemaining(Consumer<? super V> action) {
         this.guarded.forEachRemaining(action);
      }

      @Override
      public String toString() {
         return this.guarded.toString();
      }
   }

   private static class GuardianList<V> extends GuardianStringKeyMultivaluedMap.MutableGuardian<V> implements List<V> {
      private final List<V> guarded;

      public GuardianList(List<V> guarded, Entry<String, Boolean> guard) {
         super(guard);
         this.guarded = guarded;
      }

      @Override
      public int size() {
         return this.guarded.size();
      }

      @Override
      public boolean isEmpty() {
         return this.guarded.isEmpty();
      }

      @Override
      public boolean contains(Object o) {
         return this.guarded.contains(o);
      }

      @Override
      public Iterator<V> iterator() {
         return new GuardianStringKeyMultivaluedMap.GuardianIterator<>(this.guarded.iterator(), this.guard);
      }

      @Override
      public Object[] toArray() {
         this.guard.setValue(true);
         return this.guarded.toArray();
      }

      @Override
      public <T> T[] toArray(T[] a) {
         this.guard.setValue(true);
         return (T[])this.guarded.toArray(a);
      }

      @Override
      public boolean add(V e) {
         this.guard.setValue(true);
         return this.guarded.add(e);
      }

      @Override
      public boolean remove(Object o) {
         this.guard.setValue(true);
         return this.guarded.remove(o);
      }

      @Override
      public boolean containsAll(Collection<?> c) {
         return this.guarded.containsAll(c);
      }

      @Override
      public boolean addAll(Collection<? extends V> c) {
         this.guard.setValue(true);
         return this.guarded.addAll(c);
      }

      @Override
      public boolean addAll(int index, Collection<? extends V> c) {
         this.guard.setValue(true);
         return this.guarded.addAll(index, c);
      }

      @Override
      public boolean removeAll(Collection<?> c) {
         this.guard.setValue(true);
         return this.guarded.removeAll(c);
      }

      @Override
      public boolean retainAll(Collection<?> c) {
         this.guard.setValue(true);
         return this.guarded.retainAll(c);
      }

      @Override
      public void clear() {
         this.guard.setValue(true);
         this.guarded.clear();
      }

      @Override
      public V get(int index) {
         return this.guardMutable(this.guarded.get(index));
      }

      @Override
      public V set(int index, V element) {
         this.guard.setValue(true);
         return this.guarded.set(index, element);
      }

      @Override
      public void add(int index, V element) {
         this.guard.setValue(true);
         this.guarded.add(index, element);
      }

      @Override
      public V remove(int index) {
         this.guard.setValue(true);
         return this.guarded.remove(index);
      }

      @Override
      public int indexOf(Object o) {
         return this.guarded.indexOf(o);
      }

      @Override
      public int lastIndexOf(Object o) {
         return this.guarded.lastIndexOf(o);
      }

      @Override
      public ListIterator<V> listIterator() {
         return new GuardianStringKeyMultivaluedMap.GuardianListIterator<>(this.guarded.listIterator(), this.guard);
      }

      @Override
      public ListIterator<V> listIterator(int index) {
         return new GuardianStringKeyMultivaluedMap.GuardianListIterator<>(this.guarded.listIterator(index), this.guard);
      }

      @Override
      public List<V> subList(int fromIndex, int toIndex) {
         List<V> sublist = this.guarded.subList(fromIndex, toIndex);
         return (List<V>)(sublist != null ? new GuardianStringKeyMultivaluedMap.GuardianList<>(sublist, this.guard) : sublist);
      }

      @Override
      public String toString() {
         return this.guarded.toString();
      }

      @Override
      public boolean equals(Object obj) {
         return GuardianStringKeyMultivaluedMap.GuardianList.class.isInstance(obj)
            ? this.guarded.equals(((GuardianStringKeyMultivaluedMap.GuardianList)obj).guarded)
            : this.guarded.equals(obj);
      }

      @Override
      public int hashCode() {
         return this.guarded.hashCode();
      }
   }

   private static class GuardianListIterator<V> extends GuardianStringKeyMultivaluedMap.GuardianIterator<V> implements ListIterator<V> {
      public GuardianListIterator(Iterator<V> guarded, Entry<String, Boolean> guard) {
         super(guarded, guard);
      }

      @Override
      public boolean hasPrevious() {
         return ((ListIterator)this.guarded).hasPrevious();
      }

      @Override
      public V previous() {
         return this.guardMutable(((ListIterator)this.guarded).previous());
      }

      @Override
      public int nextIndex() {
         return ((ListIterator)this.guarded).nextIndex();
      }

      @Override
      public int previousIndex() {
         return ((ListIterator)this.guarded).previousIndex();
      }

      @Override
      public void set(V v) {
         ((ListIterator)this.guarded).set(v);
         this.guard.setValue(true);
      }

      @Override
      public void add(V v) {
         ((ListIterator)this.guarded).add(v);
         this.guard.setValue(true);
      }
   }

   private static class MutableGuardian<V> {
      protected final Entry<String, Boolean> guard;

      private MutableGuardian(Entry<String, Boolean> guard) {
         this.guard = guard;
      }

      protected V guardMutable(V mutable) {
         if (GuardianStringKeyMultivaluedMap.isMutable(mutable)) {
            this.guard.setValue(true);
         }

         return mutable;
      }
   }
}
