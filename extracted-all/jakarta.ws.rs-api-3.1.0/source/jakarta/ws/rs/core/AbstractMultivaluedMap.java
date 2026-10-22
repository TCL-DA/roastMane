package jakarta.ws.rs.core;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public abstract class AbstractMultivaluedMap<K, V> implements MultivaluedMap<K, V>, Serializable {
   protected final Map<K, List<V>> store;

   public AbstractMultivaluedMap(Map<K, List<V>> store) {
      if (store == null) {
         throw new NullPointerException("Underlying store must not be 'null'.");
      } else {
         this.store = store;
      }
   }

   @Override
   public final void putSingle(K key, V value) {
      List<V> values = this.getValues(key);
      values.clear();
      if (value != null) {
         values.add(value);
      } else {
         this.addNull(values);
      }
   }

   protected void addNull(List<V> values) {
   }

   protected void addFirstNull(List<V> values) {
   }

   @Override
   public final void add(K key, V value) {
      List<V> values = this.getValues(key);
      if (value != null) {
         values.add(value);
      } else {
         this.addNull(values);
      }
   }

   @Override
   public final void addAll(K key, V... newValues) {
      if (newValues == null) {
         throw new NullPointerException("Supplied array of values must not be null.");
      } else if (newValues.length != 0) {
         List<V> values = this.getValues(key);

         for (V value : newValues) {
            if (value != null) {
               values.add(value);
            } else {
               this.addNull(values);
            }
         }
      }
   }

   @Override
   public final void addAll(K key, List<V> valueList) {
      if (valueList == null) {
         throw new NullPointerException("Supplied list of values must not be null.");
      } else if (!valueList.isEmpty()) {
         List<V> values = this.getValues(key);

         for (V value : valueList) {
            if (value != null) {
               values.add(value);
            } else {
               this.addNull(values);
            }
         }
      }
   }

   @Override
   public final V getFirst(K key) {
      List<V> values = this.store.get(key);
      return values != null && values.size() > 0 ? values.get(0) : null;
   }

   @Override
   public final void addFirst(K key, V value) {
      List<V> values = this.getValues(key);
      if (value != null) {
         values.add(0, value);
      } else {
         this.addFirstNull(values);
      }
   }

   protected final List<V> getValues(K key) {
      List<V> l = this.store.get(key);
      if (l == null) {
         l = new LinkedList<>();
         this.store.put(key, l);
      }

      return l;
   }

   @Override
   public String toString() {
      return this.store.toString();
   }

   @Override
   public int hashCode() {
      return this.store.hashCode();
   }

   @Override
   public boolean equals(Object o) {
      return this.store.equals(o);
   }

   @Override
   public Collection<List<V>> values() {
      return this.store.values();
   }

   @Override
   public int size() {
      return this.store.size();
   }

   public List<V> remove(Object key) {
      return this.store.remove(key);
   }

   @Override
   public void putAll(Map<? extends K, ? extends List<V>> m) {
      this.store.putAll(m);
   }

   public List<V> put(K key, List<V> value) {
      return this.store.put(key, value);
   }

   @Override
   public Set<K> keySet() {
      return this.store.keySet();
   }

   @Override
   public boolean isEmpty() {
      return this.store.isEmpty();
   }

   public List<V> get(Object key) {
      return this.store.get(key);
   }

   @Override
   public Set<Entry<K, List<V>>> entrySet() {
      return this.store.entrySet();
   }

   @Override
   public boolean containsValue(Object value) {
      return this.store.containsValue(value);
   }

   @Override
   public boolean containsKey(Object key) {
      return this.store.containsKey(key);
   }

   @Override
   public void clear() {
      this.store.clear();
   }

   @Override
   public boolean equalsIgnoreValueOrder(MultivaluedMap<K, V> omap) {
      if (this == omap) {
         return true;
      } else if (!this.keySet().equals(omap.keySet())) {
         return false;
      } else {
         for (Entry<K, List<V>> e : this.entrySet()) {
            List<V> olist = omap.get(e.getKey());
            if (e.getValue().size() != olist.size()) {
               return false;
            }

            for (V v : e.getValue()) {
               if (!olist.contains(v)) {
                  return false;
               }
            }
         }

         return true;
      }
   }
}
