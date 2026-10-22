package com.cropster.roastingintelligence.model.livegoals;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.cropster.roastingintelligence.model.Goal;
import org.cropster.roastingintelligence.model.Measure;

public final class CheckResult {
   private final CheckResult.CheckStatus checkStatus;
   private final Measure checkedValue;
   private final Measure error;
   private final CheckResult.EvaluationStatus evaluationStatus;
   private final CheckResult.GoalWrapper goalWrapper;
   private final Measure referenceValue;

   public static CheckResult completed(CheckResult result) {
      return new CheckResult(
         result.getGoal(),
         result.getCheckedValue(),
         result.getReferenceValue(),
         result.getCheckStatus(),
         CheckResult.EvaluationStatus.COMPLETED,
         result.getError()
      );
   }

   public static CheckResult failed(Goal goal) {
      return new CheckResult(goal, null, null, CheckResult.CheckStatus.FAILED, CheckResult.EvaluationStatus.ONGOING, null);
   }

   public static CheckResult failed(Goal goal, Measure checkValue) {
      return new CheckResult(goal, checkValue, null, CheckResult.CheckStatus.FAILED, CheckResult.EvaluationStatus.ONGOING, null);
   }

   public static CheckResult failed(Goal goal, Measure checkValue, Measure referenceValue, Measure error) {
      return new CheckResult(goal, checkValue, referenceValue, CheckResult.CheckStatus.FAILED, CheckResult.EvaluationStatus.ONGOING, error);
   }

   public static CheckResult invalid(Goal goal) {
      return new CheckResult(goal, null, null, CheckResult.CheckStatus.INVALID, CheckResult.EvaluationStatus.ONGOING, null);
   }

   public static CheckResult initialized(Goal goal) {
      return new CheckResult(goal, null, null, CheckResult.CheckStatus.INITIALIZED, CheckResult.EvaluationStatus.ONGOING, null);
   }

   public static CheckResult missing(Goal goal) {
      return new CheckResult(goal, null, null, CheckResult.CheckStatus.MISSING, CheckResult.EvaluationStatus.ONGOING, null);
   }

   public static CheckResult passed(CheckResult result) {
      return new CheckResult(
         result.getGoal(),
         result.getCheckedValue(),
         result.getReferenceValue(),
         result.getCheckStatus(),
         CheckResult.EvaluationStatus.ONGOING,
         result.getError()
      );
   }

   public static CheckResult passed(Goal goal) {
      return new CheckResult(goal, null, null, CheckResult.CheckStatus.PASSED, CheckResult.EvaluationStatus.ONGOING, null);
   }

   public static CheckResult passed(Goal goal, Measure checkValue) {
      return new CheckResult(goal, checkValue, null, CheckResult.CheckStatus.PASSED, CheckResult.EvaluationStatus.ONGOING, null);
   }

   public static CheckResult passed(Goal goal, Measure checkValue, Measure referenceValue, Measure error) {
      return new CheckResult(goal, checkValue, referenceValue, CheckResult.CheckStatus.PASSED, CheckResult.EvaluationStatus.ONGOING, error);
   }

   public static CheckResult waiting(Goal goal) {
      return new CheckResult(goal, null, null, CheckResult.CheckStatus.WAITING, CheckResult.EvaluationStatus.ONGOING, null);
   }

   private CheckResult(
      Goal goal,
      Measure checkedValue,
      Measure referenceValue,
      CheckResult.CheckStatus checkStatus,
      CheckResult.EvaluationStatus evaluationStatus,
      Measure error
   ) {
      this.goalWrapper = new CheckResult.GoalWrapper(goal);
      this.checkedValue = checkedValue;
      this.referenceValue = referenceValue;
      this.checkStatus = checkStatus;
      this.evaluationStatus = evaluationStatus;
      this.error = error;
   }

   public CheckResult.CheckStatus getCheckStatus() {
      return this.checkStatus;
   }

