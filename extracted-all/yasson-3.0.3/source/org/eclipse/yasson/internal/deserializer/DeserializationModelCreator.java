package org.eclipse.yasson.internal.deserializer;

import jakarta.json.bind.JsonbException;
import jakarta.json.stream.JsonParser;
import jakarta.json.stream.JsonParser.Event;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.eclipse.yasson.internal.DeserializationContextImpl;
import org.eclipse.yasson.internal.JsonbConfigProperties;
import org.eclipse.yasson.internal.JsonbContext;
import org.eclipse.yasson.internal.JsonbDateFormatter;
import org.eclipse.yasson.internal.JsonbNumberFormatter;
import org.eclipse.yasson.internal.ReflectionUtils;
import org.eclipse.yasson.internal.components.AdapterBinding;
import org.eclipse.yasson.internal.components.DeserializerBinding;
import org.eclipse.yasson.internal.deserializer.types.TypeDeserializers;
import org.eclipse.yasson.internal.model.ClassModel;
import org.eclipse.yasson.internal.model.CreatorModel;
import org.eclipse.yasson.internal.model.JsonbCreator;
import org.eclipse.yasson.internal.model.PropertyModel;
import org.eclipse.yasson.internal.model.customization.ClassCustomization;
import org.eclipse.yasson.internal.model.customization.ComponentBoundCustomization;
import org.eclipse.yasson.internal.model.customization.Customization;
import org.eclipse.yasson.internal.model.customization.PropertyCustomization;
import org.eclipse.yasson.internal.model.customization.TypeInheritanceConfiguration;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

public class DeserializationModelCreator {
   private static final ModelDeserializer<Object> NULL_PROVIDER = (value, context) -> null;
   private static final Map<Class<?>, ModelDeserializer<Object>> DEFAULT_CREATOR_VALUES;
   private static final Set<Event> MAP_KEY_EVENTS = new HashSet<>();
   private final Map<DeserializationModelCreator.CachedItem, ModelDeserializer<JsonParser>> models = new ConcurrentHashMap<>();
   private final JsonbContext jsonbContext;
   private final Map<Class<?>, Class<?>> userTypeMapping;

   public DeserializationModelCreator(JsonbContext jsonbContext) {
      this.jsonbContext = jsonbContext;
      this.userTypeMapping = jsonbContext.getConfigProperties().getUserTypeMapping();
   }

   public ModelDeserializer<JsonParser> deserializerChain(Type type) {
      LinkedList<Type> chain = new LinkedList<>();
      ClassModel classModel = this.jsonbContext.getMappingContext().getOrCreateClassModel(ReflectionUtils.getRawType(type));
      return this.deserializerChain(chain, type, classModel.getClassCustomization(), classModel);
   }

   private ModelDeserializer<JsonParser> deserializerChain(LinkedList<Type> chain, Type type, Customization propertyCustomization, ClassModel classModel) {
      if (chain.contains(type)) {
         return new CyclicReferenceDeserializer(type);
      } else {
         ModelDeserializer var5;
         try {
            chain.add(type);
            var5 = this.deserializerChainInternal(chain, type, propertyCustomization, classModel);
         } finally {
            chain.removeLast();
         }

         return var5;
      }
   }

