package org.glassfish.hk2.utilities;

import org.glassfish.hk2.api.Descriptor;
import org.glassfish.hk2.api.DescriptorType;
import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.api.FactoryDescriptors;

public class FactoryDescriptorsImpl implements FactoryDescriptors {
   private final Descriptor asService;
   private final Descriptor asProvideMethod;

   public FactoryDescriptorsImpl(Descriptor asService, Descriptor asProvideMethod) {
      if (asService != null && asProvideMethod != null) {
         if (!DescriptorType.CLASS.equals(asService.getDescriptorType())) {
            throw new IllegalArgumentException("Creation of FactoryDescriptors must have first argument of type CLASS");
         } else if (!asService.getAdvertisedContracts().contains(Factory.class.getName())) {
            throw new IllegalArgumentException("Creation of FactoryDescriptors must have Factory as a contract of the first argument");
         } else if (!DescriptorType.PROVIDE_METHOD.equals(asProvideMethod.getDescriptorType())) {
            throw new IllegalArgumentException("Creation of FactoryDescriptors must have second argument of type PROVIDE_METHOD");
         } else {
            this.asService = asService;
            this.asProvideMethod = asProvideMethod;
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public Descriptor getFactoryAsAService() {
      return this.asService;
   }

   @Override
   public Descriptor getFactoryAsAFactory() {
      return this.asProvideMethod;
   }

   @Override
   public int hashCode() {
      return this.asService.hashCode() ^ this.asProvideMethod.hashCode();
   }

   @Override
   public boolean equals(Object o) {
      if (o == null) {
         return false;
      } else if (!(o instanceof FactoryDescriptors)) {
         return false;
      } else {
         FactoryDescriptors other = (FactoryDescriptors)o;
         Descriptor otherService = other.getFactoryAsAService();
         Descriptor otherFactory = other.getFactoryAsAFactory();
         return otherService != null && otherFactory != null ? this.asService.equals(otherService) && this.asProvideMethod.equals(otherFactory) : false;
      }
   }

   @Override
   public String toString() {
      return "FactoryDescriptorsImpl(\n" + this.asService + ",\n" + this.asProvideMethod + ",\n\t" + System.identityHashCode(this) + ")";
   }
}
