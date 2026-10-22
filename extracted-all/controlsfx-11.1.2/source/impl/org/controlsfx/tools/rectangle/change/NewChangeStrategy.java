package impl.org.controlsfx.tools.rectangle.change;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;

public class NewChangeStrategy extends AbstractFixedPointChangeStrategy {
   private Point2D startingPoint;

   public NewChangeStrategy(boolean ratioFixed, double ratio, Rectangle2D bounds) {
      super(ratioFixed, ratio, bounds);
   }

   @Override
   protected void beforeBeginHook(Point2D point) {
      this.startingPoint = point;
   }

   @Override
   protected Point2D getFixedCorner() {
      return this.startingPoint;
   }
}
