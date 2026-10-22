package org.eclipse.yasson.internal;

import jakarta.json.bind.adapter.JsonbAdapter;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.bind.serializer.JsonbSerializer;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import org.eclipse.yasson.internal.components.AbstractComponentBinding;
import org.eclipse.yasson.internal.components.AdapterBinding;
import org.eclipse.yasson.internal.components.ComponentBindings;
import org.eclipse.yasson.internal.components.DeserializerBinding;
import org.eclipse.yasson.internal.components.SerializerBinding;
import org.eclipse.yasson.internal.model.customization.ComponentBoundCustomization;

public class ComponentMatcher {
   private final JsonbContext jsonbContext;
   private volatile boolean genericComponents;
   private final ConcurrentMap<Type, ComponentBindings> userComponents;

   ComponentMatcher(JsonbContext context) {
      Objects.requireNonNull(context);
      this.jsonbContext = context;
      this.userComponents = new ConcurrentHashMap<>();
      this.init();
   }

   void init() {
      JsonbSerializer<?>[] serializers = this.jsonbContext.getConfig().getProperty("jsonb.serializers").orElseGet(() -> new JsonbSerializer[0]);

      for (JsonbSerializer<?> serializer : serializers) {
         SerializerBinding<?> serializerBinding = this.introspectSerializerBinding((Class<? extends JsonbSerializer>)serializer.getClass(), serializer);
         this.addSerializer(serializerBinding.getBindingType(), serializerBinding);
      }

      JsonbDeserializer<?>[] deserializers = this.jsonbContext.getConfig().getProperty("jsonb.derializers").orElseGet(() -> new JsonbDeserializer[0]);

      for (JsonbDeserializer<?> deserializer : deserializers) {
         DeserializerBinding<?> deserializerBinding = this.introspectDeserializerBinding(
            (Class<? extends JsonbDeserializer>)deserializer.getClass(), deserializer
         );
         this.addDeserializer(deserializerBinding.getBindingType(), deserializerBinding);
      }

      JsonbAdapter<?, ?>[] adapters = this.jsonbContext.getConfig().getProperty("jsonb.adapters").orElseGet(() -> new JsonbAdapter[0]);

      for (JsonbAdapter<?, ?> adapter : adapters) {
         AdapterBinding adapterBinding = this.introspectAdapterBinding((Class<? extends JsonbAdapter>)adapter.getClass(), adapter);
         this.addAdapter(adapterBinding.getBindingType(), adapterBinding);
      }
   }

   private ComponentBindings getBindingInfo(Type type) {
      return this.userComponents.compute(type, (type1, bindingInfo) -> (ComponentBindings)(bindingInfo != null ? bindingInfo : new ComponentBindings(type1)));
   }

   private void addSerializer(Type bindingType, SerializerBinding<?> serializer) {
      this.userComponents.computeIfPresent(bindingType, (type, bindings) -> {
         if (bindings.getSerializer() != null) {
            return (ComponentBindings)bindings;
         } else {
            this.registerGeneric(bindingType);
            return new ComponentBindings(bindingType, serializer, bindings.getDeserializer(), bindings.getAdapterInfo());
         }
      });
   }

   private void addDeserializer(Type bindingType, DeserializerBinding<?> deserializer) {
      this.userComponents.computeIfPresent(bindingType, (type, bindings) -> {
         if (bindings.getDeserializer() != null) {
            return (ComponentBindings)bindings;
         } else {
            this.registerGeneric(bindingType);
            return new ComponentBindings(bindingType, bindings.getSerializer(), deserializer, bindings.getAdapterInfo());
         }
      });
   }

   private void addAdapter(Type bindingType, AdapterBinding adapter) {
      this.userComponents.computeIfPresent(bindingType, (type, bindings) -> {
         if (bindings.getAdapterInfo() != null) {
            return (ComponentBindings)bindings;
         } else {
            this.registerGeneric(bindingType);
            return new ComponentBindings(bindingType, bindings.getSerializer(), bindings.getDeserializer(), adapter);
         }
      });
   }

