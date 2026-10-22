package org.apache.commons.collections4;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public interface Bag<E> extends Collection<E> {
   int getCount(Object var1);

   @Override
   boolean add(E var1);

   boolean add(E var1, int var2);

   @Override
   boolean remove(Object var1);

   boolean remove(Object var1, int var2);

   Set<E> uniqueSet();

   @Override
   int size();

   @Override
   boolean containsAll(Collection<?> var1);

   @Override
   boolean removeAll(Collection<?> var1);

   @Override
   boolean retainAll(Collection<?> var1);

   @Override
   Iterator<E> iterator();
}
