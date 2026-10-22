package org.eclipse.yasson.internal;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import org.eclipse.yasson.internal.model.ClassModel;
import org.eclipse.yasson.internal.model.JsonbAnnotatedElement;
import org.eclipse.yasson.internal.model.customization.ClassCustomization;

public class MappingContext {
   private final JsonbContext jsonbContext;
   private final ConcurrentHashMap<Class<?>, ClassModel> classes = new ConcurrentHashMap<>();
   private final ClassParser classParser;

   public MappingContext(JsonbContext jsonbContext) {
      Objects.requireNonNull(jsonbContext);
      this.jsonbContext = jsonbContext;
      this.classParser = new ClassParser(jsonbContext);
   }

   public ClassModel getOrCreateClassModel(Class<?> clazz) {
      ClassModel classModel = this.classes.get(clazz);
      if (classModel != null) {
         return classModel;
      } else {
         Deque<Class<?>> newClassModels = new ArrayDeque<>();

         for (Class<?> classToParse = clazz; classToParse != Object.class && classToParse != null; classToParse = classToParse.getSuperclass()) {
            newClassModels.push(classToParse);
         }

         if (clazz == Object.class) {
            return this.classes.computeIfAbsent(clazz, c -> new ClassModel((Class<?>)c, ClassCustomization.empty(), null, null));
         } else {
            ClassModel parentClassModel = null;

            while (!newClassModels.isEmpty()) {
               Class<?> toParse = newClassModels.pop();
               parentClassModel = this.classes.computeIfAbsent(toParse, createParseClassModelFunction(parentClassModel, this.classParser, this.jsonbContext));
            }

            return this.classes.get(clazz);
         }
      }
   }

   private static Function<Class<?>, ClassModel> createParseClassModelFunction(ClassModel parentClassModel, ClassParser classParser, JsonbContext jsonbContext) {
      return aClass -> {
         JsonbAnnotatedElement<Class<?>> clsElement = jsonbContext.getAnnotationIntrospector().collectAnnotations(aClass);
         ClassCustomization customization = jsonbContext.getAnnotationIntrospector()
            .introspectCustomization(
               clsElement,
               parentClassModel == null ? ClassCustomization.empty() : parentClassModel.getClassCustomization(),
               jsonbContext.getConfigProperties().getPropertyNamingStrategy()
            );
         ClassModel newClassModel = new ClassModel(aClass, customization, parentClassModel, jsonbContext.getConfigProperties().getPropertyNamingStrategy());
         if (!BuiltInTypes.isKnownType(aClass)) {
            classParser.parseProperties(newClassModel, clsElement);
         }

         return newClassModel;
      };
   }

   public ClassModel getClassModel(Class<?> clazz) {
      return this.classes.get(clazz);
   }
}
