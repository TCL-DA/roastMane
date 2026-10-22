package com.sun.javafx.fxml;

import com.sun.javafx.reflect.FieldUtil;
import com.sun.javafx.reflect.MethodUtil;
import com.sun.javafx.reflect.ReflectUtil;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import javafx.beans.value.ObservableValue;

public class BeanAdapter extends AbstractMap<String, Object> {
   private final Object bean;
   private static final HashMap<Class<?>, BeanAdapter.MethodCache> globalMethodCache = new HashMap<>();
   private final BeanAdapter.MethodCache localCache;
   public static final String GET_PREFIX = "get";
   public static final String IS_PREFIX = "is";
   public static final String SET_PREFIX = "set";
   public static final String PROPERTY_SUFFIX = "Property";
   public static final String VALUE_OF_METHOD_NAME = "valueOf";

   public BeanAdapter(Object var1) {
      this.bean = var1;
      this.localCache = getClassMethodCache(var1.getClass());
   }

   private static BeanAdapter.MethodCache getClassMethodCache(final Class<?> var0) {
      if (var0 == Object.class) {
         return null;
      } else {
         synchronized (globalMethodCache) {
            BeanAdapter.MethodCache var1;
            if ((var1 = globalMethodCache.get(var0)) != null) {
               return var1;
            } else {
               HashMap var3 = new HashMap();
               ReflectUtil.checkPackageAccess(var0);
               if (Modifier.isPublic(var0.getModifiers())) {
                  Method[] var4 = AccessController.doPrivileged(new PrivilegedAction<Method[]>() {
                     public Method[] run() {
                        return var0.getDeclaredMethods();
                     }
                  });

                  for (int var5 = 0; var5 < var4.length; var5++) {
                     Method var6 = var4[var5];
                     int var7 = var6.getModifiers();
                     if (Modifier.isPublic(var7) && !Modifier.isStatic(var7)) {
                        String var8 = var6.getName();
                        Object var9 = (List)var3.get(var8);
                        if (var9 == null) {
                           var9 = new ArrayList();
                           var3.put(var8, var9);
                        }

                        var9.add(var6);
                     }
                  }
               }

               BeanAdapter.MethodCache var12 = new BeanAdapter.MethodCache(var3, getClassMethodCache(var0.getSuperclass()));
               globalMethodCache.put(var0, var12);
               return var12;
            }
         }
      }
   }

   public Object getBean() {
      return this.bean;
   }

   private Method getGetterMethod(String var1) {
      Method var2 = this.localCache.getMethod(getMethodName("get", var1));
      if (var2 == null) {
         var2 = this.localCache.getMethod(getMethodName("is", var1));
      }

      return var2;
   }

   private Method getSetterMethod(String var1) {
      Class var2 = this.getType(var1);
      if (var2 == null) {
         throw new UnsupportedOperationException("Cannot determine type for property.");
      } else {
         return this.localCache.getMethod(getMethodName("set", var1), var2);
      }
   }

   private static String getMethodName(String var0, String var1) {
      return var0 + Character.toUpperCase(var1.charAt(0)) + var1.substring(1);
   }

