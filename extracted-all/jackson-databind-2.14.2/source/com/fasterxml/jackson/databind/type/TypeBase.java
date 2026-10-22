package com.fasterxml.jackson.databind.type;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import java.io.IOException;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class TypeBase extends JavaType implements JsonSerializable {
   private static final long serialVersionUID = 1L;
   private static final TypeBindings NO_BINDINGS = TypeBindings.emptyBindings();
   private static final JavaType[] NO_TYPES = new JavaType[0];
   protected final JavaType _superClass;
   protected final JavaType[] _superInterfaces;
   protected final TypeBindings _bindings;
   transient volatile String _canonicalName;

   protected TypeBase(
      Class<?> raw, TypeBindings bindings, JavaType superClass, JavaType[] superInts, int hash, Object valueHandler, Object typeHandler, boolean asStatic
   ) {
      super(raw, hash, valueHandler, typeHandler, asStatic);
      this._bindings = bindings == null ? NO_BINDINGS : bindings;
      this._superClass = superClass;
      this._superInterfaces = superInts;
   }

   protected TypeBase(TypeBase base) {
      super(base);
      this._superClass = base._superClass;
      this._superInterfaces = base._superInterfaces;
      this._bindings = base._bindings;
   }

   public String toCanonical() {
      String str = this._canonicalName;
      if (str == null) {
         str = this.buildCanonicalName();
      }

      return str;
   }

   protected String buildCanonicalName() {
      return this._class.getName();
   }

   @Override
   public abstract StringBuilder getGenericSignature(StringBuilder var1);

   @Override
   public abstract StringBuilder getErasedSignature(StringBuilder var1);

   @Override
   public TypeBindings getBindings() {
      return this._bindings;
   }

   @Override
   public int containedTypeCount() {
      return this._bindings.size();
   }

   @Override
   public JavaType containedType(int index) {
      return this._bindings.getBoundType(index);
   }

   @Deprecated
   @Override
   public String containedTypeName(int index) {
      return this._bindings.getBoundName(index);
   }

   @Override
   public JavaType getSuperClass() {
      return this._superClass;
   }

   @Override
   public List<JavaType> getInterfaces() {
      if (this._superInterfaces == null) {
         return Collections.emptyList();
      } else {
         switch (this._superInterfaces.length) {
            case 0:
               return Collections.emptyList();
            case 1:
               return Collections.singletonList(this._superInterfaces[0]);
            default:
               return Arrays.asList(this._superInterfaces);
         }
      }
   }

   @Override
   public final JavaType findSuperType(Class<?> rawTarget) {
      if (rawTarget == this._class) {
         return this;
      } else {
         if (rawTarget.isInterface() && this._superInterfaces != null) {
            int i = 0;

            for (int count = this._superInterfaces.length; i < count; i++) {
               JavaType type = this._superInterfaces[i].findSuperType(rawTarget);
               if (type != null) {
                  return type;
               }
            }
         }

         if (this._superClass != null) {
            JavaType type = this._superClass.findSuperType(rawTarget);
            if (type != null) {
               return type;
            }
         }

         return null;
      }
   }

   @Override
   public JavaType[] findTypeParameters(Class<?> expType) {
      JavaType match = this.findSuperType(expType);
      return match == null ? NO_TYPES : match.getBindings().typeParameterArray();
   }

   @Override
   public void serializeWithType(JsonGenerator g, SerializerProvider provider, TypeSerializer typeSer) throws IOException {
      WritableTypeId typeIdDef = new WritableTypeId(this, JsonToken.VALUE_STRING);
      typeSer.writeTypePrefix(g, typeIdDef);
      this.serialize(g, provider);
      typeSer.writeTypeSuffix(g, typeIdDef);
   }

   @Override
   public void serialize(JsonGenerator gen, SerializerProvider provider) throws IOException {
      gen.writeString(this.toCanonical());
   }

   protected static StringBuilder _classSignature(Class<?> cls, StringBuilder sb, boolean trailingSemicolon) {
      if (cls.isPrimitive()) {
         if (cls == boolean.class) {
            sb.append('Z');
         } else if (cls == byte.class) {
            sb.append('B');
         } else if (cls == short.class) {
            sb.append('S');
         } else if (cls == char.class) {
            sb.append('C');
         } else if (cls == int.class) {
            sb.append('I');
         } else if (cls == long.class) {
            sb.append('J');
         } else if (cls == float.class) {
            sb.append('F');
         } else if (cls == double.class) {
            sb.append('D');
         } else {
            if (cls != void.class) {
               throw new IllegalStateException("Unrecognized primitive type: " + cls.getName());
            }

            sb.append('V');
         }
      } else {
         sb.append('L');
         String name = cls.getName();
         int i = 0;

         for (int len = name.length(); i < len; i++) {
            char c = name.charAt(i);
            if (c == '.') {
               c = '/';
            }

            sb.append(c);
         }

         if (trailingSemicolon) {
            sb.append(';');
         }
      }

      return sb;
   }

   protected static JavaType _bogusSuperClass(Class<?> cls) {
      Class<?> parent = cls.getSuperclass();
      return parent == null ? null : TypeFactory.unknownType();
   }

   protected boolean _hasNTypeParameters(int count) {
      TypeVariable<?>[] params = this._class.getTypeParameters();
      return params.length == count;
   }
}
