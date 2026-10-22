package org.apache.commons.math3.stat.descriptive;

import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.util.MathUtils;

public class SynchronizedDescriptiveStatistics extends DescriptiveStatistics {
   private static final long serialVersionUID = 1L;

   public SynchronizedDescriptiveStatistics() {
      this(-1);
   }

   public SynchronizedDescriptiveStatistics(int window) throws MathIllegalArgumentException {
      super(window);
   }

   public SynchronizedDescriptiveStatistics(SynchronizedDescriptiveStatistics original) throws NullArgumentException {
      copy(original, this);
   }

   @Override
   public synchronized void addValue(double v) {
      super.addValue(v);
   }

   @Override
   public synchronized double apply(UnivariateStatistic stat) {
      return super.apply(stat);
   }

   @Override
   public synchronized void clear() {
      super.clear();
   }

   @Override
   public synchronized double getElement(int index) {
      return super.getElement(index);
   }

   @Override
   public synchronized long getN() {
      return super.getN();
   }

   @Override
   public synchronized double getStandardDeviation() {
      return super.getStandardDeviation();
   }

   @Override
   public synchronized double getQuadraticMean() {
      return super.getQuadraticMean();
   }

   @Override
   public synchronized double[] getValues() {
      return super.getValues();
   }

   @Override
   public synchronized int getWindowSize() {
      return super.getWindowSize();
   }

   @Override
   public synchronized void setWindowSize(int windowSize) throws MathIllegalArgumentException {
      super.setWindowSize(windowSize);
   }

   @Override
   public synchronized String toString() {
      return super.toString();
   }

   public synchronized SynchronizedDescriptiveStatistics copy() {
      SynchronizedDescriptiveStatistics result = new SynchronizedDescriptiveStatistics();
      copy(this, result);
      return result;
   }

   public static void copy(SynchronizedDescriptiveStatistics source, SynchronizedDescriptiveStatistics dest) throws NullArgumentException {
      MathUtils.checkNotNull(source);
      MathUtils.checkNotNull(dest);
      synchronized (source) {
         synchronized (dest) {
            DescriptiveStatistics.copy(source, dest);
         }
      }
   }
}
