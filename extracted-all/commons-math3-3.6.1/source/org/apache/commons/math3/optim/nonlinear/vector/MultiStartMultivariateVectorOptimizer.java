package org.apache.commons.math3.optim.nonlinear.vector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.optim.BaseMultiStartMultivariateOptimizer;
import org.apache.commons.math3.optim.PointVectorValuePair;
import org.apache.commons.math3.random.RandomVectorGenerator;

@Deprecated
public class MultiStartMultivariateVectorOptimizer extends BaseMultiStartMultivariateOptimizer<PointVectorValuePair> {
   private final MultivariateVectorOptimizer optimizer;
   private final List<PointVectorValuePair> optima = new ArrayList<>();

   public MultiStartMultivariateVectorOptimizer(MultivariateVectorOptimizer optimizer, int starts, RandomVectorGenerator generator) throws NullArgumentException, NotStrictlyPositiveException {
      super(optimizer, starts, generator);
      this.optimizer = optimizer;
   }

   public PointVectorValuePair[] getOptima() {
      Collections.sort(this.optima, this.getPairComparator());
      return this.optima.toArray(new PointVectorValuePair[0]);
   }

   protected void store(PointVectorValuePair optimum) {
      this.optima.add(optimum);
   }

   @Override
   protected void clear() {
      this.optima.clear();
   }

   private Comparator<PointVectorValuePair> getPairComparator() {
      return new Comparator<PointVectorValuePair>() {
         private final RealVector target = new ArrayRealVector(MultiStartMultivariateVectorOptimizer.this.optimizer.getTarget(), false);
         private final RealMatrix weight = MultiStartMultivariateVectorOptimizer.this.optimizer.getWeight();

         public int compare(PointVectorValuePair o1, PointVectorValuePair o2) {
            if (o1 == null) {
               return o2 == null ? 0 : 1;
            } else {
               return o2 == null ? -1 : Double.compare(this.weightedResidual(o1), this.weightedResidual(o2));
            }
         }

         private double weightedResidual(PointVectorValuePair pv) {
            RealVector v = new ArrayRealVector(pv.getValueRef(), false);
            RealVector r = this.target.subtract(v);
            return r.dotProduct(this.weight.operate(r));
         }
      };
   }
}
