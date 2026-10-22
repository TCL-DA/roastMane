package org.apache.commons.math3.complex;

import java.io.Serializable;
import org.apache.commons.math3.Field;
import org.apache.commons.math3.FieldElement;

public class ComplexField implements Field<Complex>, Serializable {
   private static final long serialVersionUID = -6130362688700788798L;

   private ComplexField() {
   }

   public static ComplexField getInstance() {
      return ComplexField.LazyHolder.INSTANCE;
   }

   public Complex getOne() {
      return Complex.ONE;
   }

   public Complex getZero() {
      return Complex.ZERO;
   }

   @Override
   public Class<? extends FieldElement<Complex>> getRuntimeClass() {
      return Complex.class;
   }

   private Object readResolve() {
      return ComplexField.LazyHolder.INSTANCE;
   }

   private static class LazyHolder {
      private static final ComplexField INSTANCE = new ComplexField();
   }
}
