package org.glassfish.hk2.utilities;

import org.glassfish.hk2.api.MethodParameter;

public class MethodParameterImpl implements MethodParameter {
   private final int index;
   private final Object value;

   public MethodParameterImpl(int index, Object value) {
      this.index = index;
      this.value = value;
   }

   @Override
   public int getParameterPosition() {
      return this.index;
   }

   @Override
   public Object getParameterValue() {
      return this.value;
   }

   @Override
   public String toString() {
      return "MethodParamterImpl(" + this.index + "," + this.value + "," + System.identityHashCode(this) + ")";
   }
}
