package org.glassfish.hk2.api;

public class UnsatisfiedDependencyException extends HK2RuntimeException {
   private static final long serialVersionUID = 1191047707346290567L;
   private final transient Injectee injectionPoint;

   public UnsatisfiedDependencyException() {
      this(null);
   }

   public UnsatisfiedDependencyException(Injectee injectee) {
      super("There was no object available for injection at " + (injectee == null ? "<null>" : injectee.toString()));
      this.injectionPoint = injectee;
   }

   public UnsatisfiedDependencyException(Injectee injectee, String serviceLocatorName) {
      super(
         "There was no object available in "
            + (serviceLocatorName == null ? "" : serviceLocatorName)
            + " for injection at "
            + (injectee == null ? "<null>" : injectee.toString())
      );
      this.injectionPoint = injectee;
   }

   public Injectee getInjectee() {
      return this.injectionPoint;
   }
}
