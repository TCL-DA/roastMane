package org.glassfish.hk2.utilities;

import jakarta.inject.Named;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.Descriptor;
import org.glassfish.hk2.api.DescriptorType;
import org.glassfish.hk2.api.DescriptorVisibility;
import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.utilities.reflection.ReflectionHelper;

public abstract class AbstractActiveDescriptor<T> extends DescriptorImpl implements ActiveDescriptor<T> {
   private static final long serialVersionUID = 7080312303893604939L;
   private static final Set<Annotation> EMPTY_QUALIFIER_SET = Collections.emptySet();
   private Set<Type> advertisedContracts = new LinkedHashSet<>();
   private Annotation scopeAnnotation;
   private Class<? extends Annotation> scope;
   private Set<Annotation> qualifiers;
   private Long factoryServiceId;
   private Long factoryLocatorId;
   private boolean isReified = true;
   private transient boolean cacheSet = false;
   private transient T cachedValue;
   private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
   private final Lock rLock = this.rwLock.readLock();
   private final Lock wLock = this.rwLock.writeLock();

   public AbstractActiveDescriptor() {
      this.scope = null;
   }

   protected AbstractActiveDescriptor(Descriptor baseDescriptor) {
      super(baseDescriptor);
      this.isReified = false;
      this.scope = null;
   }

   protected AbstractActiveDescriptor(
      Set<Type> advertisedContracts,
      Class<? extends Annotation> scope,
      String name,
      Set<Annotation> qualifiers,
      DescriptorType descriptorType,
      DescriptorVisibility descriptorVisibility,
      int ranking,
      Boolean proxy,
      Boolean proxyForSameScope,
      String analyzerName,
      Map<String, List<String>> metadata
   ) {
      this.scope = scope;
      this.advertisedContracts.addAll(advertisedContracts);
      if (qualifiers != null && !qualifiers.isEmpty()) {
         this.qualifiers = new LinkedHashSet<>();
         this.qualifiers.addAll(qualifiers);
      }

      this.setRanking(ranking);
      this.setDescriptorType(descriptorType);
      this.setDescriptorVisibility(descriptorVisibility);
      this.setName(name);
      this.setProxiable(proxy);
      this.setProxyForSameScope(proxyForSameScope);
      if (scope != null) {
         this.setScope(scope.getName());
      }

      for (Type t : advertisedContracts) {
         Class<?> raw = ReflectionHelper.getRawClass(t);
         if (raw != null) {
            this.addAdvertisedContract(raw.getName());
         }
      }

      if (qualifiers != null) {
         for (Annotation q : qualifiers) {
            this.addQualifier(q.annotationType().getName());
         }
      }

      this.setClassAnalysisName(analyzerName);
      if (metadata != null) {
         for (Entry<String, List<String>> entry : metadata.entrySet()) {
            String key = entry.getKey();

            for (String value : entry.getValue()) {
               this.addMetadata(key, value);
            }
         }
      }
   }

   private void removeNamedQualifier() {
      try {
         this.wLock.lock();
         if (this.qualifiers == null) {
            return;
         }

         Iterator var1 = this.qualifiers.iterator();

         Annotation qualifier;
         do {
            if (!var1.hasNext()) {
               return;
            }

            qualifier = (Annotation)var1.next();
         } while (!qualifier.annotationType().equals(Named.class));

         this.removeQualifierAnnotation(qualifier);
      } finally {
         this.wLock.unlock();
      }
   }

   public void setImplementationType(Type t) {
      throw new AssertionError("Can not set type of " + this.getClass().getName() + " descriptor");
   }

   @Override
   public void setName(String name) {
      try {
         this.wLock.lock();
         super.setName(name);
         this.removeNamedQualifier();
         if (name != null) {
            this.addQualifierAnnotation(new NamedImpl(name));
            return;
         }
      } finally {
         this.wLock.unlock();
      }
   }

   @Override
   public T getCache() {
      Object var1;
      try {
         this.rLock.lock();
         var1 = this.cachedValue;
      } finally {
         this.rLock.unlock();
      }

      return (T)var1;
   }

   @Override
   public boolean isCacheSet() {
      boolean var1;
      try {
         this.rLock.lock();
         var1 = this.cacheSet;
      } finally {
         this.rLock.unlock();
      }

      return var1;
   }

   @Override
   public void setCache(T cacheMe) {
      try {
         this.wLock.lock();
         this.cachedValue = cacheMe;
         this.cacheSet = true;
      } finally {
         this.wLock.unlock();
      }
   }

