package nonapi.io.github.classgraph.json;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.concurrent.Callable;
import nonapi.io.github.classgraph.reflection.ReflectionUtils;

public final class JSONUtils {
   private static Method isAccessibleMethod;
   private static Method setAccessibleMethod;
   private static Method trySetAccessibleMethod;
   static final String ID_KEY = "__ID";
   static final String ID_PREFIX = "[#";
   static final String ID_SUFFIX = "]";
   private static final String[] JSON_CHAR_REPLACEMENTS;
   private static final String[] INDENT_LEVELS;

   private static boolean isAccessible(AccessibleObject obj) {
      if (isAccessibleMethod != null) {
         try {
            if ((Boolean)isAccessibleMethod.invoke(obj)) {
               return true;
            }
         } catch (Throwable var2) {
         }
      }

      return false;
   }

   private static boolean tryMakeAccessible(AccessibleObject obj) {
      if (setAccessibleMethod != null) {
         try {
            setAccessibleMethod.invoke(obj, true);
            return true;
         } catch (Throwable var3) {
         }
      }

      if (trySetAccessibleMethod != null) {
         try {
            if ((Boolean)trySetAccessibleMethod.invoke(obj)) {
               return true;
            }
         } catch (Throwable var2) {
         }
      }

      return false;
   }

   public static boolean makeAccessible(final AccessibleObject obj, ReflectionUtils reflectionUtils) {
      if (!isAccessible(obj) && !tryMakeAccessible(obj)) {
         try {
            return reflectionUtils.doPrivileged(new Callable<Boolean>() {
               public Boolean call() throws Exception {
                  return JSONUtils.tryMakeAccessible(obj);
               }
            });
         } catch (Throwable var3) {
            return false;
         }
      } else {
         return true;
      }
   }

   private JSONUtils() {
   }

   static void escapeJSONString(String unsafeStr, StringBuilder buf) {
      if (unsafeStr != null) {
         boolean needsEscaping = false;
         int i = 0;

         for (int n = unsafeStr.length(); i < n; i++) {
            char c = unsafeStr.charAt(i);
            if (c > 255 || JSON_CHAR_REPLACEMENTS[c] != null) {
               needsEscaping = true;
               break;
            }
         }

         if (!needsEscaping) {
            buf.append(unsafeStr);
         } else {
            i = 0;

            for (int nx = unsafeStr.length(); i < nx; i++) {
               char c = unsafeStr.charAt(i);
               if (c > 255) {
                  buf.append("\\u");
                  int nibble3 = (c & '\uf000') >> 12;
                  buf.append(nibble3 <= 9 ? (char)(48 + nibble3) : (char)(65 + nibble3 - 10));
                  int nibble2 = (c & 3840) >> 8;
                  buf.append(nibble2 <= 9 ? (char)(48 + nibble2) : (char)(65 + nibble2 - 10));
                  int nibble1 = (c & 240) >> 4;
                  buf.append(nibble1 <= 9 ? (char)(48 + nibble1) : (char)(65 + nibble1 - 10));
                  int nibble0 = c & 15;
                  buf.append(nibble0 <= 9 ? (char)(48 + nibble0) : (char)(65 + nibble0 - 10));
               } else {
                  String replacement = JSON_CHAR_REPLACEMENTS[c];
                  if (replacement == null) {
                     buf.append(c);
                  } else {
                     buf.append(replacement);
                  }
               }
            }
         }
      }
   }

   public static String escapeJSONString(String unsafeStr) {
      StringBuilder buf = new StringBuilder(unsafeStr.length() * 2);
      escapeJSONString(unsafeStr, buf);
      return buf.toString();
   }

   static void indent(int depth, int indentWidth, StringBuilder buf) {
      int maxIndent = INDENT_LEVELS.length - 1;
      int d = depth * indentWidth;

      while (d > 0) {
         int n = Math.min(d, maxIndent);
         buf.append(INDENT_LEVELS[n]);
         d -= n;
      }
   }

   static Object getFieldValue(Object containingObj, Field field) throws IllegalArgumentException, IllegalAccessException {
      Class<?> fieldType = field.getType();
      if (fieldType == int.class) {
         return field.getInt(containingObj);
      } else if (fieldType == long.class) {
         return field.getLong(containingObj);
      } else if (fieldType == short.class) {
         return field.getShort(containingObj);
      } else if (fieldType == double.class) {
         return field.getDouble(containingObj);
      } else if (fieldType == float.class) {
         return field.getFloat(containingObj);
      } else if (fieldType == boolean.class) {
         return field.getBoolean(containingObj);
      } else if (fieldType == byte.class) {
         return field.getByte(containingObj);
      } else {
         return fieldType == char.class ? field.getChar(containingObj) : field.get(containingObj);
      }
   }

