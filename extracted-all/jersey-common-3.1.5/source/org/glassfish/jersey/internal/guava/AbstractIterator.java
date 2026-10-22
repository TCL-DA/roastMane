package org.glassfish.jersey.internal.guava;

import java.util.NoSuchElementException;

public abstract class AbstractIterator<T> extends UnmodifiableIterator<T> {
   private AbstractIterator.State state = AbstractIterator.State.NOT_READY;
   private T next;

   AbstractIterator() {
   }

   protected abstract T computeNext();

   final T endOfData() {
      this.state = AbstractIterator.State.DONE;
      return null;
   }

   @Override
   public final boolean hasNext() {
      Preconditions.checkState(this.state != AbstractIterator.State.FAILED);
      switch (this.state) {
         case DONE:
            return false;
         case READY:
            return true;
         default:
            return this.tryToComputeNext();
      }
   }

   private boolean tryToComputeNext() {
      this.state = AbstractIterator.State.FAILED;
      this.next = this.computeNext();
      if (this.state != AbstractIterator.State.DONE) {
         this.state = AbstractIterator.State.READY;
         return true;
      } else {
         return false;
      }
   }

   @Override
   public final T next() {
      if (!this.hasNext()) {
         throw new NoSuchElementException();
      } else {
         this.state = AbstractIterator.State.NOT_READY;
         T result = this.next;
         this.next = null;
         return result;
      }
   }

   private static enum State {
      READY,
      NOT_READY,
      DONE,
      FAILED;
   }
}
