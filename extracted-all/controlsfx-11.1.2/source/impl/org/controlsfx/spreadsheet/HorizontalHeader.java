package impl.org.controlsfx.spreadsheet;

import java.util.BitSet;
import java.util.ConcurrentModificationException;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ListChangeListener.Change;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.control.skin.NestedTableColumnHeader;
import javafx.scene.control.skin.TableColumnHeader;
import javafx.scene.control.skin.TableHeaderRow;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetColumn;
import org.controlsfx.control.spreadsheet.SpreadsheetView;

public class HorizontalHeader extends TableHeaderRow {
   final GridViewSkin gridViewSkin;
   private int lastColumnResized = -1;
   private boolean working = true;
   protected BitSet selectedColumns = new BitSet();
   private final InvalidationListener verticalHeaderListener = new InvalidationListener() {
      public void invalidated(Observable observable) {
         HorizontalHeader.this.updateTableWidth();
      }
   };
   private final ChangeListener<Boolean> horizontalHeaderVisibilityListener = new ChangeListener<Boolean>() {
      public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
         HorizontalHeader.this.updateHorizontalHeaderVisibility(arg2);
      }
   };
   private final ListChangeListener<SpreadsheetColumn> fixedColumnsListener = new ListChangeListener<SpreadsheetColumn>() {
      public void onChanged(Change<? extends SpreadsheetColumn> change) {
         while (change.next()) {
            for (SpreadsheetColumn remitem : change.getRemoved()) {
               HorizontalHeader.this.unfixColumn(remitem);
            }

            for (SpreadsheetColumn additem : change.getAddedSubList()) {
               HorizontalHeader.this.fixColumn(additem);
            }
         }

         HorizontalHeader.this.updateHighlightSelection();
      }
   };
   private final InvalidationListener selectionListener = new InvalidationListener() {
      public void invalidated(Observable valueModel) {
         HorizontalHeader.this.updateHighlightSelection();
      }
   };

   public HorizontalHeader(GridViewSkin skin) {
      super(skin);
      this.gridViewSkin = skin;
   }

   public void init() {
      SpreadsheetView spv = this.gridViewSkin.handle.getView();
      this.updateHorizontalHeaderVisibility(spv.isShowColumnHeader());
      spv.showRowHeaderProperty().addListener(this.verticalHeaderListener);
      this.gridViewSkin.verticalHeader.verticalHeaderWidthProperty().addListener(this.verticalHeaderListener);
      spv.showColumnHeaderProperty().addListener(this.horizontalHeaderVisibilityListener);
      this.gridViewSkin.getSelectedColumns().addListener(this.selectionListener);
      spv.getFixedColumns().addListener(this.fixedColumnsListener);
      Platform.runLater(() -> {
         for (SpreadsheetColumn column : spv.getFixedColumns()) {
            this.fixColumn(column);
         }

         this.requestLayout();
         this.installHeaderMouseEvent();
      });
      this.getRootHeader().getColumnHeaders().addListener(o -> {
         for (SpreadsheetColumn fixItem : spv.getFixedColumns()) {
            this.fixColumn(fixItem);
         }

         this.updateHighlightSelection();
         this.installHeaderMouseEvent();
      });
   }

   void clearSelectedColumns() {
      this.selectedColumns.clear();
   }

   protected void updateTableWidth() {
      super.updateTableWidth();
      double padding = 0.0;
      if (this.working
         && this.gridViewSkin != null
         && this.gridViewSkin.spreadsheetView != null
         && this.gridViewSkin.spreadsheetView.showRowHeaderProperty().get()
         && this.gridViewSkin.verticalHeader != null) {
         padding += this.gridViewSkin.verticalHeader.getVerticalHeaderWidth();
      }

      Rectangle clip = (Rectangle)this.getClip();
      clip.setWidth(clip.getWidth() == 0.0 ? 0.0 : clip.getWidth() - padding);
   }

   protected void updateScrollX() {
      super.updateScrollX();
      this.gridViewSkin.horizontalPickers.updateScrollX();
      if (this.working) {
         this.requestLayout();
         ((HorizontalHeaderColumn)this.getRootHeader()).layoutFixedColumns();
      }
   }

   protected NestedTableColumnHeader createRootHeader() {
      return new HorizontalHeaderColumn(null);
   }

   private void installHeaderMouseEvent() {
      for (TableColumnHeader columnHeader : this.getRootHeader().getColumnHeaders()) {
         EventHandler<MouseEvent> mouseEventHandler = mouseEvent -> {
            if (mouseEvent.isPrimaryButtonDown()) {
               this.headerClicked((TableColumn)columnHeader.getTableColumn(), mouseEvent);
            }
         };
         ((Node)columnHeader.getChildrenUnmodifiable().get(0)).setOnMousePressed(mouseEventHandler);
      }
   }

   private void headerClicked(TableColumn column, MouseEvent event) {
      TableViewSelectionModel<ObservableList<SpreadsheetCell>> sm = this.gridViewSkin.handle.getGridView().getSelectionModel();
      int lastRow = this.gridViewSkin.getItemCount() - 1;
      int indexColumn = column.getTableView().getColumns().indexOf(column);
      TablePosition focusedPosition = sm.getTableView().getFocusModel().getFocusedCell();
      if (event.isShortcutDown()) {
         BitSet tempSet = (BitSet)this.selectedColumns.clone();
         sm.selectRange(0, column, lastRow, column);
         this.selectedColumns.or(tempSet);
         this.selectedColumns.set(indexColumn);
      } else if (event.isShiftDown() && focusedPosition != null && focusedPosition.getTableColumn() != null) {
         sm.clearSelection();
         sm.selectRange(0, column, lastRow, focusedPosition.getTableColumn());
         sm.getTableView().getFocusModel().focus(0, focusedPosition.getTableColumn());
         int min = Math.min(indexColumn, focusedPosition.getColumn());
         int max = Math.max(indexColumn, focusedPosition.getColumn());
         this.selectedColumns.set(min, max + 1);
      } else {
         sm.clearSelection();
         sm.selectRange(0, column, lastRow, column);
         sm.getTableView().getFocusModel().focus(0, column);
         this.selectedColumns.set(indexColumn);
      }
   }

   private void fixColumn(SpreadsheetColumn column) {
      this.addStyleHeader(this.gridViewSkin.spreadsheetView.getViewColumn(this.gridViewSkin.spreadsheetView.getColumns().indexOf(column)));
   }

   private void unfixColumn(SpreadsheetColumn column) {
      try {
         this.removeStyleHeader(this.gridViewSkin.spreadsheetView.getViewColumn(this.gridViewSkin.spreadsheetView.getColumns().indexOf(column)));
      } catch (ConcurrentModificationException var3) {
      }
   }

   private void removeStyleHeader(Integer i) {
      if (this.getRootHeader().getColumnHeaders().size() > i) {
         ((TableColumnHeader)this.getRootHeader().getColumnHeaders().get(i)).getStyleClass().removeAll(new String[]{"fixed"});
      }
   }

   private void addStyleHeader(Integer i) {
      if (this.getRootHeader().getColumnHeaders().size() > i) {
         ((TableColumnHeader)this.getRootHeader().getColumnHeaders().get(i)).getStyleClass().addAll(new String[]{"fixed"});
      }
   }

   private void updateHighlightSelection() {
      for (TableColumnHeader i : this.getRootHeader().getColumnHeaders()) {
         i.getStyleClass().removeAll(new String[]{"selected"});
      }

      for (Integer i : this.gridViewSkin.getSelectedColumns()) {
         if (this.getRootHeader().getColumnHeaders().size() > i) {
            ((TableColumnHeader)this.getRootHeader().getColumnHeaders().get(i)).getStyleClass().addAll(new String[]{"selected"});
         }
      }
   }

   private void updateHorizontalHeaderVisibility(boolean visible) {
      this.working = visible;
      this.setManaged(this.working);
      if (!visible) {
         this.getStyleClass().add("invisible");
      } else {
         this.getStyleClass().remove("invisible");
         this.requestLayout();
         ((HorizontalHeaderColumn)this.getRootHeader()).layoutFixedColumns();
         this.updateHighlightSelection();
      }
   }

   protected double computePrefHeight(double width) {
      if (!this.gridViewSkin.handle.getView().isShowColumnHeader()) {
         return 0.0;
      } else {
         double headerPrefHeight = this.getRootHeader().prefHeight(width);
         headerPrefHeight = headerPrefHeight == 0.0 ? 24.0 : headerPrefHeight;
         double height = this.snappedTopInset() + headerPrefHeight + this.snappedBottomInset();
         return height < 24.0 ? 24.0 : height;
      }
   }
}
