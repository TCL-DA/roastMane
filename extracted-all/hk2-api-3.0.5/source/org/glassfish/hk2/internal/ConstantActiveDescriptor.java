package org.glassfish.hk2.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.glassfish.hk2.api.DescriptorType;
import org.glassfish.hk2.api.DescriptorVisibility;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.utilities.AbstractActiveDescriptor;

public class ConstantActiveDescriptor<T> extends AbstractActiveDescriptor<T> {
   private static final long serialVersionUID = -9196390718074767455L;
   private final T theOne;

   public ConstantActiveDescriptor() {
      this.theOne = null;
   }

   public ConstantActiveDescriptor(
      T theOne,
      Set<Type> advertisedContracts,
      Class<? extends Annotation> scope,
      String name,
      Set<Annotation> qualifiers,
      DescriptorVisibility descriptorVisibility,
      Boolean proxy,
      Boolean proxyForSameScope,
      String classAnalysisName,
      Map<String, List<String>> metadata,
      int rank
   ) {
      super(
         advertisedContracts, scope, name, qualifiers, DescriptorType.CLASS, descriptorVisibility, rank, proxy, proxyForSameScope, classAnalysisName, metadata
      );
      if (theOne == null) {
         throw new IllegalArgumentException();
      } else {
         this.theOne = theOne;
      }
   }

   @Override
   public String getImplementation() {
      return this.theOne.getClass().getName();
   }

   @Override
   public T getCache() {
      return this.theOne;
   }

   @Override
   public boolean isCacheSet() {
      return true;
   }

   @Override
   public Class<?> getImplementationClass() {
      return this.theOne.getClass();
   }

   @Override
   public Type getImplementationType() {
      return this.theOne.getClass();
   }

   @Override
   public T create(ServiceHandle<?> root) {
      return this.theOne;
   }
}
