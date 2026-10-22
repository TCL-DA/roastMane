package com.sun.javafx.fxml.builder;

import com.sun.javafx.fxml.BeanAdapter;
import com.sun.javafx.fxml.ModuleHelper;
import com.sun.javafx.reflect.ConstructorUtil;
import com.sun.javafx.reflect.ReflectUtil;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;
import javafx.beans.NamedArg;
import javafx.util.Builder;

public class ProxyBuilder<T> extends AbstractMap<String, Object> implements Builder<T> {
   private Class<?> type;
   private final Map<Constructor, Map<String, ProxyBuilder.AnnotationValue>> constructorsMap;
   private final Map<String, ProxyBuilder.Property> propertiesMap;
   private final Set<Constructor> constructors;
   private Set<String> propertyNames;
   private boolean hasDefaultConstructor = false;
   private Constructor defaultConstructor;
   private static final String SETTER_PREFIX = "set";
   private static final String GETTER_PREFIX = "get";
   private final Comparator<Constructor> constructorComparator = (var0, var1x) -> {
      int var2x = var0.getParameterCount();
      int var3 = var1x.getParameterCount();
      int var4 = Math.min(var2x, var3);

      for (int var5 = 0; var5 < var4; var5++) {
         Class var6x = var0.getParameterTypes()[var5];
         Class var7x = var1x.getParameterTypes()[var5];
         if (!var6x.equals(var7x)) {
            if (var6x.equals(int.class) && var7x.equals(double.class)) {
               return -1;
            }

            if (var6x.equals(double.class) && var7x.equals(int.class)) {
               return 1;
            }

            return var6x.getCanonicalName().compareTo(var7x.getCanonicalName());
         }
      }

      return var2x - var3;
   };
   private final Map<String, Object> userValues = new HashMap<>();
   private final Map<String, Object> containers = new HashMap<>();
   private static final Map<Class<?>, Object> DEFAULTS_MAP = new HashMap<>(9);

   public ProxyBuilder(Class<?> var1) {
      this.type = var1;
      this.constructorsMap = new HashMap<>();
      Constructor[] var2 = ConstructorUtil.getConstructors(this.type);

      for (Constructor var6 : var2) {
         Class[] var8 = var6.getParameterTypes();
         Annotation[][] var9 = var6.getParameterAnnotations();
         if (var8.length == 0) {
            this.hasDefaultConstructor = true;
            this.defaultConstructor = var6;
         } else {
            int var10 = 0;
            boolean var11 = true;
            LinkedHashMap var7 = new LinkedHashMap();

            for (Class var15 : var8) {
               NamedArg var16 = null;

               for (Annotation var20 : var9[var10]) {
                  if (var20 instanceof NamedArg) {
                     var16 = (NamedArg)var20;
                     break;
                  }
               }

               if (var16 == null) {
                  var11 = false;
                  break;
               }

               ProxyBuilder.AnnotationValue var21 = new ProxyBuilder.AnnotationValue(var16.value(), var16.defaultValue(), var15);
               var7.put(var16.value(), var21);
               var10++;
            }

            if (var11) {
               this.constructorsMap.put(var6, var7);
            }
         }
      }

      if (!this.hasDefaultConstructor && this.constructorsMap.isEmpty()) {
         throw new RuntimeException("Cannot create instance of " + this.type.getCanonicalName() + " the constructor is not properly annotated.");
      } else {
         this.constructors = new TreeSet<>(this.constructorComparator);
         this.constructors.addAll(this.constructorsMap.keySet());
         this.propertiesMap = this.scanForSetters();
      }
   }

   public Object put(String var1, Object var2) {
      this.userValues.put(var1, var2);
      return null;
   }

   private Object getTemporaryContainer(String var1) {
      Object var2 = this.containers.get(var1);
      if (var2 == null) {
         var2 = this.getReadOnlyProperty(var1);
         if (var2 != null) {
            this.containers.put(var1, var2);
         }
      }

      return var2;
   }

   private Object getReadOnlyProperty(String var1) {
      return new ProxyBuilder.ArrayListWrapper();
   }

   @Override
   public int size() {
      throw new UnsupportedOperationException();
   }

