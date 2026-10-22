package org.glassfish.jersey.internal.util.collection;

import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import java.util.List;

public class NullableMultivaluedHashMap<K, V> extends MultivaluedHashMap<K, V> {
   public NullableMultivaluedHashMap() {
   }

   public NullableMultivaluedHashMap(int initialCapacity) {
      super(initialCapacity);
   }

   public NullableMultivaluedHashMap(int initialCapacity, float loadFactor) {
      super(initialCapacity, loadFactor);
   }

   public NullableMultivaluedHashMap(MultivaluedMap<? extends K, ? extends V> map) {
      super(map);
   }

   protected void addFirstNull(List<V> values) {
      values.add(null);
   }

   protected void addNull(List<V> values) {
      values.add(null);
   }
}
