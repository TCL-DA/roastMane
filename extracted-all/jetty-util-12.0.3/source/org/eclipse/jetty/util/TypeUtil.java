package org.eclipse.jetty.util;

import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.module.Configuration;
import java.lang.module.ModuleReference;
import java.lang.module.ResolvedModule;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.ServiceLoader.Provider;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TypeUtil {
   private static final Logger LOG = LoggerFactory.getLogger(TypeUtil.class);
   public static final Class<?>[] NO_ARGS = new Class[0];
   public static final int CR = 13;
   public static final int LF = 10;
   private static final Pattern TRAILING_DIGITS = Pattern.compile("^\\D*(\\d+)$");
   private static final HashMap<String, Class<?>> name2Class = new HashMap<>();
   private static final HashMap<Class<?>, Function<String, Object>> class2Value = new HashMap<>();
   private static final MethodHandle[] LOCATION_METHODS;

   public static <T> List<T> asList(T[] a) {
      return a == null ? Collections.emptyList() : Arrays.asList(a);
   }

   public static Class<?> fromName(String name) {
      return name2Class.get(name);
   }

   public static String toShortName(Class<?> type) {
      StringBuilder b = new StringBuilder();
      Package pkg = type.getPackage();
      if (pkg != null) {
         String p = pkg.getName();
         if (StringUtil.isNotBlank(p)) {
            String[] ss = p.split("\\.");

            for (String s : ss) {
               b.append(s.charAt(0));
               Matcher matcher = TRAILING_DIGITS.matcher(s);
               if (matcher.matches()) {
                  b.append(matcher.group(1));
               }
            }
         }

         b.append('.');
      }

      Class<?> enclosing = type.getEnclosingClass();
      if (enclosing != null) {
         appendEnclosing(b, enclosing);
      }

      b.append(type.getSimpleName());
      return b.toString();
   }

   private static void appendEnclosing(StringBuilder b, Class<?> enclosing) {
      Class<?> e = enclosing.getEnclosingClass();
      if (e != null) {
         appendEnclosing(b, e);
      }

      b.append(enclosing.getSimpleName()).append('$');
   }

   public static String toClassReference(Class<?> clazz) {
      return toClassReference(clazz.getName());
   }

   public static String toClassReference(String className) {
      return StringUtil.replace(className, '.', '/').concat(".class");
   }

   public static Object valueOf(Class<?> type, String value) {
      try {
         if (type.equals(String.class)) {
            return value;
         }

         Function<String, Object> vos = class2Value.get(type);
         if (vos != null) {
            return vos.apply(value);
         }

         if (!type.equals(char.class) && !type.equals(Character.class)) {
            Constructor<?> c = type.getConstructor(String.class);
            return c.newInstance(value);
         }

         return value.charAt(0);
      } catch (IllegalAccessException | InstantiationException | NoSuchMethodException var4) {
         LOG.trace("IGNORED", var4);
      } catch (InvocationTargetException var5) {
         if (var5.getTargetException() instanceof Error) {
            throw (Error)var5.getTargetException();
         }

         LOG.trace("IGNORED", var5);
      }

      return null;
   }

   public static Object valueOf(String type, String value) {
      return valueOf(fromName(type), value);
   }

   public static int parseInt(String s, int offset, int length, int base) throws NumberFormatException {
      int value = 0;
      if (length < 0) {
         length = s.length() - offset;
      }

      for (int i = 0; i < length; i++) {
         char c = s.charAt(offset + i);
         int digit = convertHexDigit((int)c);
         if (digit < 0 || digit >= base) {
            throw new NumberFormatException(s.substring(offset, offset + length));
         }

         value = value * base + digit;
      }

      return value;
   }

   public static int parseInt(byte[] b, int offset, int length, int base) throws NumberFormatException {
      int value = 0;
      if (length < 0) {
         length = b.length - offset;
      }

      for (int i = 0; i < length; i++) {
         char c = (char)(255 & b[offset + i]);
         int digit = c - '0';
         if (digit < 0 || digit >= base || digit >= 10) {
            digit = '\n' + c - 65;
            if (digit < 10 || digit >= base) {
               digit = '\n' + c - 97;
            }
         }

         if (digit < 0 || digit >= base) {
            throw new NumberFormatException(new String(b, offset, length, StandardCharsets.US_ASCII));
         }

         value = value * base + digit;
      }

      return value;
   }

   public static String toString(byte[] bytes, int base) {
      StringBuilder buf = new StringBuilder();

      for (byte b : bytes) {
         int bi = 255 & b;
         int c = 48 + bi / base % base;
         if (c > 57) {
            c = 97 + (c - 48 - 10);
         }

         buf.append((char)c);
         c = 48 + bi % base;
         if (c > 57) {
            c = 97 + (c - 48 - 10);
         }

         buf.append((char)c);
      }

      return buf.toString();
   }

   public static byte convertHexDigit(byte c) {
      byte b = (byte)((c & 31) + (c >> 6) * 25 - 16);
      if (b >= 0 && b <= 15) {
         return b;
      } else {
         throw new NumberFormatException("!hex " + c);
      }
   }

   public static int convertHexDigit(char c) {
      int d = (c & 31) + (c >> 6) * 25 - 16;
      if (d >= 0 && d <= 15) {
         return d;
      } else {
         throw new NumberFormatException("!hex " + c);
      }
   }

   public static int convertHexDigit(int c) {
      int d = (c & 31) + (c >> 6) * 25 - 16;
      if (d >= 0 && d <= 15) {
         return d;
      } else {
         throw new NumberFormatException("!hex " + c);
      }
   }

   public static boolean isHex(String str, int offset, int len) {
      if (str == null) {
         return false;
      } else if (offset + len > str.length()) {
         return false;
      } else {
         for (int i = offset; i < offset + len; i++) {
            char c = str.charAt(i);
            if ((c < '0' || c > '9') && (c < 'a' || c > 'f') && (c < 'A' || c > 'F')) {
               return false;
            }
         }

         return true;
      }
   }

   public static void toHex(byte b, Appendable buf) {
      try {
         int d = 15 & (240 & b) >> 4;
         buf.append((char)((d > 9 ? 55 : 48) + d));
         d = 15 & b;
         buf.append((char)((d > 9 ? 55 : 48) + d));
      } catch (IOException var3) {
         throw new RuntimeException(var3);
      }
   }

   public static void toHex(int value, Appendable buf) throws IOException {
      int d = 15 & (-268435456 & value) >> 28;
      buf.append((char)((d > 9 ? 55 : 48) + d));
      d = 15 & (251658240 & value) >> 24;
      buf.append((char)((d > 9 ? 55 : 48) + d));
      d = 15 & (15728640 & value) >> 20;
      buf.append((char)((d > 9 ? 55 : 48) + d));
      d = 15 & (983040 & value) >> 16;
      buf.append((char)((d > 9 ? 55 : 48) + d));
      d = 15 & (61440 & value) >> 12;
      buf.append((char)((d > 9 ? 55 : 48) + d));
      d = 15 & (3840 & value) >> 8;
      buf.append((char)((d > 9 ? 55 : 48) + d));
      d = 15 & (240 & value) >> 4;
      buf.append((char)((d > 9 ? 55 : 48) + d));
      d = 15 & value;
      buf.append((char)((d > 9 ? 55 : 48) + d));
   }

   public static void toHex(long value, Appendable buf) throws IOException {
      toHex((int)(value >> 32), buf);
      toHex((int)value, buf);
   }

   public static void dump(Class<?> c) {
      System.err.println("Dump: " + c);
      dump(c.getClassLoader());
   }

   public static void dump(ClassLoader cl) {
      System.err.println("Dump Loaders:");

      while (cl != null) {
         System.err.println("  loader " + cl);
         cl = cl.getParent();
      }
   }

   public static boolean isTrue(Object o) {
      if (o == null) {
         return false;
      } else {
         return o instanceof Boolean ? (Boolean)o : Boolean.parseBoolean(o.toString());
      }
   }

   public static boolean isFalse(Object o) {
      if (o == null) {
         return false;
      } else {
         return o instanceof Boolean ? !(Boolean)o : "false".equalsIgnoreCase(o.toString());
      }
   }

   public static URI getLocationOfClass(Class<?> clazz) {
      for (MethodHandle locationMethod : LOCATION_METHODS) {
         try {
            URI location = (URI)locationMethod.invoke((Class)clazz);
            if (location != null) {
               return location;
            }
         } catch (Throwable var7) {
            var7.printStackTrace(System.err);
         }
      }

      return null;
   }

   public static URI getSystemClassLoaderLocation(Class<?> clazz) {
      return getClassLoaderLocation(clazz, ClassLoader.getSystemClassLoader());
   }

   public static URI getClassLoaderLocation(Class<?> clazz) {
      return getClassLoaderLocation(clazz, clazz.getClassLoader());
   }

   public static URI getClassLoaderLocation(Class<?> clazz, ClassLoader loader) {
      if (loader == null) {
         return null;
      } else {
         try {
            String resourceName = toClassReference(clazz);
            URL url = loader.getResource(resourceName);
            if (url != null) {
               URI uri = url.toURI();
               return URIUtil.unwrapContainer(uri);
            }
         } catch (URISyntaxException var5) {
         }

         return null;
      }
   }

   public static URI getCodeSourceLocation(Class<?> clazz) {
      try {
         ProtectionDomain domain = clazz.getProtectionDomain();
         if (domain != null) {
            CodeSource source = domain.getCodeSource();
            if (source != null) {
               URL location = source.getLocation();
               if (location != null) {
                  return location.toURI();
               }
            }
         }
      } catch (URISyntaxException var4) {
      }

      return null;
   }

   public static URI getModuleLocation(Class<?> clazz) {
      Module module = clazz.getModule();
      if (module == null) {
         return null;
      } else {
         ModuleLayer layer = module.getLayer();
         if (layer == null) {
            return null;
         } else {
            Configuration configuration = layer.configuration();
            if (configuration == null) {
               return null;
            } else {
               Optional<ResolvedModule> resolvedModule = configuration.findModule(module.getName());
               if (resolvedModule.isEmpty()) {
                  return null;
               } else {
                  ModuleReference moduleReference = resolvedModule.get().reference();
                  if (moduleReference == null) {
                     return null;
                  } else {
                     Optional<URI> location = moduleReference.location();
                     return location.orElse(null);
                  }
               }
            }
         }
      }
   }

   public static <T> Iterator<T> concat(final Iterator<T> i1, final Iterator<T> i2) {
      return new Iterator<T>() {
         @Override
         public boolean hasNext() {
            return i1.hasNext() || i2.hasNext();
         }

         @Override
         public T next() {
            return i1.hasNext() ? i1.next() : i2.next();
         }
      };
   }

   private static <T> Stream<T> mapToService(Provider<T> provider) {
      try {
         return Stream.of(provider.get());
      } catch (ServiceConfigurationError var2) {
         LOG.warn("Service Provider failed to load", var2);
         return Stream.empty();
      }
   }

   public static <T> Stream<T> serviceStream(ServiceLoader<T> serviceLoader) {
      return serviceProviderStream(serviceLoader).flatMap(TypeUtil::mapToService);
   }

   public static <T> Stream<Provider<T>> serviceProviderStream(ServiceLoader<T> serviceLoader) {
      return StreamSupport.stream(new ServiceLoaderSpliterator<>(serviceLoader), false);
   }

   public static <T> Predicate<T> truePredicate() {
      return new Predicate<T>() {
         @Override
         public boolean test(T t) {
            return true;
         }

         @Override
         public Predicate<T> and(Predicate<? super T> other) {
            return Objects.requireNonNull((Predicate<T>)other);
         }

         @Override
         public Predicate<T> negate() {
            return TypeUtil.falsePredicate();
         }

         @Override
         public Predicate<T> or(Predicate<? super T> other) {
            return this;
         }
      };
   }

   public static <T> Predicate<T> falsePredicate() {
      return new Predicate<T>() {
         @Override
         public boolean test(T t) {
            return false;
         }

         @Override
         public Predicate<T> and(Predicate<? super T> other) {
            return this;
         }

         @Override
         public Predicate<T> negate() {
            return TypeUtil.truePredicate();
         }

         @Override
         public Predicate<T> or(Predicate<? super T> other) {
            return Objects.requireNonNull((Predicate<T>)other);
         }
      };
   }

   private TypeUtil() {
   }

   static {
      name2Class.put("boolean", boolean.class);
      name2Class.put("byte", byte.class);
      name2Class.put("char", char.class);
      name2Class.put("double", double.class);
      name2Class.put("float", float.class);
      name2Class.put("int", int.class);
      name2Class.put("long", long.class);
      name2Class.put("short", short.class);
      name2Class.put("void", void.class);
      name2Class.put("java.lang.Boolean.TYPE", boolean.class);
      name2Class.put("java.lang.Byte.TYPE", byte.class);
      name2Class.put("java.lang.Character.TYPE", char.class);
      name2Class.put("java.lang.Double.TYPE", double.class);
      name2Class.put("java.lang.Float.TYPE", float.class);
      name2Class.put("java.lang.Integer.TYPE", int.class);
      name2Class.put("java.lang.Long.TYPE", long.class);
      name2Class.put("java.lang.Short.TYPE", short.class);
      name2Class.put("java.lang.Void.TYPE", void.class);
      name2Class.put("java.lang.Boolean", Boolean.class);
      name2Class.put("java.lang.Byte", Byte.class);
      name2Class.put("java.lang.Character", Character.class);
      name2Class.put("java.lang.Double", Double.class);
      name2Class.put("java.lang.Float", Float.class);
      name2Class.put("java.lang.Integer", Integer.class);
      name2Class.put("java.lang.Long", Long.class);
      name2Class.put("java.lang.Short", Short.class);
      name2Class.put("Boolean", Boolean.class);
      name2Class.put("Byte", Byte.class);
      name2Class.put("Character", Character.class);
      name2Class.put("Double", Double.class);
      name2Class.put("Float", Float.class);
      name2Class.put("Integer", Integer.class);
      name2Class.put("Long", Long.class);
      name2Class.put("Short", Short.class);
      name2Class.put(null, void.class);
      name2Class.put("string", String.class);
      name2Class.put("String", String.class);
      name2Class.put("java.lang.String", String.class);
      class2Value.put(boolean.class, Boolean::valueOf);
      class2Value.put(byte.class, Byte::valueOf);
      class2Value.put(double.class, Double::valueOf);
      class2Value.put(float.class, Float::valueOf);
      class2Value.put(int.class, Integer::valueOf);
      class2Value.put(long.class, Long::valueOf);
      class2Value.put(short.class, Short::valueOf);
      class2Value.put(Boolean.class, Boolean::valueOf);
      class2Value.put(Byte.class, Byte::valueOf);
      class2Value.put(Double.class, Double::valueOf);
      class2Value.put(Float.class, Float::valueOf);
      class2Value.put(Integer.class, Integer::valueOf);
      class2Value.put(Long.class, Long::valueOf);
      class2Value.put(Short.class, Short::valueOf);
      List<MethodHandle> locationMethods = new ArrayList<>();
      Lookup lookup = MethodHandles.lookup();
      MethodType type = MethodType.methodType(URI.class, Class.class);

      try {
         locationMethods.add(lookup.findStatic(TypeUtil.class, "getCodeSourceLocation", type));
         locationMethods.add(lookup.findStatic(TypeUtil.class, "getModuleLocation", type));
         locationMethods.add(lookup.findStatic(TypeUtil.class, "getClassLoaderLocation", type));
         locationMethods.add(lookup.findStatic(TypeUtil.class, "getSystemClassLoaderLocation", type));
         LOCATION_METHODS = locationMethods.toArray(new MethodHandle[0]);
      } catch (Exception var4) {
         throw new RuntimeException("Unable to establish Location Lookup Handles", var4);
      }
   }
}