   private ModelDeserializer<JsonParser> deserializerChainInternal(
      LinkedList<Type> chain, Type type, Customization propertyCustomization, ClassModel classModel
   ) {
      Class<?> rawType = classModel.getType();
      DeserializationModelCreator.CachedItem cachedItem = this.createCachedItem(type, propertyCustomization);
      if (this.models.containsKey(cachedItem)) {
         return this.models.get(cachedItem);
      } else if (this.userTypeMapping.containsKey(rawType)) {
         Class<?> userTypeRaw = this.userTypeMapping.get(rawType);
         ModelDeserializer<JsonParser> deserializer = this.deserializerChain(userTypeRaw);
         this.models.put(cachedItem, deserializer);
         return deserializer;
      } else {
         Optional<AdapterBinding> adapterBinding = this.adapterBinding(type, (ComponentBoundCustomization)propertyCustomization);
         if (adapterBinding.isPresent()) {
            AdapterBinding adapter = adapterBinding.get();
            Class<?> toType = ReflectionUtils.getRawType(adapter.getToType());
            ClassModel targetModel = this.jsonbContext.getMappingContext().getOrCreateClassModel(toType);
            ModelDeserializer<JsonParser> typeDeserializer = this.typeDeserializer(toType, targetModel.getClassCustomization(), JustReturn.instance());
            if (typeDeserializer == null) {
               typeDeserializer = this.deserializerChain(adapter.getToType());
            }

            ModelDeserializer<JsonParser> targetAdapterModel = typeDeserializer;
            AdapterDeserializer adapterDeserializer = new AdapterDeserializer(adapter, JustReturn.instance());
            ModelDeserializer<JsonParser> adapterDeser = (parser, context) -> {
               Object fromJson = targetAdapterModel.deserialize(parser, context);
               return adapterDeserializer.deserialize(fromJson, context);
            };
            this.models.put(cachedItem, adapterDeser);
            return adapterDeser;
         } else {
            ModelDeserializer<JsonParser> typeDeserializer = this.typeDeserializer(rawType, propertyCustomization, JustReturn.instance());
            if (typeDeserializer != null) {
               this.models.put(cachedItem, typeDeserializer);
               return typeDeserializer;
            } else if (Collection.class.isAssignableFrom(rawType)) {
               return this.createCollectionDeserializer(cachedItem, rawType, chain, propertyCustomization);
            } else if (Map.class.isAssignableFrom(rawType)) {
               return this.createMapDeserializer(cachedItem, rawType, chain, propertyCustomization);
            } else if (rawType.isArray()) {
               return this.createArrayDeserializer(cachedItem, rawType, chain, propertyCustomization);
            } else if (type instanceof GenericArrayType) {
               return this.createGenericArray(cachedItem, rawType, chain, propertyCustomization);
            } else {
               return (ModelDeserializer<JsonParser>)(Optional.class.isAssignableFrom(rawType)
                  ? this.createOptionalDeserializer(chain, type, propertyCustomization, cachedItem)
                  : this.createObjectDeserializer(chain, type, propertyCustomization, classModel, rawType, cachedItem));
            }
         }
      }
   }

   private ModelDeserializer<JsonParser> createObjectDeserializer(
      LinkedList<Type> chain,
      Type type,
      Customization propertyCustomization,
      ClassModel classModel,
      Class<?> rawType,
      DeserializationModelCreator.CachedItem cachedItem
   ) {
      ClassCustomization classCustomization = classModel.getClassCustomization();
      Optional<DeserializerBinding<?>> deserializerBinding = this.userDeserializer(type, (ComponentBoundCustomization)propertyCustomization);
      if (deserializerBinding.isPresent()) {
         UserDefinedDeserializer user = new UserDefinedDeserializer(
            deserializerBinding.get().getJsonbDeserializer(), JustReturn.instance(), type, classCustomization
         );
         this.models.put(cachedItem, user);
         return user;
      } else {
         JsonbCreator creator = classCustomization.getCreator();
         boolean hasCreator = creator != null;
         List<String> params = hasCreator ? this.creatorParamsList(creator) : Collections.emptyList();
         Function<String, String> renamer = this.propertyRenamer();
         Map<String, ModelDeserializer<JsonParser>> processors = new LinkedHashMap<>();
         Map<String, ModelDeserializer<Object>> defaultCreatorValues = new HashMap<>();

         for (PropertyModel propertyModel : classModel.getSortedProperties()) {
            if (propertyModel.isWritable() && !params.contains(propertyModel.getReadName())) {
               ModelDeserializer<JsonParser> modelDeserializer = this.memberTypeProcessor(chain, propertyModel, hasCreator);
               processors.put(renamer.apply(propertyModel.getReadName()), modelDeserializer);
            }
         }

         for (String s : params) {
            CreatorModel creatorModel = creator.findByName(s);
            ModelDeserializer<JsonParser> modelDeserializer = this.typeProcessor(
               chain, creatorModel.getType(), creatorModel.getCustomization(), JustReturn.instance()
            );
            String parameterName = renamer.apply(creatorModel.getName());
            processors.put(parameterName, modelDeserializer);
            if (creatorModel.getCustomization().isRequired()) {
               defaultCreatorValues.put(parameterName, new RequiredCreatorParameter(parameterName));
            } else {
               Class<?> rawParamType = ReflectionUtils.getRawType(creatorModel.getType());
               defaultCreatorValues.put(parameterName, DEFAULT_CREATOR_VALUES.getOrDefault(rawParamType, NULL_PROVIDER));
            }
         }

         TypeInheritanceConfiguration typeInheritanceConfiguration = classCustomization.getPolymorphismConfig();
         Set<String> ignoredProperties = this.collectIgnoredProperties(typeInheritanceConfiguration);
         boolean failOnUnknownProperties = this.jsonbContext.getConfigProperties().getConfigFailOnUnknownProperties();
         ModelDeserializer<JsonParser> instanceCreator;
         if (hasCreator) {
            instanceCreator = new JsonbCreatorDeserializer(
               processors, defaultCreatorValues, creator, rawType, renamer, failOnUnknownProperties, ignoredProperties
            );
         } else {
            ModelDeserializer<JsonParser> typeWrapper = new ObjectDeserializer(processors, renamer, rawType, failOnUnknownProperties, ignoredProperties);
            instanceCreator = new DefaultObjectInstanceCreator(typeWrapper, rawType, classModel.getDefaultConstructor());
         }

         PositionChecker positionChecker = new PositionChecker(instanceCreator, rawType, Event.START_OBJECT);
         if (typeInheritanceConfiguration != null && !typeInheritanceConfiguration.isInherited()) {
            ModelDeserializer<JsonParser> var24 = new InheritanceInstanceCreator(rawType, this, typeInheritanceConfiguration, positionChecker);
            positionChecker = new PositionChecker(var24, rawType, Event.START_OBJECT);
         }

         ModelDeserializer<JsonParser> nullChecker = new NullCheckDeserializer(positionChecker, JustReturn.instance());
         this.models.put(cachedItem, nullChecker);
         return nullChecker;
      }
   }

