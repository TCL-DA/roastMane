package org.eclipse.yasson.internal;

import jakarta.json.bind.JsonbException;
import jakarta.json.bind.config.PropertyVisibilityStrategy;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.eclipse.yasson.internal.model.ClassModel;
import org.eclipse.yasson.internal.model.CreatorModel;
import org.eclipse.yasson.internal.model.JsonbAnnotatedElement;
import org.eclipse.yasson.internal.model.JsonbCreator;
import org.eclipse.yasson.internal.model.Property;
import org.eclipse.yasson.internal.model.PropertyModel;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

class ClassParser {
   private static final String IS_PREFIX = "is";
   private static final String GET_PREFIX = "get";
   private static final String SET_PREFIX = "set";
   private final JsonbContext jsonbContext;

   ClassParser(JsonbContext jsonbContext) {
      this.jsonbContext = jsonbContext;
   }

   void parseProperties(ClassModel classModel, JsonbAnnotatedElement<Class<?>> classElement) {
      Map<String, Property> classProperties = new HashMap<>();
      parseFields(classElement, classProperties);
      this.parseClassAndInterfaceMethods(classElement, classProperties);
      List<PropertyModel> sortedParentProperties = this.getSortedParentProperties(classModel, classElement, classProperties);
      List<PropertyModel> classPropertyModels = classProperties.values()
         .stream()
         .map(property -> new PropertyModel(classModel, property, this.jsonbContext))
         .collect(Collectors.toList());
      List<PropertyModel> unsortedMerged = new ArrayList<>(sortedParentProperties.size() + classPropertyModels.size());
      unsortedMerged.addAll(sortedParentProperties);
      unsortedMerged.addAll(classPropertyModels);
      checkPropertyNameClash(unsortedMerged, classModel.getType());
      mergePropertyModels(classPropertyModels);
      List<PropertyModel> sortedPropertyModels = new ArrayList<>(sortedParentProperties.size() + classPropertyModels.size());
      sortedPropertyModels.addAll(sortedParentProperties);
      sortedPropertyModels.addAll(this.jsonbContext.getConfigProperties().getPropertyOrdering().orderProperties(classPropertyModels, classModel));
      JsonbCreator creator = classModel.getClassCustomization().getCreator();
      if (creator != null) {
         sortedPropertyModels.forEach(propertyModel -> {
            for (CreatorModel creatorModel : creator.getParams()) {
               if (creatorModel.getName().equals(propertyModel.getPropertyName())) {
                  creatorModel.getCustomization().setPropertyModel(propertyModel);
               }
            }
         });
      }

      classModel.setProperties(sortedPropertyModels);
   }

   private static void mergePropertyModels(List<PropertyModel> unsortedMerged) {
      PropertyModel[] clone = unsortedMerged.toArray(new PropertyModel[0]);

      for (int i = 0; i < clone.length; i++) {
         for (int j = i + 1; j < clone.length; j++) {
            PropertyModel firstPropertyModel = clone[i];
            PropertyModel secondPropertyModel = clone[j];
            if (firstPropertyModel.equals(secondPropertyModel)) {
               unsortedMerged.remove(firstPropertyModel);
               unsortedMerged.remove(secondPropertyModel);
               if (!firstPropertyModel.isReadable() && !firstPropertyModel.isWritable()) {
                  unsortedMerged.add(secondPropertyModel);
               } else if (!secondPropertyModel.isReadable() && !secondPropertyModel.isWritable()) {
                  unsortedMerged.add(firstPropertyModel);
               } else {
                  unsortedMerged.add(new PropertyModel(firstPropertyModel, secondPropertyModel));
               }
            }
         }
      }
   }

   private void parseClassAndInterfaceMethods(JsonbAnnotatedElement<Class<?>> classElement, Map<String, Property> classProperties) {
      Class<?> concreteClass = classElement.getElement();
      this.parseMethods(concreteClass, classElement, classProperties);

      for (Class<?> ifc : this.jsonbContext.getAnnotationIntrospector().collectInterfaces(concreteClass)) {
         this.parseIfaceMethodAnnotations(ifc, classElement, classProperties);
      }
   }

   private void parseIfaceMethodAnnotations(Class<?> ifc, JsonbAnnotatedElement<Class<?>> classElement, Map<String, Property> classProperties) {
      Method[] declaredMethods = AccessController.doPrivileged(ifc::getDeclaredMethods);

      for (Method method : declaredMethods) {
         String methodName = method.getName();
         if (isPropertyMethod(method)) {
            String propertyName = toPropertyMethod(methodName);
            Property property = classProperties.get(propertyName);
            if (method.isDefault()) {
               if (property == null) {
                  property = this.registerMethod(propertyName, method, classElement, classProperties);
               } else if (isSetter(method)) {
                  if (property.getSetter() == null) {
                     property.setSetter(method);
                  }
               } else if (property.getGetter() == null) {
                  property.setGetter(method);
               }
            }

            if (property != null) {
               JsonbAnnotatedElement<Method> methodElement = isGetter(method) ? property.getGetterElement() : property.getSetterElement();

               for (Annotation ann : method.getDeclaredAnnotations()) {
                  if (methodElement.getAnnotation(ann.annotationType()).isEmpty()) {
                     methodElement.putAnnotation(ann, true, null);
                  }
               }
            }
         }
      }
   }

   private Property registerMethod(String propertyName, Method method, JsonbAnnotatedElement<Class<?>> classElement, Map<String, Property> classProperties) {
      Property property = classProperties.computeIfAbsent(propertyName, n -> new Property(n, classElement));
      if (isSetter(method)) {
         property.setSetter(method);
      } else {
         property.setGetter(method);
      }

      return property;
   }

