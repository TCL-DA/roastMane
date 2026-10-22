package org.eclipse.yasson.internal.model;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;

public class JsonbAnnotatedElement<T extends AnnotatedElement> {
   private final Map<Class<? extends Annotation>, LinkedList<JsonbAnnotatedElement.AnnotationWrapper<?>>> annotations = new HashMap<>(4);
   private final T element;

   public JsonbAnnotatedElement(T element) {
      for (Annotation ann : element.getAnnotations()) {
         if (element instanceof Class) {
            this.putAnnotation(ann, false, (Class<?>)element);
         } else {
            this.putAnnotation(ann, false, null);
         }
      }

      this.element = element;
   }

   public T getElement() {
      return this.element;
   }

   public <AT extends Annotation> Optional<AT> getAnnotation(Class<AT> annotationClass) {
      return Optional.ofNullable(this.annotations.get(annotationClass))
         .map(LinkedList::getFirst)
         .map(JsonbAnnotatedElement.AnnotationWrapper::getAnnotation)
         .map(annotationClass::cast);
   }

   public <AT extends Annotation> LinkedList<JsonbAnnotatedElement.AnnotationWrapper<?>> getAnnotations(Class<AT> annotationClass) {
      return this.annotations.getOrDefault(annotationClass, new LinkedList<>());
   }

   public <AT extends Annotation> JsonbAnnotatedElement.AnnotationWrapper<AT> getAnnotationWrapper(Class<AT> annotationClass) {
      return (JsonbAnnotatedElement.AnnotationWrapper<AT>)this.annotations.get(annotationClass).getFirst();
   }

   public Annotation[] getAnnotations() {
      return this.annotations
         .values()
         .stream()
         .flatMap(Collection::stream)
         .map(JsonbAnnotatedElement.AnnotationWrapper::getAnnotation)
         .toArray(Annotation[]::new);
   }

   public void putAnnotation(Annotation annotation, boolean inherited, Class<?> definedType) {
      this.annotations
         .computeIfAbsent(annotation.annotationType(), aClass -> new LinkedList<>())
         .add(new JsonbAnnotatedElement.AnnotationWrapper<>(annotation, inherited, definedType));
   }

   public void putAnnotationWrapper(JsonbAnnotatedElement.AnnotationWrapper<?> annotationWrapper) {
      this.annotations.computeIfAbsent(annotationWrapper.getAnnotation().annotationType(), aClass -> new LinkedList<>()).add(annotationWrapper);
   }

   public static final class AnnotationWrapper<T extends Annotation> {
      private final T annotation;
      private final boolean inherited;
      private final Class<?> definedType;

      public AnnotationWrapper(T annotation, boolean inherited, Class<?> definedType) {
         this.annotation = annotation;
         this.inherited = inherited;
         this.definedType = definedType;
      }

      public T getAnnotation() {
         return this.annotation;
      }

      public boolean isInherited() {
         return this.inherited;
      }

      public Class<?> getDefinedType() {
         return this.definedType;
      }

      @Override
      public String toString() {
         return this.definedType.getName();
      }
   }
}
