package impl.org.controlsfx.tools.rectangle.change;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;

public interface Rectangle2DChangeStrategy {
   Rectangle2D beginChange(Point2D var1);

   Rectangle2D continueChange(Point2D var1);

   Rectangle2D endChange(Point2D var1);
}
