package org.eclipse.yasson.internal.model;

import jakarta.json.bind.JsonbException;
import jakarta.json.bind.config.PropertyNamingStrategy;
import jakarta.json.bind.config.PropertyVisibilityStrategy;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.EnumSet;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import org.eclipse.yasson.internal.AnnotationIntrospector;
import org.eclipse.yasson.internal.JsonbContext;
import org.eclipse.yasson.internal.JsonbDateFormatter;
import org.eclipse.yasson.internal.JsonbNumberFormatter;
import org.eclipse.yasson.internal.components.AdapterBinding;
import org.eclipse.yasson.internal.components.SerializerBinding;
import org.eclipse.yasson.internal.model.customization.PropertyCustomization;

public final class PropertyModel implements Comparable<PropertyModel> {
   private static final Lookup LOOKUP = ModulesUtil.lookup();
   private final String propertyName;
   private final String readName;
   private final String writeName;
   private final Type propertyType;
   private final ClassModel classModel;
   private final Property property;
   private final PropertyCustomization customization;
   private final MethodHandle getValueHandle;
   private final MethodHandle setValueHandle;
   private final Field field;
   private final Method getter;
   private final Method setter;
   private final Type getterMethodType;
   private final Type setterMethodType;

   public PropertyModel(PropertyModel a, PropertyModel b) {
      if (!a.equals(b)) {
         throw new IllegalStateException("Property models " + a + " and " + b + " cannot be merged");
      } else {
         this.classModel = a.classModel;
         this.propertyName = a.propertyName;
         this.readName = a.readName;
         this.writeName = a.writeName;
         this.propertyType = a.propertyType;
         this.customization = a.customization;
         this.getterMethodType = a.getterMethodType != null ? a.getterMethodType : b.getterMethodType;
         this.setterMethodType = a.setterMethodType != null ? a.setterMethodType : b.setterMethodType;
         this.property = a.property;
         if (b.property.getField() != null) {
            this.property.setField(b.property.getField());
         }

         if (b.property.getGetter() != null) {
            this.property.setGetter(b.property.getGetter());
         }

         if (b.property.getSetter() != null) {
            this.property.setSetter(b.property.getSetter());
         }

         this.field = this.property.getField();
         this.getter = this.property.getGetter();
         this.setter = this.property.getSetter();
         PropertyVisibilityStrategy strategy = this.classModel.getClassCustomization().getPropertyVisibilityStrategy();
         this.getValueHandle = createReadHandle(this.field, this.getter, isMethodVisible(this.getter, strategy), strategy);
         this.setValueHandle = createWriteHandle(this.field, this.setter, isMethodVisible(this.setter, strategy), strategy);
      }
   }

   public PropertyModel(ClassModel classModel, Property property, JsonbContext jsonbContext) {
      this.classModel = classModel;
      this.property = property;
      this.propertyName = property.getName();
      this.propertyType = property.getPropertyType();
      this.field = property.getField();
      this.getter = property.getGetter();
      this.setter = property.getSetter();
      PropertyVisibilityStrategy strategy = classModel.getClassCustomization().getPropertyVisibilityStrategy();
      boolean getterVisible = isMethodVisible(this.getter, strategy);
      boolean setterVisible = isMethodVisible(this.setter, strategy);
      this.getValueHandle = createReadHandle(this.field, this.getter, getterVisible, strategy);
      this.setValueHandle = createWriteHandle(this.field, this.setter, setterVisible, strategy);
      this.getterMethodType = getterVisible ? property.getGetterType() : null;
      this.setterMethodType = setterVisible ? property.getSetterType() : null;
      this.customization = this.introspectCustomization(property, jsonbContext, classModel);
      this.readName = calculateReadWriteName(
         this.customization.getJsonReadName(), this.propertyName, jsonbContext.getConfigProperties().getPropertyNamingStrategy()
      );
      this.writeName = calculateReadWriteName(
         this.customization.getJsonWriteName(), this.propertyName, jsonbContext.getConfigProperties().getPropertyNamingStrategy()
      );
   }

