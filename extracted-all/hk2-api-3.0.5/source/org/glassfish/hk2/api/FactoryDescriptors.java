package org.glassfish.hk2.api;

public interface FactoryDescriptors {
   Descriptor getFactoryAsAService();

   Descriptor getFactoryAsAFactory();
}
