package org.glassfish.jersey.internal.guava;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class HashBasedTable<R, C, V> extends StandardTable<R, C, V> {
   private static final long serialVersionUID = 0L;

   private HashBasedTable(Map<R, Map<C, V>> backingMap, HashBasedTable.Factory<C, V> factory) {
      super(backingMap, factory);
   }

   public static <R, C, V> HashBasedTable<R, C, V> create() {
      return new HashBasedTable<>(new HashMap<>(), new HashBasedTable.Factory<>(0));
   }

   @Override
   public boolean contains(Object rowKey, Object columnKey) {
      return super.contains(rowKey, columnKey);
   }

   @Override
   public boolean containsColumn(Object columnKey) {
      return super.containsColumn(columnKey);
   }

   @Override
   public boolean containsValue(Object value) {
      return super.containsValue(value);
   }

   @Override
   public V get(Object rowKey, Object columnKey) {
      return super.get(rowKey, columnKey);
   }

   @Override
   public boolean equals(Object obj) {
      return super.equals(obj);
   }

   private static class Factory<C, V> implements Supplier<Map<C, V>>, Serializable {
      private static final long serialVersionUID = 0L;
      final int expectedSize;

      Factory(int expectedSize) {
         this.expectedSize = expectedSize;
      }

      public Map<C, V> get() {
         return Maps.newHashMapWithExpectedSize(this.expectedSize);
      }
   }
}
