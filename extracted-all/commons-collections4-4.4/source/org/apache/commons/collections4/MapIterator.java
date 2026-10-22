package org.apache.commons.collections4;

import java.util.Iterator;

public interface MapIterator<K, V> extends Iterator<K> {
   @Override
   boolean hasNext();

   @Override
   K next();

   K getKey();

   V getValue();

   @Override
   void remove();

   V setValue(V var1);
}
