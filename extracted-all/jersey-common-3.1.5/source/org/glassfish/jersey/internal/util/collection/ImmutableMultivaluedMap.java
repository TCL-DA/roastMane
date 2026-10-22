package org.glassfish.jersey.internal.util.collection;

import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class ImmutableMultivaluedMap<K, V> implements MultivaluedMap<K, V> {
   private final MultivaluedMap<K, V> delegate;

   public static <K, V> ImmutableMultivaluedMap<K, V> empty() {
      return new ImmutableMultivaluedMap<>(new MultivaluedHashMap());
   }

   public ImmutableMultivaluedMap(MultivaluedMap<K, V> delegate) {
      if (delegate == null) {
         throw new NullPointerException("ImmutableMultivaluedMap delegate must not be 'null'.");
      } else {
         this.delegate = delegate;
      }
   }

   public boolean equalsIgnoreValueOrder(MultivaluedMap<K, V> otherMap) {
      return this.delegate.equalsIgnoreValueOrder(otherMap);
   }

   public void putSingle(K key, V value) {
      throw new UnsupportedOperationException("This MultivaluedMap implementation is immutable.");
   }

   public void add(K key, V value) {
      throw new UnsupportedOperationException("This MultivaluedMap implementation is immutable.");
   }

   public V getFirst(K key) {
      return (V)this.delegate.getFirst(key);
   }

   public void addAll(K key, V... newValues) {
      throw new UnsupportedOperationException("This MultivaluedMap implementation is immutable.");
   }

   public void addAll(K key, List<V> valueList) {
      throw new UnsupportedOperationException("This MultivaluedMap implementation is immutable.");
   }

   public void addFirst(K key, V value) {
      throw new UnsupportedOperationException("This MultivaluedMap implementation is immutable.");
   }

   public int size() {
      return this.delegate.size();
   }

   public boolean isEmpty() {
      return this.delegate.isEmpty();
   }

   public boolean containsKey(Object key) {
      return this.delegate.containsKey(key);
   }

   public boolean containsValue(Object value) {
      return this.delegate.containsValue(value);
   }

   public List<V> get(Object key) {
      return (List<V>)this.delegate.get(key);
   }

   public List<V> put(K key, List<V> value) {
      throw new UnsupportedOperationException("This MultivaluedMap implementation is immutable.");
   }

   public List<V> remove(Object key) {
      throw new UnsupportedOperationException("This MultivaluedMap implementation is immutable.");
   }

   public void putAll(Map<? extends K, ? extends List<V>> m) {
      throw new UnsupportedOperationException("This MultivaluedMap implementation is immutable.");
   }

   public void clear() {
      throw new UnsupportedOperationException("This MultivaluedMap implementation is immutable.");
   }

   public Set<K> keySet() {
      return Collections.unmodifiableSet(this.delegate.keySet());
   }

   public Collection<List<V>> values() {
      return Collections.unmodifiableCollection(this.delegate.values());
   }

   public Set<Entry<K, List<V>>> entrySet() {
      return Collections.unmodifiableSet(this.delegate.entrySet());
   }

   @Override
   public String toString() {
      return this.delegate.toString();
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (!(o instanceof ImmutableMultivaluedMap)) {
         return false;
      } else {
         ImmutableMultivaluedMap that = (ImmutableMultivaluedMap)o;
         return this.delegate.equals(that.delegate);
      }
   }

   @Override
   public int hashCode() {
      return this.delegate.hashCode();
   }
}
