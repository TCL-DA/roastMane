package org.apache.commons.collections4;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public interface MultiSet<E> extends Collection<E> {
   int getCount(Object var1);

   int setCount(E var1, int var2);

   @Override
   boolean add(E var1);

   int add(E var1, int var2);

   @Override
   boolean remove(Object var1);

   int remove(Object var1, int var2);

   Set<E> uniqueSet();

   Set<MultiSet.Entry<E>> entrySet();

   @Override
   Iterator<E> iterator();

   @Override
   int size();

   @Override
   boolean containsAll(Collection<?> var1);

   @Override
   boolean removeAll(Collection<?> var1);

   @Override
   boolean retainAll(Collection<?> var1);

   @Override
   boolean equals(Object var1);

   @Override
   int hashCode();

   public interface Entry<E> {
      E getElement();

      int getCount();

      @Override
      boolean equals(Object var1);

      @Override
      int hashCode();
   }
}