   private ModelDeserializer<JsonParser> createCollectionDeserializer(
      DeserializationModelCreator.CachedItem cachedItem, Class<?> rawType, LinkedList<Type> chain, Customization propertyCustomization
   ) {
      Type type = cachedItem.type;
      Type colType = (Type)(type instanceof ParameterizedType ? ((ParameterizedType)type).getActualTypeArguments()[0] : Object.class);
      colType = ReflectionUtils.resolveType(chain, colType);
      ClassModel classModel = this.jsonbContext.getMappingContext().getOrCreateClassModel(ReflectionUtils.getRawType(colType));
      ModelDeserializer<JsonParser> typeProcessor = this.typeProcessor(chain, colType, classModel.getClassCustomization(), JustReturn.instance());
      CollectionDeserializer collectionDeserializer = new CollectionDeserializer(typeProcessor);
      CollectionInstanceCreator instanceDeserializer = new CollectionInstanceCreator(collectionDeserializer, type);
      PositionChecker positionChecker = new PositionChecker(instanceDeserializer, rawType, Event.START_ARRAY);
      NullCheckDeserializer nullChecker = new NullCheckDeserializer(positionChecker, JustReturn.instance());
      this.models.put(cachedItem, nullChecker);
      return nullChecker;
   }

   private ModelDeserializer<JsonParser> createMapDeserializer(
      DeserializationModelCreator.CachedItem cachedItem, Class<?> rawType, LinkedList<Type> chain, Customization propertyCustomization
   ) {
      Type type = cachedItem.type;
      Type keyType = (Type)(type instanceof ParameterizedType ? ((ParameterizedType)type).getActualTypeArguments()[0] : Object.class);
      Type valueType = (Type)(type instanceof ParameterizedType ? ((ParameterizedType)type).getActualTypeArguments()[1] : Object.class);
      ModelDeserializer<JsonParser> keyProcessor = this.typeProcessor(chain, keyType, ClassCustomization.empty(), JustReturn.instance(), MAP_KEY_EVENTS);
      ModelDeserializer<JsonParser> valueProcessor = this.typeProcessor(chain, valueType, propertyCustomization, JustReturn.instance());
      MapDeserializer mapDeserializer = new MapDeserializer(keyProcessor, valueProcessor);
      MapInstanceCreator mapInstanceCreator = new MapInstanceCreator(mapDeserializer, this.jsonbContext.getConfigProperties(), rawType);
      PositionChecker positionChecker = new PositionChecker(mapInstanceCreator, rawType, PositionChecker.Checker.CONTAINER);
      NullCheckDeserializer nullChecker = new NullCheckDeserializer(positionChecker, JustReturn.instance());
      this.models.put(cachedItem, nullChecker);
      return nullChecker;
   }

