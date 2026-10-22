package org.jvnet.hk2.internal;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.aopalliance.intercept.ConstructorInterceptor;
import org.aopalliance.intercept.MethodInterceptor;
import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.ClassAnalyzer;
import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.InjectionResolver;
import org.glassfish.hk2.api.InstanceLifecycleEventType;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.PostConstruct;
import org.glassfish.hk2.api.PreDestroy;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.utilities.reflection.ReflectionHelper;

public class ClazzCreator<T> implements Creator<T> {
   private final ServiceLocatorImpl locator;
   private final Class<?> implClass;
   private final Set<ClazzCreator.ResolutionInfo> myInitializers = new LinkedHashSet<>();
   private final Set<ClazzCreator.ResolutionInfo> superInitializers = new LinkedHashSet<>();
   private final Set<ClazzCreator.ResolutionInfo> myFields = new LinkedHashSet<>();
   private final Set<ClazzCreator.ResolutionInfo> superFields = new LinkedHashSet<>();
   private ActiveDescriptor<?> selfDescriptor;
   private ClazzCreator.ResolutionInfo myConstructor;
   private List<SystemInjecteeImpl> allInjectees;
   private Method postConstructMethod;
   private Method preDestroyMethod;

   ClazzCreator(ServiceLocatorImpl locator, Class<?> implClass) {
      this.locator = locator;
      this.implClass = implClass;
   }

   void initialize(ActiveDescriptor<?> selfDescriptor, String analyzerName, Collector collector) {
      this.selfDescriptor = selfDescriptor;
      if (selfDescriptor != null && selfDescriptor.getAdvertisedContracts().contains(ClassAnalyzer.class.getName())) {
         String descriptorAnalyzerName = selfDescriptor.getName();
         if (descriptorAnalyzerName == null) {
            descriptorAnalyzerName = this.locator.getDefaultClassAnalyzerName();
         }

         String incomingAnalyzerName = analyzerName;
         if (analyzerName == null) {
            incomingAnalyzerName = this.locator.getDefaultClassAnalyzerName();
         }

         if (descriptorAnalyzerName.equals(incomingAnalyzerName)) {
            collector.addThrowable(
               new IllegalArgumentException(
                  "The ClassAnalyzer named "
                     + descriptorAnalyzerName
                     + " is its own ClassAnalyzer. Ensure that an implementation of ClassAnalyzer is not its own ClassAnalyzer"
               )
            );
            this.myConstructor = null;
            return;
         }
      }

      ClassAnalyzer analyzer = Utilities.getClassAnalyzer(this.locator, analyzerName, collector);
      if (analyzer == null) {
         this.myConstructor = null;
      } else {
         List<SystemInjecteeImpl> baseAllInjectees = new LinkedList<>();
         AnnotatedElement element = Utilities.getConstructor(this.implClass, analyzer, collector);
         if (element == null) {
            this.myConstructor = null;
         } else {
            List<SystemInjecteeImpl> injectees = Utilities.getConstructorInjectees((Constructor<?>)element, selfDescriptor);
            if (injectees == null) {
               this.myConstructor = null;
            } else {
               baseAllInjectees.addAll(injectees);
               this.myConstructor = new ClazzCreator.ResolutionInfo(element, injectees);

               for (Method initMethod : Utilities.getInitMethods(this.implClass, analyzer, collector)) {
                  injectees = Utilities.getMethodInjectees(this.implClass, initMethod, selfDescriptor);
                  if (injectees == null) {
                     return;
                  }

                  baseAllInjectees.addAll(injectees);
                  if (initMethod.getDeclaringClass().equals(this.implClass)) {
                     this.myInitializers.add(new ClazzCreator.ResolutionInfo(initMethod, injectees));
                  } else {
                     this.superInitializers.add(new ClazzCreator.ResolutionInfo(initMethod, injectees));
                  }
               }

               for (Field field : Utilities.getInitFields(this.implClass, analyzer, collector)) {
                  injectees = Utilities.getFieldInjectees(this.implClass, field, selfDescriptor);
                  if (injectees == null) {
                     return;
                  }

                  baseAllInjectees.addAll(injectees);
                  if (field.getDeclaringClass().equals(this.implClass)) {
                     this.myFields.add(new ClazzCreator.ResolutionInfo(field, injectees));
                  } else {
                     this.superFields.add(new ClazzCreator.ResolutionInfo(field, injectees));
                  }
               }

               this.postConstructMethod = Utilities.getPostConstruct(this.implClass, analyzer, collector);
               this.preDestroyMethod = Utilities.getPreDestroy(this.implClass, analyzer, collector);
               this.allInjectees = Collections.unmodifiableList(baseAllInjectees);
               Utilities.validateSelfInjectees(selfDescriptor, this.allInjectees, collector);
            }
         }
      }
   }