   public Type getPropertyDeserializationType() {
      return this.setterMethodType == null ? this.propertyType : this.setterMethodType;
   }

   public Type getPropertySerializationType() {
      return this.getterMethodType == null ? this.propertyType : this.getterMethodType;
   }

   private SerializerBinding<?> getUserSerializerBinding(Property property, JsonbContext jsonbContext) {
      SerializerBinding<?> serializerBinding = jsonbContext.getAnnotationIntrospector().getSerializerBinding(property);
      return serializerBinding != null
         ? serializerBinding
         : jsonbContext.getComponentMatcher().getSerializerBinding(this.getPropertySerializationType(), null).orElse(null);
   }

   private PropertyCustomization introspectCustomization(Property property, JsonbContext jsonbContext, ClassModel classModel) {
      AnnotationIntrospector introspector = jsonbContext.getAnnotationIntrospector();
      PropertyCustomization.Builder builder = PropertyCustomization.builder();
      EnumSet<AnnotationTarget> transientInfo = introspector.getJsonbTransientCategorized(property);
      ClassModel parent = classModel;

      while ((parent = parent.getParentClassModel()) != null) {
         PropertyModel parentProperty = parent.getPropertyModel(property.getName());
         if (parentProperty != null) {
            if (parentProperty.customization.isReadTransient()) {
               transientInfo.add(AnnotationTarget.GETTER);
            }

            if (parentProperty.customization.isWriteTransient()) {
               transientInfo.add(AnnotationTarget.SETTER);
            }
         }
      }

      if (transientInfo.size() != 0) {
         builder.readTransient(transientInfo.contains(AnnotationTarget.GETTER));
         builder.writeTransient(transientInfo.contains(AnnotationTarget.SETTER));
         if (transientInfo.contains(AnnotationTarget.PROPERTY)) {
            if (!transientInfo.contains(AnnotationTarget.GETTER)) {
               builder.readTransient(true);
            }

            if (!transientInfo.contains(AnnotationTarget.SETTER)) {
               builder.writeTransient(true);
            }
         }

         if (builder.readTransient()) {
            introspector.checkTransientIncompatible(property.getFieldElement());
            introspector.checkTransientIncompatible(property.getGetterElement());
         }

         if (builder.writeTransient()) {
            introspector.checkTransientIncompatible(property.getFieldElement());
            introspector.checkTransientIncompatible(property.getSetterElement());
         }
      }

      if (!builder.readTransient()) {
         builder.jsonWriteName(introspector.getJsonbPropertyJsonWriteName(property));
         builder.nillable(introspector.isPropertyNillable(property).orElse(classModel.getClassCustomization().isNillable()));
         builder.serializerBinding(this.getUserSerializerBinding(property, jsonbContext));
      }

      if (!builder.writeTransient()) {
         builder.jsonReadName(introspector.getJsonbPropertyJsonReadName(property));
         builder.deserializerBinding(introspector.getDeserializerBinding(property));
      }

      AdapterBinding adapterBinding = jsonbContext.getAnnotationIntrospector().getAdapterBinding(property);
      if (adapterBinding != null) {
         builder.serializeAdapter(adapterBinding);
         builder.deserializeAdapter(adapterBinding);
      } else {
         builder.serializeAdapter(jsonbContext.getComponentMatcher().getSerializeAdapterBinding(this.getPropertySerializationType(), null).orElse(null));
         builder.deserializeAdapter(jsonbContext.getComponentMatcher().getDeserializeAdapterBinding(this.getPropertyDeserializationType(), null).orElse(null));
      }

      introspectDateFormatter(property, introspector, builder, jsonbContext);
      introspectNumberFormatter(property, introspector, builder);
      builder.implementationClass(introspector.getImplementationClass(property));
      return builder.build();
   }

