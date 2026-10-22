package impl.org.controlsfx.tableview2;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.WeakEventHandler;
import javafx.scene.control.Skin;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableRow;
import javafx.scene.input.MouseEvent;
import org.controlsfx.control.tableview2.TableView2;

public class TableRow2<S> extends TableRow<S> {
   private final TableView2<S> tableView;
   private final TableView2Skin<S> skin;
   DoubleProperty verticalShift = new SimpleDoubleProperty();
   TableView2<S> parentTableView = null;
   private final InvalidationListener setPrefHeightListener = o -> this.setRowHeight(this.computePrefHeight(-1.0));
   private final WeakInvalidationListener weakPrefHeightListener = new WeakInvalidationListener(this.setPrefHeightListener);
   private final EventHandler<MouseEvent> dragDetectedEventHandler = event -> {
      if (event.getTarget().getClass().equals(TableRow2.class)
         && event.getPickResult().getIntersectedNode() != null
         && event.getPickResult().getIntersectedNode().getClass().equals(TableCell.class)) {
         Event.fireEvent(event.getPickResult().getIntersectedNode(), event);
      }
   };
   private final WeakEventHandler<MouseEvent> weakDragHandler = new WeakEventHandler(this.dragDetectedEventHandler);

   public TableRow2(TableView2<S> tableView) {
      this.tableView = tableView;
      if (tableView.getParent() != null && tableView.getParent() instanceof RowHeader) {
         this.parentTableView = ((RowHeader)tableView.getParent()).getParentTableView();
      }

      this.skin = (TableView2Skin<S>)(this.parentTableView != null ? this.parentTableView : tableView).getSkin();
      this.indexProperty().addListener(this.weakPrefHeightListener);
      this.visibleProperty().addListener(this.weakPrefHeightListener);
      this.skin.rowHeightMap.addListener(change -> {
         if (change.wasAdded() && (Integer)change.getKey() == this.getIndex()) {
            this.setRowHeight((Double)change.getValueAdded());
         } else if (change.wasRemoved() && (Integer)change.getKey() == this.getIndex()) {
            this.setRowHeight(this.computePrefHeight(-1.0));
         }
      });
      this.skin.rowHeightMap.addListener(o -> {
         if (this.getIndex() > -1 && this.getIndex() < this.skin.rowHeightMap.size()) {
            this.setRowHeight((Double)this.skin.rowHeightMap.getOrDefault(this.getIndex(), this.computePrefHeight(-1.0)));
         }
      });
      this.addEventHandler(MouseEvent.DRAG_DETECTED, this.weakDragHandler);
   }

   void addCell(TableCell<S, ?> cell) {
      this.getChildren().add(cell);
   }

   void removeCell(TableCell<S, ?> gc) {
      this.getChildren().remove(gc);
   }

   protected double computePrefHeight(double width) {
      return this.skin.getRowHeight(this.getIndex());
   }

   protected double computeMinHeight(double width) {
      return this.skin.getRowHeight(this.getIndex());
   }

   protected Skin<?> createDefaultSkin() {
      return new TableRow2Skin<S>(this.tableView, this);
   }

   public void setRowHeight(double height) {
      this.runOnFXThread(() -> this.setHeight(height));
      this.setPrefHeight(height);
   }

   private void runOnFXThread(Runnable runnable) {
      if (Platform.isFxApplicationThread()) {
         runnable.run();
      } else {
         Platform.runLater(runnable);
      }
   }
}
