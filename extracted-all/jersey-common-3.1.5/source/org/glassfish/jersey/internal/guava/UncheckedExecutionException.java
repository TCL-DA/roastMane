package org.glassfish.jersey.internal.guava;

public class UncheckedExecutionException extends RuntimeException {
   private static final long serialVersionUID = 0L;

   public UncheckedExecutionException(Throwable cause) {
      super(cause);
   }
}