   public Measure getCheckedValue() {
      return this.checkedValue;
   }

   public Measure getError() {
      return this.error;
   }

   public CheckResult.EvaluationStatus getEvaluationStatus() {
      return this.evaluationStatus;
   }

   public Goal getGoal() {
      return this.goalWrapper.getGoal();
   }

   public Measure getReferenceValue() {
      return this.referenceValue;
   }

   public boolean isFailed() {
      return this.checkStatus == CheckResult.CheckStatus.FAILED;
   }

   public boolean isInitialized() {
      return this.checkStatus == CheckResult.CheckStatus.INITIALIZED;
   }

   public boolean isInvalid() {
      return this.checkStatus == CheckResult.CheckStatus.INVALID;
   }

   public boolean isMissing() {
      return this.checkStatus == CheckResult.CheckStatus.MISSING;
   }

   public boolean isPassed() {
      return this.checkStatus == CheckResult.CheckStatus.PASSED;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == null) {
         return false;
      } else if (obj == this) {
         return true;
      } else if (obj.getClass() != this.getClass()) {
         return false;
      } else {
         CheckResult rhs = (CheckResult)obj;
         return new EqualsBuilder()
            .appendSuper(super.equals(obj))
            .append(this.checkStatus, rhs.checkStatus)
            .append(this.checkedValue, this.checkedValue)
            .append(this.evaluationStatus, this.evaluationStatus)
            .append(this.goalWrapper, this.goalWrapper)
            .append(this.referenceValue, this.referenceValue)
            .isEquals();
      }
   }

   @Override
   public int hashCode() {
      return new HashCodeBuilder(19, 43)
         .append(this.checkStatus)
         .append(this.checkedValue)
         .append(this.evaluationStatus)
         .append(this.goalWrapper)
         .append(this.referenceValue)
         .toHashCode();
   }

   public static enum CheckStatus {
      INITIALIZED,
      WAITING,
      FAILED,
      MISSING,
      PASSED,
      INVALID;
   }

   public static enum EvaluationStatus {
      COMPLETED,
      ONGOING;
   }

   private static class GoalWrapper {
      private final Goal goal;

      GoalWrapper(Goal goal) {
         this.goal = goal;
      }

      public Goal getGoal() {
         return this.goal;
      }

      @Override
      public boolean equals(Object obj) {
         if (obj == null) {
            return false;
         } else if (obj == this) {
            return true;
         } else if (obj.getClass() != this.getClass()) {
            return false;
         } else {
            Goal rhs = ((CheckResult.GoalWrapper)obj).getGoal();
            return new EqualsBuilder()
               .appendSuper(super.equals(obj))
               .append(this.goal.getEndTime(), rhs.getEndTime())
               .append(this.goal.getLocalizedMessage(), rhs.getLocalizedMessage())
               .append(this.goal.getLowerBound(), rhs.getLowerBound())
               .append(this.goal.getName(), rhs.getName())
               .append(this.goal.getProfiles(), rhs.getProfiles())
               .append(this.goal.getStartTime(), rhs.getStartTime())
               .append(this.goal.getUpperBound(), rhs.getUpperBound())
               .append(this.goal.isAbsolute(), rhs.isAbsolute())
               .append(this.goal.isGlobal(), rhs.isGlobal())
               .isEquals();
         }
      }

      @Override
      public int hashCode() {
         return new HashCodeBuilder(19, 43)
            .append(this.goal.getEndTime())
            .append(this.goal.getLocalizedMessage())
            .append(this.goal.getLowerBound())
            .append(this.goal.getName())
            .append(this.goal.getProfiles())
            .append(this.goal.getStartTime())
            .append(this.goal.getUpperBound())
            .append(this.goal.isAbsolute())
            .append(this.goal.isGlobal())
            .toHashCode();
      }

      @Override
      public String toString() {
         return this.goal.toString();
      }
   }
}
