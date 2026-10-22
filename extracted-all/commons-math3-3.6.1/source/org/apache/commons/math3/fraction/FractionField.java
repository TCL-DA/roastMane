package org.apache.commons.math3.fraction;

import java.io.Serializable;
import org.apache.commons.math3.Field;
import org.apache.commons.math3.FieldElement;

public class FractionField implements Field<Fraction>, Serializable {
   private static final long serialVersionUID = -1257768487499119313L;

   private FractionField() {
   }

   public static FractionField getInstance() {
      return FractionField.LazyHolder.INSTANCE;
   }

   public Fraction getOne() {
      return Fraction.ONE;
   }

   public Fraction getZero() {
      return Fraction.ZERO;
   }

   @Override
   public Class<? extends FieldElement<Fraction>> getRuntimeClass() {
      return Fraction.class;
   }

   private Object readResolve() {
      return FractionField.LazyHolder.INSTANCE;
   }

   private static class LazyHolder {
      private static final FractionField INSTANCE = new FractionField();
   }
}
