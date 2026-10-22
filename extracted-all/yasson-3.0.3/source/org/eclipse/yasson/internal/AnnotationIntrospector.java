package org.eclipse.yasson.internal;

import jakarta.json.bind.JsonbException;
import jakarta.json.bind.adapter.JsonbAdapter;
import jakarta.json.bind.annotation.JsonbDateFormat;
import jakarta.json.bind.annotation.JsonbNillable;
import jakarta.json.bind.annotation.JsonbNumberFormat;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.json.bind.annotation.JsonbPropertyOrder;
import jakarta.json.bind.annotation.JsonbSubtype;
import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.json.bind.annotation.JsonbTypeAdapter;
import jakarta.json.bind.annotation.JsonbTypeDeserializer;
import jakarta.json.bind.annotation.JsonbTypeInfo;
import jakarta.json.bind.annotation.JsonbTypeSerializer;
import jakarta.json.bind.annotation.JsonbVisibility;
import jakarta.json.bind.config.PropertyNamingStrategy;
import jakarta.json.bind.config.PropertyVisibilityStrategy;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.bind.serializer.JsonbSerializer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.security.AccessController;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.Queue;
import java.util.Set;
import org.eclipse.yasson.ImplementationClass;
import org.eclipse.yasson.internal.components.AdapterBinding;
import org.eclipse.yasson.internal.components.DeserializerBinding;
import org.eclipse.yasson.internal.components.SerializerBinding;
import org.eclipse.yasson.internal.model.AnnotationTarget;
import org.eclipse.yasson.internal.model.CreatorModel;
import org.eclipse.yasson.internal.model.JsonbAnnotatedElement;
import org.eclipse.yasson.internal.model.JsonbCreator;
import org.eclipse.yasson.internal.model.Property;
import org.eclipse.yasson.internal.model.customization.ClassCustomization;
import org.eclipse.yasson.internal.model.customization.TypeInheritanceConfiguration;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

public class AnnotationIntrospector {
   private final JsonbContext jsonbContext;
   private final ConstructorPropertiesAnnotationIntrospector constructorPropertiesIntrospector;
   private static final Set<Class<? extends Annotation>> REPEATABLE = Set.of(JsonbTypeInfo.class);
   private static final List<Class<? extends Annotation>> TRANSIENT_INCOMPATIBLE = Arrays.asList(
      JsonbDateFormat.class, JsonbNumberFormat.class, JsonbProperty.class, JsonbTypeAdapter.class, JsonbTypeSerializer.class, JsonbTypeDeserializer.class
   );

   public AnnotationIntrospector(JsonbContext jsonbContext) {
      Objects.requireNonNull(jsonbContext);
      this.jsonbContext = jsonbContext;
      this.constructorPropertiesIntrospector = ConstructorPropertiesAnnotationIntrospector.forContext(jsonbContext);
   }

   public String getJsonbPropertyJsonWriteName(Property property) {
      Objects.requireNonNull(property);
      return this.getJsonbPropertyCustomizedName(property, property.getGetterElement());
   }

   public String getJsonbPropertyJsonReadName(Property property) {
      Objects.requireNonNull(property);
      return this.getJsonbPropertyCustomizedName(property, property.getSetterElement());
   }

   private String getJsonbPropertyCustomizedName(Property property, JsonbAnnotatedElement<Method> methodElement) {
      JsonbProperty methodAnnotation = this.getMethodAnnotation(JsonbProperty.class, methodElement);
      if (methodAnnotation != null && !methodAnnotation.value().isEmpty()) {
         return methodAnnotation.value();
      } else {
         JsonbProperty fieldAnnotation = this.getFieldAnnotation(JsonbProperty.class, property.getFieldElement());
         return fieldAnnotation != null && !fieldAnnotation.value().isEmpty() ? fieldAnnotation.value() : null;
      }
   }

