package org.glassfish.hk2.utilities.reflection.internal;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.glassfish.hk2.utilities.reflection.MethodWrapper;
import org.glassfish.hk2.utilities.reflection.Pretty;
import org.glassfish.hk2.utilities.reflection.ReflectionHelper;

public class MethodWrapperImpl implements MethodWrapper {
   private final Method method;
   private final int hashCode;

   public MethodWrapperImpl(Method method) {
      if (method == null) {
         throw new IllegalArgumentException();
      } else {
         this.method = method;
         int hashCode = 0;
         hashCode ^= method.getName().hashCode();
         hashCode ^= method.getReturnType().hashCode();
         int modifiers = method.getModifiers();
         if (Modifier.isPrivate(modifiers)) {
            hashCode ^= method.getDeclaringClass().hashCode();
         } else if (this.isPackagePrivate(modifiers)) {
            hashCode ^= method.getDeclaringClass().getPackage().hashCode();
         }

         for (Class<?> param : method.getParameterTypes()) {
            hashCode ^= param.hashCode();
         }

         this.hashCode = hashCode;
      }
   }

   private boolean isPackagePrivate(int modifiers) {
      return !Modifier.isPublic(modifiers) && !Modifier.isProtected(modifiers) && !Modifier.isPrivate(modifiers);
   }

   @Override
   public Method getMethod() {
      return this.method;
   }

   @Override
   public int hashCode() {
      return this.hashCode;
   }

   @Override
   public boolean equals(Object o) {
      if (o == null) {
         return false;
      } else if (!(o instanceof MethodWrapperImpl)) {
         return false;
      } else {
         MethodWrapperImpl other = (MethodWrapperImpl)o;
         if (!this.method.getName().equals(other.method.getName())) {
            return false;
         } else if (!this.method.getReturnType().equals(other.method.getReturnType())) {
            return false;
         } else {
            Class<?>[] myParams = this.method.getParameterTypes();
            Class<?>[] otherParams = other.method.getParameterTypes();
            if (myParams.length != otherParams.length) {
               return false;
            } else if (!ReflectionHelper.isPrivate(this.method) && !ReflectionHelper.isPrivate(other.method)) {
               for (int lcv = 0; lcv < myParams.length; lcv++) {
                  if (!myParams[lcv].equals(otherParams[lcv])) {
                     return false;
                  }
               }

               return true;
            } else {
               return false;
            }
         }
      }
   }

   @Override
   public String toString() {
      return "MethodWrapperImpl(" + Pretty.method(this.method) + "," + System.identityHashCode(this) + ")";
   }
}