   void initialize(ActiveDescriptor<?> selfDescriptor, Collector collector) {
      this.initialize(selfDescriptor, selfDescriptor == null ? null : selfDescriptor.getClassAnalysisName(), collector);
   }

   void resetSelfDescriptor(ActiveDescriptor<?> selfDescriptor) {
      this.selfDescriptor = selfDescriptor;

      for (Injectee injectee : this.allInjectees) {
         if (injectee instanceof SystemInjecteeImpl) {
            ((SystemInjecteeImpl)injectee).resetInjecteeDescriptor(selfDescriptor);
         }
      }
   }

   private void resolve(
      Map<SystemInjecteeImpl, Object> addToMe, InjectionResolver<?> resolver, SystemInjecteeImpl injectee, ServiceHandle<?> root, Collector errorCollection
   ) {
      if (injectee.isSelf()) {
         addToMe.put(injectee, this.selfDescriptor);
      } else {
         Object addIn = null;

         try {
            addIn = resolver.resolve(injectee, root);
         } catch (Throwable var8) {
            errorCollection.addThrowable(var8);
         }

         if (addIn != null) {
            addToMe.put(injectee, addIn);
         }
      }
   }

   private Map<SystemInjecteeImpl, Object> resolveAllDependencies(ServiceHandle<?> root) throws MultiException, IllegalStateException {
      Collector errorCollector = new Collector();
      Map<SystemInjecteeImpl, Object> retVal = new LinkedHashMap<>();

      for (SystemInjecteeImpl injectee : this.myConstructor.injectees) {
         InjectionResolver<?> resolver = this.locator.getInjectionResolverForInjectee(injectee);
         this.resolve(retVal, resolver, injectee, root, errorCollector);
      }

      for (ClazzCreator.ResolutionInfo fieldRI : this.superFields) {
         for (SystemInjecteeImpl injectee : fieldRI.injectees) {
            InjectionResolver<?> resolver = this.locator.getInjectionResolverForInjectee(injectee);
            this.resolve(retVal, resolver, injectee, root, errorCollector);
         }
      }

      for (ClazzCreator.ResolutionInfo methodRI : this.superInitializers) {
         for (SystemInjecteeImpl injectee : methodRI.injectees) {
            InjectionResolver<?> resolver = this.locator.getInjectionResolverForInjectee(injectee);
            this.resolve(retVal, resolver, injectee, root, errorCollector);
         }
      }

      for (ClazzCreator.ResolutionInfo fieldRI : this.myFields) {
         for (SystemInjecteeImpl injectee : fieldRI.injectees) {
            InjectionResolver<?> resolver = this.locator.getInjectionResolverForInjectee(injectee);
            this.resolve(retVal, resolver, injectee, root, errorCollector);
         }
      }

      for (ClazzCreator.ResolutionInfo methodRI : this.myInitializers) {
         for (SystemInjecteeImpl injectee : methodRI.injectees) {
            InjectionResolver<?> resolver = this.locator.getInjectionResolverForInjectee(injectee);
            this.resolve(retVal, resolver, injectee, root, errorCollector);
         }
      }

      if (errorCollector.hasErrors()) {
         errorCollector.addThrowable(
            new IllegalArgumentException("While attempting to resolve the dependencies of " + this.implClass.getName() + " errors were found")
         );
         errorCollector.throwIfErrors();
      }

      return retVal;
   }

