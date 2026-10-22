package org.controlsfx.control;

import impl.org.controlsfx.skin.SearchableComboBoxSkin;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Skin;

public class SearchableComboBox<T> extends ComboBox<T> {
   private static final String DEFAULT_STYLE_CLASS = "searchable-combo-box";

   public SearchableComboBox() {
      this(FXCollections.observableArrayList());
   }

   public SearchableComboBox(ObservableList<T> items) {
      super(items);
      this.getStyleClass().add("searchable-combo-box");
   }

   protected Skin<?> createDefaultSkin() {
      return new SearchableComboBoxSkin(this);
   }
}
