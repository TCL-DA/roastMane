package org.apache.commons.math3.util;

import java.io.Serializable;
import org.apache.commons.math3.Field;
import org.apache.commons.math3.FieldElement;

public class BigRealField implements Field<BigReal>, Serializable {
   private static final long serialVersionUID = 4756431066541037559L;

   private BigRealField() {
   }

   public static BigRealField getInstance() {
      return BigRealField.LazyHolder.INSTANCE;
   }

   public BigReal getOne() {
      return BigReal.ONE;
   }

   public BigReal getZero() {
      return BigReal.ZERO;
   }

   @Override
   public Class<? extends FieldElement<BigReal>> getRuntimeClass() {
      return BigReal.class;
   }

   private Object readResolve() {
      return BigRealField.LazyHolder.INSTANCE;
   }

   private static class LazyHolder {
      private static final BigRealField INSTANCE = new BigRealField();
   }
}