   @Override
   public Set<Entry<String, Object>> entrySet() {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean isEmpty() {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean containsKey(Object var1) {
      return this.getTemporaryContainer(var1.toString()) != null;
   }

   @Override
   public boolean containsValue(Object var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Object get(Object var1) {
      return this.getTemporaryContainer(var1.toString());
   }

   public T build() {
      Object var1 = null;

      for (Entry var3 : this.containers.entrySet()) {
         this.put((String)var3.getKey(), var3.getValue());
      }

      this.propertyNames = this.userValues.keySet();

      for (Constructor var9 : this.constructors) {
         Set var4 = this.getArgumentNames(var9);
         if (this.propertyNames.equals(var4)) {
            var1 = this.createObjectWithExactArguments(var9, var4);
            if (var1 != null) {
               return (T)var1;
            }
         }
      }

      Set var8 = this.propertiesMap.keySet();
      if (var8.containsAll(this.propertyNames) && this.hasDefaultConstructor) {
         var1 = this.createObjectFromDefaultConstructor();
         if (var1 != null) {
            return (T)var1;
         }
      }

      HashSet var10 = new HashSet<>(this.propertyNames);
      var10.retainAll(var8);

      for (Constructor var6 : this.chooseBestConstructors(var8)) {
         var1 = this.createObjectFromConstructor(var6, var10);
         if (var1 != null) {
            return (T)var1;
         }
      }

      if (var1 == null) {
         throw new RuntimeException(
            "Cannot create instance of " + this.type.getCanonicalName() + " with given set of properties: " + this.userValues.keySet().toString()
         );
      } else {
         return (T)var1;
      }
   }

   private Set<Constructor> chooseBestConstructors(Set<String> var1) {
      HashSet var2 = new HashSet<>(this.propertyNames);
      var2.removeAll(var1);
      HashSet var3 = new HashSet<>(this.propertyNames);
      var3.retainAll(var1);
      int var4 = Integer.MAX_VALUE;
      int var5 = Integer.MAX_VALUE;
      TreeSet var6 = new TreeSet<>(this.constructorComparator);
      Object var7 = null;

      for (Constructor var9 : this.constructors) {
         Set var10 = this.getArgumentNames(var9);
         if (var10.containsAll(var2)) {
            HashSet var11 = new HashSet(var10);
            var11.removeAll(this.propertyNames);
            HashSet var12 = new HashSet(var3);
            var12.removeAll(var10);
            int var13 = var11.size();
            if (var4 == var13 && var5 == var12.size()) {
               var6.add(var9);
            }

            if (var4 > var13 || var4 == var13 && var5 > var12.size()) {
               var4 = var13;
               var5 = var12.size();
               var6.clear();
               var6.add(var9);
            }
         }
      }

      if (var7 != null && !var7.isEmpty()) {
         throw new RuntimeException("Cannot create instance of " + this.type.getCanonicalName() + " no constructor contains all properties specified in FXML.");
      } else {
         return var6;
      }
   }

   private Set<String> getArgumentNames(Constructor var1) {
      Map var2 = this.constructorsMap.get(var1);
      Set var3 = null;
      if (var2 != null) {
         var3 = var2.keySet();
      }

      return var3;
   }

   private Object createObjectFromDefaultConstructor() throws RuntimeException {
      Object var1 = null;

      try {
         var1 = this.createInstance(this.defaultConstructor, new Object[0]);
      } catch (Exception var6) {
         throw new RuntimeException(var6);
      }

      for (String var3 : this.propertyNames) {
         try {
            ProxyBuilder.Property var4 = this.propertiesMap.get(var3);
            var4.invoke(var1, this.getUserValue(var3, var4.getType()));
         } catch (Exception var5) {
            throw new RuntimeException(var5);
         }
      }

      return var1;
   }

   private Object createObjectFromConstructor(Constructor var1, Set<String> var2) {
      Object var3 = null;
      Map var4 = this.constructorsMap.get(var1);
      Object[] var5 = new Object[var4.size()];
      int var6 = 0;
      HashSet var7 = new HashSet(var2);

      for (ProxyBuilder.AnnotationValue var9 : var4.values()) {
         Object var10 = this.getUserValue(var9.getName(), var9.getType());
         if (var10 != null) {
            try {
               var5[var6] = BeanAdapter.coerce(var10, var9.getType());
            } catch (Exception var15) {
               return null;
            }
         } else if (!var9.getDefaultValue().isEmpty()) {
            try {
               var5[var6] = BeanAdapter.coerce(var9.getDefaultValue(), var9.getType());
            } catch (Exception var14) {
               return null;
            }
         } else {
            var5[var6] = getDefaultValue(var9.getType());
         }

         var7.remove(var9.getName());
         var6++;
      }

      try {
         var3 = this.createInstance(var1, var5);
      } catch (Exception var13) {
      }

      if (var3 != null) {
         for (String var17 : var7) {
            try {
               ProxyBuilder.Property var18 = this.propertiesMap.get(var17);
               var18.invoke(var3, this.getUserValue(var17, var18.getType()));
            } catch (Exception var12) {
               return null;
            }
         }
      }

      return var3;
   }

   private Object getUserValue(String var1, Class<?> var2) {
      Object var3 = this.userValues.get(var1);
      if (var3 == null) {
         return null;
      } else if (var2.isAssignableFrom(var3.getClass())) {
         return var3;
      } else {
         if (var2.isArray()) {
            try {
               return convertListToArray(var3, var2);
            } catch (RuntimeException var5) {
            }
         }

         if (ProxyBuilder.ArrayListWrapper.class.equals(var3.getClass())) {
            List var4 = (List)var3;
            return var4.get(0);
         } else {
            return var3;
         }
      }
   }

   private Object createObjectWithExactArguments(Constructor var1, Set<String> var2) {
      Object var3 = null;
      Object[] var4 = new Object[var2.size()];
      Map var5 = this.constructorsMap.get(var1);
      int var6 = 0;

      for (String var8 : var2) {
         Class var9 = ((ProxyBuilder.AnnotationValue)var5.get(var8)).getType();
         Object var10 = this.getUserValue(var8, var9);

         try {
            var4[var6++] = BeanAdapter.coerce(var10, var9);
         } catch (Exception var13) {
            return null;
         }
      }

      try {
         var3 = this.createInstance(var1, var4);
      } catch (Exception var12) {
      }

      return var3;
   }

   private Object createInstance(Constructor var1, Object[] var2) throws Exception {
      Object var3 = null;
      ReflectUtil.checkPackageAccess(this.type);
      return var1.newInstance(var2);
   }

   private Map<String, ProxyBuilder.Property> scanForSetters() {
      HashMap var1 = new HashMap();
      HashMap var2 = getClassMethodCache(this.type);

      for (String var4 : var2.keySet()) {
         if (var4.startsWith("set") && var4.length() > "set".length()) {
            String var5 = var4.substring("set".length());
            var5 = Character.toLowerCase(var5.charAt(0)) + var5.substring(1);

            for (Method var8 : (List)var2.get(var4)) {
               Class var9 = var8.getReturnType();
               Class[] var10 = var8.getParameterTypes();
               if (var9.equals(void.class) && var10.length == 1) {
                  var1.put(var5, new ProxyBuilder.Setter(var8, var10[0]));
               }
            }
         }

         if (var4.startsWith("get") && var4.length() > "get".length()) {
            String var12 = var4.substring("get".length());
            var12 = Character.toLowerCase(var12.charAt(0)) + var12.substring(1);

            for (Method var16 : (List)var2.get(var4)) {
               Class var17 = var16.getReturnType();
               Class[] var18 = var16.getParameterTypes();
               if (Collection.class.isAssignableFrom(var17) && var18.length == 0) {
                  var1.put(var12, new ProxyBuilder.Getter(var16, var17));
               }
            }
         }
      }

      return var1;
   }

   private static HashMap<String, LinkedList<Method>> getClassMethodCache(Class<?> var0) {
      HashMap var1 = new HashMap();
      ReflectUtil.checkPackageAccess(var0);
      Method[] var2 = var0.getMethods();

      for (Method var6 : var2) {
         int var7 = var6.getModifiers();
         if (Modifier.isPublic(var7) && !Modifier.isStatic(var7)) {
            String var8 = var6.getName();
            LinkedList var9 = (LinkedList)var1.get(var8);
            if (var9 == null) {
               var9 = new LinkedList();
               var1.put(var8, var9);
            }

            var9.add(var6);
         }
      }

      return var1;
   }

   private static Object[] convertListToArray(Object var0, Class<?> var1) {
      Class var2 = var1.getComponentType();
      List var3 = BeanAdapter.coerce(var0, List.class);
      return var3.toArray((Object[])Array.newInstance(var2, 0));
   }

   private static Object getDefaultValue(Class var0) {
      return DEFAULTS_MAP.get(var0);
   }

   static {
      DEFAULTS_MAP.put(byte.class, (byte)0);
      DEFAULTS_MAP.put(short.class, (short)0);
      DEFAULTS_MAP.put(int.class, 0);
      DEFAULTS_MAP.put(long.class, 0L);
      DEFAULTS_MAP.put(float.class, 0.0F);
      DEFAULTS_MAP.put(double.class, 0.0);
      DEFAULTS_MAP.put(char.class, '\u0000');
      DEFAULTS_MAP.put(boolean.class, false);
      DEFAULTS_MAP.put(Object.class, null);
   }

   private static class AnnotationValue {
      private final String name;
      private final String defaultValue;
      private final Class<?> type;

      public AnnotationValue(String var1, String var2, Class<?> var3) {
         this.name = var1;
         this.defaultValue = var2;
         this.type = var3;
      }

      public String getName() {
         return this.name;
      }

      public String getDefaultValue() {
         return this.defaultValue;
      }

      public Class<?> getType() {
         return this.type;
      }
   }

   private static class ArrayListWrapper<T> extends ArrayList<T> {
   }

   private static class Getter extends ProxyBuilder.Property {
      public Getter(Method var1, Class<?> var2) {
         super(var1, var2);
      }

      @Override
      public void invoke(Object var1, Object var2) throws Exception {
         Collection var3 = (Collection)ModuleHelper.invoke(this.method, var1, new Object[0]);
         if (var2 instanceof Collection) {
            Collection var4 = (Collection)var2;
            var3.addAll(var4);
         } else {
            var3.add(var2);
         }
      }
   }

   private abstract static class Property {
      protected final Method method;
      protected final Class<?> type;

      public Property(Method var1, Class<?> var2) {
         this.method = var1;
         this.type = var2;
      }

      public Class<?> getType() {
         return this.type;
      }

      public abstract void invoke(Object var1, Object var2) throws Exception;
   }

   private static class Setter extends ProxyBuilder.Property {
      public Setter(Method var1, Class<?> var2) {
         super(var1, var2);
      }

      @Override
      public void invoke(Object var1, Object var2) throws Exception {
         Object[] var3 = new Object[]{BeanAdapter.coerce(var2, this.type)};
         ModuleHelper.invoke(this.method, var1, var3);
      }
   }
}
