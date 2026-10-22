package org.glassfish.jersey.message.filtering.spi;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Set;
import org.glassfish.jersey.internal.util.ReflectionHelper;

public abstract class AbstractEntityProcessor implements EntityProcessor {
   @Override
   public EntityProcessor.Result process(EntityProcessorContext context) {
      switch (context.getType()) {
         case CLASS_READER:
         case CLASS_WRITER:
            return this.process(null, null, FilteringHelper.EMPTY_ANNOTATIONS, context.getEntityClass().getDeclaredAnnotations(), context.getEntityGraph());
         case PROPERTY_READER:
         case PROPERTY_WRITER:
         case METHOD_READER:
         case METHOD_WRITER:
            Field field = context.getField();
            Method method = context.getMethod();
            boolean isProperty = field != null;
            String fieldName;
            Type fieldType;
            if (isProperty) {
               fieldName = field.getName();
               fieldType = field.getGenericType();
            } else {
               fieldName = ReflectionHelper.getPropertyName(method);
               fieldType = ReflectionHelper.isGetter(method) ? method.getGenericReturnType() : method.getGenericParameterTypes()[0];
            }

            return this.process(
               fieldName, FilteringHelper.getEntityClass(fieldType), this.getAnnotations(field), this.getAnnotations(method), context.getEntityGraph()
            );
         default:
            return EntityProcessor.Result.SKIP;
      }
   }

   private Annotation[] getAnnotations(AccessibleObject accessibleObject) {
      return accessibleObject == null ? FilteringHelper.EMPTY_ANNOTATIONS : accessibleObject.getDeclaredAnnotations();
   }

   protected EntityProcessor.Result process(String fieldName, Class<?> fieldClass, Annotation[] fieldAnnotations, Annotation[] annotations, EntityGraph graph) {
      return EntityProcessor.Result.SKIP;
   }

   protected final void addFilteringScopes(String field, Class<?> fieldClass, Set<String> filteringScopes, EntityGraph graph) {
      if (!filteringScopes.isEmpty()) {
         if (FilteringHelper.filterableEntityClass(fieldClass)) {
            graph.addSubgraph(field, fieldClass, filteringScopes);
         } else {
            graph.addField(field, filteringScopes);
         }
      }
   }

   protected final void addGlobalScopes(Set<String> filteringScopes, EntityGraph graph) {
      graph.addFilteringScopes(filteringScopes);
   }
}
