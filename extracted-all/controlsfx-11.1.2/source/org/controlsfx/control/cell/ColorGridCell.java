package org.controlsfx.control.cell;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.controlsfx.control.GridCell;

public class ColorGridCell extends GridCell<Color> {
   private Rectangle colorRect;
   private static final boolean debug = false;

   public ColorGridCell() {
      this.getStyleClass().add("color-grid-cell");
      this.colorRect = new Rectangle();
      this.colorRect.setStroke(Color.BLACK);
      this.colorRect.heightProperty().bind(this.heightProperty());
      this.colorRect.widthProperty().bind(this.widthProperty());
      this.setGraphic(this.colorRect);
   }

   protected void updateItem(Color item, boolean empty) {
      super.updateItem(item, empty);
      if (empty) {
         this.setGraphic(null);
      } else {
         this.colorRect.setFill(item);
         this.setGraphic(this.colorRect);
      }
   }
}