   private static void introspectDateFormatter(
      Property property, AnnotationIntrospector introspector, PropertyCustomization.Builder builder, JsonbContext jsonbContext
   ) {
      Map<AnnotationTarget, JsonbDateFormatter> jsonDateFormatCategorized = introspector.getJsonbDateFormatCategorized(property);
      JsonbDateFormatter configDateFormatter = jsonbContext.getConfigProperties().getConfigDateFormatter();
      if (!builder.readTransient()) {
         JsonbDateFormatter dateFormatter = getTargetForMostPreciseScope(
            jsonDateFormatCategorized, AnnotationTarget.GETTER, AnnotationTarget.PROPERTY, AnnotationTarget.CLASS
         );
         builder.serializeDateFormatter(dateFormatter != null ? dateFormatter : configDateFormatter);
      }

      if (!builder.writeTransient()) {
         JsonbDateFormatter dateFormatter = getTargetForMostPreciseScope(
            jsonDateFormatCategorized, AnnotationTarget.SETTER, AnnotationTarget.PROPERTY, AnnotationTarget.CLASS
         );
         builder.deserializeDateFormatter(dateFormatter != null ? dateFormatter : configDateFormatter);
      }
   }

   private static void introspectNumberFormatter(Property property, AnnotationIntrospector introspector, PropertyCustomization.Builder builder) {
      Map<AnnotationTarget, JsonbNumberFormatter> jsonNumberFormatCategorized = introspector.getJsonNumberFormatter(property);
      if (!builder.readTransient()) {
         builder.serializeNumberFormatter(
            getTargetForMostPreciseScope(jsonNumberFormatCategorized, AnnotationTarget.GETTER, AnnotationTarget.PROPERTY, AnnotationTarget.CLASS)
         );
      }

      if (!builder.writeTransient()) {
         builder.deserializeNumberFormatter(
            getTargetForMostPreciseScope(jsonNumberFormatCategorized, AnnotationTarget.SETTER, AnnotationTarget.PROPERTY, AnnotationTarget.CLASS)
         );
      }
   }

   private static <T> T getTargetForMostPreciseScope(Map<AnnotationTarget, T> collectedAnnotations, AnnotationTarget... targets) {
      for (AnnotationTarget target : targets) {
         T result = collectedAnnotations.get(target);
         if (result != null) {
            return result;
         }
      }

      return null;
   }

   public Object getValue(Object object) {
      try {
         return (Object)this.getValueHandle.invoke((Object)object);
      } catch (Throwable var3) {
         throw new JsonbException("Error getting value on: " + object, var3);
      }
   }

   public void setValue(Object object, Object value) {
      if (this.isWritable()) {
         try {
            this.setValueHandle.invoke((Object)object, (Object)value);
         } catch (Throwable var4) {
            throw new JsonbException("Error setting value on: " + object, var4);
         }
      }
   }

   public boolean isReadable() {
      return !this.customization.isReadTransient() && this.getValueHandle != null;
   }

   public boolean isWritable() {
      return !this.customization.isWriteTransient() && this.setValueHandle != null;
   }

   public String getPropertyName() {
      return this.propertyName;
   }

   public ClassModel getClassModel() {
      return this.classModel;
   }

   public PropertyCustomization getCustomization() {
      return this.customization;
   }