   private ModelDeserializer<JsonParser> createArrayDeserializer(
      DeserializationModelCreator.CachedItem cachedItem, Class<?> rawType, LinkedList<Type> chain, Customization propertyCustomization
   ) {
      JsonbConfigProperties configProperties = this.jsonbContext.getConfigProperties();
      if (rawType.equals(byte[].class) && !configProperties.getBinaryDataStrategy().equals("BYTE")) {
         String strategy = configProperties.getBinaryDataStrategy();
         ModelDeserializer<JsonParser> typeProcessor = this.typeProcessor(chain, String.class, propertyCustomization, JustReturn.instance());
         ModelDeserializer<JsonParser> base64Deserializer = ArrayInstanceCreator.createBase64Deserializer(strategy, typeProcessor);
         NullCheckDeserializer nullChecker = new NullCheckDeserializer(base64Deserializer, JustReturn.instance());
         this.models.put(cachedItem, nullChecker);
         return nullChecker;
      } else {
         Class<?> arrayType = rawType.getComponentType();
         ModelDeserializer<JsonParser> typeProcessor = this.typeProcessor(chain, arrayType, propertyCustomization, JustReturn.instance());
         return this.createArrayCommonDeserializer(cachedItem, rawType, arrayType, typeProcessor);
      }
   }

   private ModelDeserializer<JsonParser> createGenericArray(
      DeserializationModelCreator.CachedItem cachedItem, Class<?> rawType, LinkedList<Type> chain, Customization propertyCustomization
   ) {
      GenericArrayType type = (GenericArrayType)cachedItem.type;
      Class<?> component = ReflectionUtils.getRawType(type.getGenericComponentType());
      ModelDeserializer<JsonParser> typeProcessor = this.typeProcessor(chain, type.getGenericComponentType(), propertyCustomization, JustReturn.instance());
      return this.createArrayCommonDeserializer(cachedItem, rawType, component, typeProcessor);
   }

   private ModelDeserializer<JsonParser> createArrayCommonDeserializer(
      DeserializationModelCreator.CachedItem cachedItem, Class<?> rawType, Class<?> component, ModelDeserializer<JsonParser> typeProcessor
   ) {
      ArrayDeserializer arrayDeserializer = new ArrayDeserializer(typeProcessor);
      ArrayInstanceCreator arrayInstanceCreator = ArrayInstanceCreator.create(rawType, component, arrayDeserializer);
      PositionChecker positionChecker = new PositionChecker(arrayInstanceCreator, rawType, Event.START_ARRAY);
      NullCheckDeserializer nullChecker = new NullCheckDeserializer(positionChecker, JustReturn.instance());
      this.models.put(cachedItem, nullChecker);
      return nullChecker;
   }

   private OptionalDeserializer createOptionalDeserializer(
      LinkedList<Type> chain, Type type, Customization propertyCustomization, DeserializationModelCreator.CachedItem cachedItem
   ) {
      Type colType = (Type)(type instanceof ParameterizedType ? ((ParameterizedType)type).getActualTypeArguments()[0] : Object.class);
      ModelDeserializer<JsonParser> typeProcessor = this.typeProcessor(chain, colType, propertyCustomization, JustReturn.instance());
      OptionalDeserializer optionalDeserializer = new OptionalDeserializer(typeProcessor, JustReturn.instance());
      this.models.put(cachedItem, optionalDeserializer);
      return optionalDeserializer;
   }

   private Set<String> collectIgnoredProperties(TypeInheritanceConfiguration typeInheritanceConfiguration) {
      Set<String> ignoredProperties = new HashSet<>();
      if (typeInheritanceConfiguration != null) {
         for (TypeInheritanceConfiguration current = typeInheritanceConfiguration; current != null; current = current.getParentConfig()) {
            ignoredProperties.add(current.getFieldName());
         }
      }

      return ignoredProperties;
   }

