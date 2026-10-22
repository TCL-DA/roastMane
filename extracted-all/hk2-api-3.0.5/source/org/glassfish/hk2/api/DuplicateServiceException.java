package org.glassfish.hk2.api;

public class DuplicateServiceException extends HK2RuntimeException {
   private static final long serialVersionUID = 7182947621027566487L;
   private Descriptor existingDescriptor;
   private String serviceLocatorName;

   public DuplicateServiceException() {
   }

   public DuplicateServiceException(Descriptor existingDescriptor) {
      this.existingDescriptor = existingDescriptor;
   }

   public DuplicateServiceException(Descriptor existingDescriptor, String serviceLocatorName) {
      this.existingDescriptor = existingDescriptor;
      this.serviceLocatorName = serviceLocatorName;
   }

   public Descriptor getExistingDescriptor() {
      return this.existingDescriptor;
   }

   @Override
   public String toString() {
      String result = "DuplicateServiceException(" + this.existingDescriptor;
      if (this.serviceLocatorName != null) {
         result = result + ", locator=" + this.serviceLocatorName;
      }

      return result + "," + System.identityHashCode(this) + ")";
   }
}
