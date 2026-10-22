package org.glassfish.hk2.utilities;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import org.glassfish.hk2.api.DescriptorVisibility;
import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.JustInTimeInjectionResolver;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.Visibility;

@Singleton
@Visibility(DescriptorVisibility.LOCAL)
public class GreedyResolver implements JustInTimeInjectionResolver {
   private final ServiceLocator locator;

   @Inject
   private GreedyResolver(ServiceLocator locator) {
      this.locator = locator;
   }

   @Override
   public boolean justInTimeResolution(Injectee failedInjectionPoint) {
      Type type = failedInjectionPoint.getRequiredType();
      if (type == null) {
         return false;
      } else {
         Class<?> clazzToAdd = null;
         if (type instanceof Class) {
            clazzToAdd = (Class<?>)type;
         } else if (type instanceof ParameterizedType) {
            Type rawType = ((ParameterizedType)type).getRawType();
            if (rawType instanceof Class) {
               clazzToAdd = (Class<?>)rawType;
            }
         }

         if (clazzToAdd == null) {
            return false;
         } else {
            if (clazzToAdd.isInterface()) {
               GreedyDefaultImplementation gdi = clazzToAdd.getAnnotation(GreedyDefaultImplementation.class);
               if (gdi == null) {
                  return false;
               }

               clazzToAdd = gdi.value();
            }

            ServiceLocatorUtilities.addClasses(this.locator, clazzToAdd);
            return true;
         }
      }
   }
}
