package org.glassfish.jersey.internal.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

public class Pretty {
   private static final String DOT = ".";
   private static final String NULL_STRING = "null";
   private static final String CONSTRUCTOR_NAME = "<init>";

   private Pretty() {
   }

   public static String clazz(Class<?> clazz) {
      if (clazz == null) {
         return "null";
      } else {
         String cn = clazz.getName();
         int index = cn.lastIndexOf(".");
         return index < 0 ? cn : cn.substring(index + 1);
      }
   }

   public static String pType(ParameterizedType pType) {
      StringBuffer sb = new StringBuffer();
      sb.append(clazz(ReflectionHelper.getRawClass(pType)) + "<");
      boolean first = true;

      for (Type t : pType.getActualTypeArguments()) {
         if (first) {
            first = false;
            sb.append(type(t));
         } else {
            sb.append("," + type(t));
         }
      }

      sb.append(">");
      return sb.toString();
   }

   public static String type(Type t) {
      if (t == null) {
         return "null";
      } else if (t instanceof Class) {
         return clazz((Class<?>)t);
      } else {
         return t instanceof ParameterizedType ? pType((ParameterizedType)t) : t.toString();
      }
   }

   public static String constructor(Constructor<?> constructor) {
      return constructor == null ? "null" : "<init>" + prettyPrintParameters(constructor.getParameterTypes());
   }

   public static String method(Method method) {
      return method == null ? "null" : method.getName() + prettyPrintParameters(method.getParameterTypes());
   }

   public static String field(Field field) {
      if (field == null) {
         return "null";
      } else {
         Type t = field.getGenericType();
         String baseString;
         if (t instanceof Class) {
            baseString = clazz((Class<?>)t);
         } else {
            baseString = type(t);
         }

         return "field(" + baseString + " " + field.getName() + " in " + field.getDeclaringClass().getName() + ")";
      }
   }

   public static String array(Object[] array) {
      if (array == null) {
         return "null";
      } else {
         StringBuffer sb = new StringBuffer("{");
         boolean first = true;

         for (Object item : array) {
            if (item != null && item instanceof Class) {
               item = clazz((Class<?>)item);
            }

            if (first) {
               first = false;
               sb.append(item == null ? "null" : item.toString());
            } else {
               sb.append("," + (item == null ? "null" : item.toString()));
            }
         }

         sb.append("}");
         return sb.toString();
      }
   }

   public static String collection(Collection<?> collection) {
      return collection == null ? "null" : array(collection.toArray(new Object[collection.size()]));
   }

   private static String prettyPrintParameters(Class<?>[] params) {
      if (params == null) {
         return "null";
      } else {
         StringBuffer sb = new StringBuffer("(");
         boolean first = true;

         for (Class<?> param : params) {
            if (first) {
               sb.append(clazz(param));
               first = false;
            } else {
               sb.append("," + clazz(param));
            }
         }

         sb.append(")");
         return sb.toString();
      }
   }
}
