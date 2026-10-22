package org.glassfish.jersey.internal.guava;

import java.util.Iterator;

public interface PeekingIterator<E> extends Iterator<E> {
   E peek();

   @Override
   E next();

   @Override
   void remove();
}
