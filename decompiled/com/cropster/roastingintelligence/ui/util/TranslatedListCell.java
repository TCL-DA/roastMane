package com.cropster.roastingintelligence.ui.util;

import java.util.Map;
import java.util.function.BiFunction;
import javafx.scene.control.ListCell;

public class TranslatedListCell<T> extends ListCell<T> {
   private final String nullText;
   private Map<T, String> styles;
   private BiFunction<TranslatedListCell<T>, T, String> styleProvider;

   public TranslatedListCell() {
      this(null);
   }

   public TranslatedListCell(String nullText) {
      this.nullText = nullText;
   }

   public TranslatedListCell<T> withStyles(Map<T, String> styles) {
      this.styles = styles;
      return this;
   }

   public TranslatedListCell<T> withStyleProvider(BiFunction<TranslatedListCell<T>, T, String> styleProvider) {
      this.styleProvider = styleProvider;
      return this;
   }

   protected void updateItem(T item, boolean empty) {
      super.updateItem(item, empty);
      this.setDisable(false);
      this.setGraphic(null);
      this.setStyle(null);
      if (this.styles != null) {
         if (item != null && this.styles.containsKey(item)) {
            this.setStyle(this.styles.get(item));
         }
      } else if (this.styleProvider != null && item != null) {
         this.setStyle(this.styleProvider.apply(this, item));
      }

      if (item instanceof Enum) {
         this.setText(org.cropster.roastingintelligence.util.Messages.m_((Enum<?>)item));
      } else if (item != null) {
         this.setText(org.cropster.roastingintelligence.util.Messages.m_(item.toString()));
      } else {
         this.setText(this.nullText);
         this.setStyle("-fx-text-fill: -fx-color-text-disabled; ");
         this.setDisable(true);
      }
   }
}