   private Function<String, String> propertyRenamer() {
      boolean isCaseInsensitive = this.jsonbContext
         .getConfig()
         .getProperty("jsonb.property-naming-strategy")
         .filter(prop -> prop.equals("CASE_INSENSITIVE"))
         .isPresent();
      return isCaseInsensitive ? String::toLowerCase : value -> value;
   }

   private Optional<AdapterBinding> adapterBinding(Type type, ComponentBoundCustomization classCustomization) {
      return this.jsonbContext.getComponentMatcher().getDeserializeAdapterBinding(type, classCustomization);
   }

   private Optional<DeserializerBinding<?>> userDeserializer(Type type, ComponentBoundCustomization classCustomization) {
      return this.jsonbContext.getComponentMatcher().getDeserializerBinding(type, classCustomization);
   }

   private List<String> creatorParamsList(JsonbCreator creator) {
      return Arrays.stream(creator.getParams()).map(CreatorModel::getName).collect(Collectors.toList());
   }

   private ModelDeserializer<JsonParser> memberTypeProcessor(LinkedList<Type> chain, PropertyModel propertyModel, boolean hasCreator) {
      Type type = propertyModel.getPropertyDeserializationType();
      ModelDeserializer<Object> memberDeserializer = new ValueSetterDeserializer(propertyModel.getSetValueHandle());
      if (hasCreator) {
         memberDeserializer = new DeferredDeserializer(memberDeserializer);
      }

      return this.typeProcessor(chain, type, propertyModel.getCustomization(), memberDeserializer);
   }

   private ModelDeserializer<JsonParser> typeProcessor(
      LinkedList<Type> chain, Type type, Customization customization, ModelDeserializer<Object> memberDeserializer
   ) {
      return this.typeProcessor(chain, type, customization, memberDeserializer, PositionChecker.Checker.VALUES.getEvents());
   }

   private ModelDeserializer<JsonParser> typeProcessor(
      LinkedList<Type> chain, Type type, Customization customization, ModelDeserializer<Object> memberDeserializer, Set<Event> events
   ) {
      Type resolved = ReflectionUtils.resolveType(chain, type);
      Class<?> rawType = ReflectionUtils.getRawType(resolved);
      Optional<DeserializerBinding<?>> deserializerBinding = this.userDeserializer(resolved, (ComponentBoundCustomization)customization);
      if (deserializerBinding.isPresent()) {
         return new UserDefinedDeserializer(deserializerBinding.get().getJsonbDeserializer(), memberDeserializer, resolved, customization);
      } else {
         Optional<AdapterBinding> adapterBinding = this.adapterBinding(resolved, (ComponentBoundCustomization)customization);
         if (adapterBinding.isPresent()) {
            AdapterBinding adapter = adapterBinding.get();
            ModelDeserializer<JsonParser> typeDeserializer = this.typeDeserializer(
               ReflectionUtils.getRawType(adapter.getToType()), customization, JustReturn.instance(), events
            );
            if (typeDeserializer == null) {
               typeDeserializer = this.deserializerChain(adapter.getToType());
            }

            ModelDeserializer<JsonParser> targetAdapterModel = typeDeserializer;
            AdapterDeserializer adapterDeserializer = new AdapterDeserializer(adapter, memberDeserializer);
            return (parser, context) -> {
               DeserializationContextImpl newContext = new DeserializationContextImpl(context);
               Object fromJson = targetAdapterModel.deserialize(parser, newContext);
               return adapterDeserializer.deserialize(fromJson, context);
            };
         } else {
            ModelDeserializer<JsonParser> typeDeserializer = this.typeDeserializer(rawType, customization, memberDeserializer, events);
            if (typeDeserializer == null) {
               Class<?> implClass = this.resolveImplClass(rawType, customization);
               return this.createNewChain(chain, memberDeserializer, implClass, resolved, customization);
            } else {
               return typeDeserializer;
            }
         }
      }
   }

   private ModelDeserializer<JsonParser> createNewChain(
      LinkedList<Type> chain, ModelDeserializer<Object> memberDeserializer, Class<?> rawType, Type type, Customization propertyCustomization
   ) {
      ClassModel classModel = this.jsonbContext.getMappingContext().getOrCreateClassModel(rawType);
      ModelDeserializer<JsonParser> modelDeserializer = this.deserializerChain(chain, type, propertyCustomization, classModel);
      return new ContextSwitcher(memberDeserializer, modelDeserializer);
   }

