package com.cropster.roastingintelligence.model.livegoals.checkers;

import com.cropster.roastingintelligence.model.livegoals.CheckResult;
import org.cropster.roastingintelligence.model.Goal;
import org.cropster.roastingintelligence.model.Measure;

public abstract class CompletedOnPassedOrFailedChecker extends SimpleChecker {
   private final boolean continuous;

   protected CompletedOnPassedOrFailedChecker(String name) {
      this(name, false);
   }

   protected CompletedOnPassedOrFailedChecker(String name, boolean continuous) {
      super(name);
      this.continuous = continuous;
   }

   @Override
   public CheckResult evaluate(Goal goal, Measure actualValue, Measure referenceValue) {
      CheckResult evaluation = super.evaluate(goal, actualValue, referenceValue);
      if (this.continuous) {
         return evaluation;
      } else {
         return !evaluation.isPassed() && !evaluation.isFailed() ? evaluation : CheckResult.completed(evaluation);
      }
   }
}
