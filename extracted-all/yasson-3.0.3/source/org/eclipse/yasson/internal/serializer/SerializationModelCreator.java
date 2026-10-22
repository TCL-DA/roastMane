package org.eclipse.yasson.internal.serializer;

import jakarta.json.bind.JsonbException;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import org.eclipse.yasson.internal.ComponentMatcher;
import org.eclipse.yasson.internal.JsonbContext;
import org.eclipse.yasson.internal.ReflectionUtils;
import org.eclipse.yasson.internal.components.AdapterBinding;
import org.eclipse.yasson.internal.components.SerializerBinding;
import org.eclipse.yasson.internal.model.ClassModel;
import org.eclipse.yasson.internal.model.PropertyModel;
import org.eclipse.yasson.internal.model.customization.ClassCustomization;
import org.eclipse.yasson.internal.model.customization.ComponentBoundCustomization;
import org.eclipse.yasson.internal.model.customization.Customization;
import org.eclipse.yasson.internal.model.customization.TypeInheritanceConfiguration;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;
import org.eclipse.yasson.internal.serializer.types.ObjectTypeSerializer;
import org.eclipse.yasson.internal.serializer.types.TypeSerializers;

public class SerializationModelCreator {
   private final Map<Type, ModelSerializer> explicitChain = new ConcurrentHashMap<>();
   private final Map<Type, ModelSerializer> dynamicChain = new ConcurrentHashMap<>();
   private final JsonbContext jsonbContext;

   public SerializationModelCreator(JsonbContext jsonbContext) {
      this.jsonbContext = jsonbContext;
   }

   public static ModelSerializer wrapInCommonSet(ModelSerializer modelSerializer, Customization customization, JsonbContext jsonbContext) {
      return Stream.of(modelSerializer).map(KeyWriter::new).map(serializer -> new NullSerializer(serializer, customization, jsonbContext)).findFirst().get();
   }

   public ModelSerializer serializerChain(Type type, boolean rootValue, boolean resolveRootAdapter) {
      Class<?> rawType = ReflectionUtils.getRawType(type);
      ClassModel classModel = this.jsonbContext.getMappingContext().getOrCreateClassModel(rawType);
      LinkedList<Type> chain = new LinkedList<>();
      return this.serializerChain(chain, type, classModel.getClassCustomization(), rootValue, false, resolveRootAdapter);
   }

   public ModelSerializer serializerChainRuntime(LinkedList<Type> chain, Type type, Customization propertyCustomization, boolean rootValue, boolean isKey) {
      if (chain.contains(type)) {
         return new CyclicReferenceSerializer(type);
      } else {
         Class<?> rawType = ReflectionUtils.getRawType(type);
         Class<?> rawLast = ReflectionUtils.getRawType(chain.getLast());
         return rawLast.equals(rawType)
            ? this.serializerChainInternal(chain, chain.getLast(), propertyCustomization, rootValue, isKey, true)
            : this.serializerChainInternal(chain, type, propertyCustomization, rootValue, isKey, true);
      }
   }

   private ModelSerializer serializerChain(
      LinkedList<Type> chain, Type type, Customization propertyCustomization, boolean rootValue, boolean isKey, boolean resolveRootAdapter
   ) {
      if (chain.contains(type)) {
         return new CyclicReferenceSerializer(type);
      } else {
         ModelSerializer var7;
         try {
            chain.add(type);
            var7 = this.serializerChainInternal(chain, type, propertyCustomization, rootValue, isKey, resolveRootAdapter);
         } finally {
            chain.removeLast();
         }

         return var7;
      }
   }

