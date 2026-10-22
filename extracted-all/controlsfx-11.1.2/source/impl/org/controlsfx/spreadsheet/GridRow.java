package impl.org.controlsfx.spreadsheet;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.MapChangeListener.Change;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.WeakEventHandler;
import javafx.scene.control.Skin;
import javafx.scene.control.TableRow;
import javafx.scene.input.MouseEvent;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetView;

public class GridRow extends TableRow<ObservableList<SpreadsheetCell>> {
   private final SpreadsheetHandle handle;
   DoubleProperty verticalShift = new SimpleDoubleProperty();
   private final InvalidationListener setPrefHeightListener = new InvalidationListener() {
      public void invalidated(Observable o) {
         GridRow.this.setRowHeight(GridRow.this.computePrefHeight(-1.0));
      }
   };
   private final WeakInvalidationListener weakPrefHeightListener = new WeakInvalidationListener(this.setPrefHeightListener);
   private final EventHandler<MouseEvent> dragDetectedEventHandler = new EventHandler<MouseEvent>() {
      public void handle(MouseEvent event) {
         if (event.getTarget().getClass().equals(GridRow.class)
            && event.getPickResult().getIntersectedNode() != null
            && event.getPickResult().getIntersectedNode().getClass().equals(CellView.class)) {
            Event.fireEvent(event.getPickResult().getIntersectedNode(), event);
         }
      }
   };
   private final WeakEventHandler<MouseEvent> weakDragHandler = new WeakEventHandler(this.dragDetectedEventHandler);
   private final InvalidationListener gridListener = new InvalidationListener() {
      public void invalidated(Observable o) {
         GridRow.this.setRowHeight(GridRow.this.computePrefHeight(-1.0));
         GridRow.this.handle.getView().comparatorProperty().addListener(GridRow.this.weakComparatorListener);
      }
   };
   private final WeakInvalidationListener weakGridListener = new WeakInvalidationListener(this.gridListener);
   private final InvalidationListener comparatorListener = new InvalidationListener() {
      public void invalidated(Observable o) {
         GridRow.this.updateIndex(GridRow.this.getIndex());
         GridRow.this.setRowHeight(GridRow.this.computePrefHeight(-1.0));
      }
   };
   private final WeakInvalidationListener weakComparatorListener = new WeakInvalidationListener(this.comparatorListener);

   public GridRow(final SpreadsheetHandle handle) {
      this.handle = handle;
      this.indexProperty().addListener(this.weakPrefHeightListener);
      this.visibleProperty().addListener(this.weakPrefHeightListener);
      handle.getView().gridProperty().addListener(this.weakGridListener);
      handle.getView().hiddenRowsProperty().addListener(this.weakPrefHeightListener);
      handle.getView().hiddenColumnsProperty().addListener(this.weakPrefHeightListener);
      handle.getView().comparatorProperty().addListener(this.weakComparatorListener);
      handle.getCellsViewSkin().rowHeightMap.addListener(new MapChangeListener<Integer, Double>() {
         public void onChanged(Change<? extends Integer, ? extends Double> change) {
            if (change.wasAdded() && (Integer)change.getKey() == handle.getView().getModelRow(GridRow.this.getIndex())) {
               GridRow.this.setRowHeight((Double)change.getValueAdded());
            } else if (change.wasRemoved() && (Integer)change.getKey() == handle.getView().getModelRow(GridRow.this.getIndex())) {
               GridRow.this.setRowHeight(GridRow.this.computePrefHeight(-1.0));
            }
         }
      });
      this.addEventHandler(MouseEvent.DRAG_DETECTED, this.weakDragHandler);
   }

   void addCell(CellView cell) {
      this.getChildren().add(cell);
   }

   void removeCell(CellView gc) {
      this.getChildren().remove(gc);
   }

   SpreadsheetView getSpreadsheetView() {
      return this.handle.getView();
   }

   protected double computePrefHeight(double width) {
      return this.handle.getCellsViewSkin().getRowHeight(this.getIndex());
   }

   protected double computeMinHeight(double width) {
      return this.handle.getCellsViewSkin().getRowHeight(this.getIndex());
   }

   protected Skin<?> createDefaultSkin() {
      return new GridRowSkin(this.handle, this);
   }

   public void setRowHeight(double height) {
      CellView.getValue(() -> this.setHeight(height));
      this.setPrefHeight(height);
      this.handle.getCellsViewSkin().rectangleSelection.updateRectangle();
   }
}
