package com.cropster.roastingintelligence.ui.util;

import java.util.concurrent.TimeUnit;
import javafx.event.EventHandler;
import javafx.scene.control.ListCell;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public abstract class AbstractListCell<T> extends ListCell<T> {
   protected AbstractListCell() {
      this.setPrefWidth(0.0);
      EventHandler<MouseEvent> eventHandler = event -> {
         if (this.getListView().isEditable() && this.getListView().getEditingIndex() >= 0) {
            event.consume();
         }

         if (this.isSelected() && event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
            event.consume();
         }
      };
      this.addEventHandler(MouseEvent.MOUSE_PRESSED, eventHandler);
   }

   protected abstract void initViewer(T var1);

   protected abstract void initEditor(T var1);

   public void startEdit() {
      if (!this.isSelected()) {
         this.getListView().getSelectionModel().select(this.getIndex());
      }

      if (this.isEditable() && this.getListView().isEditable()) {
         super.startEdit();
         if (this.isEditing()) {
            this.initEditor((T)this.getItem());
            this.setText(null);
            PlatformScheduler.schedule(() -> {
               double editorMinY = this.getLayoutY() + this.getLayoutBounds().getMinY();
               double editorMaxY = this.getLayoutY() + this.getLayoutBounds().getMaxY();
               if (editorMinY < 0.0 || editorMaxY > this.getListView().getHeight()) {
                  this.getListView().scrollTo(this.getIndex());
               }
            }, 222, TimeUnit.MILLISECONDS);
         }
      }
   }

   public void cancelEdit() {
      super.cancelEdit();
      this.initViewer((T)this.getItem());
      this.setText(null);
   }

   protected void updateItem(T item, boolean empty) {
      super.updateItem(item, empty);
      if (empty || item == null) {
         this.setGraphic(null);
      } else if (this.isEditing()) {
         this.initEditor(item);
      } else {
         this.initViewer(item);
      }

      this.setText(null);
   }
}
