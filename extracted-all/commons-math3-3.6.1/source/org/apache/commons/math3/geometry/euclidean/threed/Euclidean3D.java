package org.apache.commons.math3.geometry.euclidean.threed;

import java.io.Serializable;
import org.apache.commons.math3.geometry.Space;
import org.apache.commons.math3.geometry.euclidean.twod.Euclidean2D;

public class Euclidean3D implements Serializable, Space {
   private static final long serialVersionUID = 6249091865814886817L;

   private Euclidean3D() {
   }

   public static Euclidean3D getInstance() {
      return Euclidean3D.LazyHolder.INSTANCE;
   }

   @Override
   public int getDimension() {
      return 3;
   }

   public Euclidean2D getSubSpace() {
      return Euclidean2D.getInstance();
   }

   private Object readResolve() {
      return Euclidean3D.LazyHolder.INSTANCE;
   }

   private static class LazyHolder {
      private static final Euclidean3D INSTANCE = new Euclidean3D();
   }
}
