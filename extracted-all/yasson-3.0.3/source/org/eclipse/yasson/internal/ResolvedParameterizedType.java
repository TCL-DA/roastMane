package org.eclipse.yasson.internal;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Objects;

class ResolvedParameterizedType implements ParameterizedType {
   private final ParameterizedType original;
   private final Type[] resolvedTypeArgs;

   ResolvedParameterizedType(ParameterizedType original, Type[] resolvedTypeArgs) {
      this.original = original;
      this.resolvedTypeArgs = resolvedTypeArgs;
   }

   @Override
   public Type[] getActualTypeArguments() {
      return this.resolvedTypeArgs;
   }

   @Override
   public Type getRawType() {
      return this.original.getRawType();
   }

   @Override
   public Type getOwnerType() {
      return this.original.getOwnerType();
   }

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append(this.original.toString());
      if (this.resolvedTypeArgs != null && this.resolvedTypeArgs.length > 0) {
         sb.append(" resolved arguments: [");

         for (Type typeArg : this.resolvedTypeArgs) {
            sb.append(typeArg);
         }

         sb.append("]");
      }

      return sb.toString();
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && o instanceof ParameterizedType) {
         ParameterizedType that = (ParameterizedType)o;
         return this.getRawType().equals(that.getRawType())
            && Objects.equals(this.getOwnerType(), that.getOwnerType())
            && Arrays.equals((Object[])this.resolvedTypeArgs, (Object[])that.getActualTypeArguments());
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Arrays.hashCode((Object[])this.resolvedTypeArgs)
         ^ (this.getOwnerType() == null ? 0 : this.getOwnerType().hashCode())
         ^ (this.getRawType() == null ? 0 : this.getRawType().hashCode());
   }
}