   static boolean isBasicValueType(Class<?> cls) {
      return cls == String.class
         || cls == Integer.class
         || cls == int.class
         || cls == Long.class
         || cls == long.class
         || cls == Short.class
         || cls == short.class
         || cls == Float.class
         || cls == float.class
         || cls == Double.class
         || cls == double.class
         || cls == Byte.class
         || cls == byte.class
         || cls == Character.class
         || cls == char.class
         || cls == Boolean.class
         || cls == boolean.class
         || cls.isEnum()
         || cls == Class.class;
   }

   static boolean isBasicValueType(Type type) {
      if (type instanceof Class) {
         return isBasicValueType((Class<?>)type);
      } else {
         return type instanceof ParameterizedType ? isBasicValueType(((ParameterizedType)type).getRawType()) : false;
      }
   }

   static boolean isBasicValueType(Object obj) {
      return obj == null
         || obj instanceof String
         || obj instanceof Integer
         || obj instanceof Boolean
         || obj instanceof Long
         || obj instanceof Float
         || obj instanceof Double
         || obj instanceof Short
         || obj instanceof Byte
         || obj instanceof Character
         || obj.getClass().isEnum()
         || obj instanceof Class;
   }

   static boolean isCollectionOrArray(Object obj) {
      Class<?> cls = obj.getClass();
      return Collection.class.isAssignableFrom(cls) || cls.isArray();
   }

   static Class<?> getRawType(Type type) {
      if (type instanceof Class) {
         return (Class<?>)type;
      } else if (type instanceof ParameterizedType) {
         return (Class<?>)((ParameterizedType)type).getRawType();
      } else {
         throw new IllegalArgumentException("Illegal type: " + type);
      }
   }

   static boolean fieldIsSerializable(Field field, boolean onlySerializePublicFields, ReflectionUtils reflectionUtils) {
      int modifiers = field.getModifiers();
      return (!onlySerializePublicFields || Modifier.isPublic(modifiers))
            && !Modifier.isTransient(modifiers)
            && !Modifier.isFinal(modifiers)
            && (modifiers & 4096) == 0
         ? makeAccessible(field, reflectionUtils)
         : false;
   }

   static {
      try {
         isAccessibleMethod = AccessibleObject.class.getDeclaredMethod("isAccessible");
      } catch (Throwable var7) {
      }

      try {
         setAccessibleMethod = AccessibleObject.class.getDeclaredMethod("setAccessible", boolean.class);
      } catch (Throwable var6) {
      }

      try {
         trySetAccessibleMethod = AccessibleObject.class.getDeclaredMethod("trySetAccessible");
      } catch (Throwable var5) {
      }

      JSON_CHAR_REPLACEMENTS = new String[256];

      for (int c = 0; c < 256; c++) {
         if (c == 32) {
            c = 127;
         }

         int nibble1 = c >> 4;
         char hexDigit1 = nibble1 <= 9 ? (char)(48 + nibble1) : (char)(65 + nibble1 - 10);
         int nibble0 = c & 15;
         char hexDigit0 = nibble0 <= 9 ? (char)(48 + nibble0) : (char)(65 + nibble0 - 10);
         JSON_CHAR_REPLACEMENTS[c] = "\\u00" + hexDigit1 + "" + hexDigit0;
      }

      JSON_CHAR_REPLACEMENTS[34] = "\\\"";
      JSON_CHAR_REPLACEMENTS[92] = "\\\\";
      JSON_CHAR_REPLACEMENTS[10] = "\\n";
      JSON_CHAR_REPLACEMENTS[13] = "\\r";
      JSON_CHAR_REPLACEMENTS[9] = "\\t";
      JSON_CHAR_REPLACEMENTS[8] = "\\b";
      JSON_CHAR_REPLACEMENTS[12] = "\\f";
      INDENT_LEVELS = new String[17];
      StringBuilder buf = new StringBuilder();

      for (int i = 0; i < INDENT_LEVELS.length; i++) {
         INDENT_LEVELS[i] = buf.toString();
         buf.append(' ');
      }
   }
}
