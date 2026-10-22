package org.apache.commons.math3.linear;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.util.LocalizedFormats;

public class NonSquareMatrixException extends DimensionMismatchException {
   private static final long serialVersionUID = -660069396594485772L;

   public NonSquareMatrixException(int wrong, int expected) {
      super(LocalizedFormats.NON_SQUARE_MATRIX, wrong, expected);
   }
}
