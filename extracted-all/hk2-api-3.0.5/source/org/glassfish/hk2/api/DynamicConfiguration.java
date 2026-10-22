package org.glassfish.hk2.api;

public interface DynamicConfiguration {
   <T> ActiveDescriptor<T> bind(Descriptor var1);

   <T> ActiveDescriptor<T> bind(Descriptor var1, boolean var2);

   FactoryDescriptors bind(FactoryDescriptors var1);

   FactoryDescriptors bind(FactoryDescriptors var1, boolean var2);

   <T> ActiveDescriptor<T> addActiveDescriptor(ActiveDescriptor<T> var1) throws IllegalArgumentException;

   <T> ActiveDescriptor<T> addActiveDescriptor(ActiveDescriptor<T> var1, boolean var2) throws IllegalArgumentException;

   <T> ActiveDescriptor<T> addActiveDescriptor(Class<T> var1) throws MultiException, IllegalArgumentException;

   <T> FactoryDescriptors addActiveFactoryDescriptor(Class<? extends Factory<T>> var1) throws MultiException, IllegalArgumentException;

   void addUnbindFilter(Filter var1) throws IllegalArgumentException;

   void addIdempotentFilter(Filter... var1) throws IllegalArgumentException;

   void registerTwoPhaseResources(TwoPhaseResource... var1);

   void commit() throws MultiException;
}