   private ModelSerializer serializerChainInternal(
      LinkedList<Type> chain, Type type, Customization propertyCustomization, boolean rootValue, boolean isKey, boolean resolveRootAdapter
   ) {
      if (this.explicitChain.containsKey(type)) {
         return this.explicitChain.get(type);
      } else {
         Class<?> rawType = ReflectionUtils.getRawType(type);
         Optional<ModelSerializer> serializerBinding = this.userSerializer(type, (ComponentBoundCustomization)propertyCustomization);
         if (serializerBinding.isPresent()) {
            return serializerBinding.get();
         } else {
            if (resolveRootAdapter) {
               Optional<AdapterBinding> maybeAdapter = this.adapterBinding(type, (ComponentBoundCustomization)propertyCustomization);
               if (maybeAdapter.isPresent()) {
                  AdapterBinding adapterBinding = maybeAdapter.get();
                  Type toType = adapterBinding.getToType();
                  Class<?> rawToType = ReflectionUtils.getRawType(toType);
                  ModelSerializer typeSerializer = TypeSerializers.getTypeSerializer(rawToType, propertyCustomization, this.jsonbContext);
                  if (typeSerializer == null) {
                     typeSerializer = this.serializerChain(toType, rootValue, !type.equals(toType));
                  }

                  AdapterSerializer adapterSerializer = new AdapterSerializer(adapterBinding, typeSerializer);
                  RecursionChecker recursionChecker = new RecursionChecker(adapterSerializer);
                  NullSerializer nullSerializer = new NullSerializer(recursionChecker, propertyCustomization, this.jsonbContext);
                  this.explicitChain.put(type, nullSerializer);
                  return nullSerializer;
               }
            }

            ModelSerializer typeSerializer = null;
            if (!Object.class.equals(rawType)) {
               typeSerializer = TypeSerializers.getTypeSerializer(chain, rawType, propertyCustomization, this.jsonbContext, isKey);
            }

            if (typeSerializer != null) {
               if (this.jsonbContext.getConfigProperties().isStrictIJson() && rootValue) {
                  throw new JsonbException(Messages.getMessage(MessageKeys.IJSON_ENABLED_SINGLE_VALUE));
               } else {
                  return typeSerializer;
               }
            } else {
               ClassModel classModel = this.jsonbContext.getMappingContext().getOrCreateClassModel(rawType);
               if (Collection.class.isAssignableFrom(rawType)) {
                  return this.createCollectionSerializer(chain, type, propertyCustomization);
               } else if (Map.class.isAssignableFrom(rawType)) {
                  return this.createMapSerializer(chain, type, propertyCustomization);
               } else if (rawType.isArray()) {
                  return this.createArraySerializer(chain, rawType, propertyCustomization);
               } else if (type instanceof GenericArrayType) {
                  return this.createGenericArraySerializer(chain, type, propertyCustomization);
               } else {
                  return Optional.class.equals(rawType)
                     ? this.createOptionalSerializer(chain, type, propertyCustomization, isKey)
                     : this.createObjectSerializer(chain, type, classModel);
               }
            }
         }
      }
   }

   private ModelSerializer createObjectSerializer(LinkedList<Type> chain, Type type, ClassModel classModel) {
      LinkedHashMap<String, ModelSerializer> propertySerializers = new LinkedHashMap<>();
      TypeInheritanceConfiguration typeInheritanceConfiguration = classModel.getClassCustomization().getPolymorphismConfig();
      if (typeInheritanceConfiguration != null) {
         this.addPolymorphismProperty(typeInheritanceConfiguration, propertySerializers, classModel);
      }

      for (PropertyModel model : classModel.getSortedProperties()) {
         if (model.isReadable()) {
            String name = model.getWriteName();
            ModelSerializer memberModel = this.memberSerializer(chain, model.getPropertySerializationType(), model.getCustomization(), false);
            propertySerializers.put(name, new ValueGetterSerializer(model.getGetValueHandle(), memberModel));
         }
      }

      ModelSerializer objectSerializer = new ObjectSerializer(propertySerializers);
      RecursionChecker recursionChecker = new RecursionChecker(objectSerializer);
      KeyWriter keyWriter = new KeyWriter(recursionChecker);
      NullVisibilitySwitcher nullVisibilitySwitcher = new NullVisibilitySwitcher(false, keyWriter);
      NullSerializer nullSerializer = new NullSerializer(nullVisibilitySwitcher, classModel.getClassCustomization(), this.jsonbContext);
      this.explicitChain.put(type, nullSerializer);
      return nullSerializer;
   }

   private void addPolymorphismProperty(
      TypeInheritanceConfiguration typeInheritanceConfiguration, LinkedHashMap<String, ModelSerializer> propertySerializers, ClassModel classModel
   ) {
      Class<?> rawType = classModel.getType();
      String alias = typeInheritanceConfiguration.getAliases().get(rawType);
      ModelSerializer serializer = this.createPolymorphismPropertySerializer(typeInheritanceConfiguration, alias);
      if ((!typeInheritanceConfiguration.isInherited() || alias != null) && typeInheritanceConfiguration.getParentConfig() != null) {
         this.addParentPolymorphismProperty(typeInheritanceConfiguration.getParentConfig(), propertySerializers, classModel);
      }

      if (serializer != null) {
         propertySerializers.put(typeInheritanceConfiguration.getFieldName(), serializer);
      }

      for (PropertyModel propertyModel : classModel.getSortedProperties()) {
         if (propertySerializers.containsKey(propertyModel.getWriteName())) {
            throw new JsonbException("CHANGE naming conflict!");
         }
      }
   }

