package com.fasterxml.jackson.databind.type;

import com.fasterxml.jackson.databind.JavaType;

public class ResolvedRecursiveType extends TypeBase {
   private static final long serialVersionUID = 1L;
   protected JavaType _referencedType;

   public ResolvedRecursiveType(Class<?> erasedType, TypeBindings bindings) {
      super(erasedType, bindings, null, null, 0, null, null, false);
   }

   public void setReference(JavaType ref) {
      if (this._referencedType != null) {
         throw new IllegalStateException("Trying to re-set self reference; old value = " + this._referencedType + ", new = " + ref);
      } else {
         this._referencedType = ref;
      }
   }

   @Override
   public JavaType getSuperClass() {
      return this._referencedType != null ? this._referencedType.getSuperClass() : super.getSuperClass();
   }

   public JavaType getSelfReferencedType() {
      return this._referencedType;
   }

   @Override
   public TypeBindings getBindings() {
      return this._referencedType != null ? this._referencedType.getBindings() : super.getBindings();
   }

   @Override
   public StringBuilder getGenericSignature(StringBuilder sb) {
      return this._referencedType != null ? this._referencedType.getErasedSignature(sb) : sb.append("?");
   }

   @Override
   public StringBuilder getErasedSignature(StringBuilder sb) {
      return this._referencedType != null ? this._referencedType.getErasedSignature(sb) : sb;
   }

   @Override
   public JavaType withContentType(JavaType contentType) {
      return this;
   }

   @Override
   public JavaType withTypeHandler(Object h) {
      return this;
   }

   @Override
   public JavaType withContentTypeHandler(Object h) {
      return this;
   }

   @Override
   public JavaType withValueHandler(Object h) {
      return this;
   }

   @Override
   public JavaType withContentValueHandler(Object h) {
      return this;
   }

   @Override
   public JavaType withStaticTyping() {
      return this;
   }

   @Deprecated
   @Override
   protected JavaType _narrow(Class<?> subclass) {
      return this;
   }

   @Override
   public JavaType refine(Class<?> rawType, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces) {
      return null;
   }

   @Override
   public boolean isContainerType() {
      return false;
   }

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder(40).append("[recursive type; ");
      if (this._referencedType == null) {
         sb.append("UNRESOLVED");
      } else {
         sb.append(this._referencedType.getRawClass().getName());
      }

      return sb.toString();
   }

   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (o == null) {
         return false;
      } else {
         return o.getClass() == this.getClass() ? false : false;
      }
   }
}
