package com.sun.javafx.property.adapter;

import com.sun.javafx.reflect.ReflectUtil;
import java.lang.reflect.Method;

public class JavaBeanPropertyBuilderHelper {
   private static final String IS_PREFIX = "is";
   private static final String GET_PREFIX = "get";
   private static final String SET_PREFIX = "set";
   private String propertyName;
   private Class<?> beanClass;
   private Object bean;
   private String getterName;
   private String setterName;
   private Method getter;
   private Method setter;
   private PropertyDescriptor descriptor;

   public void name(String var1) {
      if (var1 == null ? this.propertyName != null : !var1.equals(this.propertyName)) {
         this.propertyName = var1;
         this.descriptor = null;
      }
   }

   public void beanClass(Class<?> var1) {
      if (var1 == null ? this.beanClass != null : !var1.equals(this.beanClass)) {
         ReflectUtil.checkPackageAccess(var1);
         this.beanClass = var1;
         this.descriptor = null;
      }
   }

   public void bean(Object var1) {
      this.bean = var1;
      if (var1 != null) {
         Class var2 = var1.getClass();
         if (this.beanClass == null || !this.beanClass.isAssignableFrom(var2)) {
            ReflectUtil.checkPackageAccess(var2);
            this.beanClass = var2;
            this.descriptor = null;
         }
      }
   }

   public Object getBean() {
      return this.bean;
   }

   public void getterName(String var1) {
      if (var1 == null ? this.getterName != null : !var1.equals(this.getterName)) {
         this.getterName = var1;
         this.descriptor = null;
      }
   }

   public void setterName(String var1) {
      if (var1 == null ? this.setterName != null : !var1.equals(this.setterName)) {
         this.setterName = var1;
         this.descriptor = null;
      }
   }

   public void getter(Method var1) {
      if (var1 == null ? this.getter != null : !var1.equals(this.getter)) {
         this.getter = var1;
         this.descriptor = null;
      }
   }

   public void setter(Method var1) {
      if (var1 == null ? this.setter != null : !var1.equals(this.setter)) {
         this.setter = var1;
         this.descriptor = null;
      }
   }

   public PropertyDescriptor getDescriptor() throws NoSuchMethodException {
      if (this.descriptor == null) {
         if (this.propertyName == null) {
            throw new NullPointerException("Property name has to be specified");
         }

         if (this.propertyName.isEmpty()) {
            throw new IllegalArgumentException("Property name cannot be empty");
         }

         String var1 = ReadOnlyPropertyDescriptor.capitalizedName(this.propertyName);
         Method var2 = this.getter;
         if (var2 == null) {
            if (this.getterName != null && !this.getterName.isEmpty()) {
               var2 = this.beanClass.getMethod(this.getterName);
            } else {
               try {
                  var2 = this.beanClass.getMethod("is" + var1);
               } catch (NoSuchMethodException var5) {
                  var2 = this.beanClass.getMethod("get" + var1);
               }
            }
         }

         Method var3 = this.setter;
         if (var3 == null) {
            Class var4 = var2.getReturnType();
            if (this.setterName != null && !this.setterName.isEmpty()) {
               var3 = this.beanClass.getMethod(this.setterName, var4);
            } else {
               var3 = this.beanClass.getMethod("set" + var1, var4);
            }
         }

         this.descriptor = new PropertyDescriptor(this.propertyName, this.beanClass, var2, var3);
      }

      return this.descriptor;
   }
}