   public JsonbCreator getCreator(Class<?> clazz, PropertyNamingStrategy propertyNamingStrategy) {
      JsonbCreator jsonbCreator = null;
      Constructor<?>[] declaredConstructors = AccessController.doPrivileged(clazz::getDeclaredConstructors);

      for (Constructor<?> constructor : declaredConstructors) {
         jakarta.json.bind.annotation.JsonbCreator annot = this.findAnnotation(
            constructor.getDeclaredAnnotations(), jakarta.json.bind.annotation.JsonbCreator.class
         );
         if (annot != null) {
            jsonbCreator = this.createJsonbCreator(constructor, jsonbCreator, clazz, propertyNamingStrategy);
         }
      }

      Method[] declaredMethods = AccessController.doPrivileged(clazz::getDeclaredMethods);

      for (Method method : declaredMethods) {
         jakarta.json.bind.annotation.JsonbCreator annot = this.findAnnotation(method.getDeclaredAnnotations(), jakarta.json.bind.annotation.JsonbCreator.class);
         if (annot != null && Modifier.isStatic(method.getModifiers())) {
            if (!clazz.equals(method.getReturnType())) {
               throw new JsonbException(Messages.getMessage(MessageKeys.INCOMPATIBLE_FACTORY_CREATOR_RETURN_TYPE, method, clazz));
            }

            jsonbCreator = this.createJsonbCreator(method, jsonbCreator, clazz, propertyNamingStrategy);
         }
      }

      if (jsonbCreator == null) {
         jsonbCreator = ClassMultiReleaseExtension.findCreator(clazz, declaredConstructors, this, propertyNamingStrategy);
         if (jsonbCreator == null) {
            jsonbCreator = this.constructorPropertiesIntrospector.getCreator(declaredConstructors);
         }
      }

      return jsonbCreator;
   }

