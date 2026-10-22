package com.fasterxml.jackson.databind.introspect;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Type;

public final class AnnotatedConstructor extends AnnotatedWithParams {
   private static final long serialVersionUID = 1L;
   protected final Constructor<?> _constructor;
   protected AnnotatedConstructor.Serialization _serialization;

   public AnnotatedConstructor(TypeResolutionContext ctxt, Constructor<?> constructor, AnnotationMap classAnn, AnnotationMap[] paramAnn) {
      super(ctxt, classAnn, paramAnn);
      if (constructor == null) {
         throw new IllegalArgumentException("Null constructor not allowed");
      } else {
         this._constructor = constructor;
      }
   }

   protected AnnotatedConstructor(AnnotatedConstructor.Serialization ser) {
      super(null, null, null);
      this._constructor = null;
      this._serialization = ser;
   }

   public AnnotatedConstructor withAnnotations(AnnotationMap ann) {
      return new AnnotatedConstructor(this._typeContext, this._constructor, ann, this._paramAnnotations);
   }

   public Constructor<?> getAnnotated() {
      return this._constructor;
   }

   @Override
   public int getModifiers() {
      return this._constructor.getModifiers();
   }

   @Override
   public String getName() {
      return this._constructor.getName();
   }

   @Override
   public JavaType getType() {
      return this._typeContext.resolveType(this.getRawType());
   }

   @Override
   public Class<?> getRawType() {
      return this._constructor.getDeclaringClass();
   }

   @Override
   public int getParameterCount() {
      return this._constructor.getParameterCount();
   }

   @Override
   public Class<?> getRawParameterType(int index) {
      Class<?>[] types = this._constructor.getParameterTypes();
      return index >= types.length ? null : types[index];
   }

   @Override
   public JavaType getParameterType(int index) {
      Type[] types = this._constructor.getGenericParameterTypes();
      return index >= types.length ? null : this._typeContext.resolveType(types[index]);
   }

   @Deprecated
   @Override
   public Type getGenericParameterType(int index) {
      Type[] types = this._constructor.getGenericParameterTypes();
      return index >= types.length ? null : types[index];
   }

   @Override
   public final Object call() throws Exception {
      return this._constructor.newInstance((Object[])null);
   }

   @Override
   public final Object call(Object[] args) throws Exception {
      return this._constructor.newInstance(args);
   }

   @Override
   public final Object call1(Object arg) throws Exception {
      return this._constructor.newInstance(arg);
   }

   @Override
   public Class<?> getDeclaringClass() {
      return this._constructor.getDeclaringClass();
   }

   @Override
   public Member getMember() {
      return this._constructor;
   }

   @Override
   public void setValue(Object pojo, Object value) throws UnsupportedOperationException {
      throw new UnsupportedOperationException("Cannot call setValue() on constructor of " + this.getDeclaringClass().getName());
   }

   @Override
   public Object getValue(Object pojo) throws UnsupportedOperationException {
      throw new UnsupportedOperationException("Cannot call getValue() on constructor of " + this.getDeclaringClass().getName());
   }

   @Override
   public String toString() {
      int argCount = this._constructor.getParameterCount();
      return String.format(
         "[constructor for %s (%d arg%s), annotations: %s",
         ClassUtil.nameOf(this._constructor.getDeclaringClass()),
         argCount,
         argCount == 1 ? "" : "s",
         this._annotations
      );
   }

   @Override
   public int hashCode() {
      return this._constructor.getName().hashCode();
   }

   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!ClassUtil.hasClass(o, this.getClass())) {
         return false;
      } else {
         AnnotatedConstructor other = (AnnotatedConstructor)o;
         return other._constructor == null ? this._constructor == null : other._constructor.equals(this._constructor);
      }
   }

   Object writeReplace() {
      return new AnnotatedConstructor(new AnnotatedConstructor.Serialization(this._constructor));
   }

   Object readResolve() {
      Class<?> clazz = this._serialization.clazz;

      try {
         Constructor<?> ctor = clazz.getDeclaredConstructor(this._serialization.args);
         if (!ctor.isAccessible()) {
            ClassUtil.checkAndFixAccess(ctor, false);
         }

         return new AnnotatedConstructor(null, ctor, null, null);
      } catch (Exception var3) {
         throw new IllegalArgumentException("Could not find constructor with " + this._serialization.args.length + " args from Class '" + clazz.getName());
      }
   }

   private static final class Serialization implements Serializable {
      private static final long serialVersionUID = 1L;
      protected Class<?> clazz;
      protected Class<?>[] args;

      public Serialization(Constructor<?> ctor) {
         this.clazz = ctor.getDeclaringClass();
         this.args = ctor.getParameterTypes();
      }
   }
}
