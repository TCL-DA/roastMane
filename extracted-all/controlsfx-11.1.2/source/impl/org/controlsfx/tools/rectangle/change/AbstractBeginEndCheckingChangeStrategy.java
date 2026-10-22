package impl.org.controlsfx.tools.rectangle.change;

import java.util.Objects;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;

abstract class AbstractBeginEndCheckingChangeStrategy implements Rectangle2DChangeStrategy {
   private boolean beforeBegin = true;

   protected AbstractBeginEndCheckingChangeStrategy() {
   }

   @Override
   public final Rectangle2D beginChange(Point2D point) {
      Objects.requireNonNull(point, "The specified point must not be null.");
      if (!this.beforeBegin) {
         throw new IllegalStateException("The change already began, so 'beginChange' must not be called again before 'endChange' was called.");
      } else {
         this.beforeBegin = false;
         this.beforeBeginHook(point);
         return this.doBegin(point);
      }
   }

   @Override
   public final Rectangle2D continueChange(Point2D point) {
      Objects.requireNonNull(point, "The specified point must not be null.");
      if (this.beforeBegin) {
         throw new IllegalStateException("The change did not begin. Call 'beginChange' before 'continueChange'.");
      } else {
         return this.doContinue(point);
      }
   }

   @Override
   public final Rectangle2D endChange(Point2D point) {
      Objects.requireNonNull(point, "The specified point must not be null.");
      if (this.beforeBegin) {
         throw new IllegalStateException("The change did not begin. Call 'beginChange' before 'endChange'.");
      } else {
         Rectangle2D finalRectangle = this.doEnd(point);
         this.afterEndHook(point);
         this.beforeBegin = true;
         return finalRectangle;
      }
   }

   protected void beforeBeginHook(Point2D point) {
   }

   protected abstract Rectangle2D doBegin(Point2D var1);

   protected abstract Rectangle2D doContinue(Point2D var1);

   protected abstract Rectangle2D doEnd(Point2D var1);

   protected void afterEndHook(Point2D point) {
   }
}
