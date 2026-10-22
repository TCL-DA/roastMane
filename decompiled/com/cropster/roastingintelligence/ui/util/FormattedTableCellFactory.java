package com.cropster.roastingintelligence.ui.util;

import java.text.Format;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.text.TextAlignment;
import javafx.util.Callback;

public class FormattedTableCellFactory<T, S> implements Callback<TableColumn<T, S>, TableCell<T, S>> {
   private Format format;
   private TextAlignment alignment = TextAlignment.LEFT;

   public FormattedTableCellFactory() {
   }

   public FormattedTableCellFactory(Format format) {
      this.format = format;
   }

   public Format getFormat() {
      return this.format;
   }

   public void setFormat(Format format) {
      this.format = format;
   }

   public TextAlignment getAlignment() {
      return this.alignment;
   }

   public void setAlignment(TextAlignment alignment) {
      this.alignment = alignment;
   }

   public TableCell<T, S> call(TableColumn<T, S> param) {
      TableCell<T, S> cell = new TableCell<T, S>() {
         protected void updateItem(S item, boolean empty) {
            if (item != this.getItem()) {
               super.updateItem(item, empty);
               if (item == null) {
                  this.setText(null);
                  this.setGraphic(null);
               } else if (FormattedTableCellFactory.this.format != null) {
                  this.setText(FormattedTableCellFactory.this.format.format(item));
               } else if (item instanceof Node) {
                  this.setText(null);
                  this.setGraphic((Node)item);
               } else {
                  this.setText(item.toString());
                  this.setGraphic(null);
               }
            }
         }
      };
      cell.setTextAlignment(this.alignment);
      switch (this.alignment) {
         case CENTER:
            cell.setAlignment(Pos.CENTER);
            break;
         case RIGHT:
            cell.setAlignment(Pos.CENTER_RIGHT);
            break;
         default:
            cell.setAlignment(Pos.CENTER_LEFT);
      }

      return cell;
   }
}
