package org.eclipse.yasson.internal.model;

import jakarta.json.bind.config.PropertyNamingStrategy;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import org.eclipse.yasson.internal.ClassMultiReleaseExtension;
import org.eclipse.yasson.internal.ReflectionUtils;
import org.eclipse.yasson.internal.model.customization.ClassCustomization;
import org.eclipse.yasson.internal.model.customization.StrategiesProvider;

public class ClassModel {
   private final Class<?> clazz;
   private final ClassCustomization classCustomization;
   private final ClassModel parentClassModel;
   private final AtomicBoolean isInitialized = new AtomicBoolean(false);
   private Constructor<?> defaultConstructor;
   private Map<String, PropertyModel> properties;
   private PropertyModel[] sortedProperties;
   private final PropertyNamingStrategy propertyNamingStrategy;

   public PropertyModel getPropertyModel(String name) {
      return this.properties.get(name);
   }

   public ClassModel(Class<?> clazz, ClassCustomization customization, ClassModel parentClassModel, PropertyNamingStrategy propertyNamingStrategy) {
      this.clazz = clazz;
      this.classCustomization = customization;
      this.parentClassModel = parentClassModel;
      this.propertyNamingStrategy = propertyNamingStrategy;
      this.setProperties(new ArrayList<>());
   }

   public PropertyModel findPropertyModelByJsonReadName(String jsonReadName) {
      Objects.requireNonNull(jsonReadName);
      return this.searchProperty(this, jsonReadName);
   }

   private PropertyModel searchProperty(ClassModel classModel, String jsonReadName) {
      PropertyModel result = classModel.getPropertyModel(jsonReadName);
      if (result != null && result.getPropertyName().equals(result.getReadName())) {
         return result;
      } else {
         for (PropertyModel propertyModel : this.properties.values()) {
            if (this.equalsReadName(jsonReadName, propertyModel)) {
               return propertyModel;
            }
         }

         return null;
      }
   }

   private boolean equalsReadName(String jsonName, PropertyModel propertyModel) {
      String propertyReadName = propertyModel.getReadName();
      return this.propertyNamingStrategy == StrategiesProvider.CASE_INSENSITIVE_STRATEGY
         ? jsonName.equalsIgnoreCase(propertyReadName)
         : jsonName.equals(propertyReadName);
   }

   public Class<?> getType() {
      return this.clazz;
   }

   public ClassCustomization getClassCustomization() {
      return this.classCustomization;
   }

   public ClassModel getParentClassModel() {
      return this.parentClassModel;
   }

   public PropertyModel[] getSortedProperties() {
      return this.sortedProperties;
   }

   public void setProperties(List<PropertyModel> parsedProperties) {
      this.sortedProperties = parsedProperties.toArray(new PropertyModel[0]);
      this.properties = parsedProperties.stream().collect(Collectors.toMap(PropertyModel::getPropertyName, mod -> (PropertyModel)mod));
   }

   public Map<String, PropertyModel> getProperties() {
      return Collections.unmodifiableMap(this.properties);
   }

   public Constructor<?> getDefaultConstructor() {
      if (!this.isInitialized.get()) {
         if (ClassMultiReleaseExtension.isRecord(this.clazz)) {
            this.defaultConstructor = null;
         } else {
            this.defaultConstructor = ReflectionUtils.getDefaultConstructor(this.clazz, false);
         }

         this.isInitialized.set(true);
      }

      return this.defaultConstructor;
   }

   @Override
   public String toString() {
      return "ClassModel{clazz=" + this.clazz + "}";
   }
}
