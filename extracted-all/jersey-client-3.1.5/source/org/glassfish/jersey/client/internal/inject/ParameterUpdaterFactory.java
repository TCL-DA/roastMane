package org.glassfish.jersey.client.internal.inject;

import jakarta.inject.Singleton;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.ext.ParamConverter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import org.glassfish.jersey.client.inject.ParameterUpdater;
import org.glassfish.jersey.client.inject.ParameterUpdaterProvider;
import org.glassfish.jersey.client.internal.LocalizationMessages;
import org.glassfish.jersey.internal.inject.ParamConverterFactory;
import org.glassfish.jersey.internal.inject.PrimitiveMapper;
import org.glassfish.jersey.internal.inject.UpdaterException;
import org.glassfish.jersey.internal.util.ReflectionHelper;
import org.glassfish.jersey.internal.util.collection.ClassTypePair;
import org.glassfish.jersey.internal.util.collection.LazyValue;
import org.glassfish.jersey.model.Parameter;

@Singleton
final class ParameterUpdaterFactory implements ParameterUpdaterProvider {
   private final LazyValue<ParamConverterFactory> paramConverterFactory;

   public ParameterUpdaterFactory(LazyValue<ParamConverterFactory> paramConverterFactory) {
      this.paramConverterFactory = paramConverterFactory;
   }

   @Override
   public ParameterUpdater<?, ?> get(Parameter p) {
      return this.process(
         (ParamConverterFactory)this.paramConverterFactory.get(), p.getDefaultValue(), p.getRawType(), p.getType(), p.getAnnotations(), p.getSourceName()
      );
   }

   private ParameterUpdater<?, ?> process(
      ParamConverterFactory paramConverterFactory, String defaultValue, Class<?> rawType, Type type, Annotation[] annotations, String parameterName
   ) {
      ParamConverter<?> converter = paramConverterFactory.getConverter(rawType, type, annotations);
      if (converter != null) {
         try {
            return new SingleValueUpdater(converter, parameterName, defaultValue);
         } catch (UpdaterException var11) {
            throw var11;
         } catch (Exception var12) {
            throw new ProcessingException(LocalizationMessages.ERROR_PARAMETER_TYPE_PROCESSING(rawType), var12);
         }
      } else {
         if (rawType == List.class || rawType == Set.class || rawType == SortedSet.class) {
            List<ClassTypePair> typePairs = ReflectionHelper.getTypeArgumentAndClass(type);
            ClassTypePair typePair = typePairs.size() == 1 ? typePairs.get(0) : null;
            if (typePair != null) {
               converter = paramConverterFactory.getConverter(typePair.rawClass(), typePair.type(), annotations);
            }

            if (converter != null) {
               try {
                  return CollectionUpdater.getInstance(rawType, converter, parameterName, defaultValue);
               } catch (UpdaterException var13) {
                  throw var13;
               } catch (Exception var14) {
                  throw new ProcessingException(LocalizationMessages.ERROR_PARAMETER_TYPE_PROCESSING(rawType), var14);
               }
            }
         }

         if (rawType == String.class) {
            return new SingleStringValueUpdater(parameterName, defaultValue);
         } else if (rawType == Character.class) {
            return new PrimitiveCharacterUpdater(parameterName, defaultValue, PrimitiveMapper.primitiveToDefaultValueMap.get(rawType));
         } else if (rawType.isPrimitive()) {
            Class<?> wrappedRaw = (Class<?>)PrimitiveMapper.primitiveToClassMap.get(rawType);
            if (wrappedRaw == null) {
               return null;
            } else {
               return (ParameterUpdater<?, ?>)(wrappedRaw == Character.class
                  ? new PrimitiveCharacterUpdater(parameterName, defaultValue, PrimitiveMapper.primitiveToDefaultValueMap.get(wrappedRaw))
                  : new PrimitiveValueOfUpdater(parameterName, defaultValue, PrimitiveMapper.primitiveToDefaultValueMap.get(wrappedRaw)));
            }
         } else {
            return null;
         }
      }
   }
}
