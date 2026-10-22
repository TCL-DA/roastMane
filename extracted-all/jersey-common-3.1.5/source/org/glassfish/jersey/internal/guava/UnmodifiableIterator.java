package org.glassfish.jersey.internal.guava;

import java.util.Iterator;

public abstract class UnmodifiableIterator<E> implements Iterator<E> {
   UnmodifiableIterator() {
   }

   @Deprecated
   @Override
   public final void remove() {
      throw new UnsupportedOperationException();
   }
}
