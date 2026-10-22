package org.glassfish.jersey.internal.inject;

import java.lang.reflect.Type;
import java.util.Objects;
import java.util.Set;

public class ServiceHolderImpl<T> implements ServiceHolder<T> {
   private final T service;
   private final Class<T> implementationClass;
   private final Set<Type> contractTypes;
   private final int rank;

   public ServiceHolderImpl(T service, Set<Type> contractTypes) {
      this(service, (Class<T>)service.getClass(), contractTypes, 0);
   }

   public ServiceHolderImpl(T service, Class<T> implementationClass, Set<Type> contractTypes, int rank) {
      this.service = service;
      this.implementationClass = implementationClass;
      this.contractTypes = contractTypes;
      this.rank = rank;
   }

   @Override
   public T getInstance() {
      return this.service;
   }

   @Override
   public Class<T> getImplementationClass() {
      return this.implementationClass;
   }

   @Override
   public Set<Type> getContractTypes() {
      return this.contractTypes;
   }

   @Override
   public int getRank() {
      return this.rank;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (!(o instanceof ServiceHolderImpl)) {
         return false;
      } else {
         ServiceHolderImpl<?> that = (ServiceHolderImpl<?>)o;
         return this.rank == that.rank
            && Objects.equals(this.service, that.service)
            && Objects.equals(this.implementationClass, that.implementationClass)
            && Objects.equals(this.contractTypes, that.contractTypes);
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.service, this.implementationClass, this.contractTypes, this.rank);
   }
}
