package org.apache.commons.math3.util;

import org.apache.commons.math3.Field;
import org.apache.commons.math3.FieldElement;

public class Decimal64Field implements Field<Decimal64> {
   private static final Decimal64Field INSTANCE = new Decimal64Field();

   private Decimal64Field() {
   }

   public static final Decimal64Field getInstance() {
      return INSTANCE;
   }

   public Decimal64 getZero() {
      return Decimal64.ZERO;
   }

   public Decimal64 getOne() {
      return Decimal64.ONE;
   }

   @Override
   public Class<? extends FieldElement<Decimal64>> getRuntimeClass() {
      return Decimal64.class;
   }
}
