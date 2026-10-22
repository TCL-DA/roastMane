package com.cropster.roastingintelligence.model.livegoals.checkers;

import com.cropster.util.model.Unit;
import java.util.Objects;
import org.cropster.roastingintelligence.model.Goal;
import org.cropster.roastingintelligence.model.Measure;
import org.cropster.roastingintelligence.model.Roast;
import org.cropster.roastingintelligence.model.RoastingComment;

public class FirstCrackChecker extends AbstractChecker {
   public static Measure getFirstCrackMeasure(Roast roast) throws NullPointerException {
      Objects.requireNonNull(roast, "Null 'roast' parameter");
      RoastingComment eventComment = roast.getComments().stream().filter(c -> "firstCrack".equals(c.getEvent())).findFirst().orElse(null);
      return eventComment == null ? null : new Measure((double)eventComment.getTime(), Unit.MILLISECOND).toUnit(Unit.DURATION);
   }

   public FirstCrackChecker() {
      super("Checker.firstCrack");
   }

   @Override
   public Measure getReferenceValue(Roast referenceRoast) throws NullPointerException {
      return getFirstCrackMeasure(referenceRoast);
   }

   @Override
   public boolean isGoalValid(Goal goal) {
      return super.isGoalValid(goal) && "firstCrack".equals(goal.getName());
   }
}
