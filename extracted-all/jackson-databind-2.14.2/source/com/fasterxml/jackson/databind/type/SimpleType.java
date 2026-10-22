package com.fasterxml.jackson.databind.type;

import com.fasterxml.jackson.databind.JavaType;
import java.util.Collection;
import java.util.Map;

public class SimpleType extends TypeBase {
   private static final long serialVersionUID = 1L;

   protected SimpleType(Class<?> cls) {
      this(cls, TypeBindings.emptyBindings(), null, null);
   }

   protected SimpleType(Class<?> cls, TypeBindings bindings, JavaType superClass, JavaType[] superInts) {
      this(cls, bindings, superClass, superInts, null, null, false);
   }

   protected SimpleType(TypeBase base) {
      super(base);
   }

   protected SimpleType(
      Class<?> cls, TypeBindings bindings, JavaType superClass, JavaType[] superInts, Object valueHandler, Object typeHandler, boolean asStatic
   ) {
      super(cls, bindings, superClass, superInts, 0, valueHandler, typeHandler, asStatic);
   }

   protected SimpleType(
      Class<?> cls, TypeBindings bindings, JavaType superClass, JavaType[] superInts, int extraHash, Object valueHandler, Object typeHandler, boolean asStatic
   ) {
      super(cls, bindings, superClass, superInts, extraHash, valueHandler, typeHandler, asStatic);
   }

   public static SimpleType constructUnsafe(Class<?> raw) {
      return new SimpleType(raw, null, null, null, null, null, false);
   }

   @Deprecated
   public static SimpleType construct(Class<?> cls) {
      if (Map.class.isAssignableFrom(cls)) {
         throw new IllegalArgumentException("Cannot construct SimpleType for a Map (class: " + cls.getName() + ")");
      } else if (Collection.class.isAssignableFrom(cls)) {
         throw new IllegalArgumentException("Cannot construct SimpleType for a Collection (class: " + cls.getName() + ")");
      } else if (cls.isArray()) {
         throw new IllegalArgumentException("Cannot construct SimpleType for an array (class: " + cls.getName() + ")");
      } else {
         TypeBindings b = TypeBindings.emptyBindings();
         return new SimpleType(cls, b, _buildSuperClass(cls.getSuperclass(), b), null, null, null, false);
      }
   }

   @Deprecated
   @Override
   protected JavaType _narrow(Class<?> subclass) {
      if (this._class == subclass) {
         return this;
      } else if (!this._class.isAssignableFrom(subclass)) {
         return new SimpleType(subclass, this._bindings, this, this._superInterfaces, this._valueHandler, this._typeHandler, this._asStatic);
      } else {
         Class<?> next = subclass.getSuperclass();
         if (next == this._class) {
            return new SimpleType(subclass, this._bindings, this, this._superInterfaces, this._valueHandler, this._typeHandler, this._asStatic);
         } else if (next != null && this._class.isAssignableFrom(next)) {
            JavaType superb = this._narrow(next);
            return new SimpleType(subclass, this._bindings, superb, null, this._valueHandler, this._typeHandler, this._asStatic);
         } else {
            Class<?>[] nextI = subclass.getInterfaces();

            for (Class<?> iface : nextI) {
               if (iface == this._class) {
                  return new SimpleType(subclass, this._bindings, null, new JavaType[]{this}, this._valueHandler, this._typeHandler, this._asStatic);
               }

               if (this._class.isAssignableFrom(iface)) {
                  JavaType superb = this._narrow(iface);
                  return new SimpleType(subclass, this._bindings, null, new JavaType[]{superb}, this._valueHandler, this._typeHandler, this._asStatic);
               }
            }

            throw new IllegalArgumentException("Internal error: Cannot resolve sub-type for Class " + subclass.getName() + " to " + this._class.getName());
         }
      }
   }

   @Override
   public JavaType withContentType(JavaType contentType) {
      throw new IllegalArgumentException("Simple types have no content types; cannot call withContentType()");
   }

   public SimpleType withTypeHandler(Object h) {
      return this._typeHandler == h
         ? this
         : new SimpleType(this._class, this._bindings, this._superClass, this._superInterfaces, this._valueHandler, h, this._asStatic);
   }

   @Override
   public JavaType withContentTypeHandler(Object h) {
      throw new IllegalArgumentException("Simple types have no content types; cannot call withContenTypeHandler()");
   }

   public SimpleType withValueHandler(Object h) {
      return h == this._valueHandler
         ? this
         : new SimpleType(this._class, this._bindings, this._superClass, this._superInterfaces, h, this._typeHandler, this._asStatic);
   }

   public SimpleType withContentValueHandler(Object h) {
      throw new IllegalArgumentException("Simple types have no content types; cannot call withContenValueHandler()");
   }

   public SimpleType withStaticTyping() {
      return this._asStatic
         ? this
         : new SimpleType(this._class, this._bindings, this._superClass, this._superInterfaces, this._valueHandler, this._typeHandler, true);
   }

   @Override
   public JavaType refine(Class<?> rawType, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces) {
      return null;
   }

   @Override
   protected String buildCanonicalName() {
      StringBuilder sb = new StringBuilder();
      sb.append(this._class.getName());
      int count = this._bindings.size();
      if (count > 0 && this._hasNTypeParameters(count)) {
         sb.append('<');

         for (int i = 0; i < count; i++) {
            JavaType t = this.containedType(i);
            if (i > 0) {
               sb.append(',');
            }

            sb.append(t.toCanonical());
         }

         sb.append('>');
      }

      return sb.toString();
   }

   @Override
   public boolean isContainerType() {
      return false;
   }

   @Override
   public boolean hasContentType() {
      return false;
   }

   @Override
   public StringBuilder getErasedSignature(StringBuilder sb) {
      return _classSignature(this._class, sb, true);
   }

   @Override
   public StringBuilder getGenericSignature(StringBuilder sb) {
      _classSignature(this._class, sb, false);
      int count = this._bindings.size();
      if (count > 0) {
         sb.append('<');

         for (int i = 0; i < count; i++) {
            sb = this.containedType(i).getGenericSignature(sb);
         }

         sb.append('>');
      }

      sb.append(';');
      return sb;
   }

   private static JavaType _buildSuperClass(Class<?> superClass, TypeBindings b) {
      if (superClass == null) {
         return null;
      } else if (superClass == Object.class) {
         return TypeFactory.unknownType();
      } else {
         JavaType superSuper = _buildSuperClass(superClass.getSuperclass(), b);
         return new SimpleType(superClass, b, superSuper, null, null, null, false);
      }
   }

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder(40);
      sb.append("[simple type, class ").append(this.buildCanonicalName()).append(']');
      return sb.toString();
   }

   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (o == null) {
         return false;
      } else if (o.getClass() != this.getClass()) {
         return false;
      } else {
         SimpleType other = (SimpleType)o;
         if (other._class != this._class) {
            return false;
         } else {
            TypeBindings b1 = this._bindings;
            TypeBindings b2 = other._bindings;
            return b1.equals(b2);
         }
      }
   }
}
