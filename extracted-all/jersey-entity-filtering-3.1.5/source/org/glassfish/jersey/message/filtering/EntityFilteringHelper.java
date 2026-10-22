package org.glassfish.jersey.message.filtering;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.glassfish.jersey.message.filtering.spi.FilteringHelper;

final class EntityFilteringHelper {
   public static Set<String> getFilteringScopes(Annotation[] annotations) {
      return getFilteringScopes(annotations, true);
   }

   public static Set<String> getFilteringScopes(Annotation[] annotations, boolean filter) {
      if (annotations.length == 0) {
         return Collections.emptySet();
      } else {
         Set<String> contexts = new HashSet<>(annotations.length);
         annotations = filter ? getFilteringAnnotations(annotations) : annotations;

         for (Annotation annotation : annotations) {
            contexts.add(annotation.annotationType().getName());
         }

         return contexts;
      }
   }

   public static Annotation[] getFilteringAnnotations(Annotation[] annotations) {
      if (annotations != null && annotations.length != 0) {
         List<Annotation> filteringAnnotations = new ArrayList<>(annotations.length);

         for (Annotation annotation : annotations) {
            Class<? extends Annotation> annotationType = annotation.annotationType();

            for (Annotation metaAnnotation : annotationType.getDeclaredAnnotations()) {
               if (metaAnnotation instanceof EntityFiltering) {
                  filteringAnnotations.add(annotation);
               }
            }
         }

         return filteringAnnotations.toArray(new Annotation[filteringAnnotations.size()]);
      } else {
         return FilteringHelper.EMPTY_ANNOTATIONS;
      }
   }

   public static <T extends Annotation> T getAnnotation(Annotation[] annotations, Class<T> clazz) {
      for (Annotation annotation : annotations) {
         if (annotation.annotationType().getClass().isAssignableFrom(clazz)) {
            return (T)annotation;
         }
      }

      return null;
   }

   private EntityFilteringHelper() {
   }
}
