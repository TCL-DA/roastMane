package org.apache.commons.collections4.functors;

import org.apache.commons.collections4.Closure;
import org.apache.commons.collections4.FunctorException;

public abstract class CatchAndRethrowClosure<E> implements Closure<E> {
   @Override
   public void execute(E input) {
      try {
         this.executeAndThrow(input);
      } catch (RuntimeException var3) {
         throw var3;
      } catch (Throwable var4) {
         throw new FunctorException(var4);
      }
   }

   protected abstract void executeAndThrow(E var1) throws Throwable;
}