   private void registerGeneric(Type bindingType) {
      if (bindingType instanceof ParameterizedType && !this.genericComponents) {
         this.genericComponents = true;
      }
   }

   public Optional<SerializerBinding<?>> getSerializerBinding(Type propertyRuntimeType, ComponentBoundCustomization customization) {
      return customization != null && customization.getSerializerBinding() != null
         ? Optional.of(customization.getSerializerBinding())
         : this.searchComponentBinding(propertyRuntimeType, ComponentBindings::getSerializer);
   }

   public Optional<DeserializerBinding<?>> getDeserializerBinding(Type propertyRuntimeType, ComponentBoundCustomization customization) {
      return customization != null && customization.getDeserializerBinding() != null
         ? Optional.of(customization.getDeserializerBinding())
         : this.searchComponentBinding(propertyRuntimeType, ComponentBindings::getDeserializer);
   }

   public Optional<AdapterBinding> getSerializeAdapterBinding(Type propertyRuntimeType, ComponentBoundCustomization customization) {
      return customization != null && customization.getSerializeAdapterBinding() != null
         ? Optional.of(customization.getSerializeAdapterBinding())
         : this.searchComponentBinding(propertyRuntimeType, ComponentBindings::getAdapterInfo);
   }

   public Optional<AdapterBinding> getDeserializeAdapterBinding(Type propertyRuntimeType, ComponentBoundCustomization customization) {
      return customization != null && customization.getDeserializeAdapterBinding() != null
         ? Optional.of(customization.getDeserializeAdapterBinding())
         : this.searchComponentBinding(propertyRuntimeType, ComponentBindings::getAdapterInfo);
   }

   private <T extends AbstractComponentBinding> Optional<T> searchComponentBinding(Type runtimeType, Function<ComponentBindings, T> supplier) {
      ComponentBindings binding = this.userComponents.get(runtimeType);
      if (binding != null) {
         Optional<T> match = this.getMatchingBinding(runtimeType, binding, supplier);
         if (match.isPresent()) {
            return match;
         }
      }

      Optional<Class<?>> runtimeClass = ReflectionUtils.getOptionalRawType(runtimeType);
      if (runtimeClass.isPresent()) {
         for (Class<?> ifc : runtimeClass.get().getInterfaces()) {
            ComponentBindings ifcBinding = this.userComponents.get(ifc);
            if (ifcBinding != null) {
               Optional<T> match = this.getMatchingBinding(ifc, ifcBinding, supplier);
               if (match.isPresent()) {
                  return match;
               }
            }
         }

         Class<?> superClass = runtimeClass.get().getSuperclass();
         if (superClass != null && superClass != Object.class) {
            Optional<T> superBinding = this.searchComponentBinding(superClass, supplier);
            if (superBinding.isPresent()) {
               return superBinding;
            }
         }
      }

      return Optional.empty();
   }

   private <T> Optional<T> getMatchingBinding(Type runtimeType, ComponentBindings binding, Function<ComponentBindings, T> supplier) {
      T component = supplier.apply(binding);
      return component != null && this.matches(runtimeType, binding.getBindingType()) ? Optional.of(component) : Optional.empty();
   }

   private boolean matches(Type runtimeType, Type componentBindingType) {
      if (componentBindingType.equals(runtimeType)) {
         return true;
      } else if (componentBindingType instanceof Class && runtimeType instanceof Class) {
         return ((Class)componentBindingType).isAssignableFrom((Class<?>)runtimeType);
      } else {
         return !this.genericComponents
            ? false
            : runtimeType instanceof ParameterizedType
               && componentBindingType instanceof ParameterizedType
               && ReflectionUtils.getRawType(componentBindingType).isAssignableFrom(ReflectionUtils.getRawType(runtimeType))
               && this.matchTypeArguments((ParameterizedType)runtimeType, (ParameterizedType)componentBindingType);
      }
   }

