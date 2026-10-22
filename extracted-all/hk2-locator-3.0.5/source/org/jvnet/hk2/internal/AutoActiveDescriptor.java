package org.jvnet.hk2.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.DescriptorType;
import org.glassfish.hk2.api.DescriptorVisibility;
import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.utilities.AbstractActiveDescriptor;

public class AutoActiveDescriptor<T> extends AbstractActiveDescriptor<T> {
   private static final long serialVersionUID = -7921574114250721537L;
   private Class<?> implClass;
   private Creator<T> creator;
   private SystemDescriptor<?> hk2Parent;
   private Type implType;

   public AutoActiveDescriptor() {
   }

   public AutoActiveDescriptor(
      Class<?> clazz,
      Creator<T> creator,
      Set<Type> advertisedContracts,
      Class<? extends Annotation> scope,
      String name,
      Set<Annotation> qualifiers,
      DescriptorVisibility descriptorVisibility,
      int ranking,
      Boolean proxy,
      Boolean proxyForSameScope,
      String classAnalysisName,
      Map<String, List<String>> metadata,
      DescriptorType descriptorType,
      Type clazzType
   ) {
      super(
         advertisedContracts,
         scope,
         name,
         qualifiers,
         DescriptorType.CLASS,
         descriptorVisibility,
         ranking,
         proxy,
         proxyForSameScope,
         classAnalysisName,
         metadata
      );
      this.implClass = clazz;
      this.creator = creator;
      this.setImplementation(this.implClass.getName());
      this.setDescriptorType(descriptorType);
      if (clazzType == null) {
         this.implType = clazz;
      } else {
         this.implType = clazzType;
      }
   }

   void resetSelfDescriptor(ActiveDescriptor<?> toMe) {
      if (this.creator instanceof ClazzCreator) {
         ClazzCreator<?> cc = (ClazzCreator<?>)this.creator;
         cc.resetSelfDescriptor(toMe);
      }
   }

   void setHK2Parent(SystemDescriptor<?> hk2Parent) {
      this.hk2Parent = hk2Parent;
   }

   public Class<?> getImplementationClass() {
      return this.implClass;
   }

   public Type getImplementationType() {
      return this.implType;
   }

   public void setImplementationType(Type t) {
      this.implType = t;
   }

   public T create(ServiceHandle<?> root) {
      return this.creator.create(root, this.hk2Parent);
   }

   public void dispose(T instance) {
      this.creator.dispose(instance);
   }

   public List<Injectee> getInjectees() {
      return this.creator.getInjectees();
   }
}
