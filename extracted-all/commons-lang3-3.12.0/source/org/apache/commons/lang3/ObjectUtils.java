package org.apache.commons.lang3;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.function.Supplier;
import org.apache.commons.lang3.exception.CloneFailedException;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.text.StrBuilder;
import org.apache.commons.lang3.time.DurationUtils;

public class ObjectUtils {
   private static final char AT_SIGN = '@';
   public static final ObjectUtils.Null NULL = new ObjectUtils.Null();

   public static boolean allNotNull(Object... values) {
      if (values == null) {
         return false;
      } else {
         for (Object val : values) {
            if (val == null) {
               return false;
            }
         }

         return true;
      }
   }

   public static boolean allNull(Object... values) {
      return !anyNotNull(values);
   }

   public static boolean anyNotNull(Object... values) {
      return firstNonNull(values) != null;
   }

   public static boolean anyNull(Object... values) {
      return !allNotNull(values);
   }

   public static <T> T clone(T obj) {
      if (!(obj instanceof Cloneable)) {
         return null;
      } else {
         Object result;
         if (obj.getClass().isArray()) {
            Class<?> componentType = obj.getClass().getComponentType();
            if (componentType.isPrimitive()) {
               int length = Array.getLength(obj);
               result = Array.newInstance(componentType, length);

               while (length-- > 0) {
                  Array.set(result, length, Array.get(obj, length));
               }
            } else {
               result = obj.clone();
            }
         } else {
            try {
               Method clone = obj.getClass().getMethod("clone");
               result = clone.invoke(obj);
            } catch (NoSuchMethodException var4) {
               throw new CloneFailedException("Cloneable type " + obj.getClass().getName() + " has no clone method", var4);
            } catch (IllegalAccessException var5) {
               throw new CloneFailedException("Cannot clone Cloneable type " + obj.getClass().getName(), var5);
            } catch (InvocationTargetException var6) {
               throw new CloneFailedException("Exception cloning Cloneable type " + obj.getClass().getName(), var6.getCause());
            }
         }

         return (T)result;
      }
   }

   public static <T> T cloneIfPossible(T obj) {
      T clone = clone(obj);
      return clone == null ? obj : clone;
   }

   public static <T extends Comparable<? super T>> int compare(T c1, T c2) {
      return compare(c1, c2, false);
   }

   public static <T extends Comparable<? super T>> int compare(T c1, T c2, boolean nullGreater) {
      if (c1 == c2) {
         return 0;
      } else if (c1 == null) {
         return nullGreater ? 1 : -1;
      } else if (c2 == null) {
         return nullGreater ? -1 : 1;
      } else {
         return c1.compareTo(c2);
      }
   }

   public static boolean CONST(boolean v) {
      return v;
   }

   public static byte CONST(byte v) {
      return v;
   }

   public static char CONST(char v) {
      return v;
   }

   public static double CONST(double v) {
      return v;
   }

   public static float CONST(float v) {
      return v;
   }

   public static int CONST(int v) {
      return v;
   }

   public static long CONST(long v) {
      return v;
   }

   public static short CONST(short v) {
      return v;
   }

   public static <T> T CONST(T v) {
      return v;
   }

   public static byte CONST_BYTE(int v) {
      if (v >= -128 && v <= 127) {
         return (byte)v;
      } else {
         throw new IllegalArgumentException("Supplied value must be a valid byte literal between -128 and 127: [" + v + "]");
      }
   }

   public static short CONST_SHORT(int v) {
      if (v >= -32768 && v <= 32767) {
         return (short)v;
      } else {
         throw new IllegalArgumentException("Supplied value must be a valid byte literal between -32768 and 32767: [" + v + "]");
      }
   }

   public static <T> T defaultIfNull(T object, T defaultValue) {
      return object != null ? object : defaultValue;
   }

   @Deprecated
   public static boolean equals(Object object1, Object object2) {
      if (object1 == object2) {
         return true;
      } else {
         return object1 != null && object2 != null ? object1.equals(object2) : false;
      }
   }

   @SafeVarargs
   public static <T> T firstNonNull(T... values) {
      if (values != null) {
         for (T val : values) {
            if (val != null) {
               return val;
            }
         }
      }

      return null;
   }

   @SafeVarargs
   public static <T> T getFirstNonNull(Supplier<T>... suppliers) {
      if (suppliers != null) {
         for (Supplier<T> supplier : suppliers) {
            if (supplier != null) {
               T value = supplier.get();
               if (value != null) {
                  return value;
               }
            }
         }
      }

      return null;
   }

   public static <T> T getIfNull(T object, Supplier<T> defaultSupplier) {
      return object != null ? object : (defaultSupplier == null ? null : defaultSupplier.get());
   }

   @Deprecated
   public static int hashCode(Object obj) {
      return obj == null ? 0 : obj.hashCode();
   }

   @Deprecated
   public static int hashCodeMulti(Object... objects) {
      int hash = 1;
      if (objects != null) {
         for (Object object : objects) {
            int tmpHash = hashCode(object);
            hash = hash * 31 + tmpHash;
         }
      }

      return hash;
   }

   public static void identityToString(Appendable appendable, Object object) throws IOException {
      Validate.notNull(object, "object");
      appendable.append(object.getClass().getName()).append('@').append(Integer.toHexString(System.identityHashCode(object)));
   }