   private boolean matchTypeArguments(ParameterizedType requiredType, ParameterizedType componentBound) {
      Type[] requiredTypeArguments = requiredType.getActualTypeArguments();
      Type[] adapterBoundTypeArguments = componentBound.getActualTypeArguments();
      if (requiredTypeArguments.length != adapterBoundTypeArguments.length) {
         return false;
      } else {
         for (int i = 0; i < requiredTypeArguments.length; i++) {
            Type adapterTypeArgument = adapterBoundTypeArguments[i];
            if (!requiredTypeArguments[i].equals(adapterTypeArgument)) {
               return false;
            }
         }

         return true;
      }
   }

   AdapterBinding introspectAdapterBinding(Class<? extends JsonbAdapter> adapterClass, JsonbAdapter instance) {
      ParameterizedType adapterRuntimeType = ReflectionUtils.findParameterizedType(adapterClass, JsonbAdapter.class);
      Type[] adapterTypeArguments = adapterRuntimeType.getActualTypeArguments();
      Type adaptFromType = this.resolveTypeArg(adapterTypeArguments[0], adapterClass);
      Type adaptToType = this.resolveTypeArg(adapterTypeArguments[1], adapterClass);
      ComponentBindings componentBindings = this.getBindingInfo(adaptFromType);
      if (componentBindings.getAdapterInfo() != null && componentBindings.getAdapterInfo().getAdapter().getClass().equals(adapterClass)) {
         return componentBindings.getAdapterInfo();
      } else {
         JsonbAdapter newAdapter = instance != null ? instance : this.jsonbContext.getComponentInstanceCreator().getOrCreateComponent(adapterClass);
         return new AdapterBinding(adaptFromType, adaptToType, newAdapter);
      }
   }

   DeserializerBinding introspectDeserializerBinding(Class<? extends JsonbDeserializer> deserializerClass, JsonbDeserializer instance) {
      ParameterizedType deserializerRuntimeType = ReflectionUtils.findParameterizedType(deserializerClass, JsonbDeserializer.class);
      Type deserializerBindingType = this.resolveTypeArg(deserializerRuntimeType.getActualTypeArguments()[0], deserializerClass);
      ComponentBindings componentBindings = this.getBindingInfo(deserializerBindingType);
      if (componentBindings.getDeserializer() != null && componentBindings.getDeserializer().getClass().equals(deserializerClass)) {
         return componentBindings.getDeserializer();
      } else {
         JsonbDeserializer deserializer = instance != null ? instance : this.jsonbContext.getComponentInstanceCreator().getOrCreateComponent(deserializerClass);
         return new DeserializerBinding(deserializerBindingType, deserializer);
      }
   }

   SerializerBinding introspectSerializerBinding(Class<? extends JsonbSerializer> serializerClass, JsonbSerializer instance) {
      ParameterizedType serializerRuntimeType = ReflectionUtils.findParameterizedType(serializerClass, JsonbSerializer.class);
      Type serBindingType = this.resolveTypeArg(serializerRuntimeType.getActualTypeArguments()[0], serializerClass);
      ComponentBindings componentBindings = this.getBindingInfo(serBindingType);
      if (componentBindings.getSerializer() != null && componentBindings.getSerializer().getClass().equals(serializerClass)) {
         return componentBindings.getSerializer();
      } else {
         JsonbSerializer serializer = instance != null ? instance : this.jsonbContext.getComponentInstanceCreator().getOrCreateComponent(serializerClass);
         return new SerializerBinding(serBindingType, serializer);
      }
   }

   private Type resolveTypeArg(Type adapterTypeArg, Type adapterType) {
      if (adapterTypeArg instanceof ParameterizedType) {
         return ReflectionUtils.resolveTypeArguments((ParameterizedType)adapterTypeArg, adapterType);
      } else if (adapterTypeArg instanceof TypeVariable) {
         LinkedList<Type> chain = new LinkedList<>();
         chain.add(adapterType);
         return ReflectionUtils.resolveItemVariableType(chain, (TypeVariable<?>)adapterTypeArg, true);
      } else {
         return adapterTypeArg;
      }
   }
}
