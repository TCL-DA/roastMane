package com.cropster.roastingintelligence.ui.util;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class TranslatedTableCellFactory<T> implements Callback<TableColumn<T, String>, TableCell<T, String>> {
   public TableCell<T, String> call(TableColumn<T, String> param) {
      return new TableCell<T, String>() {
         protected void updateItem(String item, boolean empty) {
            if (item != this.getItem()) {
               super.updateItem(item, empty);
               if (item == null) {
                  this.setText(null);
                  this.setGraphic(null);
               } else {
                  this.setText(org.cropster.roastingintelligence.util.Messages.m_(item));
                  this.setGraphic(null);
               }
            }
         }
      };
   }
}