   public static String identityToString(Object object) {
      if (object == null) {
         return null;
      } else {
         String name = object.getClass().getName();
         String hexString = Integer.toHexString(System.identityHashCode(object));
         StringBuilder builder = new StringBuilder(name.length() + 1 + hexString.length());
         builder.append(name).append('@').append(hexString);
         return builder.toString();
      }
   }

   @Deprecated
   public static void identityToString(StrBuilder builder, Object object) {
      Validate.notNull(object, "object");
      String name = object.getClass().getName();
      String hexString = Integer.toHexString(System.identityHashCode(object));
      builder.ensureCapacity(builder.length() + name.length() + 1 + hexString.length());
      builder.append(name).append('@').append(hexString);
   }

   public static void identityToString(StringBuffer buffer, Object object) {
      Validate.notNull(object, "object");
      String name = object.getClass().getName();
      String hexString = Integer.toHexString(System.identityHashCode(object));
      buffer.ensureCapacity(buffer.length() + name.length() + 1 + hexString.length());
      buffer.append(name).append('@').append(hexString);
   }

   public static void identityToString(StringBuilder builder, Object object) {
      Validate.notNull(object, "object");
      String name = object.getClass().getName();
      String hexString = Integer.toHexString(System.identityHashCode(object));
      builder.ensureCapacity(builder.length() + name.length() + 1 + hexString.length());
      builder.append(name).append('@').append(hexString);
   }

   public static boolean isEmpty(Object object) {
      if (object == null) {
         return true;
      } else if (object instanceof CharSequence) {
         return ((CharSequence)object).length() == 0;
      } else if (object.getClass().isArray()) {
         return Array.getLength(object) == 0;
      } else if (object instanceof Collection) {
         return ((Collection)object).isEmpty();
      } else {
         return object instanceof Map ? ((Map)object).isEmpty() : false;
      }
   }

   public static boolean isNotEmpty(Object object) {
      return !isEmpty(object);
   }

   @SafeVarargs
   public static <T extends Comparable<? super T>> T max(T... values) {
      T result = null;
      if (values != null) {
         for (T value : values) {
            if (compare(value, result, false) > 0) {
               result = value;
            }
         }
      }

      return result;
   }

   @SafeVarargs
   public static <T> T median(Comparator<T> comparator, T... items) {
      Validate.notEmpty(items, "null/empty items");
      Validate.noNullElements(items);
      Validate.notNull(comparator, "comparator");
      TreeSet<T> sort = new TreeSet<>(comparator);
      Collections.addAll(sort, items);
      return (T)sort.toArray()[(sort.size() - 1) / 2];
   }

   @SafeVarargs
   public static <T extends Comparable<? super T>> T median(T... items) {
      Validate.notEmpty(items);
      Validate.noNullElements(items);
      TreeSet<T> sort = new TreeSet<>();
      Collections.addAll(sort, items);
      return (T)sort.toArray()[(sort.size() - 1) / 2];
   }

   @SafeVarargs
   public static <T extends Comparable<? super T>> T min(T... values) {
      T result = null;
      if (values != null) {
         for (T value : values) {
            if (compare(value, result, true) < 0) {
               result = value;
            }
         }
      }

      return result;
   }

   @SafeVarargs
   public static <T> T mode(T... items) {
      if (ArrayUtils.isNotEmpty(items)) {
         HashMap<T, MutableInt> occurrences = new HashMap<>(items.length);

         for (T t : items) {
            MutableInt count = occurrences.get(t);
            if (count == null) {
               occurrences.put(t, new MutableInt(1));
            } else {
               count.increment();
            }
         }

         T result = null;
         int max = 0;

         for (Entry<T, MutableInt> e : occurrences.entrySet()) {
            int cmp = e.getValue().intValue();
            if (cmp == max) {
               result = null;
            } else if (cmp > max) {
               max = cmp;
               result = e.getKey();
            }
         }

         return result;
      } else {
         return null;
      }
   }

   public static boolean notEqual(Object object1, Object object2) {
      return !equals(object1, object2);
   }

   public static <T> T requireNonEmpty(T obj) {
      return requireNonEmpty(obj, "object");
   }

   public static <T> T requireNonEmpty(T obj, String message) {
      Objects.requireNonNull(obj, message);
      if (isEmpty(obj)) {
         throw new IllegalArgumentException(message);
      } else {
         return obj;
      }
   }

   @Deprecated
   public static String toString(Object obj) {
      return obj == null ? "" : obj.toString();
   }

   @Deprecated
   public static String toString(Object obj, String nullStr) {
      return obj == null ? nullStr : obj.toString();
   }

   public static String toString(Object obj, Supplier<String> supplier) {
      return obj == null ? (supplier == null ? null : supplier.get()) : obj.toString();
   }

   public static void wait(Object obj, Duration duration) throws InterruptedException {
      DurationUtils.accept(obj::wait, DurationUtils.zeroIfNull(duration));
   }

   public static class Null implements Serializable {
      private static final long serialVersionUID = 7092611880189329093L;

      Null() {
      }

      private Object readResolve() {
         return ObjectUtils.NULL;
      }
   }
}
