package org.controlsfx.control.tableview2.filter.filtermenubutton;

import java.util.function.Predicate;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.layout.StackPane;
import javafx.util.StringConverter;

public abstract class FilterMenuButton extends MenuButton {
   protected Label symbol;
   protected StringProperty buttonText = new SimpleStringProperty();

   public FilterMenuButton() {
      if (this.getSkin() != null) {
         this.insertSymbol();
      } else {
         this.skinProperty().addListener(new InvalidationListener() {
            public void invalidated(Observable observable) {
               if (FilterMenuButton.this.getSkin() != null) {
                  FilterMenuButton.this.insertSymbol();
                  FilterMenuButton.this.skinProperty().removeListener(this);
               }
            }
         });
      }
   }

   public abstract Predicate<?> parse(String var1);

   public abstract Predicate<?> parse(String var1, StringConverter<?> var2);

   private void insertSymbol() {
      StackPane arrow = (StackPane)this.lookup(".arrow");
      if (arrow != null) {
         arrow.getStyleClass().setAll(new String[]{"operator"});
         this.symbol = new Label();
         this.symbol.getStyleClass().add("symbol");
         arrow.getChildren().add(this.symbol);
         this.symbol.textProperty().bind(this.buttonText);
      }
   }

   public abstract String getErrorMessage();
}
