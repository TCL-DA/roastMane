package org.glassfish.jersey.internal.inject;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Configuration;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.ext.ParamConverter;
import jakarta.ws.rs.ext.ParamConverterProvider;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.security.AccessController;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import org.glassfish.jersey.CommonProperties;
import org.glassfish.jersey.internal.LocalizationMessages;
import org.glassfish.jersey.internal.util.ReflectionHelper;
import org.glassfish.jersey.internal.util.collection.ClassTypePair;
import org.glassfish.jersey.message.internal.HttpDateFormat;

@Singleton
public class ParamConverters {
   private abstract static class AbstractStringReader<T> extends ParamConverters.ParamConverterCompliance implements ParamConverter<T> {
      private AbstractStringReader(boolean canReturnNull) {
         super(canReturnNull);
      }

      public T fromString(String value) {
         if (value == null) {
            return this.nullOrThrow();
         } else {
            try {
               return this._fromString(value);
            } catch (InvocationTargetException var4) {
               if (value.isEmpty()) {
                  return null;
               } else {
                  Throwable cause = var4.getCause();
                  if (cause instanceof WebApplicationException) {
                     throw (WebApplicationException)cause;
                  } else {
                     throw new ExtractorException(cause);
                  }
               }
            } catch (Exception var5) {
               throw new ProcessingException(var5);
            }
         }
      }

      protected abstract T _fromString(String var1) throws Exception;

      public String toString(T value) throws IllegalArgumentException {
         return value == null ? this.nullOrThrow() : value.toString();
      }
   }

   @Singleton
   public static class AggregatedProvider implements ParamConverterProvider {
      private final ParamConverterProvider[] providers;

      @Inject
      public AggregatedProvider(@Context InjectionManager manager, @Context Configuration configuration) {
         boolean canThrowNull = !CommonProperties.getValue(configuration.getProperties(), "jersey.config.paramconverters.throw.iae", Boolean.FALSE);
         this.providers = new ParamConverterProvider[]{
            new ParamConverters.DateProvider(canThrowNull),
            new ParamConverters.TypeFromStringEnum(canThrowNull),
            new ParamConverters.TypeValueOf(canThrowNull),
            new ParamConverters.CharacterProvider(canThrowNull),
            new ParamConverters.InputStreamProvider(),
            new ParamConverters.TypeFromString(canThrowNull),
            new ParamConverters.StringConstructor(canThrowNull),
            new ParamConverters.OptionalCustomProvider(manager, canThrowNull),
            new ParamConverters.OptionalProvider()
         };
      }

      public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
         for (ParamConverterProvider p : this.providers) {
            ParamConverter<T> reader = p.getConverter(rawType, genericType, annotations);
            if (reader != null) {
               return reader;
            }
         }

