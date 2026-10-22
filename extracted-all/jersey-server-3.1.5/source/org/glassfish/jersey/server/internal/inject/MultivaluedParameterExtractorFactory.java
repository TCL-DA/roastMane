package org.glassfish.jersey.server.internal.inject;

import jakarta.inject.Singleton;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.ext.ParamConverter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.security.AccessController;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import org.glassfish.jersey.internal.inject.ExtractorException;
import org.glassfish.jersey.internal.inject.ParamConverterFactory;
import org.glassfish.jersey.internal.inject.PrimitiveMapper;
import org.glassfish.jersey.internal.util.ReflectionHelper;
import org.glassfish.jersey.internal.util.collection.ClassTypePair;
import org.glassfish.jersey.internal.util.collection.LazyValue;
import org.glassfish.jersey.model.Parameter;
import org.glassfish.jersey.server.internal.LocalizationMessages;

@Singleton
final class MultivaluedParameterExtractorFactory implements MultivaluedParameterExtractorProvider {
   private final LazyValue<ParamConverterFactory> paramConverterFactory;

   public MultivaluedParameterExtractorFactory(LazyValue<ParamConverterFactory> paramConverterFactory) {
      this.paramConverterFactory = paramConverterFactory;
   }

   @Override
   public MultivaluedParameterExtractor<?> get(Parameter p) {
      return this.process(
         (ParamConverterFactory)this.paramConverterFactory.get(), p.getDefaultValue(), p.getRawType(), p.getType(), p.getAnnotations(), p.getSourceName()
      );
   }

   private MultivaluedParameterExtractor<?> process(
      ParamConverterFactory paramConverterFactory, String defaultValue, Class<?> rawType, Type type, Annotation[] annotations, String parameterName
   ) {
      ParamConverter<?> converter = paramConverterFactory.getConverter(rawType, type, annotations);
      if (converter != null) {
         try {
            return new SingleValueExtractor(converter, parameterName, defaultValue);
         } catch (ExtractorException var11) {
            throw var11;
         } catch (Exception var12) {
            throw new ProcessingException(LocalizationMessages.ERROR_PARAMETER_TYPE_PROCESSING(rawType), var12);
         }
      } else if (rawType == List.class || rawType == Set.class || rawType == SortedSet.class) {
         List<ClassTypePair> typePairs = ReflectionHelper.getTypeArgumentAndClass(type);
         ClassTypePair typePair = typePairs.size() == 1 ? typePairs.get(0) : null;
         if (typePair != null && typePair.rawClass() != String.class) {
            converter = paramConverterFactory.getConverter(typePair.rawClass(), typePair.type(), annotations);
            if (converter == null) {
               return null;
            } else {
               try {
                  return CollectionExtractor.getInstance(rawType, converter, parameterName, defaultValue);
               } catch (ExtractorException var13) {
                  throw var13;
               } catch (Exception var14) {
                  throw new ProcessingException(LocalizationMessages.ERROR_PARAMETER_TYPE_PROCESSING(rawType), var14);
               }
            }
         } else {
            return StringCollectionExtractor.getInstance(rawType, parameterName, defaultValue);
         }
      } else if (!rawType.isArray()) {
         return this.createPrimitiveExtractor(rawType, parameterName, defaultValue);
      } else if (rawType.getComponentType().isPrimitive()) {
         MultivaluedParameterExtractor<?> primitiveExtractor = this.createPrimitiveExtractor(rawType.getComponentType(), parameterName, defaultValue);
         return primitiveExtractor == null ? null : ArrayExtractor.getInstance(rawType.getComponentType(), primitiveExtractor, parameterName, defaultValue);
      } else {
         converter = paramConverterFactory.getConverter(rawType.getComponentType(), rawType.getComponentType(), annotations);
         return converter == null ? null : ArrayExtractor.getInstance(rawType.getComponentType(), converter, parameterName, defaultValue);
      }
   }

   private MultivaluedParameterExtractor<?> createPrimitiveExtractor(Class<?> rawType, String parameterName, String defaultValue) {
      if (rawType == String.class) {
         return new SingleStringValueExtractor(parameterName, defaultValue);
      } else if (rawType == Character.class) {
         return new PrimitiveCharacterExtractor(parameterName, defaultValue, PrimitiveMapper.primitiveToDefaultValueMap.get(rawType));
      } else {
         if (rawType.isPrimitive()) {
            Class<?> wrappedRaw = (Class<?>)PrimitiveMapper.primitiveToClassMap.get(rawType);
            if (wrappedRaw == null) {
               return null;
            }

            if (wrappedRaw == Character.class) {
               return new PrimitiveCharacterExtractor(parameterName, defaultValue, PrimitiveMapper.primitiveToDefaultValueMap.get(wrappedRaw));
            }

            Method valueOf = AccessController.doPrivileged(ReflectionHelper.getValueOfStringMethodPA(wrappedRaw));
            if (valueOf != null) {
               try {
                  return new PrimitiveValueOfExtractor(valueOf, parameterName, defaultValue, PrimitiveMapper.primitiveToDefaultValueMap.get(wrappedRaw));
               } catch (Exception var7) {
                  throw new ProcessingException(LocalizationMessages.DEFAULT_COULD_NOT_PROCESS_METHOD(defaultValue, valueOf));
               }
            }
         }

         return null;
      }
   }
}