   @Override
   public Object get(Object var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         return this.get(var1.toString());
      }
   }

   private Object get(String var1) {
      Method var2 = var1.endsWith("Property") ? this.localCache.getMethod(var1) : this.getGetterMethod(var1);
      Object var3;
      if (var2 != null) {
         try {
            var3 = ModuleHelper.invoke(var2, this.bean, (Object[])null);
         } catch (IllegalAccessException var5) {
            throw new RuntimeException(var5);
         } catch (InvocationTargetException var6) {
            throw new RuntimeException(var6);
         }
      } else {
         var3 = null;
      }

      return var3;
   }

   public Object put(String var1, Object var2) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         Method var3 = this.getSetterMethod(var1);
         if (var3 == null) {
            throw new PropertyNotFoundException("Property \"" + var1 + "\" does not exist or is read-only.");
         } else {
            try {
               ModuleHelper.invoke(var3, this.bean, new Object[]{coerce(var2, this.getType(var1))});
               return null;
            } catch (IllegalAccessException var5) {
               throw new RuntimeException(var5);
            } catch (InvocationTargetException var6) {
               throw new RuntimeException(var6);
            }
         }
      }
   }

   @Override
   public boolean containsKey(Object var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         return this.getType(var1.toString()) != null;
      }
   }

   @Override
   public Set<Entry<String, Object>> entrySet() {
      throw new UnsupportedOperationException();
   }

   public boolean isReadOnly(String var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         return this.getSetterMethod(var1) == null;
      }
   }

   public <T> ObservableValue<T> getPropertyModel(String var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         return (ObservableValue<T>)this.get(var1 + "Property");
      }
   }

   public Class<?> getType(String var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         Method var2 = this.getGetterMethod(var1);
         return var2 == null ? null : var2.getReturnType();
      }
   }

   public Type getGenericType(String var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         Method var2 = this.getGetterMethod(var1);
         return var2 == null ? null : var2.getGenericReturnType();
      }
   }

   @Override
   public boolean equals(Object var1) {
      boolean var2 = false;
      if (var1 instanceof BeanAdapter) {
         BeanAdapter var3 = (BeanAdapter)var1;
         var2 = this.bean == var3.bean;
      }

      return var2;
   }

   @Override
   public int hashCode() {
      return this.bean == null ? -1 : this.bean.hashCode();
   }

   public static <T> T coerce(Object var0, Class<? extends T> var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         Object var2 = null;
         if (var0 == null) {
            var2 = null;
         } else if (var1.isAssignableFrom(var0.getClass())) {
            var2 = var0;
         } else if (var1 == Boolean.class || var1 == boolean.class) {
            var2 = Boolean.valueOf(var0.toString());
         } else if (var1 == Character.class || var1 == char.class) {
            var2 = var0.toString().charAt(0);
         } else if (var1 != Byte.class && var1 != byte.class) {
            if (var1 != Short.class && var1 != short.class) {
               if (var1 != Integer.class && var1 != int.class) {
                  if (var1 != Long.class && var1 != long.class) {
                     if (var1 == BigInteger.class) {
                        if (var0 instanceof Number) {
                           var2 = BigInteger.valueOf(((Number)var0).longValue());
                        } else {
                           var2 = new BigInteger(var0.toString());
                        }
                     } else if (var1 != Float.class && var1 != float.class) {
                        if (var1 != Double.class && var1 != double.class) {
                           if (var1 == Number.class) {
                              String var3 = var0.toString();
                              if (var3.contains(".")) {
                                 var2 = Double.valueOf(var3);
                              } else {
                                 var2 = Long.valueOf(var3);
                              }
                           } else if (var1 == BigDecimal.class) {
                              if (var0 instanceof Number) {
                                 var2 = BigDecimal.valueOf(((Number)var0).doubleValue());
                              } else {
                                 var2 = new BigDecimal(var0.toString());
                              }
                           } else if (var1 == Class.class) {
                              try {
                                 String var12 = var0.toString();
                                 ReflectUtil.checkPackageAccess(var12);
                                 ClassLoader var4 = Thread.currentThread().getContextClassLoader();
                                 var2 = Class.forName(var12, false, var4);
                              } catch (ClassNotFoundException var10) {
                                 throw new IllegalArgumentException(var10);
                              }
                           } else {
                              Class var13 = var0.getClass();
                              Method var14 = null;

                              while (var14 == null && var13 != null) {
                                 try {
                                    ReflectUtil.checkPackageAccess(var1);
                                    var14 = var1.getDeclaredMethod("valueOf", var13);
                                 } catch (NoSuchMethodException var9) {
                                 }

                                 if (var14 == null) {
                                    var13 = var13.getSuperclass();
                                 }
                              }

                              if (var14 == null) {
                                 throw new IllegalArgumentException("Unable to coerce " + var0 + " to " + var1 + ".");
                              }

                              if (var1.isEnum() && var0 instanceof String && Character.isLowerCase(((String)var0).charAt(0))) {
                                 var0 = toAllCaps((String)var0);
                              }

                              try {
                                 var2 = ModuleHelper.invoke(var14, null, new Object[]{var0});
                              } catch (IllegalAccessException var6) {
                                 throw new RuntimeException(var6);
                              } catch (InvocationTargetException var7) {
                                 throw new RuntimeException(var7);
                              } catch (SecurityException var8) {
                                 throw new RuntimeException(var8);
                              }
                           }
                        } else if (var0 instanceof Number) {
                           var2 = ((Number)var0).doubleValue();
                        } else {
                           var2 = Double.valueOf(var0.toString());
                        }
                     } else if (var0 instanceof Number) {
                        var2 = ((Number)var0).floatValue();
                     } else {
                        var2 = Float.valueOf(var0.toString());
                     }
                  } else if (var0 instanceof Number) {
                     var2 = ((Number)var0).longValue();
                  } else {
                     var2 = Long.valueOf(var0.toString());
                  }
               } else if (var0 instanceof Number) {
                  var2 = ((Number)var0).intValue();
               } else {
                  var2 = Integer.valueOf(var0.toString());
               }
            } else if (var0 instanceof Number) {
               var2 = ((Number)var0).shortValue();
            } else {
               var2 = Short.valueOf(var0.toString());
            }
         } else if (var0 instanceof Number) {
            var2 = ((Number)var0).byteValue();
         } else {
            var2 = Byte.valueOf(var0.toString());
         }

         return (T)var2;
      }
   }

   public static <T> T get(Object var0, Class<?> var1, String var2) {
      Object var3 = null;
      Class var4 = var0.getClass();
      Method var5 = getStaticGetterMethod(var1, var2, var4);
      if (var5 != null) {
         try {
            var3 = ModuleHelper.invoke(var5, null, new Object[]{var0});
         } catch (InvocationTargetException var7) {
            throw new RuntimeException(var7);
         } catch (IllegalAccessException var8) {
            throw new RuntimeException(var8);
         }
      }

      return (T)var3;
   }

   public static void put(Object var0, Class<?> var1, String var2, Object var3) {
      Class var4 = var0.getClass();
      Method var5 = null;
      if (var3 != null) {
         var5 = getStaticSetterMethod(var1, var2, var3.getClass(), var4);
      }

      if (var5 == null) {
         Class var6 = getType(var1, var2, var4);
         if (var6 != null) {
            var5 = getStaticSetterMethod(var1, var2, var6, var4);
            var3 = coerce(var3, var6);
         }
      }

      if (var5 == null) {
         throw new PropertyNotFoundException("Static property \"" + var2 + "\" does not exist or is read-only.");
      } else {
         try {
            ModuleHelper.invoke(var5, null, new Object[]{var0, var3});
         } catch (InvocationTargetException var7) {
            throw new RuntimeException(var7);
         } catch (IllegalAccessException var8) {
            throw new RuntimeException(var8);
         }
      }
   }

   public static boolean isDefined(Class<?> var0, String var1, Class<?> var2) {
      return getStaticGetterMethod(var0, var1, var2) != null;
   }

   public static Class<?> getType(Class<?> var0, String var1, Class<?> var2) {
      Method var3 = getStaticGetterMethod(var0, var1, var2);
      return var3 == null ? null : var3.getReturnType();
   }

   public static Type getGenericType(Class<?> var0, String var1, Class<?> var2) {
      Method var3 = getStaticGetterMethod(var0, var1, var2);
      return var3 == null ? null : var3.getGenericReturnType();
   }

   public static Class<?> getListItemType(Type var0) {
      Type var1 = getGenericListItemType(var0);
      if (var1 instanceof ParameterizedType) {
         var1 = ((ParameterizedType)var1).getRawType();
      }

      return (Class<?>)var1;
   }

   public static Class<?> getMapValueType(Type var0) {
      Type var1 = getGenericMapValueType(var0);
      if (var1 instanceof ParameterizedType) {
         var1 = ((ParameterizedType)var1).getRawType();
      }

      return (Class<?>)var1;
   }

   public static Type getGenericListItemType(Type var0) {
      Object var1 = null;
      Type var2 = var0;

      while (var2 != null) {
         if (var2 instanceof ParameterizedType) {
            ParameterizedType var9 = (ParameterizedType)var2;
            Class var10 = (Class)var9.getRawType();
            if (List.class.isAssignableFrom(var10)) {
               var1 = var9.getActualTypeArguments()[0];
            }
            break;
         }

         Class var3 = (Class)var2;
         Type[] var4 = var3.getGenericInterfaces();

         for (int var5 = 0; var5 < var4.length; var5++) {
            Type var6 = var4[var5];
            if (var6 instanceof ParameterizedType) {
               ParameterizedType var7 = (ParameterizedType)var6;
               Class var8 = (Class)var7.getRawType();
               if (List.class.isAssignableFrom(var8)) {
                  var1 = var7.getActualTypeArguments()[0];
                  break;
               }
            }
         }

         if (var1 != null) {
            break;
         }

         var2 = var3.getGenericSuperclass();
      }

      if (var1 != null && var1 instanceof TypeVariable) {
         var1 = Object.class;
      }

      return (Type)var1;
   }

   public static Type getGenericMapValueType(Type var0) {
      Object var1 = null;
      Type var2 = var0;

      while (var2 != null) {
         if (var2 instanceof ParameterizedType) {
            ParameterizedType var9 = (ParameterizedType)var2;
            Class var10 = (Class)var9.getRawType();
            if (Map.class.isAssignableFrom(var10)) {
               var1 = var9.getActualTypeArguments()[1];
            }
            break;
         }

         Class var3 = (Class)var2;
         Type[] var4 = var3.getGenericInterfaces();

         for (int var5 = 0; var5 < var4.length; var5++) {
            Type var6 = var4[var5];
            if (var6 instanceof ParameterizedType) {
               ParameterizedType var7 = (ParameterizedType)var6;
               Class var8 = (Class)var7.getRawType();
               if (Map.class.isAssignableFrom(var8)) {
                  var1 = var7.getActualTypeArguments()[1];
                  break;
               }
            }
         }

         if (var1 != null) {
            break;
         }

         var2 = var3.getGenericSuperclass();
      }

      if (var1 != null && var1 instanceof TypeVariable) {
         var1 = Object.class;
      }

      return (Type)var1;
   }

   public static Object getConstantValue(Class<?> var0, String var1) {
      if (var0 == null) {
         throw new IllegalArgumentException();
      } else if (var1 == null) {
         throw new IllegalArgumentException();
      } else {
         Field var2;
         try {
            var2 = FieldUtil.getField(var0, var1);
         } catch (NoSuchFieldException var7) {
            throw new IllegalArgumentException(var7);
         }

         int var3 = var2.getModifiers();
         if ((var3 & 8) != 0 && (var3 & 16) != 0) {
            try {
               return var2.get(null);
            } catch (IllegalAccessException var6) {
               throw new IllegalArgumentException(var6);
            }
         } else {
            throw new IllegalArgumentException("Field is not a constant.");
         }
      }
   }

   private static Method getStaticGetterMethod(Class<?> var0, String var1, Class<?> var2) {
      if (var0 == null) {
         throw new NullPointerException();
      } else if (var1 == null) {
         throw new NullPointerException();
      } else {
         Method var3 = null;
         if (var2 != null) {
            var1 = Character.toUpperCase(var1.charAt(0)) + var1.substring(1);
            String var4 = "get" + var1;
            String var5 = "is" + var1;

            try {
               var3 = MethodUtil.getMethod(var0, var4, new Class[]{var2});
            } catch (NoSuchMethodException var12) {
            }

            if (var3 == null) {
               try {
                  var3 = MethodUtil.getMethod(var0, var5, new Class[]{var2});
               } catch (NoSuchMethodException var11) {
               }
            }

            if (var3 == null) {
               Class[] var6 = var2.getInterfaces();

               for (int var7 = 0; var7 < var6.length; var7++) {
                  try {
                     var3 = MethodUtil.getMethod(var0, var4, new Class[]{var6[var7]});
                  } catch (NoSuchMethodException var10) {
                  }

                  if (var3 == null) {
                     try {
                        var3 = MethodUtil.getMethod(var0, var5, new Class[]{var6[var7]});
                     } catch (NoSuchMethodException var9) {
                     }
                  }

                  if (var3 != null) {
                     break;
                  }
               }
            }

            if (var3 == null) {
               var3 = getStaticGetterMethod(var0, var1, var2.getSuperclass());
            }
         }

         return var3;
      }
   }

   private static Method getStaticSetterMethod(Class<?> var0, String var1, Class<?> var2, Class<?> var3) {
      if (var0 == null) {
         throw new NullPointerException();
      } else if (var1 == null) {
         throw new NullPointerException();
      } else if (var2 == null) {
         throw new NullPointerException();
      } else {
         Method var4 = null;
         if (var3 != null) {
            var1 = Character.toUpperCase(var1.charAt(0)) + var1.substring(1);
            String var5 = "set" + var1;

            try {
               var4 = MethodUtil.getMethod(var0, var5, new Class[]{var3, var2});
            } catch (NoSuchMethodException var10) {
            }

            if (var4 == null) {
               Class[] var6 = var3.getInterfaces();

               for (int var7 = 0; var7 < var6.length; var7++) {
                  try {
                     var4 = MethodUtil.getMethod(var0, var5, new Class[]{var6[var7], var2});
                  } catch (NoSuchMethodException var9) {
                  }

                  if (var4 != null) {
                     break;
                  }
               }
            }

            if (var4 == null) {
               var4 = getStaticSetterMethod(var0, var1, var2, var3.getSuperclass());
            }
         }

         return var4;
      }
   }

   private static String toAllCaps(String var0) {
      if (var0 == null) {
         throw new NullPointerException();
      } else {
         StringBuilder var1 = new StringBuilder();
         int var2 = 0;

         for (int var3 = var0.length(); var2 < var3; var2++) {
            char var4 = var0.charAt(var2);
            if (Character.isUpperCase(var4)) {
               var1.append('_');
            }

            var1.append(Character.toUpperCase(var4));
         }

         return var1.toString();
      }
   }

   private static class MethodCache {
      private final Map<String, List<Method>> methods;
      private final BeanAdapter.MethodCache nextClassCache;

      private MethodCache(Map<String, List<Method>> var1, BeanAdapter.MethodCache var2) {
         this.methods = var1;
         this.nextClassCache = var2;
      }

      private Method getMethod(String var1, Class<?>... var2) {
         List var3 = this.methods.get(var1);
         if (var3 != null) {
            for (int var4 = 0; var4 < var3.size(); var4++) {
               Method var5 = (Method)var3.get(var4);
               if (var5.getName().equals(var1) && Arrays.equals((Object[])var5.getParameterTypes(), (Object[])var2)) {
                  return var5;
               }
            }
         }

         return this.nextClassCache != null ? this.nextClassCache.getMethod(var1, var2) : null;
      }
   }
}
