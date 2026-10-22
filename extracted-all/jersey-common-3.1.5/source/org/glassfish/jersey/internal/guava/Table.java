package org.glassfish.jersey.internal.guava;

import java.util.Map;
import java.util.Set;

public interface Table<R, C, V> {
   boolean contains(Object var1, Object var2);

   boolean containsRow(Object var1);

   boolean containsColumn(Object var1);

   boolean containsValue(Object var1);

   V get(Object var1, Object var2);

   int size();

   @Override
   boolean equals(Object var1);

   @Override
   int hashCode();

   void clear();

   V put(R var1, C var2, V var3);

   void putAll(Table<? extends R, ? extends C, ? extends V> var1);

   V remove(Object var1, Object var2);

   Map<C, V> row(R var1);

   Map<R, V> column(C var1);

   Set<Table.Cell<R, C, V>> cellSet();

   Set<R> rowKeySet();

   Set<C> columnKeySet();

   Map<R, Map<C, V>> rowMap();

   Map<C, Map<R, V>> columnMap();

   public interface Cell<R, C, V> {
      R getRowKey();

      C getColumnKey();

      V getValue();

      @Override
      boolean equals(Object var1);

      @Override
      int hashCode();
   }
}
