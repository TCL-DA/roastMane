package impl.org.controlsfx.tools.rectangle.change;

import impl.org.controlsfx.tools.rectangle.Rectangles2D;
import java.util.Objects;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;

abstract class AbstractFixedPointChangeStrategy extends AbstractRatioRespectingChangeStrategy {
   private final Rectangle2D bounds;
   private Point2D fixedCorner;

   protected AbstractFixedPointChangeStrategy(boolean ratioFixed, double ratio, Rectangle2D bounds) {
      super(ratioFixed, ratio);
      Objects.requireNonNull(bounds, "The argument 'bounds' must not be null.");
      this.bounds = bounds;
   }

   protected abstract Point2D getFixedCorner();

   private final Rectangle2D createFromCorners(Point2D point) {
      Point2D pointInBounds = Rectangles2D.inRectangle(this.bounds, point);
      return this.isRatioFixed()
         ? Rectangles2D.forDiagonalCornersAndRatio(this.fixedCorner, pointInBounds, this.getRatio())
         : Rectangles2D.forDiagonalCorners(this.fixedCorner, pointInBounds);
   }

   @Override
   protected final Rectangle2D doBegin(Point2D point) {
      boolean startPointNotInBounds = !this.bounds.contains(point);
      if (startPointNotInBounds) {
         throw new IllegalArgumentException("The change's start point (" + point + ") must lie within the bounds (" + this.bounds + ").");
      } else {
         this.fixedCorner = this.getFixedCorner();
         return this.createFromCorners(point);
      }
   }

   @Override
   protected Rectangle2D doContinue(Point2D point) {
      return this.createFromCorners(point);
   }

   @Override
   protected final Rectangle2D doEnd(Point2D point) {
      Rectangle2D newRectangle = this.createFromCorners(point);
      this.fixedCorner = null;
      return newRectangle;
   }
}
