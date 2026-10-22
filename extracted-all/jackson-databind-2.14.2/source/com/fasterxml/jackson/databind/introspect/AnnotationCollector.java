package com.fasterxml.jackson.databind.introspect;

import com.fasterxml.jackson.databind.util.Annotations;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public abstract class AnnotationCollector {
   protected static final Annotations NO_ANNOTATIONS = new AnnotationCollector.NoAnnotations();
   protected final Object _data;

   protected AnnotationCollector(Object d) {
      this._data = d;
   }

   public static Annotations emptyAnnotations() {
      return NO_ANNOTATIONS;
   }

   public static AnnotationCollector emptyCollector() {
      return AnnotationCollector.EmptyCollector.instance;
   }

   public static AnnotationCollector emptyCollector(Object data) {
      return new AnnotationCollector.EmptyCollector(data);
   }

   public abstract Annotations asAnnotations();

   public abstract AnnotationMap asAnnotationMap();

   public Object getData() {
      return this._data;
   }

   public abstract boolean isPresent(Annotation var1);

   public abstract AnnotationCollector addOrOverride(Annotation var1);

   static class EmptyCollector extends AnnotationCollector {
      public static final AnnotationCollector.EmptyCollector instance = new AnnotationCollector.EmptyCollector(null);

      EmptyCollector(Object data) {
         super(data);
      }

      @Override
      public Annotations asAnnotations() {
         return NO_ANNOTATIONS;
      }

      @Override
      public AnnotationMap asAnnotationMap() {
         return new AnnotationMap();
      }

      @Override
      public boolean isPresent(Annotation ann) {
         return false;
      }

      @Override
      public AnnotationCollector addOrOverride(Annotation ann) {
         return new AnnotationCollector.OneCollector(this._data, ann.annotationType(), ann);
      }
   }

   static class NCollector extends AnnotationCollector {
      protected final HashMap<Class<?>, Annotation> _annotations = new HashMap<>();

      public NCollector(Object data, Class<?> type1, Annotation value1, Class<?> type2, Annotation value2) {
         super(data);
         this._annotations.put(type1, value1);
         this._annotations.put(type2, value2);
      }

      @Override
      public Annotations asAnnotations() {
         if (this._annotations.size() == 2) {
            Iterator<Entry<Class<?>, Annotation>> it = this._annotations.entrySet().iterator();
            Entry<Class<?>, Annotation> en1 = it.next();
            Entry<Class<?>, Annotation> en2 = it.next();
            return new AnnotationCollector.TwoAnnotations(en1.getKey(), en1.getValue(), en2.getKey(), en2.getValue());
         } else {
            return new AnnotationMap(this._annotations);
         }
      }

      @Override
      public AnnotationMap asAnnotationMap() {
         AnnotationMap result = new AnnotationMap();

         for (Annotation ann : this._annotations.values()) {
            result.add(ann);
         }

         return result;
      }

      @Override
      public boolean isPresent(Annotation ann) {
         return this._annotations.containsKey(ann.annotationType());
      }

      @Override
      public AnnotationCollector addOrOverride(Annotation ann) {
         this._annotations.put(ann.annotationType(), ann);
         return this;
      }
   }

   public static class NoAnnotations implements Annotations, Serializable {
      private static final long serialVersionUID = 1L;

      NoAnnotations() {
      }

      @Override
      public <A extends Annotation> A get(Class<A> cls) {
         return null;
      }

      @Override
      public boolean has(Class<?> cls) {
         return false;
      }

      @Override
      public boolean hasOneOf(Class<? extends Annotation>[] annoClasses) {
         return false;
      }

      @Override
      public int size() {
         return 0;
      }
   }

   public static class OneAnnotation implements Annotations, Serializable {
      private static final long serialVersionUID = 1L;
      private final Class<?> _type;
      private final Annotation _value;

      public OneAnnotation(Class<?> type, Annotation value) {
         this._type = type;
         this._value = value;
      }

      @Override
      public <A extends Annotation> A get(Class<A> cls) {
         return (A)(this._type == cls ? this._value : null);
      }

      @Override
      public boolean has(Class<?> cls) {
         return this._type == cls;
      }

      @Override
      public boolean hasOneOf(Class<? extends Annotation>[] annoClasses) {
         for (Class<?> cls : annoClasses) {
            if (cls == this._type) {
               return true;
            }
         }

         return false;
      }

      @Override
      public int size() {
         return 1;
      }
   }

   static class OneCollector extends AnnotationCollector {
      private Class<?> _type;
      private Annotation _value;

      public OneCollector(Object data, Class<?> type, Annotation value) {
         super(data);
         this._type = type;
         this._value = value;
      }

      @Override
      public Annotations asAnnotations() {
         return new AnnotationCollector.OneAnnotation(this._type, this._value);
      }

      @Override
      public AnnotationMap asAnnotationMap() {
         return AnnotationMap.of(this._type, this._value);
      }

      @Override
      public boolean isPresent(Annotation ann) {
         return ann.annotationType() == this._type;
      }

      @Override
      public AnnotationCollector addOrOverride(Annotation ann) {
         Class<?> type = ann.annotationType();
         if (this._type == type) {
            this._value = ann;
            return this;
         } else {
            return new AnnotationCollector.NCollector(this._data, this._type, this._value, type, ann);
         }
      }
   }

   public static class TwoAnnotations implements Annotations, Serializable {
      private static final long serialVersionUID = 1L;
      private final Class<?> _type1;
      private final Class<?> _type2;
      private final Annotation _value1;
      private final Annotation _value2;

      public TwoAnnotations(Class<?> type1, Annotation value1, Class<?> type2, Annotation value2) {
         this._type1 = type1;
         this._value1 = value1;
         this._type2 = type2;
         this._value2 = value2;
      }

      @Override
      public <A extends Annotation> A get(Class<A> cls) {
         if (this._type1 == cls) {
            return (A)this._value1;
         } else {
            return (A)(this._type2 == cls ? this._value2 : null);
         }
      }

      @Override
      public boolean has(Class<?> cls) {
         return this._type1 == cls || this._type2 == cls;
      }

      @Override
      public boolean hasOneOf(Class<? extends Annotation>[] annoClasses) {
         for (Class<?> cls : annoClasses) {
            if (cls == this._type1 || cls == this._type2) {
               return true;
            }
         }

         return false;
      }

      @Override
      public int size() {
         return 2;
      }
   }
}
