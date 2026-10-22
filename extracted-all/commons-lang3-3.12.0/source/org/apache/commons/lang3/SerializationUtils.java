package org.apache.commons.lang3;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class SerializationUtils {
   public static <T extends Serializable> T clone(T object) {
      if (object == null) {
         return null;
      } else {
         byte[] objectData = serialize(object);
         ByteArrayInputStream bais = new ByteArrayInputStream(objectData);

         try (SerializationUtils.ClassLoaderAwareObjectInputStream in = new SerializationUtils.ClassLoaderAwareObjectInputStream(
               bais, object.getClass().getClassLoader()
            )) {
            return (T)in.readObject();
         } catch (ClassNotFoundException var19) {
            throw new SerializationException("ClassNotFoundException while reading cloned object data", var19);
         } catch (IOException var20) {
            throw new SerializationException("IOException while reading or closing cloned object data", var20);
         }
      }
   }

   public static <T> T deserialize(byte[] objectData) {
      Validate.notNull(objectData, "objectData");
      return deserialize(new ByteArrayInputStream(objectData));
   }

   public static <T> T deserialize(InputStream inputStream) {
      Validate.notNull(inputStream, "inputStream");

      try (ObjectInputStream in = new ObjectInputStream(inputStream)) {
         return (T)in.readObject();
      } catch (IOException | ClassNotFoundException var16) {
         throw new SerializationException(var16);
      }
   }

   public static <T extends Serializable> T roundtrip(T obj) {
      return deserialize(serialize(obj));
   }

   public static byte[] serialize(Serializable obj) {
      ByteArrayOutputStream baos = new ByteArrayOutputStream(512);
      serialize(obj, baos);
      return baos.toByteArray();
   }

   public static void serialize(Serializable obj, OutputStream outputStream) {
      Validate.notNull(outputStream, "outputStream");

      try (ObjectOutputStream out = new ObjectOutputStream(outputStream)) {
         out.writeObject(obj);
      } catch (IOException var15) {
         throw new SerializationException(var15);
      }
   }

   static class ClassLoaderAwareObjectInputStream extends ObjectInputStream {
      private static final Map<String, Class<?>> primitiveTypes = new HashMap<>();
      private final ClassLoader classLoader;

      ClassLoaderAwareObjectInputStream(InputStream in, ClassLoader classLoader) throws IOException {
         super(in);
         this.classLoader = classLoader;
      }

      @Override
      protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
         String name = desc.getName();

         try {
            return Class.forName(name, false, this.classLoader);
         } catch (ClassNotFoundException var7) {
            try {
               return Class.forName(name, false, Thread.currentThread().getContextClassLoader());
            } catch (ClassNotFoundException var6) {
               Class<?> cls = primitiveTypes.get(name);
               if (cls != null) {
                  return cls;
               } else {
                  throw var6;
               }
            }
         }
      }

      static {
         primitiveTypes.put("byte", byte.class);
         primitiveTypes.put("short", short.class);
         primitiveTypes.put("int", int.class);
         primitiveTypes.put("long", long.class);
         primitiveTypes.put("float", float.class);
         primitiveTypes.put("double", double.class);
         primitiveTypes.put("boolean", boolean.class);
         primitiveTypes.put("char", char.class);
         primitiveTypes.put("void", void.class);
      }
   }
}