   private void addParentPolymorphismProperty(
      TypeInheritanceConfiguration typeInheritanceConfiguration, LinkedHashMap<String, ModelSerializer> propertySerializers, ClassModel classModel
   ) {
      Class<?> rawType = classModel.getType();
      TypeInheritanceConfiguration current = typeInheritanceConfiguration;
      LinkedHashMap<String, ModelSerializer> toBeAdded = new LinkedHashMap<>();

      while (current != null) {
         String alias = current.getAliases()
            .entrySet()
            .stream()
            .filter(entryx -> ((Class)entryx.getKey()).isAssignableFrom(rawType))
            .map(Entry::getValue)
            .findFirst()
            .orElse(null);
         if (alias != null) {
            ModelSerializer serializer = this.createPolymorphismPropertySerializer(current, alias);
            toBeAdded.put(current.getFieldName(), serializer);
            current = current.getParentConfig();
         }
      }

      ListIterator<Entry<String, ModelSerializer>> iterator = new ArrayList<>(toBeAdded.entrySet()).listIterator(toBeAdded.size());

      while (iterator.hasPrevious()) {
         Entry<String, ModelSerializer> entry = iterator.previous();
         propertySerializers.put(entry.getKey(), entry.getValue());
      }
   }

   private ModelSerializer createPolymorphismPropertySerializer(TypeInheritanceConfiguration configuration, String alias) {
      return alias != null ? (value, generator, context) -> generator.write(configuration.getFieldName(), alias) : null;
   }

   private ModelSerializer createCollectionSerializer(LinkedList<Type> chain, Type type, Customization customization) {
      Type colType = (Type)(type instanceof ParameterizedType ? ((ParameterizedType)type).getActualTypeArguments()[0] : Object.class);
      Type resolvedKey = ReflectionUtils.resolveType(chain, colType);
      Class<?> rawClass = ReflectionUtils.getRawType(resolvedKey);
      ClassModel classModel = this.jsonbContext.getMappingContext().getOrCreateClassModel(rawClass);
      ModelSerializer typeSerializer = this.memberSerializer(chain, colType, classModel.getClassCustomization(), false);
      CollectionSerializer collectionSerializer = new CollectionSerializer(typeSerializer);
      KeyWriter keyWriter = new KeyWriter(collectionSerializer);
      NullVisibilitySwitcher nullVisibilitySwitcher = new NullVisibilitySwitcher(true, keyWriter);
      return new NullSerializer(nullVisibilitySwitcher, customization, this.jsonbContext);
   }

   private ModelSerializer createMapSerializer(LinkedList<Type> chain, Type type, Customization propertyCustomization) {
      Type keyType = (Type)(type instanceof ParameterizedType ? ((ParameterizedType)type).getActualTypeArguments()[0] : Object.class);
      Type valueType = (Type)(type instanceof ParameterizedType ? ((ParameterizedType)type).getActualTypeArguments()[1] : Object.class);
      Type resolvedKey = ReflectionUtils.resolveType(chain, keyType);
      Class<?> rawClass = ReflectionUtils.getRawType(resolvedKey);
      ModelSerializer keySerializer = this.memberSerializer(chain, keyType, ClassCustomization.empty(), true);
      ModelSerializer valueSerializer = this.memberSerializer(chain, valueType, propertyCustomization, false);
      MapSerializer mapSerializer = MapSerializer.create(rawClass, keySerializer, valueSerializer);
      KeyWriter keyWriter = new KeyWriter(mapSerializer);
      NullVisibilitySwitcher nullVisibilitySwitcher = new NullVisibilitySwitcher(true, keyWriter);
      return new NullSerializer(nullVisibilitySwitcher, propertyCustomization, this.jsonbContext);
   }

   private ModelSerializer createArraySerializer(LinkedList<Type> chain, Class<?> raw, Customization propertyCustomization) {
      Class<?> arrayComponent = raw.getComponentType();
      ModelSerializer modelSerializer = this.memberSerializer(chain, arrayComponent, propertyCustomization, false);
      ModelSerializer arraySerializer = ArraySerializer.create(raw, this.jsonbContext, modelSerializer);
      KeyWriter keyWriter = new KeyWriter(arraySerializer);
      NullVisibilitySwitcher nullVisibilitySwitcher = new NullVisibilitySwitcher(true, keyWriter);
      return new NullSerializer(nullVisibilitySwitcher, propertyCustomization, this.jsonbContext);
   }

   private ModelSerializer createGenericArraySerializer(LinkedList<Type> chain, Type type, Customization propertyCustomization) {
      Class<?> raw = ReflectionUtils.getRawType(type);
      Class<?> component = ReflectionUtils.getRawType(((GenericArrayType)type).getGenericComponentType());
      ModelSerializer modelSerializer = this.memberSerializer(chain, component, propertyCustomization, false);
      ModelSerializer arraySerializer = ArraySerializer.create(raw, this.jsonbContext, modelSerializer);
      KeyWriter keyWriter = new KeyWriter(arraySerializer);
      NullVisibilitySwitcher nullVisibilitySwitcher = new NullVisibilitySwitcher(true, keyWriter);
      return new NullSerializer(nullVisibilitySwitcher, propertyCustomization, this.jsonbContext);
   }

