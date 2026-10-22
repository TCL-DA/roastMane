package impl.org.controlsfx.tools.rectangle.change;

import impl.org.controlsfx.tools.rectangle.Edge2D;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;

public class ToEastChangeStrategy extends AbstractFixedEdgeChangeStrategy {
   private final Edge2D westernEdge;

   public ToEastChangeStrategy(Rectangle2D original, boolean ratioFixed, double ratio, Rectangle2D bounds) {
      super(ratioFixed, ratio, bounds);
      Point2D edgeCenterPoint = new Point2D(original.getMinX(), (original.getMinY() + original.getMaxY()) / 2.0);
      this.westernEdge = new Edge2D(edgeCenterPoint, Orientation.VERTICAL, original.getMaxY() - original.getMinY());
   }

   public ToEastChangeStrategy(Rectangle2D original, boolean ratioFixed, double ratio, double maxX, double maxY) {
      this(original, ratioFixed, ratio, new Rectangle2D(0.0, 0.0, maxX, maxY));
   }

   @Override
   protected Edge2D getFixedEdge() {
      return this.westernEdge;
   }
}
