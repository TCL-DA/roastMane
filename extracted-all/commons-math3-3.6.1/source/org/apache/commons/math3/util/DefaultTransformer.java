package org.apache.commons.math3.util;

import java.io.Serializable;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;

public class DefaultTransformer implements NumberTransformer, Serializable {
   private static final long serialVersionUID = 4019938025047800455L;

   @Override
   public double transform(Object o) throws NullArgumentException, MathIllegalArgumentException {
      if (o == null) {
         throw new NullArgumentException(LocalizedFormats.OBJECT_TRANSFORMATION);
      } else if (o instanceof Number) {
         return ((Number)o).doubleValue();
      } else {
         try {
            return Double.parseDouble(o.toString());
         } catch (NumberFormatException var3) {
            throw new MathIllegalArgumentException(LocalizedFormats.CANNOT_TRANSFORM_TO_DOUBLE, o.toString());
         }
      }
   }

   @Override
   public boolean equals(Object other) {
      return this == other ? true : other instanceof DefaultTransformer;
   }

   @Override
   public int hashCode() {
      return 401993047;
   }
}
