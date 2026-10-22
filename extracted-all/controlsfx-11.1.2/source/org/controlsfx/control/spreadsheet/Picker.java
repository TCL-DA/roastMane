package org.controlsfx.control.spreadsheet;

import java.util.Collection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public abstract class Picker {
   private final ObservableList<String> styleClass = FXCollections.observableArrayList();

   public Picker() {
      this("picker-label");
   }

   public Picker(String... styleClass) {
      this.styleClass.addAll(styleClass);
   }

   public Picker(Collection<String> styleClass) {
      this.styleClass.addAll(styleClass);
   }

   public final ObservableList<String> getStyleClass() {
      return this.styleClass;
   }

   public abstract void onClick();
}
