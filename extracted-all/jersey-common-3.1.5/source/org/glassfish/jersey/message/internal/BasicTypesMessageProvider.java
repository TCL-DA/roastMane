package org.glassfish.jersey.message.internal;

import jakarta.inject.Singleton;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.NoContentException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.security.AccessController;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.glassfish.jersey.internal.LocalizationMessages;
import org.glassfish.jersey.internal.util.ReflectionHelper;

@Produces({"text/plain"})
@Consumes({"text/plain"})
@Singleton
final class BasicTypesMessageProvider extends AbstractMessageReaderWriterProvider<Object> {
   public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return this.canProcess(type);
   }

   public Object readFrom(
      Class<Object> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream
   ) throws IOException, WebApplicationException {
      String entityString = ReaderWriter.readFromAsString(entityStream, mediaType);
      if (entityString.isEmpty()) {
         throw new NoContentException(LocalizationMessages.ERROR_READING_ENTITY_MISSING());
      } else {
         BasicTypesMessageProvider.PrimitiveTypes primitiveType = BasicTypesMessageProvider.PrimitiveTypes.forType(type);
         if (primitiveType != null) {
            return primitiveType.convert(entityString);
         } else {
            Constructor constructor = AccessController.doPrivileged(ReflectionHelper.getStringConstructorPA(type));
            if (constructor != null) {
               try {
                  return type.cast(constructor.newInstance(entityString));
               } catch (Exception var11) {
                  throw new MessageBodyProcessingException(LocalizationMessages.ERROR_ENTITY_PROVIDER_BASICTYPES_CONSTRUCTOR(type));
               }
            } else if (AtomicInteger.class.isAssignableFrom(type)) {
               return new AtomicInteger((Integer)BasicTypesMessageProvider.PrimitiveTypes.INTEGER.convert(entityString));
            } else if (AtomicLong.class.isAssignableFrom(type)) {
               return new AtomicLong((Long)BasicTypesMessageProvider.PrimitiveTypes.LONG.convert(entityString));
            } else {
               throw new MessageBodyProcessingException(LocalizationMessages.ERROR_ENTITY_PROVIDER_BASICTYPES_UNKWNOWN(type));
            }
         }
      }
   }

   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return this.canProcess(type);
   }

   private boolean canProcess(Class<?> type) {
      if (BasicTypesMessageProvider.PrimitiveTypes.forType(type) != null) {
         return true;
      } else {
         if (Number.class.isAssignableFrom(type)) {
            Constructor constructor = AccessController.doPrivileged(ReflectionHelper.getStringConstructorPA(type));
            if (constructor != null) {
               return true;
            }

            if (AtomicInteger.class.isAssignableFrom(type) || AtomicLong.class.isAssignableFrom(type)) {
               return true;
            }
         }

         return false;
      }
   }

   @Override
   public long getSize(Object t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return (long)t.toString().length();
   }

   public void writeTo(
      Object o,
      Class<?> type,
      Type genericType,
      Annotation[] annotations,
      MediaType mediaType,
      MultivaluedMap<String, Object> httpHeaders,
      OutputStream entityStream
   ) throws IOException, WebApplicationException {
      ReaderWriter.writeToAsString(o.toString(), entityStream, mediaType);
   }

   private static enum PrimitiveTypes {
      BYTE(Byte.class, byte.class) {
         @Override
         public Object convert(String s) {
            return Byte.valueOf(s);
         }
      },
      SHORT(Short.class, short.class) {
         @Override
         public Object convert(String s) {
            return Short.valueOf(s);
         }
      },
      INTEGER(Integer.class, int.class) {
         @Override
         public Object convert(String s) {
            return Integer.valueOf(s);
         }
      },
      LONG(Long.class, long.class) {
         @Override
         public Object convert(String s) {
            return Long.valueOf(s);
         }
      },
      FLOAT(Float.class, float.class) {
         @Override
         public Object convert(String s) {
            return Float.valueOf(s);
         }
      },
      DOUBLE(Double.class, double.class) {
         @Override
         public Object convert(String s) {
            return Double.valueOf(s);
         }
      },
      BOOLEAN(Boolean.class, boolean.class) {
         @Override
         public Object convert(String s) {
            return Boolean.valueOf(s);
         }
      },
      CHAR(Character.class, char.class) {
         @Override
         public Object convert(String s) {
            if (s.length() != 1) {
               throw new MessageBodyProcessingException(LocalizationMessages.ERROR_ENTITY_PROVIDER_BASICTYPES_CHARACTER_MORECHARS());
            } else {
               return s.charAt(0);
            }
         }
      };

      private final Class<?> wrapper;
      private final Class<?> primitive;

      public static BasicTypesMessageProvider.PrimitiveTypes forType(Class<?> type) {
         for (BasicTypesMessageProvider.PrimitiveTypes primitive : values()) {
            if (primitive.supports(type)) {
               return primitive;
            }
         }

         return null;
      }

      private PrimitiveTypes(Class<?> wrapper, Class<?> primitive) {
         this.wrapper = wrapper;
         this.primitive = primitive;
      }

      public abstract Object convert(String var1);

      public boolean supports(Class<?> type) {
         return type == this.wrapper || type == this.primitive;
      }
   }
}
