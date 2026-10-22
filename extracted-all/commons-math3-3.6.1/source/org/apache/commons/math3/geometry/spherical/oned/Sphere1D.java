package org.apache.commons.math3.geometry.spherical.oned;

import java.io.Serializable;
import org.apache.commons.math3.exception.MathUnsupportedOperationException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.geometry.Space;

public class Sphere1D implements Serializable, Space {
   private static final long serialVersionUID = 20131218L;

   private Sphere1D() {
   }

   public static Sphere1D getInstance() {
      return Sphere1D.LazyHolder.INSTANCE;
   }

   @Override
   public int getDimension() {
      return 1;
   }

   @Override
   public Space getSubSpace() throws Sphere1D.NoSubSpaceException {
      throw new Sphere1D.NoSubSpaceException();
   }

   private Object readResolve() {
      return Sphere1D.LazyHolder.INSTANCE;
   }

   private static class LazyHolder {
      private static final Sphere1D INSTANCE = new Sphere1D();
   }

   public static class NoSubSpaceException extends MathUnsupportedOperationException {
      private static final long serialVersionUID = 20140225L;

      public NoSubSpaceException() {
         super(LocalizedFormats.NOT_SUPPORTED_IN_DIMENSION_N, 1);
      }
   }
}