   JsonbCreator createJsonbCreator(Executable executable, JsonbCreator existing, Class<?> clazz, PropertyNamingStrategy propertyNamingStrategy) {
      if (existing != null) {
         throw new JsonbException(Messages.getMessage(MessageKeys.MULTIPLE_JSONB_CREATORS, clazz));
      } else {
         Parameter[] parameters = executable.getParameters();
         CreatorModel[] creatorModels = new CreatorModel[parameters.length];

         for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            JsonbProperty jsonbPropertyAnnotation = parameter.getAnnotation(JsonbProperty.class);
            if (jsonbPropertyAnnotation != null && !jsonbPropertyAnnotation.value().isEmpty()) {
               creatorModels[i] = new CreatorModel(jsonbPropertyAnnotation.value(), parameter, executable, this.jsonbContext);
            } else {
               String translatedParameterName = propertyNamingStrategy.translateName(parameter.getName());
               creatorModels[i] = new CreatorModel(translatedParameterName, parameter, executable, this.jsonbContext);
            }
         }

         return new JsonbCreator(executable, creatorModels);
      }
   }

   public AdapterBinding getAdapterBinding(Property property) {
      Objects.requireNonNull(property);
      JsonbTypeAdapter adapterAnnotation = this.getAnnotationFromProperty(JsonbTypeAdapter.class, property)
         .orElseGet(() -> this.getAnnotationFromPropertyType(property, JsonbTypeAdapter.class));
      return adapterAnnotation == null
         ? null
         : this.getAdapterBindingFromAnnotation(adapterAnnotation, ReflectionUtils.getOptionalRawType(property.getPropertyType()));
   }

   public AdapterBinding getAdapterBinding(JsonbAnnotatedElement<Class<?>> clsElement) {
      Objects.requireNonNull(clsElement);
      JsonbTypeAdapter adapterAnnotation = clsElement.getElement().getAnnotation(JsonbTypeAdapter.class);
      return adapterAnnotation == null ? null : this.getAdapterBindingFromAnnotation(adapterAnnotation, Optional.ofNullable(clsElement.getElement()));
   }

   private AdapterBinding getAdapterBindingFromAnnotation(JsonbTypeAdapter adapterAnnotation, Optional<Class<?>> expectedClass) {
      Class<? extends JsonbAdapter> adapterClass = adapterAnnotation.value();
      AdapterBinding adapterBinding = this.jsonbContext.getComponentMatcher().introspectAdapterBinding(adapterClass, null);
      if (expectedClass.isPresent() && !ReflectionUtils.getRawType(adapterBinding.getBindingType()).isAssignableFrom(expectedClass.get())) {
         throw new JsonbException(Messages.getMessage(MessageKeys.ADAPTER_INCOMPATIBLE, adapterBinding.getBindingType(), expectedClass.get()));
      } else {
         return adapterBinding;
      }
   }

   public DeserializerBinding getDeserializerBinding(Property property) {
      Objects.requireNonNull(property);
      JsonbTypeDeserializer deserializerAnnotation = this.getAnnotationFromProperty(JsonbTypeDeserializer.class, property)
         .orElseGet(() -> this.getAnnotationFromPropertyType(property, JsonbTypeDeserializer.class));
      if (deserializerAnnotation == null) {
         return null;
      } else {
         Class<? extends JsonbDeserializer> deserializerClass = deserializerAnnotation.value();
         return this.jsonbContext.getComponentMatcher().introspectDeserializerBinding(deserializerClass, null);
      }
   }

   public DeserializerBinding<?> getDeserializerBinding(Parameter parameter) {
      Objects.requireNonNull(parameter);
      JsonbTypeDeserializer deserializerAnnotation = Optional.ofNullable(parameter.getDeclaredAnnotation(JsonbTypeDeserializer.class))
         .orElseGet(() -> this.getAnnotationFromParameterType(parameter, JsonbTypeDeserializer.class));
      if (deserializerAnnotation == null) {
         return null;
      } else {
         Class<? extends JsonbDeserializer> deserializerClass = deserializerAnnotation.value();
         return this.jsonbContext.getComponentMatcher().introspectDeserializerBinding(deserializerClass, null);
      }
   }

   public AdapterBinding getAdapterBinding(Parameter parameter) {
      Objects.requireNonNull(parameter);
      JsonbTypeAdapter adapter = Optional.ofNullable(parameter.getDeclaredAnnotation(JsonbTypeAdapter.class))
         .orElseGet(() -> this.getAnnotationFromParameterType(parameter, JsonbTypeAdapter.class));
      return adapter == null ? null : this.getAdapterBindingFromAnnotation(adapter, ReflectionUtils.getOptionalRawType(parameter.getParameterizedType()));
   }

   private <T extends Annotation> T getAnnotationFromParameterType(Parameter parameter, Class<T> annotationClass) {
      Optional<Class<?>> optionalRawType = ReflectionUtils.getOptionalRawType(parameter.getParameterizedType());
      return optionalRawType.<T>map(aClass -> this.findAnnotation(this.collectAnnotations((Class<?>)aClass).getAnnotations(), annotationClass)).orElse(null);
   }

   public DeserializerBinding getDeserializerBinding(JsonbAnnotatedElement<Class<?>> clsElement) {
      Objects.requireNonNull(clsElement);
      JsonbTypeDeserializer deserializerAnnotation = clsElement.getElement().getAnnotation(JsonbTypeDeserializer.class);
      if (deserializerAnnotation == null) {
         return null;
      } else {
         Class<? extends JsonbDeserializer> deserializerClass = deserializerAnnotation.value();
         return this.jsonbContext.getComponentMatcher().introspectDeserializerBinding(deserializerClass, null);
      }
   }

   public SerializerBinding getSerializerBinding(Property property) {
      Objects.requireNonNull(property);
      JsonbTypeSerializer serializerAnnotation = this.getAnnotationFromProperty(JsonbTypeSerializer.class, property)
         .orElseGet(() -> this.getAnnotationFromPropertyType(property, JsonbTypeSerializer.class));
      if (serializerAnnotation == null) {
         return null;
      } else {
         Class<? extends JsonbSerializer> serializerClass = serializerAnnotation.value();
         return this.jsonbContext.getComponentMatcher().introspectSerializerBinding(serializerClass, null);
      }
   }

   public SerializerBinding getSerializerBinding(JsonbAnnotatedElement<Class<?>> clsElement) {
      Objects.requireNonNull(clsElement);
      JsonbTypeSerializer serializerAnnotation = clsElement.getElement().getAnnotation(JsonbTypeSerializer.class);
      if (serializerAnnotation == null) {
         return null;
      } else {
         Class<? extends JsonbSerializer> serializerClass = serializerAnnotation.value();
         return this.jsonbContext.getComponentMatcher().introspectSerializerBinding(serializerClass, null);
      }
   }

   private <T extends Annotation> T getAnnotationFromPropertyType(Property property, Class<T> annotationClass) {
      Optional<Class<?>> optionalRawType = ReflectionUtils.getOptionalRawType(property.getPropertyType());
      return !optionalRawType.isPresent() ? null : this.findAnnotation(this.collectAnnotations(optionalRawType.get()).getAnnotations(), annotationClass);
   }

   public Optional<Boolean> isPropertyNillable(Property property) {
      Objects.requireNonNull(property);
      Optional<JsonbNillable> nillable = this.getAnnotationFromProperty(JsonbNillable.class, property);
      if (nillable.isPresent()) {
         return nillable.map(JsonbNillable::value);
      } else {
         Optional<JsonbProperty> jsonbProperty = this.getAnnotationFromProperty(JsonbProperty.class, property);
         return jsonbProperty.map(JsonbProperty::nillable);
      }
   }

   public boolean isClassNillable(JsonbAnnotatedElement<Class<?>> clazzElement) {
      JsonbNillable jsonbNillable = this.findAnnotation(clazzElement.getAnnotations(), JsonbNillable.class);
      if (jsonbNillable != null) {
         return jsonbNillable.value();
      } else {
         Class<?> clazz = clazzElement.getElement();
         return clazz != Optional.class && clazz != OptionalDouble.class && clazz != OptionalInt.class && clazz != OptionalLong.class
            ? this.jsonbContext.getConfigProperties().getConfigNullable()
            : true;
      }
   }

   public String[] getPropertyOrder(JsonbAnnotatedElement<Class<?>> clazzElement) {
      JsonbPropertyOrder jsonbPropertyOrder = clazzElement.getElement().getAnnotation(JsonbPropertyOrder.class);
      return jsonbPropertyOrder != null ? jsonbPropertyOrder.value() : null;
   }

   public EnumSet<AnnotationTarget> getJsonbTransientCategorized(Property property) {
      EnumSet<AnnotationTarget> transientTarget = EnumSet.noneOf(AnnotationTarget.class);
      Map<AnnotationTarget, JsonbTransient> annotationFromPropertyCategorized = this.getAnnotationFromPropertyCategorized(JsonbTransient.class, property);
      if (annotationFromPropertyCategorized.size() > 0) {
         transientTarget.addAll(annotationFromPropertyCategorized.keySet());
         return transientTarget;
      } else {
         return transientTarget;
      }
   }

   public Map<AnnotationTarget, JsonbDateFormatter> getJsonbDateFormatCategorized(Property property) {
      Objects.requireNonNull(property);
      Map<AnnotationTarget, JsonbDateFormatter> result = new HashMap<>();
      Map<AnnotationTarget, JsonbDateFormat> annotationFromPropertyCategorized = this.getAnnotationFromPropertyCategorized(JsonbDateFormat.class, property);
      if (annotationFromPropertyCategorized.size() != 0) {
         annotationFromPropertyCategorized.forEach(
            (key, annotation) -> result.put(key, this.createJsonbDateFormatter(annotation.value(), annotation.locale(), property))
         );
      }

      Optional<Class<?>> propertyRawTypeOptional = ReflectionUtils.getOptionalRawType(property.getPropertyType());
      if (propertyRawTypeOptional.isPresent()) {
         Class<?> rawType = propertyRawTypeOptional.get();
         if (!Date.class.isAssignableFrom(rawType) && !Calendar.class.isAssignableFrom(rawType) && !TemporalAccessor.class.isAssignableFrom(rawType)) {
            return new HashMap<>();
         }
      }

      JsonbDateFormat classLevelDateFormatter = this.findAnnotation(property.getDeclaringClassElement().getAnnotations(), JsonbDateFormat.class);
      if (classLevelDateFormatter != null) {
         result.put(AnnotationTarget.CLASS, this.createJsonbDateFormatter(classLevelDateFormatter.value(), classLevelDateFormatter.locale(), property));
      }

      return result;
   }

   public JsonbDateFormatter getJsonbDateFormat(JsonbAnnotatedElement<Class<?>> clazzElement) {
      Objects.requireNonNull(clazzElement);
      JsonbDateFormat format = this.findAnnotation(clazzElement.getAnnotations(), JsonbDateFormat.class);
      return format == null ? this.jsonbContext.getConfigProperties().getConfigDateFormatter() : new JsonbDateFormatter(format.value(), format.locale());
   }

   public JsonbNumberFormatter getJsonbNumberFormat(JsonbAnnotatedElement<Class<?>> clazzElement) {
      JsonbNumberFormat formatAnnotation = this.findAnnotation(clazzElement.getAnnotations(), JsonbNumberFormat.class);
      return formatAnnotation == null ? null : new JsonbNumberFormatter(formatAnnotation.value(), formatAnnotation.locale());
   }

   public Map<AnnotationTarget, JsonbNumberFormatter> getJsonNumberFormatter(Property property) {
      Map<AnnotationTarget, JsonbNumberFormatter> result = new HashMap<>();
      Map<AnnotationTarget, JsonbNumberFormat> annotationFromPropertyCategorized = this.getAnnotationFromPropertyCategorized(JsonbNumberFormat.class, property);
      annotationFromPropertyCategorized.forEach((key, annotation) -> result.put(key, new JsonbNumberFormatter(annotation.value(), annotation.locale())));
      JsonbNumberFormat classLevelNumberFormatter = this.findAnnotation(property.getDeclaringClassElement().getAnnotations(), JsonbNumberFormat.class);
      if (classLevelNumberFormatter != null) {
         result.put(AnnotationTarget.CLASS, new JsonbNumberFormatter(classLevelNumberFormatter.value(), classLevelNumberFormatter.locale()));
      }

      return result;
   }

   public JsonbNumberFormatter getConstructorNumberFormatter(JsonbAnnotatedElement<Parameter> param) {
      return param.getAnnotation(JsonbNumberFormat.class).map(annotation -> new JsonbNumberFormatter(annotation.value(), annotation.locale())).orElse(null);
   }

   public JsonbDateFormatter getConstructorDateFormatter(JsonbAnnotatedElement<Parameter> param) {
      return param.getAnnotation(JsonbDateFormat.class)
         .map(
            annotation -> new JsonbDateFormatter(
                  DateTimeFormatter.ofPattern(annotation.value(), Locale.forLanguageTag(annotation.locale())), annotation.value(), annotation.locale()
               )
         )
         .orElse(null);
   }

   private JsonbDateFormatter createJsonbDateFormatter(String format, String locale, Property property) {
      if (!"##time-in-millis".equals(format) && !"##default".equals(format)) {
         Optional<Class<?>> optionalRawType = ReflectionUtils.getOptionalRawType(property.getPropertyType());
         Class<?> propertyRawType = optionalRawType.orElse(null);
         if (propertyRawType != null
            && !TemporalAccessor.class.isAssignableFrom(propertyRawType)
            && !Date.class.isAssignableFrom(propertyRawType)
            && !Calendar.class.isAssignableFrom(propertyRawType)) {
            throw new IllegalStateException(Messages.getMessage(MessageKeys.UNSUPPORTED_DATE_TYPE, propertyRawType));
         } else {
            DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder();
            builder.appendPattern(format);
            if (this.jsonbContext.getConfigProperties().isZeroTimeDefaulting()) {
               builder.parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0L);
               builder.parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0L);
               builder.parseDefaulting(ChronoField.HOUR_OF_DAY, 0L);
            }

            DateTimeFormatter dateTimeFormatter = builder.toFormatter(Locale.forLanguageTag(locale));
            return new JsonbDateFormatter(dateTimeFormatter, format, locale);
         }
      } else {
         return new JsonbDateFormatter(format, locale);
      }
   }

   public PropertyVisibilityStrategy getPropertyVisibilityStrategy(Class<?> clazz) {
      JsonbVisibility visibilityAnnotation = this.findAnnotation(clazz.getDeclaredAnnotations(), JsonbVisibility.class);
      if (visibilityAnnotation == null && clazz.getPackage() != null) {
         visibilityAnnotation = this.findAnnotation(clazz.getPackage().getDeclaredAnnotations(), JsonbVisibility.class);
      }

      return visibilityAnnotation != null
         ? ReflectionUtils.createNoArgConstructorInstance(ReflectionUtils.getDefaultConstructor(visibilityAnnotation.value(), true))
         : this.jsonbContext.getConfigProperties().getPropertyVisibilityStrategy();
   }

   private <T extends Annotation> Optional<T> getAnnotationFromProperty(Class<T> annotationClass, Property property) {
      T fieldAnnotation = this.getFieldAnnotation(annotationClass, property.getFieldElement());
      if (fieldAnnotation != null) {
         return Optional.of(fieldAnnotation);
      } else {
         T getterAnnotation = this.getMethodAnnotation(annotationClass, property.getGetterElement());
         if (getterAnnotation != null) {
            return Optional.of(getterAnnotation);
         } else {
            T setterAnnotation = this.getMethodAnnotation(annotationClass, property.getSetterElement());
            return setterAnnotation != null ? Optional.of(setterAnnotation) : Optional.empty();
         }
      }
   }

   private <T extends Annotation> Map<AnnotationTarget, T> getAnnotationFromPropertyCategorized(Class<T> annotationClass, Property property) {
      Map<AnnotationTarget, T> result = new HashMap<>();
      T fieldAnnotation = this.getFieldAnnotation(annotationClass, property.getFieldElement());
      if (fieldAnnotation != null) {
         result.put(AnnotationTarget.PROPERTY, fieldAnnotation);
      }

      T getterAnnotation = this.getMethodAnnotation(annotationClass, property.getGetterElement());
      if (getterAnnotation != null) {
         result.put(AnnotationTarget.GETTER, getterAnnotation);
      }

      T setterAnnotation = this.getMethodAnnotation(annotationClass, property.getSetterElement());
      if (setterAnnotation != null) {
         result.put(AnnotationTarget.SETTER, setterAnnotation);
      }

      return result;
   }

   private <T extends Annotation> T getFieldAnnotation(Class<T> annotationClass, JsonbAnnotatedElement<Field> fieldElement) {
      return fieldElement == null ? null : this.findAnnotation(fieldElement.getAnnotations(), annotationClass);
   }

   private <T extends Annotation> T findAnnotation(Annotation[] declaredAnnotations, Class<T> annotationClass) {
      return AnnotationFinder.findAnnotation(declaredAnnotations, annotationClass, new HashSet<>());
   }

   public void checkTransientIncompatible(JsonbAnnotatedElement<?> target) {
      if (target != null) {
         for (Class<? extends Annotation> ann : TRANSIENT_INCOMPATIBLE) {
            Annotation annotation = this.findAnnotation(target.getAnnotations(), ann);
            if (annotation != null) {
               throw new JsonbException(Messages.getMessage(MessageKeys.JSONB_TRANSIENT_WITH_OTHER_ANNOTATIONS));
            }
         }
      }
   }

   private <T extends Annotation> T getMethodAnnotation(Class<T> annotationClass, JsonbAnnotatedElement<Method> methodElement) {
      return methodElement == null ? null : this.findAnnotation(methodElement.getAnnotations(), annotationClass);
   }

   private <T extends Annotation> void collectFromInterfaces(Class<T> annotationClass, Class<?> clazz, Map<Class<?>, T> collectedAnnotations) {
      for (Class<?> interfaceClass : clazz.getInterfaces()) {
         T annotation = this.findAnnotation(interfaceClass.getDeclaredAnnotations(), annotationClass);
         if (annotation != null) {
            collectedAnnotations.put(interfaceClass, annotation);
         }

         this.collectFromInterfaces(annotationClass, interfaceClass, collectedAnnotations);
      }
   }

   public Set<Class<?>> collectInterfaces(Class<?> cls) {
      Set<Class<?>> collected = new LinkedHashSet<>();
      Queue<Class<?>> toScan = new LinkedList<>(Arrays.asList(cls.getInterfaces()));

      Class<?> nextIfc;
      while ((nextIfc = toScan.poll()) != null) {
         collected.add(nextIfc);
         toScan.addAll(Arrays.asList(nextIfc.getInterfaces()));
      }

      return collected;
   }

   public ClassCustomization introspectCustomization(
      JsonbAnnotatedElement<Class<?>> clsElement, ClassCustomization parentCustomization, PropertyNamingStrategy propertyNamingStrategy
   ) {
      return ((ClassCustomization.Builder)((ClassCustomization.Builder)((ClassCustomization.Builder)((ClassCustomization.Builder)ClassCustomization.builder()
                     .nillable(this.isClassNillable(clsElement)))
                  .dateTimeFormatter(this.getJsonbDateFormat(clsElement))
                  .numberFormatter(this.getJsonbNumberFormat(clsElement))
                  .creator(this.getCreator(clsElement.getElement(), propertyNamingStrategy))
                  .propertyOrder(this.getPropertyOrder(clsElement))
                  .adapterBinding(this.getAdapterBinding(clsElement)))
               .serializerBinding(this.getSerializerBinding(clsElement)))
            .deserializerBinding(this.getDeserializerBinding(clsElement)))
         .propertyVisibilityStrategy(this.getPropertyVisibilityStrategy(clsElement.getElement()))
         .polymorphismConfig(this.getPolymorphismConfig(clsElement, parentCustomization))
         .build();
   }

   private TypeInheritanceConfiguration getPolymorphismConfig(JsonbAnnotatedElement<Class<?>> clsElement, ClassCustomization parentCustomization) {
      TypeInheritanceConfiguration parentPolyConfig = parentCustomization.getPolymorphismConfig();
      LinkedList<JsonbAnnotatedElement.AnnotationWrapper<?>> annotations = clsElement.getAnnotations(JsonbTypeInfo.class);
      if (parentPolyConfig != null) {
         if (annotations.size() == 1 && annotations.getFirst().isInherited()) {
            throw new JsonbException(
               "Cannot process type information from multiple sources! Sources: "
                  + parentPolyConfig.getDefinedType().getName()
                  + " and "
                  + annotations.getFirst()
            );
         }

         if (annotations.size() > 1) {
            throw new JsonbException("Cannot process type information from multiple sources! Sources: " + annotations);
         }

         if (annotations.isEmpty()) {
            return TypeInheritanceConfiguration.builder().of(parentPolyConfig).inherited(true).build();
         }
      }

      ListIterator<JsonbAnnotatedElement.AnnotationWrapper<?>> listIterator = annotations.listIterator(annotations.size());

      while (listIterator.hasPrevious()) {
         JsonbAnnotatedElement.AnnotationWrapper<?> annotationWrapper = listIterator.previous();
         JsonbTypeInfo annotation = (JsonbTypeInfo)annotationWrapper.getAnnotation();
         TypeInheritanceConfiguration.Builder builder = TypeInheritanceConfiguration.builder();
         builder.fieldName(annotation.key())
            .inherited(annotationWrapper.isInherited())
            .parentConfig(parentPolyConfig)
            .definedType(annotationWrapper.getDefinedType());

         for (JsonbSubtype subType : annotation.value()) {
            if (!annotationWrapper.getDefinedType().isAssignableFrom(subType.type())) {
               throw new JsonbException(
                  "Defined alias type has to be child of the current type. JsonbSubType on the "
                     + annotationWrapper.getDefinedType().getName()
                     + " defines incorrect alias "
                     + subType
               );
            }

            builder.alias(subType.type(), subType.alias());
         }

         parentPolyConfig = builder.build();
      }

      this.checkDuplicityPolymorphicPropertyNames(parentPolyConfig);
      return parentPolyConfig;
   }

   private void checkDuplicityPolymorphicPropertyNames(TypeInheritanceConfiguration typeInheritanceConfiguration) {
      if (typeInheritanceConfiguration != null) {
         Map<String, TypeInheritanceConfiguration> keyNames = new HashMap<>();

         for (TypeInheritanceConfiguration current = typeInheritanceConfiguration; current != null; current = current.getParentConfig()) {
            String fieldName = current.getFieldName();
            if (keyNames.containsKey(fieldName)) {
               TypeInheritanceConfiguration conflicting = keyNames.get(fieldName);
               throw new JsonbException(
                  "One polymorphic chain cannot have two conflicting property names. Polymorphic type defined on the type "
                     + conflicting.getDefinedType().getName()
                     + " and "
                     + current.getDefinedType().getName()
                     + " have conflicting property name"
               );
            }

            keyNames.put(fieldName, current);
         }
      }
   }

   public Class<?> getImplementationClass(Property property) {
      Optional<ImplementationClass> annotationFromProperty = this.getAnnotationFromProperty(ImplementationClass.class, property);
      return annotationFromProperty.map(ImplementationClass::value).orElse(null);
   }

   public JsonbAnnotatedElement<Class<?>> collectAnnotations(Class<?> clazz) {
      JsonbAnnotatedElement<Class<?>> classElement = new JsonbAnnotatedElement<>(clazz);
      if (BuiltInTypes.isKnownType(clazz)) {
         return classElement;
      } else {
         Map<Class<? extends Annotation>, LinkedList<JsonbAnnotatedElement.AnnotationWrapper<?>>> interfaceAnnotations = this.collectInterfaceAnnotations(
            clazz, clazz
         );

         for (LinkedList<JsonbAnnotatedElement.AnnotationWrapper<?>> wrappers : interfaceAnnotations.values()) {
            for (JsonbAnnotatedElement.AnnotationWrapper<?> wrapper : wrappers) {
               if (classElement.getAnnotation(wrapper.getAnnotation().annotationType()).isEmpty()
                  || REPEATABLE.contains(wrapper.getAnnotation().annotationType())) {
                  classElement.putAnnotationWrapper(wrapper);
               }
            }
         }

         if (!clazz.isPrimitive() && !clazz.isArray() && clazz.getPackage() != null) {
            this.addIfNotPresent(classElement, null, clazz.getPackage().getAnnotations());
         }

         return classElement;
      }
   }

   private Map<Class<? extends Annotation>, LinkedList<JsonbAnnotatedElement.AnnotationWrapper<?>>> collectInterfaceAnnotations(
      Class<?> currentInterf, Class<?> processed
   ) {
      Map<Class<? extends Annotation>, LinkedList<JsonbAnnotatedElement.AnnotationWrapper<?>>> map = new HashMap<>();
      if (!currentInterf.equals(processed)) {
         for (Annotation annotation : currentInterf.getDeclaredAnnotations()) {
            map.computeIfAbsent(annotation.annotationType(), aClass -> new LinkedList<>())
               .add(new JsonbAnnotatedElement.AnnotationWrapper<>(annotation, true, currentInterf));
         }
      }

      Map<Class<? extends Annotation>, LinkedList<JsonbAnnotatedElement.AnnotationWrapper<?>>> parents = new HashMap<>();

      for (Class<?> parentInterf : currentInterf.getInterfaces()) {
         Map<Class<? extends Annotation>, LinkedList<JsonbAnnotatedElement.AnnotationWrapper<?>>> current = this.collectInterfaceAnnotations(
            parentInterf, processed
         );
         current.entrySet().stream().filter(entry -> !parents.containsKey(entry.getKey()) || REPEATABLE.contains(entry.getKey())).peek(entry -> {
            if (parents.containsKey(entry.getKey())) {
               throw new JsonbException("Cannot process annotation " + entry.getKey().getName() + " from multiple parallel sources");
            }
         }).forEach(entry -> {
            parents.computeIfAbsent(entry.getKey(), aClass -> new LinkedList<>()).addAll(entry.getValue());
            map.computeIfAbsent(entry.getKey(), aClass -> new LinkedList<>()).addAll(entry.getValue());
         });
      }

      return map;
   }

   private void addIfNotPresent(JsonbAnnotatedElement<?> element, Class<?> definedType, Annotation... annotations) {
      for (Annotation annotation : annotations) {
         if (element.getAnnotation(annotation.annotationType()).isEmpty() || REPEATABLE.contains(annotation.annotationType())) {
            element.putAnnotation(annotation, true, definedType);
         }
      }
   }

   public boolean requiredParameters(Executable executable, JsonbAnnotatedElement<Parameter> annotated) {
      return this.jsonbContext.getConfigProperties().hasRequiredCreatorParameters();
   }
}