         return null;
      }
   }

   @Singleton
   public static class CharacterProvider extends ParamConverters.ParamConverterCompliance implements ParamConverterProvider {
      private CharacterProvider(boolean canReturnNull) {
         super(canReturnNull);
      }

      public <T> ParamConverter<T> getConverter(final Class<T> rawType, Type genericType, Annotation[] annotations) {
         return rawType.equals(Character.class) ? new ParamConverter<T>() {
            public T fromString(String value) {
               if (value != null && !value.isEmpty()) {
                  if (value.length() == 1) {
                     return rawType.cast(value.charAt(0));
                  } else {
                     throw new ExtractorException(LocalizationMessages.ERROR_PARAMETER_INVALID_CHAR_VALUE(value));
                  }
               } else {
                  return CharacterProvider.this.nullOrThrow();
               }
            }

            public String toString(T value) {
               return value == null ? CharacterProvider.this.nullOrThrow() : value.toString();
            }
         } : null;
      }
   }

   @Singleton
   public static class DateProvider extends ParamConverters.ParamConverterCompliance implements ParamConverterProvider {
      private DateProvider(boolean canReturnNull) {
         super(canReturnNull);
      }

      public <T> ParamConverter<T> getConverter(final Class<T> rawType, Type genericType, Annotation[] annotations) {
         return rawType != Date.class ? null : new ParamConverter<T>() {
            public T fromString(String value) {
               if (value == null) {
                  return DateProvider.this.nullOrThrow();
               } else {
                  try {
                     return rawType.cast(HttpDateFormat.readDate(value));
                  } catch (ParseException var3) {
                     throw new ExtractorException(var3);
                  }
               }
            }

            public String toString(T value) throws IllegalArgumentException {
               return value == null ? DateProvider.this.nullOrThrow() : value.toString();
            }
         };
      }
   }

   public static class InputStreamProvider implements ParamConverterProvider {
      public <T> ParamConverter<T> getConverter(final Class<T> rawType, Type genericType, Annotation[] annotations) {
         return rawType != InputStream.class ? null : new ParamConverter<T>() {
            public T fromString(String value) {
               if (value == null) {
                  throw new IllegalArgumentException(LocalizationMessages.METHOD_PARAMETER_CANNOT_BE_NULL("value"));
               } else {
                  return rawType.cast(new ByteArrayInputStream(value.getBytes(StandardCharsets.UTF_8)));
               }
            }

            public String toString(T value) {
               if (value == null) {
                  throw new IllegalArgumentException(LocalizationMessages.METHOD_PARAMETER_CANNOT_BE_NULL("value"));
               } else {
                  try {
                     return new String(((InputStream)value).readAllBytes());
                  } catch (IOException var3) {
                     throw new ExtractorException(var3);
                  }
               }
            }
         };
      }
   }

   @Singleton
   public static class OptionalCustomProvider extends ParamConverters.ParamConverterCompliance implements ParamConverterProvider {
      private final InjectionManager manager;

      public OptionalCustomProvider(InjectionManager manager, boolean canReturnNull) {
         super(canReturnNull);
         this.manager = manager;
      }

      public <T> ParamConverter<T> getConverter(Class<T> rawType, final Type genericType, final Annotation[] annotations) {
         return rawType != Optional.class ? null : new ParamConverter<T>() {
            public T fromString(String value) {
               if (value == null) {
                  return (T)Optional.empty();
               } else {
                  List<ClassTypePair> ctps = ReflectionHelper.getTypeArgumentAndClass(genericType);
                  ClassTypePair ctp = ctps.size() == 1 ? ctps.get(0) : null;
                  boolean empty = value.isEmpty();

                  for (ParamConverterProvider provider : Providers.getProviders(OptionalCustomProvider.this.manager, ParamConverterProvider.class)) {
                     ParamConverter<?> converter = provider.getConverter(ctp.rawClass(), ctp.type(), annotations);
                     if (converter != null) {
                        if (empty) {
                           return (T)Optional.empty();
                        }

                        return (T)Optional.of(value).map(s -> converter.fromString(value));
                     }
                  }

                  return OptionalCustomProvider.this.nullOrThrow();
               }
            }

            public String toString(T value) throws IllegalArgumentException {
               return null;
            }
         };
      }
   }

   @Singleton
   public static class OptionalProvider implements ParamConverterProvider {
      public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
         final ParamConverters.OptionalProvider.Optionals optionals = ParamConverters.OptionalProvider.Optionals.getOptional(rawType);
         return optionals == null ? null : new ParamConverter<T>() {
            public T fromString(String value) {
               return (T)(value != null && !value.isEmpty() ? optionals.of(value) : optionals.empty());
            }

            public String toString(T value) throws IllegalArgumentException {
               return null;
            }
         };
      }

      private static enum Optionals {
         OPTIONAL_INT(OptionalInt.class) {
            @Override
            Object empty() {
               return OptionalInt.empty();
            }

            @Override
            Object of(Object value) {
               return OptionalInt.of(Integer.parseInt((String)value));
            }
         },
         OPTIONAL_DOUBLE(OptionalDouble.class) {
            @Override
            Object empty() {
               return OptionalDouble.empty();
            }

            @Override
            Object of(Object value) {
               return OptionalDouble.of(Double.parseDouble((String)value));
            }
         },
         OPTIONAL_LONG(OptionalLong.class) {
            @Override
            Object empty() {
               return OptionalLong.empty();
            }

            @Override
            Object of(Object value) {
               return OptionalLong.of(Long.parseLong((String)value));
            }
         };

         private final Class<?> clazz;

         private Optionals(Class<?> clazz) {
            this.clazz = clazz;
         }

         private static ParamConverters.OptionalProvider.Optionals getOptional(Class<?> clazz) {
            for (ParamConverters.OptionalProvider.Optionals optionals : values()) {
               if (optionals.clazz == clazz) {
                  return optionals;
               }
            }

            return null;
         }

         abstract Object empty();

         abstract Object of(Object var1);
      }
   }

   private static class ParamConverterCompliance {
      protected final boolean canReturnNull;

      private ParamConverterCompliance(boolean canReturnNull) {
         this.canReturnNull = canReturnNull;
      }

      protected <T> T nullOrThrow() {
         if (this.canReturnNull) {
            return null;
         } else {
            throw new IllegalArgumentException(LocalizationMessages.METHOD_PARAMETER_CANNOT_BE_NULL("value"));
         }
      }
   }

   @Singleton
   public static class StringConstructor extends ParamConverters.ParamConverterCompliance implements ParamConverterProvider {
      private StringConstructor(boolean canReturnNull) {
         super(canReturnNull);
      }

      public <T> ParamConverter<T> getConverter(final Class<T> rawType, Type genericType, Annotation[] annotations) {
         final Constructor constructor = AccessController.doPrivileged(ReflectionHelper.getStringConstructorPA(rawType));
         return constructor == null ? null : new ParamConverters.AbstractStringReader<T>(this.canReturnNull) {
            @Override
            protected T _fromString(String value) throws Exception {
               return rawType.cast(constructor.newInstance(value));
            }
         };
      }
   }

   @Singleton
   public static class TypeFromString extends ParamConverters.ParamConverterCompliance implements ParamConverterProvider {
      private TypeFromString(boolean canReturnNull) {
         super(canReturnNull);
      }

      public <T> ParamConverter<T> getConverter(final Class<T> rawType, Type genericType, Annotation[] annotations) {
         final Method fromStringMethod = AccessController.doPrivileged(ReflectionHelper.getFromStringStringMethodPA(rawType));
         return fromStringMethod == null ? null : new ParamConverters.AbstractStringReader<T>(this.canReturnNull) {
            @Override
            public T _fromString(String value) throws Exception {
               return rawType.cast(fromStringMethod.invoke(null, value));
            }
         };
      }
   }

   @Singleton
   public static class TypeFromStringEnum extends ParamConverters.TypeFromString {
      private TypeFromStringEnum(boolean canReturnNull) {
         super(canReturnNull);
      }

      @Override
      public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
         return !Enum.class.isAssignableFrom(rawType) ? null : super.getConverter(rawType, genericType, annotations);
      }
   }

   @Singleton
   public static class TypeValueOf extends ParamConverters.ParamConverterCompliance implements ParamConverterProvider {
      private TypeValueOf(boolean canReturnNull) {
         super(canReturnNull);
      }

      public <T> ParamConverter<T> getConverter(final Class<T> rawType, Type genericType, Annotation[] annotations) {
         final Method valueOf = AccessController.doPrivileged(ReflectionHelper.getValueOfStringMethodPA(rawType));
         return valueOf == null ? null : new ParamConverters.AbstractStringReader<T>(this.canReturnNull) {
            @Override
            public T _fromString(String value) throws Exception {
               return rawType.cast(valueOf.invoke(null, value));
            }
         };
      }
   }
}
