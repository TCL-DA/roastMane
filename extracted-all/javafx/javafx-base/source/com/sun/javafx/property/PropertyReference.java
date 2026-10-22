package com.sun.javafx.property;

import com.sun.javafx.reflect.ReflectUtil;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import javafx.beans.property.ReadOnlyProperty;

public final class PropertyReference<T> {
   private String name;
   private Method getter;
   private Method setter;
   private Method propertyGetter;
   private Class<?> clazz;
   private Class<?> type;
   private boolean reflected = false;

   public PropertyReference(Class<?> var1, String var2) {
      if (var2 == null) {
         throw new NullPointerException("Name must be specified");
      } else if (var2.trim().length() == 0) {
         throw new IllegalArgumentException("Name must be specified");
      } else if (var1 == null) {
         throw new NullPointerException("Class must be specified");
      } else {
         ReflectUtil.checkPackageAccess(var1);
         this.name = var2;
         this.clazz = var1;
      }
   }

   public boolean isWritable() {
      this.reflect();
      return this.setter != null;
   }

   public boolean isReadable() {
      this.reflect();
      return this.getter != null;
   }

   public boolean hasProperty() {
      this.reflect();
      return this.propertyGetter != null;
   }

   public String getName() {
      return this.name;
   }

   public Class<?> getContainingClass() {
      return this.clazz;
   }

   public Class<?> getType() {
      this.reflect();
      return this.type;
   }

   public void set(Object var1, T var2) {
      if (!this.isWritable()) {
         throw new IllegalStateException("Cannot write to readonly property " + this.name);
      } else {
         assert this.setter != null;

         try {
            MethodHelper.invoke(this.setter, var1, new Object[]{var2});
         } catch (Exception var4) {
            throw new RuntimeException(var4);
         }
      }
   }

   public T get(Object var1) {
      if (!this.isReadable()) {
         throw new IllegalStateException("Cannot read from unreadable property " + this.name);
      } else {
         assert this.getter != null;

         try {
            return (T)MethodHelper.invoke(this.getter, var1, (Object[])null);
         } catch (Exception var3) {
            throw new RuntimeException(var3);
         }
      }
   }

   public ReadOnlyProperty<T> getProperty(Object var1) {
      if (!this.hasProperty()) {
         throw new IllegalStateException("Cannot get property " + this.name);
      } else {
         assert this.propertyGetter != null;

         try {
            return (ReadOnlyProperty<T>)MethodHelper.invoke(this.propertyGetter, var1, (Object[])null);
         } catch (Exception var3) {
            throw new RuntimeException(var3);
         }
      }
   }

   @Override
   public String toString() {
      return this.name;
   }

   private void reflect() {
      if (!this.reflected) {
         this.reflected = true;

         try {
            String var1 = this.name.length() == 1
               ? this.name.substring(0, 1).toUpperCase()
               : Character.toUpperCase(this.name.charAt(0)) + this.name.substring(1);
            this.type = null;
            String var2 = "get" + var1;

            try {
               Method var3 = this.clazz.getMethod(var2);
               if (Modifier.isPublic(var3.getModifiers())) {
                  this.getter = var3;
               }
            } catch (NoSuchMethodException var13) {
            }

            if (this.getter == null) {
               var2 = "is" + var1;

               try {
                  Method var16 = this.clazz.getMethod(var2);
                  if (Modifier.isPublic(var16.getModifiers())) {
                     this.getter = var16;
                  }
               } catch (NoSuchMethodException var12) {
               }
            }

            String var17 = "set" + var1;
            if (this.getter != null) {
               this.type = this.getter.getReturnType();

               try {
                  Method var18 = this.clazz.getMethod(var17, this.type);
                  if (Modifier.isPublic(var18.getModifiers())) {
                     this.setter = var18;
                  }
               } catch (NoSuchMethodException var11) {
               }
            } else {
               Method[] var4 = this.clazz.getMethods();

               for (Method var8 : var4) {
                  Class[] var9 = var8.getParameterTypes();
                  if (var17.equals(var8.getName()) && var9.length == 1 && Modifier.isPublic(var8.getModifiers())) {
                     this.setter = var8;
                     this.type = var9[0];
                     break;
                  }
               }
            }

            String var19 = this.name + "Property";

            try {
               Method var20 = this.clazz.getMethod(var19);
               if (Modifier.isPublic(var20.getModifiers())) {
                  this.propertyGetter = var20;
               } else {
                  this.propertyGetter = null;
               }
            } catch (NoSuchMethodException var10) {
            }
         } catch (RuntimeException var14) {
            System.err.println("Failed to introspect property " + this.name);
         }
      }
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof PropertyReference)) {
         return false;
      } else {
         PropertyReference var2 = (PropertyReference)var1;
         return this.name == var2.name || this.name != null && this.name.equals(var2.name)
            ? this.clazz == var2.clazz || this.clazz != null && this.clazz.equals(var2.clazz)
            : false;
      }
   }

   @Override
   public int hashCode() {
      int var1 = 5;
      var1 = 97 * var1 + (this.name != null ? this.name.hashCode() : 0);
      return 97 * var1 + (this.clazz != null ? this.clazz.hashCode() : 0);
   }
}
