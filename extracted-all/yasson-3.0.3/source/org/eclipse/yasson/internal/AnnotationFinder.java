package org.eclipse.yasson.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

class AnnotationFinder {
   private static final String CONSTRUCTOR_PROPERTIES_ANNOTATION = "java.beans.ConstructorProperties";
   private static final Logger LOGGER = Logger.getLogger(AnnotationFinder.class.getName());
   private final String annotationClassName;
   private final Class<? extends Annotation> annotationClass;

   public static AnnotationFinder findAnnotation(Class<?> annotation) {
      return findAnnotationByName(annotation.getName());
   }

   public static AnnotationFinder findAnnotationByName(String annotationClassName) {
      return new AnnotationFinder(annotationClassName, getOptionalAnnotationClass(annotationClassName));
   }

   public static AnnotationFinder findConstructorProperties() {
      return findAnnotationByName("java.beans.ConstructorProperties");
   }

   private AnnotationFinder(String annotationClassName, Class<? extends Annotation> annotationClass) {
      this.annotationClassName = annotationClassName;
      this.annotationClass = annotationClass;
   }

   public <T extends Annotation> T in(Annotation[] annotations) {
      return this.annotationClass == null ? null : findAnnotation(annotations, (Class<T>)this.annotationClass, new HashSet<>());
   }

   public Object valueIn(Annotation[] annotations) {
      return this.invocateValueMethod(this.in(annotations));
   }

   private Object invocateValueMethod(Annotation annotation) {
      if (annotation == null) {
         return null;
      } else {
         try {
            return annotation.annotationType().getMethod("value").invoke(annotation);
         } catch (SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException var4) {
            String message = Messages.getMessage(MessageKeys.MISSING_VALUE_PROPERTY_IN_ANNOTATION, annotation.annotationType().getName());
            LOGGER.finest(message);
            return null;
         }
      }
   }

   private static <T extends Annotation> Class<T> getOptionalAnnotationClass(String classname) {
      try {
         return (Class<T>)Class.forName(classname);
      } catch (ClassNotFoundException var3) {
         String message = Messages.getMessage(MessageKeys.ANNOTATION_NOT_AVAILABLE, classname);
         LOGGER.finest(message);
         return null;
      }
   }

   public static <T extends Annotation> T findAnnotation(Annotation[] declaredAnnotations, Class<T> annotationClass, Set<Annotation> processed) {
      for (Annotation candidate : declaredAnnotations) {
         Class<? extends Annotation> annType = candidate.annotationType();
         if (annType.equals(annotationClass)) {
            return (T)candidate;
         }

         processed.add(candidate);
         List<Annotation> inheritedAnnotations = new ArrayList<>(Arrays.asList(annType.getDeclaredAnnotations()));
         inheritedAnnotations.removeAll(processed);
         if (inheritedAnnotations.size() > 0) {
            T inherited = findAnnotation(inheritedAnnotations.toArray(new Annotation[inheritedAnnotations.size()]), annotationClass, processed);
            if (inherited != null) {
               return inherited;
            }
         }
      }

      return null;
   }

   @Override
   public String toString() {
      return "AnnotationFinder [annotationClassName=" + this.annotationClassName + ", annotationClass=" + this.annotationClass + "]";
   }
}
