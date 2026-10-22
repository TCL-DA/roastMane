package com.cropster.roastingintelligence.ui.util;

import java.util.Map;
import java.util.ResourceBundle;
import javafx.scene.control.ListCell;

public class ResourceBundleListCell<T> extends ListCell<T> {
   private final ResourceBundle bundle;
   private Map<T, String> styles;

   public ResourceBundleListCell(ResourceBundle bundle) {
      this.bundle = bundle;
   }

   public ResourceBundleListCell<T> withStyles(Map<T, String> styles) {
      this.styles = styles;
      return this;
   }

   protected void updateItem(T item, boolean empty) {
      super.updateItem(item, empty);
      if (item == null) {
         this.setText(null);
      } else if (item instanceof Enum<?> e) {
         if (this.bundle.containsKey(e.name())) {
            this.setText(this.bundle.getString(e.name()));
         } else {
            this.setText(e.toString());
         }
      } else if (this.bundle.containsKey(item.toString())) {
         this.setText(this.bundle.getString(item.toString()));
      } else {
         this.setText(item.toString());
      }

      if (this.styles != null && this.styles.containsKey(item)) {
         this.setStyle(this.styles.get(item));
      } else {
         this.setStyle(null);
      }

      this.setGraphic(null);
   }
}
