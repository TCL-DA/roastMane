package org.apache.commons.math3.analysis.integration.gauss;

import java.util.Map;
import java.util.TreeMap;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.Pair;

public abstract class BaseRuleFactory<T extends Number> {
   private final Map<Integer, Pair<T[], T[]>> pointsAndWeights = new TreeMap<>();
   private final Map<Integer, Pair<double[], double[]>> pointsAndWeightsDouble = new TreeMap<>();

   public Pair<double[], double[]> getRule(int numberOfPoints) throws NotStrictlyPositiveException, DimensionMismatchException {
      if (numberOfPoints <= 0) {
         throw new NotStrictlyPositiveException(LocalizedFormats.NUMBER_OF_POINTS, numberOfPoints);
      } else {
         Pair<double[], double[]> cached = this.pointsAndWeightsDouble.get(numberOfPoints);
         if (cached == null) {
            Pair<T[], T[]> rule = this.getRuleInternal(numberOfPoints);
            cached = convertToDouble(rule);
            this.pointsAndWeightsDouble.put(numberOfPoints, cached);
         }

         return new Pair<>((double[])cached.getFirst().clone(), (double[])cached.getSecond().clone());
      }
   }

   protected synchronized Pair<T[], T[]> getRuleInternal(int numberOfPoints) throws DimensionMismatchException {
      Pair<T[], T[]> rule = this.pointsAndWeights.get(numberOfPoints);
      if (rule == null) {
         this.addRule(this.computeRule(numberOfPoints));
         return this.getRuleInternal(numberOfPoints);
      } else {
         return rule;
      }
   }

   protected void addRule(Pair<T[], T[]> rule) throws DimensionMismatchException {
      if (((Number[])rule.getFirst()).length != ((Number[])rule.getSecond()).length) {
         throw new DimensionMismatchException(((Number[])rule.getFirst()).length, ((Number[])rule.getSecond()).length);
      } else {
         this.pointsAndWeights.put(((Number[])rule.getFirst()).length, rule);
      }
   }

   protected abstract Pair<T[], T[]> computeRule(int var1) throws DimensionMismatchException;

   private static <T extends Number> Pair<double[], double[]> convertToDouble(Pair<T[], T[]> rule) {
      T[] pT = (T[])rule.getFirst();
      T[] wT = (T[])rule.getSecond();
      int len = pT.length;
      double[] pD = new double[len];
      double[] wD = new double[len];

      for (int i = 0; i < len; i++) {
         pD[i] = pT[i].doubleValue();
         wD[i] = wT[i].doubleValue();
      }

      return new Pair<>(pD, wD);
   }
}
