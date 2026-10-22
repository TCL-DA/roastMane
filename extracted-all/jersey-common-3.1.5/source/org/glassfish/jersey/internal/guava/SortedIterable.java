package org.glassfish.jersey.internal.guava;

import java.util.Comparator;
import java.util.Iterator;

interface SortedIterable<T> extends Iterable<T> {
   Comparator<? super T> comparator();

   @Override
   Iterator<T> iterator();
}
