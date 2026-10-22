package org.glassfish.hk2.utilities.cache;

public class ComputationErrorException extends RuntimeException {
   private static final long serialVersionUID = 1186268368624844657L;
   public Object computation;

   public ComputationErrorException() {
   }

   public ComputationErrorException(Object computation) {
      this.computation = computation;
   }

   public Object getComputation() {
      return this.computation;
   }
}
