package org.controlsfx.control.cell;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.controlsfx.control.GridCell;

public class ImageGridCell extends GridCell<Image> {
   private final ImageView imageView;
   private final boolean preserveImageProperties;

   public ImageGridCell() {
      this(true);
   }

   public ImageGridCell(boolean preserveImageProperties) {
      this.getStyleClass().add("image-grid-cell");
      this.preserveImageProperties = preserveImageProperties;
      this.imageView = new ImageView();
      this.imageView.fitHeightProperty().bind(this.heightProperty());
      this.imageView.fitWidthProperty().bind(this.widthProperty());
   }

   protected void updateItem(Image item, boolean empty) {
      super.updateItem(item, empty);
      if (empty) {
         this.setGraphic(null);
      } else {
         if (this.preserveImageProperties) {
            this.imageView.setPreserveRatio(item.isPreserveRatio());
            this.imageView.setSmooth(item.isSmooth());
         }

         this.imageView.setImage(item);
         this.setGraphic(this.imageView);
      }
   }
}
