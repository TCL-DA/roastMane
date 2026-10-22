package org.eclipse.yasson.internal;

import jakarta.json.bind.JsonbException;
import jakarta.json.bind.config.PropertyNamingStrategy;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;
import org.eclipse.yasson.internal.model.JsonbCreator;
import org.eclipse.yasson.internal.model.Property;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

public class ClassMultiReleaseExtension {
   private ClassMultiReleaseExtension() {
      throw new IllegalStateException("This class cannot be instantiated");
   }

   static boolean shouldTransformToPropertyName(Method method) {
      return !method.getDeclaringClass().isRecord();
   }

   static boolean isSpecialAccessorMethod(Method method, Map<String, Property> classProperties) {
      return isRecord(method.getDeclaringClass())
         && method.getParameterCount() == 0
         && !void.class.equals(method.getReturnType())
         && classProperties.containsKey(method.getName());
   }

   static JsonbCreator findCreator(
      Class<?> clazz, Constructor<?>[] declaredConstructors, AnnotationIntrospector introspector, PropertyNamingStrategy propertyNamingStrategy
   ) {
      return clazz.isRecord() && declaredConstructors.length == 1
         ? introspector.createJsonbCreator(declaredConstructors[0], null, clazz, propertyNamingStrategy)
         : null;
   }

   public static boolean isRecord(Class<?> clazz) {
      return clazz.isRecord();
   }

   public static Optional<JsonbException> exceptionToThrow(Class<?> clazz) {
      return clazz.isRecord() && clazz.getDeclaredConstructors().length > 1
         ? Optional.of(new JsonbException(Messages.getMessage(MessageKeys.RECORD_MULTIPLE_CONSTRUCTORS, new Object[]{clazz})))
         : Optional.empty();
   }
}