   private ModelSerializer createOptionalSerializer(LinkedList<Type> chain, Type type, Customization propertyCustomization, boolean isKey) {
      Type optType = (Type)(type instanceof ParameterizedType ? ((ParameterizedType)type).getActualTypeArguments()[0] : Object.class);
      ModelSerializer modelSerializer = this.memberSerializer(chain, optType, propertyCustomization, isKey);
      return new OptionalSerializer(modelSerializer);
   }

   private ModelSerializer memberSerializer(LinkedList<Type> chain, Type type, Customization customization, boolean key) {
      Type resolved = ReflectionUtils.resolveType(chain, type);
      Class<?> rawType = ReflectionUtils.getRawType(resolved);
      Optional<ModelSerializer> serializerBinding = this.userSerializer(resolved, (ComponentBoundCustomization)customization);
      if (serializerBinding.isPresent()) {
         return serializerBinding.get();
      } else {
         Optional<AdapterBinding> maybeAdapter = this.adapterBinding(resolved, (ComponentBoundCustomization)customization);
         if (maybeAdapter.isPresent()) {
            AdapterBinding adapterBinding = maybeAdapter.get();
            Type toType = adapterBinding.getToType();
            Class<?> rawToType = ReflectionUtils.getRawType(toType);
            ModelSerializer typeSerializer = TypeSerializers.getTypeSerializer(rawToType, customization, this.jsonbContext);
            if (typeSerializer == null) {
               typeSerializer = this.serializerChain(toType, false, true);
            }

            AdapterSerializer adapterSerializer = new AdapterSerializer(adapterBinding, typeSerializer);
            return new NullSerializer(adapterSerializer, customization, this.jsonbContext);
         } else {
            ModelSerializer typeSerializer = TypeSerializers.getTypeSerializer(chain, rawType, customization, this.jsonbContext, key);
            if (typeSerializer == null) {
               boolean isFinal = Modifier.isFinal(rawType.getModifiers());
               if (isFinal || Collection.class.isAssignableFrom(rawType) || Map.class.isAssignableFrom(rawType)) {
                  return this.serializerChain(chain, resolved, customization, false, key, true);
               }

               if (this.dynamicChain.containsKey(resolved)) {
                  return this.dynamicChain.get(resolved);
               }

               boolean isAbstract = Modifier.isAbstract(rawType.getModifiers());
               ModelSerializer specificTypeSerializer = null;
               if (!isAbstract && !rawType.equals(Object.class)) {
                  if (this.explicitChain.containsKey(resolved)) {
                     specificTypeSerializer = this.explicitChain.get(resolved);
                  } else {
                     specificTypeSerializer = this.serializerChain(chain, resolved, customization, false, key, true);
                  }
               }

               if (resolved instanceof Class) {
                  typeSerializer = TypeSerializers.getTypeSerializer(chain, Object.class, customization, this.jsonbContext, key);
               } else {
                  chain.add(resolved);
                  typeSerializer = TypeSerializers.getTypeSerializer(chain, Object.class, customization, this.jsonbContext, key);
                  chain.removeLast();
               }

               if (specificTypeSerializer != null && typeSerializer instanceof ObjectTypeSerializer) {
                  ((ObjectTypeSerializer)typeSerializer).addSpecificSerializer(rawType, specificTypeSerializer);
               }

               if (!key) {
                  typeSerializer = new NullSerializer(typeSerializer, customization, this.jsonbContext);
               }

               this.dynamicChain.put(type, typeSerializer);
            }

            if (!key && typeSerializer instanceof ObjectTypeSerializer) {
               typeSerializer = new NullSerializer(typeSerializer, customization, this.jsonbContext);
            }

            return typeSerializer;
         }
      }
   }

   private Optional<ModelSerializer> userSerializer(Type type, ComponentBoundCustomization classCustomization) {
      ComponentMatcher componentMatcher = this.jsonbContext.getComponentMatcher();
      return componentMatcher.getSerializerBinding(type, classCustomization)
         .map(SerializerBinding::getJsonbSerializer)
         .map(UserDefinedSerializer::new)
         .map(RecursionChecker::new)
         .map(serializer -> wrapInCommonSet(serializer, (Customization)classCustomization, this.jsonbContext));
   }

   private Optional<AdapterBinding> adapterBinding(Type type, ComponentBoundCustomization classCustomization) {
      return this.jsonbContext.getComponentMatcher().getSerializeAdapterBinding(type, classCustomization);
   }
}
