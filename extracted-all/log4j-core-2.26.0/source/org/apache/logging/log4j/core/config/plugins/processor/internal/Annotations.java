package org.apache.logging.log4j.core.config.plugins.processor.internal;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.util.Elements;

public final class Annotations {
   private static final Collection<String> PARAMETER_ANNOTATION_NAMES = Arrays.asList(
      "org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute",
      "org.apache.logging.log4j.core.config.plugins.PluginConfiguration",
      "org.apache.logging.log4j.core.config.plugins.PluginElement",
      "org.apache.logging.log4j.core.config.plugins.PluginLoggerContext",
      "org.apache.logging.log4j.core.config.plugins.PluginNode",
      "org.apache.logging.log4j.core.config.plugins.PluginValue"
   );
   private static final Collection<String> FACTORY_ANNOTATION_NAMES = Arrays.asList(
      "org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory", "org.apache.logging.log4j.core.config.plugins.PluginFactory"
   );
   private static final Collection<String> PLUGIN_ANNOTATION_NAMES = Collections.singletonList("org.apache.logging.log4j.core.config.plugins.Plugin");
   private static final Collection<String> CONSTRAINT_OR_VISITOR_ANNOTATION_NAMES = Arrays.asList(
      "org.apache.logging.log4j.core.config.plugins.validation.Constraint", "org.apache.logging.log4j.core.config.plugins.PluginVisitorStrategy"
   );
   private final Map<TypeElement, Annotations.Type> typeElementToTypeMap = new HashMap<>();

   public Annotations(final Elements elements) {
      PARAMETER_ANNOTATION_NAMES.forEach(className -> this.addTypeElementIfExists(elements, className, Annotations.Type.PARAMETER));
      FACTORY_ANNOTATION_NAMES.forEach(className -> this.addTypeElementIfExists(elements, className, Annotations.Type.FACTORY));
      PLUGIN_ANNOTATION_NAMES.forEach(className -> this.addTypeElementIfExists(elements, className, Annotations.Type.PLUGIN));
      CONSTRAINT_OR_VISITOR_ANNOTATION_NAMES.forEach(className -> this.addTypeElementIfExists(elements, className, Annotations.Type.CONSTRAINT_OR_VISITOR));
   }

   private void addTypeElementIfExists(Elements elements, CharSequence className, Annotations.Type type) {
      TypeElement element = elements.getTypeElement(className);
      if (element != null) {
         this.typeElementToTypeMap.put(element, type);
      }
   }

   public Annotations.Type classifyAnnotation(TypeElement element) {
      return this.typeElementToTypeMap.getOrDefault(element, Annotations.Type.UNKNOWN);
   }

   public Element getAnnotationClassValue(Element element, TypeElement annotation) {
      AnnotationMirror annotationMirror = element.getAnnotationMirrors()
         .stream()
         .filter(am -> am.getAnnotationType().asElement().equals(annotation))
         .findFirst()
         .orElseThrow(() -> new IllegalStateException("No `@" + annotation + "` annotation found on " + element));
      AnnotationValue annotationValue = annotationMirror.getElementValues()
         .entrySet()
         .stream()
         .filter(e -> "value".equals(e.getKey().getSimpleName().toString()))
         .map(Entry::getValue)
         .findFirst()
         .orElseThrow(() -> new IllegalStateException("No `value` found `@" + annotation + "` annotation on " + element));
      DeclaredType value = (DeclaredType)annotationValue.getValue();
      return value.asElement();
   }

   public static enum Type {
      PARAMETER,
      FACTORY,
      PLUGIN,
      CONSTRAINT_OR_VISITOR,
      UNKNOWN;
   }
}
