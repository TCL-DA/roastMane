package org.eclipse.yasson.internal;

import jakarta.json.bind.JsonbException;
import jakarta.json.bind.config.PropertyNamingStrategy;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;
import org.eclipse.yasson.internal.model.JsonbCreator;
import org.eclipse.yasson.internal.model.Property;

public class ClassMultiReleaseExtension {
   private ClassMultiReleaseExtension() {
      throw new IllegalStateException("This class cannot be instantiated");
   }

   static boolean shouldTransformToPropertyName(Method method) {
      return true;
   }

   static boolean isSpecialAccessorMethod(Method method, Map<String, Property> classProperties) {
      return false;
   }

   static JsonbCreator findCreator(
      Class<?> clazz, Constructor<?>[] declaredConstructors, AnnotationIntrospector introspector, PropertyNamingStrategy propertyNamingStrategy
   ) {
      return null;
   }

   public static boolean isRecord(Class<?> clazz) {
      return false;
   }

   public static Optional<JsonbException> exceptionToThrow(Class<?> clazz) {
      return Optional.empty();
   }
}
