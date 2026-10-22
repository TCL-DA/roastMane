package org.eclipse.yasson.internal.model;

import java.lang.reflect.Executable;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import org.eclipse.yasson.internal.AnnotationIntrospector;
import org.eclipse.yasson.internal.JsonbContext;
import org.eclipse.yasson.internal.JsonbDateFormatter;
import org.eclipse.yasson.internal.JsonbNumberFormatter;
import org.eclipse.yasson.internal.components.AdapterBinding;
import org.eclipse.yasson.internal.components.DeserializerBinding;
import org.eclipse.yasson.internal.model.customization.CreatorCustomization;

public class CreatorModel {
   private final String name;
   private final Type type;
   private final CreatorCustomization creatorCustomization;

   public CreatorModel(String name, Parameter parameter, Executable executable, JsonbContext context) {
      this.name = name;
      this.type = parameter.getParameterizedType();
      AnnotationIntrospector annotationIntrospector = context.getAnnotationIntrospector();
      JsonbAnnotatedElement<Parameter> annotated = new JsonbAnnotatedElement<>(parameter);
      boolean required = context.getAnnotationIntrospector().requiredParameters(executable, annotated);
      JsonbNumberFormatter constructorNumberFormatter = context.getAnnotationIntrospector().getConstructorNumberFormatter(annotated);
      JsonbDateFormatter constructorDateFormatter = context.getAnnotationIntrospector().getConstructorDateFormatter(annotated);
      DeserializerBinding<?> deserializerBinding = annotationIntrospector.getDeserializerBinding(parameter);
      AdapterBinding adapterBinding = annotationIntrospector.getAdapterBinding(parameter);
      JsonbAnnotatedElement<Class<?>> clsElement = annotationIntrospector.collectAnnotations(parameter.getType());
      deserializerBinding = deserializerBinding == null ? annotationIntrospector.getDeserializerBinding(clsElement) : deserializerBinding;
      adapterBinding = adapterBinding == null ? annotationIntrospector.getAdapterBinding(clsElement) : adapterBinding;
      this.creatorCustomization = ((CreatorCustomization.Builder)((CreatorCustomization.Builder)((CreatorCustomization.Builder)CreatorCustomization.builder()
                  .adapterBinding(adapterBinding))
               .deserializerBinding(deserializerBinding))
            .serializerBinding(annotationIntrospector.getSerializerBinding(clsElement)))
         .numberFormatter(constructorNumberFormatter)
         .dateFormatter(constructorDateFormatter)
         .required(required)
         .build();
   }

   public String getName() {
      return this.name;
   }

   public CreatorCustomization getCustomization() {
      return this.creatorCustomization;
   }

   public Type getType() {
      return this.type;
   }
}
