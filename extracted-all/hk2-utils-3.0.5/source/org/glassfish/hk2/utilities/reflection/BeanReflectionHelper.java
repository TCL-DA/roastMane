package org.glassfish.hk2.utilities.reflection;

import java.beans.Introspector;
import java.beans.PropertyChangeEvent;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import org.glassfish.hk2.utilities.general.GeneralUtilities;

public class BeanReflectionHelper {
   private static final String GET = "get";
   private static final String IS = "is";

   public static String getBeanPropertyNameFromGetter(Method method) {
      return isAGetter(method);
   }

   private static String isAGetter(MethodWrapper method) {
      return isAGetter(method.getMethod());
   }

   private static String isAGetter(Method m) {
      String name = m.getName();
      if (void.class.equals(m.getReturnType())) {
         return null;
      } else {
         Class<?>[] params = m.getParameterTypes();
         if (params.length != 0) {
            return null;
         } else if ((m.getModifiers() & 1) == 0) {
            return null;
         } else {
            int capIndex;
            if (name.startsWith("get") && name.length() > "get".length()) {
               capIndex = "get".length();
            } else {
               if (!name.startsWith("is") || name.length() <= "is".length()) {
                  return null;
               }

               capIndex = "is".length();
            }

            if (!Character.isUpperCase(name.charAt(capIndex))) {
               return null;
            } else {
               String rawPropName = name.substring(capIndex);
               return Introspector.decapitalize(rawPropName);
            }
         }
      }
   }

   private static Method findMethod(Method m, Class<?> c) {
      String name = m.getName();
      Class<?>[] params = new Class[0];

      try {
         return c.getMethod(name, params);
      } catch (Throwable var5) {
         return null;
      }
   }

   private static Object getValue(Object bean, Method m) {
      try {
         return m.invoke(bean);
      } catch (Throwable var3) {
         return null;
      }
   }

   private static PropertyChangeEvent[] getMapChangeEvents(Map<String, Object> oldBean, Map<String, Object> newBean) {
      LinkedList<PropertyChangeEvent> retVal = new LinkedList<>();
      Set<String> newKeys = new HashSet<>(newBean.keySet());

      for (Entry<String, Object> entry : oldBean.entrySet()) {
         String key = entry.getKey();
         Object oldValue = entry.getValue();
         Object newValue = newBean.get(key);
         newKeys.remove(key);
         if (!GeneralUtilities.safeEquals(oldValue, newValue)) {
            retVal.add(new PropertyChangeEvent(newBean, key, oldValue, newValue));
         }
      }

      for (String newKey : newKeys) {
         retVal.add(new PropertyChangeEvent(newBean, newKey, null, newBean.get(newKey)));
      }

      return retVal.toArray(new PropertyChangeEvent[retVal.size()]);
   }

   public static PropertyChangeEvent[] getChangeEvents(ClassReflectionHelper helper, Object oldBean, Object newBean) {
      if (oldBean instanceof Map) {
         return getMapChangeEvents((Map<String, Object>)oldBean, (Map<String, Object>)newBean);
      } else {
         LinkedList<PropertyChangeEvent> retVal = new LinkedList<>();

         for (MethodWrapper wrapper : helper.getAllMethods(oldBean.getClass())) {
            String propName = isAGetter(wrapper);
            if (propName != null) {
               Method method = wrapper.getMethod();
               Method newMethod = findMethod(method, newBean.getClass());
               if (newMethod != null) {
                  Object oldValue = getValue(oldBean, method);
                  Object newValue = getValue(newBean, newMethod);
                  if (!GeneralUtilities.safeEquals(oldValue, newValue)) {
                     retVal.add(new PropertyChangeEvent(newBean, propName, oldValue, newValue));
                  }
               }
            }
         }

         return retVal.toArray(new PropertyChangeEvent[retVal.size()]);
      }
   }

   public static Map<String, Object> convertJavaBeanToBeanLikeMap(ClassReflectionHelper helper, Object bean) {
      HashMap<String, Object> retVal = new HashMap<>();

      for (MethodWrapper wrapper : helper.getAllMethods(bean.getClass())) {
         String propName = isAGetter(wrapper);
         if (propName != null && !"class".equals(propName)) {
            Method method = wrapper.getMethod();
            Object value = getValue(bean, method);
            retVal.put(propName, value);
         }
      }

      return retVal;
   }
}
