package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.deser.KeyDeserializers;
import com.fasterxml.jackson.databind.introspect.AnnotatedAndMetadata;
import com.fasterxml.jackson.databind.introspect.AnnotatedConstructor;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.util.EnumResolver;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

public class StdKeyDeserializers implements KeyDeserializers, Serializable {
   private static final long serialVersionUID = 1L;

   public static KeyDeserializer constructEnumKeyDeserializer(EnumResolver enumResolver) {
      return new StdKeyDeserializer.EnumKD(enumResolver, null);
   }

   public static KeyDeserializer constructEnumKeyDeserializer(EnumResolver enumResolver, AnnotatedMethod factory) {
      return new StdKeyDeserializer.EnumKD(enumResolver, factory);
   }

   public static KeyDeserializer constructDelegatingKeyDeserializer(DeserializationConfig config, JavaType type, JsonDeserializer<?> deser) {
      return new StdKeyDeserializer.DelegatingKD(type.getRawClass(), deser);
   }

   public static KeyDeserializer findStringBasedKeyDeserializer(DeserializationConfig config, JavaType type) throws JsonMappingException {
      BeanDescription beanDesc = config.introspectForCreation(type);
      AnnotatedAndMetadata<AnnotatedConstructor, Mode> ctorInfo = _findStringConstructor(beanDesc);
      if (ctorInfo != null && ctorInfo.metadata != null) {
         return _constructCreatorKeyDeserializer(config, ctorInfo.annotated);
      } else {
         List<AnnotatedAndMetadata<AnnotatedMethod, Mode>> factoryCandidates = beanDesc.getFactoryMethodsWithMode();
         factoryCandidates.removeIf(
            m -> m.annotated.getParameterCount() != 1 || m.annotated.getRawParameterType(0) != String.class || m.metadata == Mode.PROPERTIES
         );
         AnnotatedMethod explicitFactory = _findExplicitStringFactoryMethod(factoryCandidates);
         if (explicitFactory != null) {
            return _constructCreatorKeyDeserializer(config, explicitFactory);
         } else if (ctorInfo != null) {
            return _constructCreatorKeyDeserializer(config, ctorInfo.annotated);
         } else {
            return !factoryCandidates.isEmpty() ? _constructCreatorKeyDeserializer(config, (AnnotatedMember)factoryCandidates.get(0).annotated) : null;
         }
      }
   }

   private static KeyDeserializer _constructCreatorKeyDeserializer(DeserializationConfig config, AnnotatedMember creator) {
      if (creator instanceof AnnotatedConstructor) {
         Constructor<?> rawCtor = ((AnnotatedConstructor)creator).getAnnotated();
         if (config.canOverrideAccessModifiers()) {
            ClassUtil.checkAndFixAccess(rawCtor, config.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
         }

         return new StdKeyDeserializer.StringCtorKeyDeserializer(rawCtor);
      } else {
         Method m = ((AnnotatedMethod)creator).getAnnotated();
         if (config.canOverrideAccessModifiers()) {
            ClassUtil.checkAndFixAccess(m, config.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
         }

         return new StdKeyDeserializer.StringFactoryKeyDeserializer(m);
      }
   }

   private static AnnotatedAndMetadata<AnnotatedConstructor, Mode> _findStringConstructor(BeanDescription beanDesc) {
      for (AnnotatedAndMetadata<AnnotatedConstructor, Mode> entry : beanDesc.getConstructorsWithMode()) {
         AnnotatedConstructor ctor = entry.annotated;
         if (ctor.getParameterCount() == 1 && String.class == ctor.getRawParameterType(0)) {
            return entry;
         }
      }

      return null;
   }

   private static AnnotatedMethod _findExplicitStringFactoryMethod(List<AnnotatedAndMetadata<AnnotatedMethod, Mode>> candidates) throws JsonMappingException {
      AnnotatedMethod match = null;

      for (AnnotatedAndMetadata<AnnotatedMethod, Mode> entry : candidates) {
         if (entry.metadata != null) {
            if (match != null) {
               Class<?> rawKeyType = entry.annotated.getDeclaringClass();
               throw new IllegalArgumentException(
                  "Multiple suitable annotated Creator factory methods to be used as the Key deserializer for type " + ClassUtil.nameOf(rawKeyType)
               );
            }

            match = entry.annotated;
         }
      }

      return match;
   }

   @Override
   public KeyDeserializer findKeyDeserializer(JavaType type, DeserializationConfig config, BeanDescription beanDesc) throws JsonMappingException {
      Class<?> raw = type.getRawClass();
      if (raw.isPrimitive()) {
         raw = ClassUtil.wrapperType(raw);
      }

      return StdKeyDeserializer.forType(raw);
   }
}
