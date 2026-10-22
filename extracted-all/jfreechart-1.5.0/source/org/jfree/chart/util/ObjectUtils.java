package org.jfree.chart.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;

public final class ObjectUtils {
   private ObjectUtils() {
   }

   public static boolean equal(Object o1, Object o2) {
      if (o1 == o2) {
         return true;
      } else {
         return o1 != null ? o1.equals(o2) : false;
      }
   }

   public static int hashCode(Object object) {
      int result = 0;
      if (object != null) {
         result = object.hashCode();
      }

      return result;
   }

   public static Object clone(Object object) throws CloneNotSupportedException {
      if (object == null) {
         throw new IllegalArgumentException("Null 'object' argument.");
      } else if (object instanceof PublicCloneable) {
         PublicCloneable pc = (PublicCloneable)object;
         return pc.clone();
      } else {
         try {
            Method method = object.getClass().getMethod("clone", (Class<?>[])null);
            if (Modifier.isPublic(method.getModifiers())) {
               return method.invoke(object, (Object[])null);
            }
         } catch (NoSuchMethodException var2) {
            throw new CloneNotSupportedException("Object without clone() method is impossible.");
         } catch (IllegalAccessException var3) {
            throw new CloneNotSupportedException("Object.clone(): unable to call method.");
         } catch (InvocationTargetException var4) {
            throw new CloneNotSupportedException("Object without clone() method is impossible.");
         }

         throw new CloneNotSupportedException("Failed to clone.");
      }
   }

   public static Collection deepClone(Collection collection) throws CloneNotSupportedException {
      if (collection == null) {
         throw new IllegalArgumentException("Null 'collection' argument.");
      } else {
         Collection result = (Collection)clone(collection);
         result.clear();

         for (Object item : collection) {
            if (item != null) {
               result.add(clone(item));
            } else {
               result.add(null);
            }
         }

         return result;
      }
   }
}
