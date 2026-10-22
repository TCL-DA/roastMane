package org.apache.logging.log4j.util.internal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import org.apache.logging.log4j.internal.annotation.SuppressFBWarnings;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.FilteredObjectInputStream;

public final class SerializationUtil {
   private static final String DEFAULT_FILTER_CLASS = "org.apache.logging.log4j.util.internal.DefaultObjectInputFilter";
   private static final Method setObjectInputFilter;
   private static final Method getObjectInputFilter;
   private static final Method newObjectInputFilter;
   public static final List<String> REQUIRED_JAVA_CLASSES;
   public static final List<String> REQUIRED_JAVA_PACKAGES;

   public static void writeWrappedObject(final Serializable obj, final ObjectOutputStream out) throws IOException {
      ByteArrayOutputStream bout = new ByteArrayOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream(bout);

      try {
         oos.writeObject(obj);
         oos.flush();
         out.writeObject(bout.toByteArray());
      } catch (Throwable var7) {
         try {
            oos.close();
         } catch (Throwable var6) {
            var7.addSuppressed(var6);
         }

         throw var7;
      }

      oos.close();
   }

   @SuppressFBWarnings(
      value = {"OBJECT_DESERIALIZATION"},
      justification = "Object deserialization uses either Java 9 native filter or our custom filter to limit the kinds of classes deserialized."
   )
   public static Object readWrappedObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
      assertFiltered(in);
      byte[] data = (byte[])in.readObject();
      ByteArrayInputStream bin = new ByteArrayInputStream(data);
      ObjectInputStream ois;
      if (in instanceof FilteredObjectInputStream) {
         ois = new FilteredObjectInputStream(bin, ((FilteredObjectInputStream)in).getAllowedClasses());
      } else {
         try {
            Object obj = getObjectInputFilter.invoke(in);
            Object filter = newObjectInputFilter.invoke(null, obj);
            ois = new ObjectInputStream(bin);
            setObjectInputFilter.invoke(ois, filter);
         } catch (InvocationTargetException | IllegalAccessException var10) {
            throw new StreamCorruptedException("Unable to set ObjectInputFilter on stream");
         }
      }

      Object var14;
      try {
         return ois.readObject();
      } catch (LinkageError | Exception var11) {
         StatusLogger.getLogger().warn("Ignoring {} during deserialization", var11.getMessage());
         var14 = null;
      } finally {
         ois.close();
      }

      return var14;
   }

   public static void assertFiltered(final ObjectInputStream stream) {
      if (!(stream instanceof FilteredObjectInputStream) && setObjectInputFilter == null) {
         throw new IllegalArgumentException("readObject requires a FilteredObjectInputStream or an ObjectInputStream that accepts an ObjectInputFilter");
      }
   }

   public static String stripArray(final Class<?> clazz) {
      Class<?> currentClazz = clazz;

      while (currentClazz.isArray()) {
         currentClazz = currentClazz.getComponentType();
      }

      return currentClazz.getName();
   }

   public static String stripArray(final String name) {
      int offset = name.lastIndexOf(91) + 1;
      if (offset == 0) {
         return name;
      } else if (name.charAt(offset) == 'L') {
         return name.substring(offset + 1, name.length() - 1);
      } else {
         String var2 = name.substring(offset);
         switch (var2) {
            case "Z":
               return "boolean";
            case "B":
               return "byte";
            case "C":
               return "char";
            case "D":
               return "double";
            case "F":
               return "float";
            case "I":
               return "int";
            case "J":
               return "long";
            case "S":
               return "short";
            default:
               throw new IllegalArgumentException("Unsupported array class signature '" + name + "'");
         }
      }
   }

   private SerializationUtil() {
   }

   static {
      Method[] methods = ObjectInputStream.class.getMethods();
      Method setMethod = null;
      Method getMethod = null;

      for (Method method : methods) {
         if (method.getName().equals("setObjectInputFilter")) {
            setMethod = method;
         } else if (method.getName().equals("getObjectInputFilter")) {
            getMethod = method;
         }
      }

      Method newMethod = null;

      try {
         if (setMethod != null) {
            Class<?> clazz = Class.forName("org.apache.logging.log4j.util.internal.DefaultObjectInputFilter");
            methods = clazz.getMethods();

            for (Method methodx : methods) {
               if (methodx.getName().equals("newInstance") && Modifier.isStatic(methodx.getModifiers())) {
                  newMethod = methodx;
                  break;
               }
            }
         }
      } catch (ClassNotFoundException var9) {
      }

      newObjectInputFilter = newMethod;
      setObjectInputFilter = setMethod;
      getObjectInputFilter = getMethod;
      REQUIRED_JAVA_CLASSES = Arrays.asList(
         "java.math.BigDecimal", "java.math.BigInteger", "java.rmi.MarshalledObject", "boolean", "byte", "char", "double", "float", "int", "long", "short"
      );
      REQUIRED_JAVA_PACKAGES = Arrays.asList("java.lang.", "java.time.", "java.util.", "org.apache.logging.log4j.");
   }
}
