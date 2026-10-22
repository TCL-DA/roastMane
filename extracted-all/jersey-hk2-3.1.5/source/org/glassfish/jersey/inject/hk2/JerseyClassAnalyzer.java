package org.glassfish.jersey.inject.hk2;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import org.glassfish.hk2.api.ClassAnalyzer;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.internal.Errors;
import org.glassfish.jersey.internal.inject.InjectionResolver;
import org.glassfish.jersey.internal.util.collection.ImmutableCollectors;
import org.glassfish.jersey.internal.util.collection.LazyValue;
import org.glassfish.jersey.internal.util.collection.Value;
import org.glassfish.jersey.internal.util.collection.Values;

@Singleton
@Named("JerseyClassAnalyzer")
public final class JerseyClassAnalyzer implements ClassAnalyzer {
   public static final String NAME = "JerseyClassAnalyzer";
   private final ClassAnalyzer defaultAnalyzer;
   private final LazyValue<Set<Class>> resolverAnnotations;

   private JerseyClassAnalyzer(ClassAnalyzer defaultAnalyzer, Supplier<List<InjectionResolver>> supplierResolvers) {
      this.defaultAnalyzer = defaultAnalyzer;
      Value<Set<Class>> resolvers = () -> supplierResolvers.get()
            .stream()
            .filter(InjectionResolver::isConstructorParameterIndicator)
            .map(InjectionResolver::getAnnotation)
            .collect(ImmutableCollectors.toImmutableSet());
      this.resolverAnnotations = Values.lazy(resolvers);
   }

   public <T> Constructor<T> getConstructor(Class<T> clazz) throws MultiException, NoSuchMethodException {
      if (clazz.isLocalClass()) {
         throw new NoSuchMethodException(org.glassfish.jersey.internal.LocalizationMessages.INJECTION_ERROR_LOCAL_CLASS_NOT_SUPPORTED(clazz.getName()));
      } else if (clazz.isMemberClass() && !Modifier.isStatic(clazz.getModifiers())) {
         throw new NoSuchMethodException(
            org.glassfish.jersey.internal.LocalizationMessages.INJECTION_ERROR_NONSTATIC_MEMBER_CLASS_NOT_SUPPORTED(clazz.getName())
         );
      } else {
         try {
            Constructor<T> retVal = this.defaultAnalyzer.getConstructor(clazz);
            Class<?>[] args = retVal.getParameterTypes();
            if (args.length != 0) {
               return retVal;
            }

            Inject i = retVal.getAnnotation(Inject.class);
            if (i != null) {
               return retVal;
            }
         } catch (NoSuchMethodException var12) {
         } catch (MultiException var13) {
            if (var13.getErrors().size() != 1 && !(var13.getErrors().get(0) instanceof IllegalArgumentException)) {
               throw var13;
            }
         }

         Constructor<?>[] constructors = AccessController.doPrivileged(clazz::getDeclaredConstructors);
         Constructor<?> selected = null;
         int selectedSize = 0;
         int maxParams = -1;

         for (Constructor<?> constructor : constructors) {
            Class<?>[] params = constructor.getParameterTypes();
            if (params.length >= maxParams && this.isCompatible(constructor)) {
               if (params.length > maxParams) {
                  maxParams = params.length;
                  selectedSize = 0;
               }

               selected = constructor;
               selectedSize++;
            }
         }

         if (selectedSize == 0) {
            throw new NoSuchMethodException(org.glassfish.jersey.internal.LocalizationMessages.INJECTION_ERROR_SUITABLE_CONSTRUCTOR_NOT_FOUND(clazz.getName()));
         } else {
            if (selectedSize > 1) {
               Errors.warning(
                  clazz,
                  org.glassfish.jersey.internal.LocalizationMessages.MULTIPLE_MATCHING_CONSTRUCTORS_FOUND(
                     selectedSize, maxParams, clazz.getName(), selected.toGenericString()
                  )
               );
            }

            return (Constructor<T>)selected;
         }
      }
   }

   private boolean isCompatible(Constructor<?> constructor) {
      if (constructor.getAnnotation(Inject.class) != null) {
         return true;
      } else {
         int paramSize = constructor.getParameterTypes().length;
         if (paramSize != 0 && ((Set)this.resolverAnnotations.get()).isEmpty()) {
            return false;
         } else if (!Modifier.isPublic(constructor.getModifiers())) {
            return paramSize == 0 && (constructor.getDeclaringClass().getModifiers() & 7) == constructor.getModifiers();
         } else {
            for (Annotation[] paramAnnotations : constructor.getParameterAnnotations()) {
               boolean found = false;

               for (Annotation paramAnnotation : paramAnnotations) {
                  if (((Set)this.resolverAnnotations.get()).contains(paramAnnotation.annotationType())) {
                     found = true;
                     break;
                  }
               }

               if (!found) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   public <T> Set<Method> getInitializerMethods(Class<T> clazz) throws MultiException {
      return this.defaultAnalyzer.getInitializerMethods(clazz);
   }

   public <T> Set<Field> getFields(Class<T> clazz) throws MultiException {
      return this.defaultAnalyzer.getFields(clazz);
   }

   public <T> Method getPostConstructMethod(Class<T> clazz) throws MultiException {
      return this.defaultAnalyzer.getPostConstructMethod(clazz);
   }

   public <T> Method getPreDestroyMethod(Class<T> clazz) throws MultiException {
      return this.defaultAnalyzer.getPreDestroyMethod(clazz);
   }

   public static final class Binder extends AbstractBinder {
      private final ServiceLocator serviceLocator;

      public Binder(ServiceLocator serviceLocator) {
         this.serviceLocator = serviceLocator;
      }

      protected void configure() {
         ClassAnalyzer defaultAnalyzer = (ClassAnalyzer)this.serviceLocator.getService(ClassAnalyzer.class, "default", new Annotation[0]);
         Supplier<List<InjectionResolver>> resolvers = () -> this.serviceLocator.getAllServices(InjectionResolver.class, new Annotation[0]);
         this.bind(new JerseyClassAnalyzer(defaultAnalyzer, resolvers)).analyzeWith("default").named("JerseyClassAnalyzer").to(ClassAnalyzer.class);
      }
   }
}