   private Object createMe(Map<SystemInjecteeImpl, Object> resolved) throws Throwable {
      Constructor<?> c = (Constructor<?>)this.myConstructor.baseElement;
      List<SystemInjecteeImpl> injectees = this.myConstructor.injectees;
      Object[] args = new Object[injectees.size()];

      for (Injectee injectee : injectees) {
         args[injectee.getPosition()] = resolved.get(injectee);
      }

      Utilities.Interceptors interceptors = Utilities.getAllInterceptors(this.locator, this.selfDescriptor, this.implClass, c);
      Map<Method, List<MethodInterceptor>> methodInterceptors = interceptors.getMethodInterceptors();
      List<ConstructorInterceptor> constructorInterceptors = interceptors.getConstructorInterceptors();
      if (methodInterceptors != null && !methodInterceptors.isEmpty() || constructorInterceptors != null && !constructorInterceptors.isEmpty()) {
         if (!Utilities.proxiesAvailable()) {
            throw new IllegalStateException(
               "A service " + this.selfDescriptor + " needs either method or constructor interception, but proxies are not available"
            );
         } else {
            boolean neutral = this.locator.getNeutralContextClassLoader();
            return methodInterceptors != null && !methodInterceptors.isEmpty()
               ? ConstructorInterceptorHandler.construct(c, args, neutral, constructorInterceptors, new ConstructorActionImpl<>(this, methodInterceptors))
               : ConstructorInterceptorHandler.construct(c, args, neutral, constructorInterceptors);
         }
      } else {
         return ReflectionHelper.makeMe(c, args, this.locator.getNeutralContextClassLoader());
      }
   }

   private void fieldMe(Map<SystemInjecteeImpl, Object> resolved, T t) throws Throwable {
      for (ClazzCreator.ResolutionInfo ri : this.myFields) {
         Field field = (Field)ri.baseElement;
         List<SystemInjecteeImpl> injectees = ri.injectees;
         Injectee fieldInjectee = null;

         for (Injectee candidate : injectees) {
            fieldInjectee = candidate;
         }

         Object putMeIn = resolved.get(fieldInjectee);
         ReflectionHelper.setField(field, t, putMeIn);
      }
   }

   private void fieldParents(Map<SystemInjecteeImpl, Object> resolved, T t) throws Throwable {
      for (ClazzCreator.ResolutionInfo ri : this.superFields) {
         Field field = (Field)ri.baseElement;
         List<SystemInjecteeImpl> injectees = ri.injectees;
         Injectee fieldInjectee = null;

         for (Injectee candidate : injectees) {
            fieldInjectee = candidate;
         }

         Object putMeIn = resolved.get(fieldInjectee);
         ReflectionHelper.setField(field, t, putMeIn);
      }
   }

   private void methodMe(Map<SystemInjecteeImpl, Object> resolved, T t) throws Throwable {
      for (ClazzCreator.ResolutionInfo ri : this.myInitializers) {
         Method m = (Method)ri.baseElement;
         List<SystemInjecteeImpl> injectees = ri.injectees;
         Object[] args = new Object[injectees.size()];

         for (Injectee injectee : injectees) {
            args[injectee.getPosition()] = resolved.get(injectee);
         }

         ReflectionHelper.invoke(t, m, args, this.locator.getNeutralContextClassLoader());
      }
   }

   private void methodParents(Map<SystemInjecteeImpl, Object> resolved, T t) throws Throwable {
      for (ClazzCreator.ResolutionInfo ri : this.superInitializers) {
         Method m = (Method)ri.baseElement;
         List<SystemInjecteeImpl> injectees = ri.injectees;
         Object[] args = new Object[injectees.size()];

         for (Injectee injectee : injectees) {
            args[injectee.getPosition()] = resolved.get(injectee);
         }

         ReflectionHelper.invoke(t, m, args, this.locator.getNeutralContextClassLoader());
      }
   }