   @Override
   public void releaseCache() {
      try {
         this.wLock.lock();
         this.cacheSet = false;
         this.cachedValue = null;
      } finally {
         this.wLock.unlock();
      }
   }

   @Override
   public boolean isReified() {
      boolean var1;
      try {
         this.rLock.lock();
         var1 = this.isReified;
      } finally {
         this.rLock.unlock();
      }

      return var1;
   }

   public void setReified(boolean reified) {
      try {
         this.wLock.lock();
         this.isReified = reified;
      } finally {
         this.wLock.unlock();
      }
   }

   @Override
   public Set<Type> getContractTypes() {
      Set var1;
      try {
         this.rLock.lock();
         var1 = Collections.unmodifiableSet(this.advertisedContracts);
      } finally {
         this.rLock.unlock();
      }

      return var1;
   }

   public void addContractType(Type addMe) {
      try {
         this.wLock.lock();
         if (addMe == null) {
            return;
         }

         this.advertisedContracts.add(addMe);
         Class<?> rawClass = ReflectionHelper.getRawClass(addMe);
         if (rawClass != null) {
            this.addAdvertisedContract(rawClass.getName());
            return;
         }
      } finally {
         this.wLock.unlock();
      }
   }

   public boolean removeContractType(Type removeMe) {
      boolean var4;
      try {
         this.wLock.lock();
         if (removeMe == null) {
            return false;
         }

         boolean retVal = this.advertisedContracts.remove(removeMe);
         Class<?> rawClass = ReflectionHelper.getRawClass(removeMe);
         if (rawClass != null) {
            return this.removeAdvertisedContract(rawClass.getName());
         }

         var4 = retVal;
      } finally {
         this.wLock.unlock();
      }

      return var4;
   }

   @Override
   public Annotation getScopeAsAnnotation() {
      return this.scopeAnnotation;
   }

   public void setScopeAsAnnotation(Annotation scopeAnnotation) {
      this.scopeAnnotation = scopeAnnotation;
      if (scopeAnnotation != null) {
         this.setScopeAnnotation(scopeAnnotation.annotationType());
      }
   }

   @Override
   public Class<? extends Annotation> getScopeAnnotation() {
      return this.scope;
   }

   public void setScopeAnnotation(Class<? extends Annotation> scopeAnnotation) {
      this.scope = scopeAnnotation;
      this.setScope(this.scope.getName());
   }

   @Override
   public Set<Annotation> getQualifierAnnotations() {
      Set var1;
      try {
         this.rLock.lock();
         if (this.qualifiers != null) {
            return Collections.unmodifiableSet(this.qualifiers);
         }

         var1 = EMPTY_QUALIFIER_SET;
      } finally {
         this.rLock.unlock();
      }

      return var1;
   }

   public void addQualifierAnnotation(Annotation addMe) {
      try {
         this.wLock.lock();
         if (addMe != null) {
            if (this.qualifiers == null) {
               this.qualifiers = new LinkedHashSet<>();
            }

            this.qualifiers.add(addMe);
            this.addQualifier(addMe.annotationType().getName());
            return;
         }
      } finally {
         this.wLock.unlock();
      }
   }

   public boolean removeQualifierAnnotation(Annotation removeMe) {
      boolean retVal;
      try {
         this.wLock.lock();
         if (removeMe == null) {
            return false;
         }

         if (this.qualifiers != null) {
            retVal = this.qualifiers.remove(removeMe);
            this.removeQualifier(removeMe.annotationType().getName());
            return retVal;
         }

         retVal = false;
      } finally {
         this.wLock.unlock();
      }

      return retVal;
   }

   @Override
   public Long getFactoryServiceId() {
      return this.factoryServiceId;
   }

   @Override
   public Long getFactoryLocatorId() {
      return this.factoryLocatorId;
   }

   public void setFactoryId(Long locatorId, Long serviceId) {
      if (!this.getDescriptorType().equals(DescriptorType.PROVIDE_METHOD)) {
         throw new IllegalStateException("The descriptor type must be PROVIDE_METHOD");
      } else {
         this.factoryServiceId = serviceId;
         this.factoryLocatorId = locatorId;
      }
   }

   @Override
   public List<Injectee> getInjectees() {
      return Collections.emptyList();
   }

   @Override
   public void dispose(T instance) {
   }

   @Override
   public int hashCode() {
      return super.hashCode();
   }

   @Override
   public boolean equals(Object o) {
      return super.equals(o);
   }
}
