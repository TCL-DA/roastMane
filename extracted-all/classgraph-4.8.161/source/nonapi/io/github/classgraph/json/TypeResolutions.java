package nonapi.io.github.classgraph.json;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;

class TypeResolutions {
   private final TypeVariable<?>[] typeVariables;
   Type[] resolvedTypeArguments;

   TypeResolutions(ParameterizedType resolvedType) {
      this.typeVariables = ((Class)resolvedType.getRawType()).getTypeParameters();
      this.resolvedTypeArguments = resolvedType.getActualTypeArguments();
      if (this.resolvedTypeArguments.length != this.typeVariables.length) {
         throw new IllegalArgumentException("Type parameter count mismatch");
      }
   }

   Type resolveTypeVariables(Type type) {
      if (type instanceof Class) {
         return type;
      } else if (type instanceof ParameterizedType) {
         ParameterizedType parameterizedType = (ParameterizedType)type;
         Type[] typeArgs = parameterizedType.getActualTypeArguments();
         Type[] typeArgsResolved = null;

         for (int i = 0; i < typeArgs.length; i++) {
            Type typeArgResolved = this.resolveTypeVariables(typeArgs[i]);
            if (typeArgsResolved == null) {
               if (!typeArgResolved.equals(typeArgs[i])) {
                  typeArgsResolved = new Type[typeArgs.length];
                  System.arraycopy(typeArgs, 0, typeArgsResolved, 0, i);
                  typeArgsResolved[i] = typeArgResolved;
               }
            } else {
               typeArgsResolved[i] = typeArgResolved;
            }
         }

         return (Type)(typeArgsResolved == null
            ? type
            : new ParameterizedTypeImpl((Class<?>)parameterizedType.getRawType(), typeArgsResolved, parameterizedType.getOwnerType()));
      } else if (type instanceof TypeVariable) {
         TypeVariable<?> typeVariable = (TypeVariable<?>)type;

         for (int ix = 0; ix < this.typeVariables.length; ix++) {
            if (this.typeVariables[ix].getName().equals(typeVariable.getName())) {
               return this.resolvedTypeArguments[ix];
            }
         }

         return type;
      } else if (!(type instanceof GenericArrayType)) {
         if (type instanceof WildcardType) {
            throw new RuntimeException("WildcardType not yet supported: " + type);
         } else {
            throw new RuntimeException("Got unexpected type: " + type);
         }
      } else {
         int numArrayDims = 0;

         Type t;
         for (t = type; t instanceof GenericArrayType; t = ((GenericArrayType)t).getGenericComponentType()) {
            numArrayDims++;
         }

         Type innermostTypeResolved = this.resolveTypeVariables(t);
         if (!(innermostTypeResolved instanceof Class)) {
            throw new IllegalArgumentException("Could not resolve generic array type " + type);
         } else {
            Class<?> innermostTypeResolvedClass = (Class<?>)innermostTypeResolved;
            int[] dims = (int[])Array.newInstance(int.class, numArrayDims);
            Object arrayInstance = Array.newInstance(innermostTypeResolvedClass, dims);
            return arrayInstance.getClass();
         }
      }
   }

   @Override
   public String toString() {
      if (this.typeVariables.length == 0) {
         return "{ }";
      } else {
         StringBuilder buf = new StringBuilder();
         buf.append("{ ");

         for (int i = 0; i < this.typeVariables.length; i++) {
            if (i > 0) {
               buf.append(", ");
            }

            buf.append(this.typeVariables[i]).append(" => ").append(this.resolvedTypeArguments[i]);
         }

         buf.append(" }");
         return buf.toString();
      }
   }
}