   private void postConstructMe(T t) throws Throwable {
      if (t != null) {
         if (t instanceof PostConstruct) {
            ((PostConstruct)t).postConstruct();
         } else if (this.postConstructMethod != null) {
            ReflectionHelper.invoke(t, this.postConstructMethod, new Object[0], this.locator.getNeutralContextClassLoader());
         }
      }
   }

   private void preDestroyMe(T t) throws Throwable {
      if (t != null) {
         if (t instanceof PreDestroy) {
            ((PreDestroy)t).preDestroy();
         } else if (this.preDestroyMethod != null) {
            ReflectionHelper.invoke(t, this.preDestroyMethod, new Object[0], this.locator.getNeutralContextClassLoader());
         }
      }
   }

   @Override
   public T create(ServiceHandle<?> root, SystemDescriptor<?> eventThrower) {
      String failureLocation = "resolve";

      try {
         Map<SystemInjecteeImpl, Object> allResolved = this.resolveAllDependencies(root);
         if (eventThrower != null) {
            eventThrower.invokeInstanceListeners(
               new InstanceLifecycleEventImpl(
                  InstanceLifecycleEventType.PRE_PRODUCTION, null, (Map<Injectee, Object>)ReflectionHelper.cast(allResolved), eventThrower
               )
            );
         }

         failureLocation = "create";
         T retVal = (T)this.createMe(allResolved);
         failureLocation = "parent field inject";
         this.fieldParents(allResolved, retVal);
         failureLocation = "parent method inject";
         this.methodParents(allResolved, retVal);
         failureLocation = "field inject";
         this.fieldMe(allResolved, retVal);
         failureLocation = "method inject";
         this.methodMe(allResolved, retVal);
         failureLocation = "post construct";
         this.postConstructMe(retVal);
         if (eventThrower != null) {
            eventThrower.invokeInstanceListeners(
               new InstanceLifecycleEventImpl(
                  InstanceLifecycleEventType.POST_PRODUCTION, retVal, (Map<Injectee, Object>)ReflectionHelper.cast(allResolved), eventThrower
               )
            );
         }

         return retVal;
      } catch (Throwable var6) {
         if (var6 instanceof MultiException) {
            MultiException me = (MultiException)var6;
            me.addError(new IllegalStateException("Unable to perform operation: " + failureLocation + " on " + this.implClass.getName()));
            throw me;
         } else {
            MultiException me = new MultiException(var6);
            me.addError(new IllegalStateException("Unable to perform operation: " + failureLocation + " on " + this.implClass.getName()));
            throw me;
         }
      }
   }

   @Override
   public void dispose(T instance) {
      try {
         this.preDestroyMe(instance);
      } catch (Throwable var3) {
         if (var3 instanceof MultiException) {
            throw (MultiException)var3;
         } else {
            throw new MultiException(var3);
         }
      }
   }

   @Override
   public List<Injectee> getInjectees() {
      return (List<Injectee>)ReflectionHelper.cast(this.allInjectees);
   }

   ServiceLocatorImpl getServiceLocator() {
      return this.locator;
   }

   Class<?> getImplClass() {
      return this.implClass;
   }

   ActiveDescriptor<?> getUnderlyingDescriptor() {
      return this.selfDescriptor;
   }

   @Override
   public String toString() {
      return "ClazzCreator(" + this.locator + "," + this.implClass.getName() + "," + System.identityHashCode(this) + ")";
   }

   private static class ResolutionInfo {
      private final AnnotatedElement baseElement;
      private final List<SystemInjecteeImpl> injectees = new LinkedList<>();

      private ResolutionInfo(AnnotatedElement baseElement, List<SystemInjecteeImpl> injectees) {
         this.baseElement = baseElement;
         this.injectees.addAll(injectees);
      }

      @Override
      public String toString() {
         return "ResolutionInfo(" + this.baseElement + "," + this.injectees + "," + System.identityHashCode(this) + ")";
      }
   }
}