   public int compareTo(PropertyModel o) {
      int compare = this.readName.compareTo(o.readName);
      return compare == 0 ? this.writeName.compareTo(o.writeName) : compare;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         PropertyModel other = (PropertyModel)o;
         return Objects.equals(this.readName, other.readName) && Objects.equals(this.writeName, other.writeName);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.readName, this.writeName);
   }

   public String getReadName() {
      return this.readName;
   }

   public String getWriteName() {
      return this.writeName;
   }

   private static String calculateReadWriteName(String readWriteName, String propertyName, PropertyNamingStrategy strategy) {
      return readWriteName != null ? readWriteName : strategy.translateName(propertyName);
   }

   public Field getField() {
      return this.field;
   }

   public Method getGetter() {
      return this.getter;
   }

   public Method getSetter() {
      return this.setter;
   }

   public static boolean isPropertyReadable(Field field, Method getter, PropertyVisibilityStrategy strategy) {
      return createReadHandle(field, getter, isMethodVisible(getter, strategy), strategy) != null;
   }

   private static MethodHandle createReadHandle(Field field, Method getter, boolean getterVisible, PropertyVisibilityStrategy strategy) {
      boolean fieldReadable = field == null || (field.getModifiers() & 136) == 0;
      if (fieldReadable) {
         if (getter != null && getterVisible) {
            try {
               return LOOKUP.unreflect(getter);
            } catch (Throwable var6) {
               throw new JsonbException("Error accessing getter '" + getter.getName() + "' declared in '" + getter.getDeclaringClass() + "'", var6);
            }
         }

         if (isFieldVisible(field, getter, strategy)) {
            try {
               return LOOKUP.unreflectGetter(field);
            } catch (IllegalAccessException var7) {
               throw new JsonbException("Error accessing field '" + field.getName() + "' declared in '" + field.getDeclaringClass() + "'", var7);
            }
         }
      }

      return null;
   }

   private static MethodHandle createWriteHandle(Field field, Method setter, boolean setterVisible, PropertyVisibilityStrategy strategy) {
      boolean fieldWritable = field == null || (field.getModifiers() & 152) == 0;
      if (fieldWritable) {
         if (setter != null && setterVisible && !setter.getDeclaringClass().isAnonymousClass()) {
            try {
               return LOOKUP.unreflect(setter);
            } catch (IllegalAccessException var6) {
               throw new JsonbException("Error accessing setter '" + setter.getName() + "' declared in '" + setter.getDeclaringClass() + "'", var6);
            }
         }

         if (isFieldVisible(field, setter, strategy) && !field.getDeclaringClass().isAnonymousClass()) {
            try {
               return LOOKUP.unreflectSetter(field);
            } catch (IllegalAccessException var7) {
               throw new JsonbException("Error accessing field '" + field.getName() + "' declared in '" + field.getDeclaringClass() + "'", var7);
            }
         }
      }

      return null;
   }

   private static boolean isFieldVisible(Field field, Method method, PropertyVisibilityStrategy strategy) {
      if (field == null) {
         return false;
      } else {
         boolean accessible = isVisible(strat -> strat.isVisible(field), method, strategy);
         if (accessible
            && (!Modifier.isPublic(field.getModifiers()) || field.getDeclaringClass().isAnonymousClass() || isNotPublicAndNonNested(field.getDeclaringClass()))
            )
          {
            overrideAccessible(field);
         }

         return accessible;
      }
   }

   private static boolean isNotPublicAndNonNested(Class<?> declaringClass) {
      return !declaringClass.isMemberClass() && !Modifier.isPublic(declaringClass.getModifiers());
   }

   private static boolean isMethodVisible(Method method, PropertyVisibilityStrategy strategy) {
      if (method != null && !Modifier.isStatic(method.getModifiers())) {
         boolean accessible = isVisible(strat -> strat.isVisible(method), method, strategy);
         if (accessible
            && (!Modifier.isPublic(method.getModifiers()) || method.getDeclaringClass().isAnonymousClass() || method.getDeclaringClass().isSynthetic())) {
            overrideAccessible(method);
         }

         return accessible;
      } else {
         return false;
      }
   }

   private static void overrideAccessible(AccessibleObject accessibleObject) {
      AccessController.doPrivileged((PrivilegedAction<Void>)(() -> {
         accessibleObject.setAccessible(true);
         return null;
      }));
   }

   private static boolean isVisible(Predicate<PropertyVisibilityStrategy> visibilityCheckFunction, Method method, PropertyVisibilityStrategy strategy) {
      return strategy != null ? visibilityCheckFunction.test(strategy) : visibilityCheckFunction.test(new PropertyModel.DefaultVisibilityStrategy(method));
   }

   public MethodHandle getGetValueHandle() {
      return this.getValueHandle;
   }

   public MethodHandle getSetValueHandle() {
      return this.setValueHandle;
   }

   private static final class DefaultVisibilityStrategy implements PropertyVisibilityStrategy {
      private final Method method;

      DefaultVisibilityStrategy(Method method) {
         this.method = method;
      }

      public boolean isVisible(Field field) {
         return (this.method == null || this.isVisible(this.method)) && Modifier.isPublic(field.getModifiers());
      }

      public boolean isVisible(Method method) {
         return Modifier.isPublic(method.getModifiers());
      }
   }
}