   private ModelDeserializer<JsonParser> typeDeserializer(Class<?> rawType, Customization customization, ModelDeserializer<Object> delegate) {
      return this.typeDeserializer(rawType, customization, delegate, PositionChecker.Checker.VALUES.getEvents());
   }

   private ModelDeserializer<JsonParser> typeDeserializer(Class<?> rawType, Customization customization, ModelDeserializer<Object> delegate, Set<Event> events) {
      return TypeDeserializers.getTypeDeserializer(rawType, customization, this.jsonbContext.getConfigProperties(), delegate, events);
   }

   private Class<?> resolveImplClass(Class<?> rawType, Customization customization) {
      if (rawType.isInterface()) {
         Class<?> implementationClass = null;
         if (customization instanceof PropertyCustomization) {
            implementationClass = ((PropertyCustomization)customization).getImplementationClass();
         }

         if (implementationClass == null) {
            implementationClass = this.jsonbContext.getConfigProperties().getUserTypeMapping().get(rawType);
         }

         if (implementationClass != null) {
            if (!rawType.isAssignableFrom(implementationClass)) {
               throw new JsonbException(Messages.getMessage(MessageKeys.IMPL_CLASS_INCOMPATIBLE, implementationClass, rawType));
            }

            return implementationClass;
         }
      }

      return rawType;
   }

   private DeserializationModelCreator.CachedItem createCachedItem(Type type, Customization customization) {
      return new DeserializationModelCreator.CachedItem(type, customization.getDeserializeNumberFormatter(), customization.getDeserializeDateFormatter());
   }

   static {
      MAP_KEY_EVENTS.add(Event.KEY_NAME);
      MAP_KEY_EVENTS.addAll(PositionChecker.Checker.VALUES.getEvents());
      Map<Class<?>, ModelDeserializer<Object>> tmpValuesMap = new HashMap<>();
      tmpValuesMap.put(byte.class, (value, context) -> (byte)0);
      tmpValuesMap.put(short.class, (value, context) -> (short)0);
      tmpValuesMap.put(int.class, (value, context) -> 0);
      tmpValuesMap.put(long.class, (value, context) -> 0L);
      tmpValuesMap.put(float.class, (value, context) -> 0.0F);
      tmpValuesMap.put(double.class, (value, context) -> 0.0);
      tmpValuesMap.put(char.class, (value, context) -> '\u0000');
      tmpValuesMap.put(boolean.class, (value, context) -> false);
      tmpValuesMap.put(Optional.class, (value, context) -> Optional.empty());
      tmpValuesMap.put(OptionalInt.class, (value, context) -> OptionalInt.empty());
      tmpValuesMap.put(OptionalLong.class, (value, context) -> OptionalLong.empty());
      tmpValuesMap.put(OptionalDouble.class, (value, context) -> OptionalDouble.empty());
      DEFAULT_CREATOR_VALUES = Map.copyOf(tmpValuesMap);
   }

   private static final class CachedItem {
      private final Type type;
      private final JsonbNumberFormatter numberFormatter;
      private final JsonbDateFormatter dateFormatter;

      CachedItem(Type type, JsonbNumberFormatter numberFormatter, JsonbDateFormatter dateFormatter) {
         this.type = type;
         this.numberFormatter = numberFormatter;
         this.dateFormatter = dateFormatter;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (o != null && this.getClass() == o.getClass()) {
            DeserializationModelCreator.CachedItem that = (DeserializationModelCreator.CachedItem)o;
            return Objects.equals(this.type, that.type)
               && Objects.equals(this.numberFormatter, that.numberFormatter)
               && Objects.equals(this.dateFormatter, that.dateFormatter);
         } else {
            return false;
         }
      }

      @Override
      public int hashCode() {
         return Objects.hash(this.type, this.numberFormatter, this.dateFormatter);
      }

      @Override
      public String toString() {
         return "CachedItem{type=" + this.type + ", numberFormatter=" + this.numberFormatter + ", dateFormatter=" + this.dateFormatter + "}";
      }
   }
}
