package org.apache.commons.math3.geometry.partitioning;

import org.apache.commons.math3.geometry.Space;

public interface SubHyperplane<S extends Space> {
   SubHyperplane<S> copySelf();

   Hyperplane<S> getHyperplane();

   boolean isEmpty();

   double getSize();

   @Deprecated
   Side side(Hyperplane<S> var1);

   SubHyperplane.SplitSubHyperplane<S> split(Hyperplane<S> var1);

   SubHyperplane<S> reunite(SubHyperplane<S> var1);

   public static class SplitSubHyperplane<U extends Space> {
      private final SubHyperplane<U> plus;
      private final SubHyperplane<U> minus;

      public SplitSubHyperplane(SubHyperplane<U> plus, SubHyperplane<U> minus) {
         this.plus = plus;
         this.minus = minus;
      }

      public SubHyperplane<U> getPlus() {
         return this.plus;
      }

      public SubHyperplane<U> getMinus() {
         return this.minus;
      }

      public Side getSide() {
         if (this.plus != null && !this.plus.isEmpty()) {
            return this.minus != null && !this.minus.isEmpty() ? Side.BOTH : Side.PLUS;
         } else {
            return this.minus != null && !this.minus.isEmpty() ? Side.MINUS : Side.HYPER;
         }
      }
   }
}
