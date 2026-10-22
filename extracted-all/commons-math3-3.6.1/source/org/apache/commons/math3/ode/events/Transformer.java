package org.apache.commons.math3.ode.events;

import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.Precision;

enum Transformer {
   UNINITIALIZED {
      @Override
      protected double transformed(double g) {
         return 0.0;
      }
   },
   PLUS {
      @Override
      protected double transformed(double g) {
         return g;
      }
   },
   MINUS {
      @Override
      protected double transformed(double g) {
         return -g;
      }
   },
   MIN {
      @Override
      protected double transformed(double g) {
         return FastMath.min(-Precision.SAFE_MIN, FastMath.min(-g, g));
      }
   },
   MAX {
      @Override
      protected double transformed(double g) {
         return FastMath.max(Precision.SAFE_MIN, FastMath.max(-g, g));
      }
   };

   private Transformer() {
   }

   protected abstract double transformed(double var1);
}
