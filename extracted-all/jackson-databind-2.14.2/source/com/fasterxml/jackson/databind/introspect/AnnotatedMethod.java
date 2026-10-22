package com.fasterxml.jackson.databind.introspect;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

public final class AnnotatedMethod extends AnnotatedWithParams implements Serializable {
   private static final long serialVersionUID = 1L;
   protected final transient Method _method;
   protected Class<?>[] _paramClasses;
   protected AnnotatedMethod.Serialization _serialization;

   public AnnotatedMethod(TypeResolutionContext ctxt, Method method, AnnotationMap classAnn, AnnotationMap[] paramAnnotations) {
      super(ctxt, classAnn, paramAnnotations);
      if (method == null) {
         throw new IllegalArgumentException("Cannot construct AnnotatedMethod with null Method");
      } else {
         this._method = method;
      }
   }

   protected AnnotatedMethod(AnnotatedMethod.Serialization ser) {
      super(null, null, null);
      this._method = null;
      this._serialization = ser;
   }

   public AnnotatedMethod withAnnotations(AnnotationMap ann) {
      return new AnnotatedMethod(this._typeContext, this._method, ann, this._paramAnnotations);
   }

   public Method getAnnotated() {
      return this._method;
   }

   @Override
   public int getModifiers() {
      return this._method.getModifiers();
   }

   @Override
   public String getName() {
      return this._method.getName();
   }

   @Override
   public JavaType getType() {
      return this._typeContext.resolveType(this._method.getGenericReturnType());
   }

   @Override
   public Class<?> getRawType() {
      return this._method.getReturnType();
   }

   @Override
   public final Object call() throws Exception {
      return this._method.invoke(null);
   }

   @Override
   public final Object call(Object[] args) throws Exception {
      return this._method.invoke(null, args);
   }

   @Override
   public final Object call1(Object arg) throws Exception {
      return this._method.invoke(null, arg);
   }

   public final Object callOn(Object pojo) throws Exception {
      return this._method.invoke(pojo, (Object[])null);
   }

   public final Object callOnWith(Object pojo, Object... args) throws Exception {
      return this._method.invoke(pojo, args);
   }

   @Override
   public int getParameterCount() {
      return this._method.getParameterCount();
   }

   @Override
   public Class<?> getRawParameterType(int index) {
      Class<?>[] types = this.getRawParameterTypes();
      return index >= types.length ? null : types[index];
   }

   @Override
   public JavaType getParameterType(int index) {
      Type[] types = this._method.getGenericParameterTypes();
      return index >= types.length ? null : this._typeContext.resolveType(types[index]);
   }

   @Deprecated
   @Override
   public Type getGenericParameterType(int index) {
      Type[] types = this.getGenericParameterTypes();
      return index >= types.length ? null : types[index];
   }

   @Override
   public Class<?> getDeclaringClass() {
      return this._method.getDeclaringClass();
   }

   public Method getMember() {
      return this._method;
   }

   @Override
   public void setValue(Object pojo, Object value) throws IllegalArgumentException {
      try {
         this._method.invoke(pojo, value);
      } catch (InvocationTargetException | IllegalAccessException var4) {
         throw new IllegalArgumentException("Failed to setValue() with method " + this.getFullName() + ": " + ClassUtil.exceptionMessage(var4), var4);
      }
   }

   @Override
   public Object getValue(Object pojo) throws IllegalArgumentException {
      try {
         return this._method.invoke(pojo, (Object[])null);
      } catch (InvocationTargetException | IllegalAccessException var3) {
         throw new IllegalArgumentException("Failed to getValue() with method " + this.getFullName() + ": " + ClassUtil.exceptionMessage(var3), var3);
      }
   }

   @Override
   public String getFullName() {
      String methodName = super.getFullName();
      switch (this.getParameterCount()) {
         case 0:
            return methodName + "()";
         case 1:
            return methodName + "(" + this.getRawParameterType(0).getName() + ")";
         default:
            return String.format("%s(%d params)", super.getFullName(), this.getParameterCount());
      }
   }

   public Class<?>[] getRawParameterTypes() {
      if (this._paramClasses == null) {
         this._paramClasses = this._method.getParameterTypes();
      }

      return this._paramClasses;
   }

   @Deprecated
   public Type[] getGenericParameterTypes() {
      return this._method.getGenericParameterTypes();
   }

   public Class<?> getRawReturnType() {
      return this._method.getReturnType();
   }

   @Deprecated
   public boolean hasReturnType() {
      Class<?> rt = this.getRawReturnType();
      return rt != void.class;
   }

   @Override
   public String toString() {
      return "[method " + this.getFullName() + "]";
   }

   @Override
   public int hashCode() {
      return this._method.getName().hashCode();
   }

   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!ClassUtil.hasClass(o, this.getClass())) {
         return false;
      } else {
         AnnotatedMethod other = (AnnotatedMethod)o;
         return other._method == null ? this._method == null : other._method.equals(this._method);
      }
   }

   Object writeReplace() {
      return new AnnotatedMethod(new AnnotatedMethod.Serialization(this._method));
   }

   Object readResolve() {
      Class<?> clazz = this._serialization.clazz;

      try {
         Method m = clazz.getDeclaredMethod(this._serialization.name, this._serialization.args);
         if (!m.isAccessible()) {
            ClassUtil.checkAndFixAccess(m, false);
         }

         return new AnnotatedMethod(null, m, null, null);
      } catch (Exception var3) {
         throw new IllegalArgumentException("Could not find method '" + this._serialization.name + "' from Class '" + clazz.getName());
      }
   }

   private static final class Serialization implements Serializable {
      private static final long serialVersionUID = 1L;
      protected Class<?> clazz;
      protected String name;
      protected Class<?>[] args;

      public Serialization(Method setter) {
         this.clazz = setter.getDeclaringClass();
         this.name = setter.getName();
         this.args = setter.getParameterTypes();
      }
   }
}
