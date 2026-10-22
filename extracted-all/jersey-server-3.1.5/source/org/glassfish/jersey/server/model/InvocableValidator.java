package org.glassfish.jersey.server.model;

import jakarta.inject.Singleton;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;
import org.glassfish.jersey.internal.Errors;
import org.glassfish.jersey.internal.inject.PerLookup;
import org.glassfish.jersey.internal.inject.Providers;
import org.glassfish.jersey.server.internal.LocalizationMessages;

class InvocableValidator extends AbstractResourceModelVisitor {
   private static final Set<Class<?>> SCOPE_ANNOTATIONS = getScopeAnnotations();
   protected final Set<Class<?>> checkedClasses = new HashSet<>();

   private static Set<Class<?>> getScopeAnnotations() {
      Set<Class<?>> scopeAnnotations = new HashSet<>();
      scopeAnnotations.add(Singleton.class);
      scopeAnnotations.add(PerLookup.class);
      return scopeAnnotations;
   }

   @Override
   public void visitInvocable(Invocable invocable) {
      Class resClass = invocable.getHandler().getHandlerClass();
      if (resClass != null && !this.checkedClasses.contains(resClass)) {
         this.checkedClasses.add(resClass);
         boolean provider = Providers.isProvider(resClass);
         int counter = 0;

         for (Annotation annotation : resClass.getAnnotations()) {
            if (SCOPE_ANNOTATIONS.contains(annotation.annotationType())) {
               counter++;
            }
         }

         if (counter == 0 && provider) {
            Errors.warning(resClass, LocalizationMessages.RESOURCE_IMPLEMENTS_PROVIDER(resClass, Providers.getProviderContracts(resClass)));
         } else if (counter > 1) {
            Errors.fatal(resClass, LocalizationMessages.RESOURCE_MULTIPLE_SCOPE_ANNOTATIONS(resClass));
         }
      }
   }

   public static boolean isSingleton(Class<?> resourceClass) {
      return resourceClass.isAnnotationPresent(Singleton.class) || Providers.isProvider(resourceClass) && !resourceClass.isAnnotationPresent(PerLookup.class);
   }

   @Override
   public void visitResourceHandlerConstructor(HandlerConstructor constructor) {
      Class<?> resClass = constructor.getConstructor().getDeclaringClass();
      boolean isSingleton = isSingleton(resClass);
      int paramCount = 0;

      for (Parameter p : constructor.getParameters()) {
         ResourceMethodValidator.validateParameter(
            p, constructor.getConstructor(), constructor.getConstructor().toGenericString(), Integer.toString(++paramCount), isSingleton
         );
      }
   }
}
