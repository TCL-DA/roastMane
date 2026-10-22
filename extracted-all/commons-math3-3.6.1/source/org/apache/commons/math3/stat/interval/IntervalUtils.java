package org.apache.commons.math3.stat.interval;

import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.util.LocalizedFormats;

public final class IntervalUtils {
   private static final BinomialConfidenceInterval AGRESTI_COULL = new AgrestiCoullInterval();
   private static final BinomialConfidenceInterval CLOPPER_PEARSON = new ClopperPearsonInterval();
   private static final BinomialConfidenceInterval NORMAL_APPROXIMATION = new NormalApproximationInterval();
   private static final BinomialConfidenceInterval WILSON_SCORE = new WilsonScoreInterval();

   private IntervalUtils() {
   }

   public static ConfidenceInterval getAgrestiCoullInterval(int numberOfTrials, int numberOfSuccesses, double confidenceLevel) {
      return AGRESTI_COULL.createInterval(numberOfTrials, numberOfSuccesses, confidenceLevel);
   }

   public static ConfidenceInterval getClopperPearsonInterval(int numberOfTrials, int numberOfSuccesses, double confidenceLevel) {
      return CLOPPER_PEARSON.createInterval(numberOfTrials, numberOfSuccesses, confidenceLevel);
   }

   public static ConfidenceInterval getNormalApproximationInterval(int numberOfTrials, int numberOfSuccesses, double confidenceLevel) {
      return NORMAL_APPROXIMATION.createInterval(numberOfTrials, numberOfSuccesses, confidenceLevel);
   }

   public static ConfidenceInterval getWilsonScoreInterval(int numberOfTrials, int numberOfSuccesses, double confidenceLevel) {
      return WILSON_SCORE.createInterval(numberOfTrials, numberOfSuccesses, confidenceLevel);
   }

   static void checkParameters(int numberOfTrials, int numberOfSuccesses, double confidenceLevel) {
      if (numberOfTrials <= 0) {
         throw new NotStrictlyPositiveException(LocalizedFormats.NUMBER_OF_TRIALS, numberOfTrials);
      } else if (numberOfSuccesses < 0) {
         throw new NotPositiveException(LocalizedFormats.NEGATIVE_NUMBER_OF_SUCCESSES, numberOfSuccesses);
      } else if (numberOfSuccesses > numberOfTrials) {
         throw new NumberIsTooLargeException(LocalizedFormats.NUMBER_OF_SUCCESS_LARGER_THAN_POPULATION_SIZE, numberOfSuccesses, numberOfTrials, true);
      } else if (confidenceLevel <= 0.0 || confidenceLevel >= 1.0) {
         throw new OutOfRangeException(LocalizedFormats.OUT_OF_BOUNDS_CONFIDENCE_LEVEL, confidenceLevel, 0, 1);
      }
   }
}
