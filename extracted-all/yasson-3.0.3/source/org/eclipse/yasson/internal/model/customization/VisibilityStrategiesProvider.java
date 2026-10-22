package org.eclipse.yasson.internal.model.customization;

import jakarta.json.bind.JsonbException;
import jakarta.json.bind.config.PropertyVisibilityStrategy;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

public class VisibilityStrategiesProvider {
   private static final PropertyVisibilityStrategy PUBLIC_PROPERTY = new VisibilityStrategiesProvider.PublicPropertyVisibilityStrategy();
   private static final PropertyVisibilityStrategy PUBLIC_ACCESSOR_METHODS = new VisibilityStrategiesProvider.PublicAccessorVisibilityStrategy();
   private static final PropertyVisibilityStrategy PUBLIC_FIELDS = new VisibilityStrategiesProvider.PublicFieldsVisibilityStrategy();
   private static final PropertyVisibilityStrategy ALL_FIELDS_AND_METHODS = new VisibilityStrategiesProvider.AllFieldsVisibilityStrategy();

   private VisibilityStrategiesProvider() {
      throw new IllegalStateException("This class cannot be instantiated");
   }

   public static PropertyVisibilityStrategy getStrategy(String strategy) {
      switch (strategy) {
         case "PUBLIC_PROPERTY":
            return PUBLIC_PROPERTY;
         case "PUBLIC_ACCESSOR_METHODS":
            return PUBLIC_ACCESSOR_METHODS;
         case "PUBLIC_FIELDS":
            return PUBLIC_FIELDS;
         case "ALL_FIELD_AND_ACCESSORS":
            return ALL_FIELDS_AND_METHODS;
         default:
            throw new JsonbException(Messages.getMessage(MessageKeys.UNKNOWN_VISIBILITY_STRATEGY, strategy));
      }
   }

   private static final class AllFieldsVisibilityStrategy implements PropertyVisibilityStrategy {
      public boolean isVisible(Field field) {
         return true;
      }

      public boolean isVisible(Method method) {
         return true;
      }
   }

   private static final class PublicAccessorVisibilityStrategy implements PropertyVisibilityStrategy {
      public boolean isVisible(Field field) {
         return false;
      }

      public boolean isVisible(Method method) {
         return Modifier.isPublic(method.getModifiers());
      }
   }

   private static final class PublicFieldsVisibilityStrategy implements PropertyVisibilityStrategy {
      public boolean isVisible(Field field) {
         return Modifier.isPublic(field.getModifiers());
      }

      public boolean isVisible(Method method) {
         return false;
      }
   }

   private static final class PublicPropertyVisibilityStrategy implements PropertyVisibilityStrategy {
      public boolean isVisible(Field field) {
         return Modifier.isPublic(field.getModifiers());
      }

      public boolean isVisible(Method method) {
         return Modifier.isPublic(method.getModifiers());
      }
   }
}