   private void parseMethods(Class<?> clazz, JsonbAnnotatedElement<Class<?>> classElement, Map<String, Property> classProperties) {
      Method[] declaredMethods = AccessController.doPrivileged(clazz::getDeclaredMethods);

      for (Method method : declaredMethods) {
         String name = method.getName();
         boolean isAccessorMethod = ClassMultiReleaseExtension.isSpecialAccessorMethod(method, classProperties) || isPropertyMethod(method);
         if (isAccessorMethod && !method.isBridge() && !isSpecialCaseMethod(clazz, method)) {
            String propertyName = ClassMultiReleaseExtension.shouldTransformToPropertyName(method) ? toPropertyMethod(name) : name;
            this.registerMethod(propertyName, method, classElement, classProperties);
         }
      }
   }

   private static boolean isSpecialCaseMethod(Class<?> clazz, Method m) {
      if (Modifier.isPublic(m.getModifiers()) && !Modifier.isStatic(m.getModifiers()) && !m.isSynthetic()) {
         return m.getName().equals("getMetaClass") && m.getReturnType().getCanonicalName().equals("groovy.lang.MetaClass")
            ? true
            : m.getName().equals("getMetadata") && m.getReturnType().getCanonicalName().equals("org.jboss.weld.proxy.WeldClientProxy$Metadata");
      } else {
         return false;
      }
   }

   private static boolean isGetter(Method m) {
      return (m.getName().startsWith("get") || m.getName().startsWith("is")) && m.getParameterCount() == 0;
   }

   private static boolean isSetter(Method m) {
      return m.getName().startsWith("set") && m.getParameterCount() == 1;
   }

   private static String toPropertyMethod(String name) {
      return lowerFirstLetter(name.substring(name.startsWith("is") ? 2 : 3));
   }

   private static String lowerFirstLetter(String name) {
      Objects.requireNonNull(name);
      if (name.length() == 0) {
         return name;
      } else if (name.length() > 1 && Character.isUpperCase(name.charAt(1)) && Character.isUpperCase(name.charAt(0))) {
         return name;
      } else {
         char[] chars = name.toCharArray();
         chars[0] = Character.toLowerCase(chars[0]);
         return new String(chars);
      }
   }

   private static boolean isPropertyMethod(Method m) {
      return isGetter(m) || isSetter(m);
   }

   private static void parseFields(JsonbAnnotatedElement<Class<?>> classElement, Map<String, Property> classProperties) {
      Field[] declaredFields = AccessController.doPrivileged((PrivilegedAction<Field[]>)(() -> classElement.getElement().getDeclaredFields()));

      for (Field field : declaredFields) {
         String name = field.getName();
         if (!field.isSynthetic()) {
            Property property = new Property(name, classElement);
            property.setField(field);
            classProperties.put(name, property);
         }
      }
   }

   private static void checkPropertyNameClash(List<PropertyModel> collectedProperties, Class<?> cls) {
      List<PropertyModel> checkedProperties = new ArrayList<>();

      for (PropertyModel collectedPropertyModel : collectedProperties) {
         for (PropertyModel checkedPropertyModel : checkedProperties) {
            if (checkedPropertyModel.getReadName().equals(collectedPropertyModel.getReadName())
                  && checkedPropertyModel.isReadable()
                  && collectedPropertyModel.isReadable()
               || checkedPropertyModel.getWriteName().equals(collectedPropertyModel.getWriteName())
                  && checkedPropertyModel.isWritable()
                  && collectedPropertyModel.isWritable()) {
               throw new JsonbException(
                  Messages.getMessage(
                     MessageKeys.PROPERTY_NAME_CLASH, checkedPropertyModel.getPropertyName(), collectedPropertyModel.getPropertyName(), cls.getName()
                  )
               );
            }
         }

         checkedProperties.add(collectedPropertyModel);
      }
   }

   private List<PropertyModel> getSortedParentProperties(
      ClassModel classModel, JsonbAnnotatedElement<Class<?>> classElement, Map<String, Property> classProperties
   ) {
      List<PropertyModel> sortedProperties = new ArrayList<>();
      if (classModel.getParentClassModel() != null) {
         for (PropertyModel parentProp : classModel.getParentClassModel().getSortedProperties()) {
            Property current = classProperties.get(parentProp.getPropertyName());
            if (current == null) {
               sortedProperties.add(parentProp);
            } else {
               Property merged = mergeProperty(current, parentProp, classElement);
               PropertyVisibilityStrategy propertyVisibilityStrategy = classModel.getClassCustomization().getPropertyVisibilityStrategy();
               if (PropertyModel.isPropertyReadable(current.getField(), current.getGetter(), propertyVisibilityStrategy)) {
                  classProperties.replace(current.getName(), merged);
               } else {
                  sortedProperties.add(new PropertyModel(classModel, merged, this.jsonbContext));
                  classProperties.remove(current.getName());
               }
            }
         }
      }

      return sortedProperties;
   }

   private static Method selectMostSpecificNonDefaultMethod(Method current, Method parent) {
      return current != null ? (parent != null && current.isDefault() && !parent.isDefault() ? parent : current) : parent;
   }

   private static Property mergeProperty(Property current, PropertyModel parentProp, JsonbAnnotatedElement<Class<?>> classElement) {
      Field field = current.getField() != null ? current.getField() : parentProp.getField();
      Method getter = selectMostSpecificNonDefaultMethod(current.getGetter(), parentProp.getGetter());
      Method setter = selectMostSpecificNonDefaultMethod(current.getSetter(), parentProp.getSetter());
      Property merged = new Property(parentProp.getPropertyName(), classElement);
      if (field != null) {
         merged.setField(field);
      }

      if (getter != null) {
         merged.setGetter(getter);
      }

      if (setter != null) {
         merged.setSetter(setter);
      }

      return merged;
   }
}
