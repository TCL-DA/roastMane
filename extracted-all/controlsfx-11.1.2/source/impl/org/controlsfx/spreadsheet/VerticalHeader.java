package impl.org.controlsfx.spreadsheet;

import impl.org.controlsfx.i18n.Localization;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import javafx.beans.InvalidationListener;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.NodeOrientation;
import javafx.scene.Cursor;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.WindowEvent;
import org.controlsfx.control.spreadsheet.Picker;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetView;

public class VerticalHeader extends StackPane {
   public static final int PICKER_SIZE = 16;
   private static final int DRAG_RECT_HEIGHT = 5;
   private static final String TABLE_ROW_KEY = "TableRow";
   private static final String PICKER_INDEX = "PickerIndex";
   private static final String TABLE_LABEL_KEY = "Label";
   private static final Image pinImage = new Image(SpreadsheetView.class.getResource("pinSpreadsheetView.png").toExternalForm());
   private final SpreadsheetHandle handle;
   private final SpreadsheetView spreadsheetView;
   private double horizontalHeaderHeight;
   private final DoubleProperty innerVerticalHeaderWidth = new SimpleDoubleProperty();
   private Rectangle clip;
   private ContextMenu blankContextMenu;
   private double lastY = 0.0;
   private static double dragAnchorY = 0.0;
   private final List<Rectangle> dragRects = new ArrayList<>();
   private int dragRectCount = 0;
   private final List<Label> labelList = new ArrayList<>();
   private int labelCount = 0;
   private GridViewSkin skin;
   private boolean resizing = false;
   private final Stack<Label> pickerPile;
   private final Stack<Label> pickerUsed;
   private final BitSet selectedRows = new BitSet();
   private final EventHandler<MouseEvent> rectMousePressed = new EventHandler<MouseEvent>() {
      public void handle(MouseEvent me) {
         if (me.getClickCount() == 2 && me.isPrimaryButtonDown()) {
            Rectangle rect = (Rectangle)me.getSource();
            GridRow row = (GridRow)rect.getProperties().get("TableRow");
            VerticalHeader.this.skin.resizeRowToFitContent(VerticalHeader.this.spreadsheetView.getModelRow(row.getIndex()));
            VerticalHeader.this.requestLayout();
         } else {
            VerticalHeader.dragAnchorY = me.getSceneY();
            VerticalHeader.this.resizing = true;
         }

         me.consume();
      }
   };
   private final EventHandler<MouseEvent> rectMouseDragged = new EventHandler<MouseEvent>() {
      public void handle(MouseEvent me) {
         Rectangle rect = (Rectangle)me.getSource();
         GridRow row = (GridRow)rect.getProperties().get("TableRow");
         Label label = (Label)rect.getProperties().get("Label");
         if (row != null) {
            VerticalHeader.this.rowResizing(row, label, me);
         }

         me.consume();
      }
   };
   private final EventHandler<MouseEvent> rectMouseReleased = new EventHandler<MouseEvent>() {
      public void handle(MouseEvent me) {
         VerticalHeader.this.lastY = 0.0;
         VerticalHeader.this.resizing = false;
         VerticalHeader.this.requestLayout();
         me.consume();
         Rectangle rect = (Rectangle)me.getSource();
         GridRow row = (GridRow)rect.getProperties().get("TableRow");
         if (VerticalHeader.this.selectedRows.get(row.getIndex())) {
            double height = row.getHeight();

            for (int i = VerticalHeader.this.selectedRows.nextSetBit(0); i >= 0; i = VerticalHeader.this.selectedRows.nextSetBit(i + 1)) {
               VerticalHeader.this.skin.rowHeightMap.put(VerticalHeader.this.spreadsheetView.getModelRow(i), height);
               Event.fireEvent(
                  VerticalHeader.this.spreadsheetView, new SpreadsheetView.RowHeightEvent(VerticalHeader.this.spreadsheetView.getModelRow(i), height)
               );
            }
         }
      }
   };
   private final EventHandler<MouseEvent> pickerMouseEvent = new EventHandler<MouseEvent>() {
      public void handle(MouseEvent mouseEvent) {
         Label picker = (Label)mouseEvent.getSource();
         ((Picker)picker.getProperties().get("PickerIndex")).onClick();
      }
   };
   private final InvalidationListener layout = arg0 -> this.requestLayout();

   public VerticalHeader(SpreadsheetHandle handle) {
      this.handle = handle;
      this.spreadsheetView = handle.getView();
      this.pickerPile = new Stack<>();
      this.pickerUsed = new Stack<>();
   }

   void init(GridViewSkin skin, HorizontalHeader horizontalHeader) {
      this.skin = skin;
      horizontalHeader.heightProperty().addListener(new ChangeListener<Number>() {
         public void changed(ObservableValue<? extends Number> arg0, Number oldHeight, Number newHeight) {
            VerticalHeader.this.horizontalHeaderHeight = newHeight.doubleValue();
            VerticalHeader.this.requestLayout();
         }
      });
      this.handle.getView().gridProperty().addListener(this.layout);
      this.handle.getView().hiddenRowsProperty().addListener(this.layout);
      this.handle.getView().hiddenColumnsProperty().addListener(this.layout);
      this.clip = new Rectangle(this.getVerticalHeaderWidth(), this.snapSize(((TableView)skin.getSkinnable()).getHeight()));
      this.clip.relocate(this.snappedTopInset(), this.snappedLeftInset());
      this.clip.setSmooth(false);
      this.clip.heightProperty().bind(((TableView)skin.getSkinnable()).heightProperty());
      this.clip.widthProperty().bind(this.innerVerticalHeaderWidth);
      this.setClip(this.clip);
      this.spreadsheetView.showRowHeaderProperty().addListener(this.layout);
      this.spreadsheetView.showColumnHeaderProperty().addListener(this.layout);
      this.spreadsheetView.getFixedRows().addListener(this.layout);
      this.spreadsheetView.fixingRowsAllowedProperty().addListener(this.layout);
      this.spreadsheetView.rowHeaderWidthProperty().addListener(this.layout);
      this.spreadsheetView.heightProperty().addListener(this.layout);
      this.spreadsheetView.getRowPickers().addListener(this.layout);
      skin.getSelectedRows().addListener(this.layout);
      this.blankContextMenu = new ContextMenu();
   }

   public double getVerticalHeaderWidth() {
      return this.innerVerticalHeaderWidth.get();
   }

   public ReadOnlyDoubleProperty verticalHeaderWidthProperty() {
      return this.innerVerticalHeaderWidth;
   }

   public double computeHeaderWidth() {
      double width = 0.0;
      if (!this.spreadsheetView.getRowPickers().isEmpty()) {
         width += 16.0;
      }

      if (this.spreadsheetView.isShowRowHeader()) {
         width += this.spreadsheetView.getRowHeaderWidth();
      }

      return width;
   }

   void clearSelectedRows() {
      this.selectedRows.clear();
   }

   protected void layoutChildren() {
      if (!this.resizing) {
         if ((this.spreadsheetView.isShowRowHeader() || !this.spreadsheetView.getRowPickers().isEmpty()) && this.skin.getCellsSize() > 0) {
            double x = this.snappedLeftInset();
            this.pickerPile.addAll(this.pickerUsed.subList(0, this.pickerUsed.size()));
            this.pickerUsed.clear();
            this.labelCount = 0;
            this.dragRectCount = 0;
            if (!this.spreadsheetView.getRowPickers().isEmpty()) {
               this.innerVerticalHeaderWidth.setValue(16);
               x += 16.0;
            } else {
               this.innerVerticalHeaderWidth.setValue(0);
            }

            if (this.spreadsheetView.isShowRowHeader()) {
               this.innerVerticalHeaderWidth.setValue(this.getVerticalHeaderWidth() + this.spreadsheetView.getRowHeaderWidth());
            }

            this.getChildren().clear();
            int cellSize = this.skin.getCellsSize();
            this.addVisibleRows(x, cellSize);
            this.addFixedRows(x, cellSize);
            if (this.spreadsheetView.showColumnHeaderProperty().get()) {
               Label label = this.getLabel(null);
               label.setOnMousePressed(event -> this.spreadsheetView.getSelectionModel().selectAll());
               label.setText("");
               label.resize(this.spreadsheetView.getRowHeaderWidth(), this.horizontalHeaderHeight);
               label.layoutYProperty().unbind();
               label.setLayoutY(0.0);
               label.setLayoutX(x);
               label.getStyleClass().clear();
               label.setContextMenu(this.blankContextMenu);
               this.getChildren().add(label);
            }

            ScrollBar hbar = this.handle.getCellsViewSkin().getHBar();
            if (hbar.isVisible()) {
               Label label = this.getLabel(null);
               label.getProperties().put("TableRow", null);
               label.setText("");
               label.resize(this.getVerticalHeaderWidth(), hbar.getHeight());
               label.layoutYProperty().unbind();
               label.relocate(this.snappedLeftInset(), this.getHeight() - hbar.getHeight());
               label.getStyleClass().clear();
               label.setContextMenu(this.blankContextMenu);
               this.getChildren().add(label);
            }
         } else {
            this.getChildren().clear();
         }
      }
   }

   public static boolean isFixedRowEmpty(SpreadsheetView spreadsheetView) {
      for (Integer fixedRow : spreadsheetView.getFixedRows()) {
         if (!spreadsheetView.getHiddenRows().get(fixedRow)) {
            return false;
         }
      }

      return true;
   }

   private void addFixedRows(double x, int cellSize) {
      double spaceUsedByFixedRows = 0.0;
      Set<Integer> currentlyFixedRow = this.handle.getCellsViewSkin().getCurrentlyFixedRow();
      if (!isFixedRowEmpty(this.spreadsheetView) && cellSize != 0) {
         for (int j = 0; j < this.spreadsheetView.getFixedRows().size(); j++) {
            int modelRow = (Integer)this.spreadsheetView.getFixedRows().get(j);
            if (!this.spreadsheetView.getHiddenRows().get(modelRow)) {
               int viewRow = this.spreadsheetView.getFilteredRow(modelRow);
               if (!currentlyFixedRow.contains(viewRow)) {
                  break;
               }

               double rowHeight = this.skin.getRowHeight(viewRow);
               double y = this.spreadsheetView.showColumnHeaderProperty().get()
                  ? this.snappedTopInset() + this.horizontalHeaderHeight + spaceUsedByFixedRows
                  : this.snappedTopInset() + spaceUsedByFixedRows;
               if (this.spreadsheetView.getRowPickers().containsKey(modelRow)) {
                  Label picker = this.getPicker((Picker)this.spreadsheetView.getRowPickers().get(modelRow));
                  picker.resize(16.0, rowHeight);
                  picker.layoutYProperty().unbind();
                  picker.setLayoutY(y);
                  this.getChildren().add(picker);
               }

               if (this.spreadsheetView.isShowRowHeader()) {
                  Label label = this.getLabel(viewRow);
                  GridRow row = this.skin.getRowIndexed(viewRow);
                  label.getProperties().put("TableRow", row);
                  label.setText(this.getRowHeader(viewRow));
                  label.resize(this.spreadsheetView.getRowHeaderWidth(), rowHeight);
                  label.setContextMenu(this.getRowContextMenu(viewRow));
                  if (row != null) {
                     label.layoutYProperty().bind(row.layoutYProperty().add(this.horizontalHeaderHeight).add(row.verticalShift));
                  }

                  label.setLayoutX(x);
                  ObservableList<String> css = label.getStyleClass();
                  if (this.skin.getSelectedRows().contains(viewRow)) {
                     css.addAll(new String[]{"selected"});
                  } else {
                     css.removeAll(new String[]{"selected"});
                  }

                  css.addAll(new String[]{"fixed"});
                  this.getChildren().add(label);
                  if (this.spreadsheetView.getGrid().isRowResizable(viewRow)) {
                     Rectangle dragRect = this.getDragRect();
                     dragRect.getProperties().put("TableRow", row);
                     dragRect.getProperties().put("Label", label);
                     dragRect.setWidth(label.getWidth());
                     dragRect.relocate(this.snappedLeftInset() + x, y + rowHeight - 5.0);
                     this.getChildren().add(dragRect);
                  }
               }

               spaceUsedByFixedRows += this.skin.getRowHeight(viewRow);
            }
         }
      }
   }

   private void addVisibleRows(double x, int cellSize) {
      double y = this.snappedTopInset();
      if (this.spreadsheetView.showColumnHeaderProperty().get()) {
         y += this.horizontalHeaderHeight;
      }

      if (cellSize != 0) {
         y += this.skin.getRow(0).getLocalToParentTransform().getTy();
      }

      int viewRowCount = this.skin.getItemCount();
      int i = 0;
      GridRow row = this.skin.getRow(i);
      double fixedRowHeight = this.skin.getFixedRowHeight();

      for (double rowHeaderWidth = this.spreadsheetView.getRowHeaderWidth();
         cellSize != 0 && row != null && row.getIndex() < viewRowCount;
         row = this.skin.getRow(++i)
      ) {
         int rowIndex = row.getIndex();
         double height = row.getHeight();
         int modelRow = this.spreadsheetView.getFilteredSourceIndex(rowIndex);
         if (row.getLayoutY() >= fixedRowHeight && this.spreadsheetView.getRowPickers().containsKey(modelRow)) {
            Label picker = this.getPicker((Picker)this.spreadsheetView.getRowPickers().get(modelRow));
            picker.resize(16.0, height);
            picker.layoutYProperty().bind(row.layoutYProperty().add(this.horizontalHeaderHeight));
            this.getChildren().add(picker);
         }

         if (this.spreadsheetView.isShowRowHeader()) {
            Label label = this.getLabel(rowIndex);
            label.getProperties().put("TableRow", row);
            label.setText(this.getRowHeader(rowIndex));
            label.resize(rowHeaderWidth, height);
            label.setLayoutX(x);
            label.layoutYProperty().bind(row.layoutYProperty().add(this.horizontalHeaderHeight));
            label.setContextMenu(this.getRowContextMenu(rowIndex));
            this.getChildren().add(label);
            ObservableList<String> css = label.getStyleClass();
            if (this.skin.getSelectedRows().contains(rowIndex)) {
               css.addAll(new String[]{"selected"});
            } else {
               css.removeAll(new String[]{"selected"});
            }

            if (this.spreadsheetView.getFixedRows().contains(modelRow)) {
               css.addAll(new String[]{"fixed"});
            } else {
               css.removeAll(new String[]{"fixed"});
            }

            y += height;
            if (this.spreadsheetView.getGrid().isRowResizable(modelRow)) {
               Rectangle dragRect = this.getDragRect();
               dragRect.getProperties().put("TableRow", row);
               dragRect.getProperties().put("Label", label);
               dragRect.setWidth(label.getWidth());
               dragRect.relocate(this.snappedLeftInset() + x, y - 5.0);
               this.getChildren().add(dragRect);
            }
         }
      }
   }

   private void rowResizing(GridRow gridRow, Label label, MouseEvent me) {
      double draggedY = me.getSceneY() - dragAnchorY;
      if (gridRow.getEffectiveNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT) {
         draggedY = -draggedY;
      }

      double delta = draggedY - this.lastY;
      Double newHeight = gridRow.getHeight() + delta;
      if (!(newHeight < 0.0)) {
         this.handle.getCellsViewSkin().rowHeightMap.put(this.spreadsheetView.getModelRow(gridRow.getIndex()), newHeight);
         Event.fireEvent(this.spreadsheetView, new SpreadsheetView.RowHeightEvent(this.spreadsheetView.getModelRow(gridRow.getIndex()), newHeight));
         label.resize(this.spreadsheetView.getRowHeaderWidth(), newHeight);
         gridRow.setPrefHeight(newHeight);
         gridRow.requestLayout();
         this.lastY = draggedY;
      }
   }

   private Label getLabel(Integer row) {
      Label label;
      if (!this.labelList.isEmpty() && this.labelList.size() > this.labelCount) {
         label = this.labelList.get(this.labelCount);
      } else {
         label = new Label();
         this.labelList.add(label);
      }

      this.labelCount++;
      label.setOnMousePressed(row == null ? null : event -> {
         if (event.isPrimaryButtonDown()) {
            if (event.getClickCount() == 2) {
               this.skin.resizeRowToFitContent(this.spreadsheetView.getModelRow(row));
               this.requestLayout();
            } else {
               this.headerClicked(row, event);
            }
         }
      });
      return label;
   }

   private void headerClicked(int row, MouseEvent event) {
      TableViewSelectionModel<ObservableList<SpreadsheetCell>> sm = this.handle.getGridView().getSelectionModel();
      int focusedRow = sm.getFocusedIndex();
      int rowCount = this.handle.getCellsViewSkin().getItemCount();
      ObservableList<TableColumn<ObservableList<SpreadsheetCell>, ?>> columns = sm.getTableView().getColumns();
      TableColumn<ObservableList<SpreadsheetCell>, ?> firstColumn = (TableColumn<ObservableList<SpreadsheetCell>, ?>)columns.get(0);
      TableColumn<ObservableList<SpreadsheetCell>, ?> lastColumn = (TableColumn<ObservableList<SpreadsheetCell>, ?>)columns.get(columns.size() - 1);
      if (event.isShortcutDown()) {
         BitSet tempSet = (BitSet)this.selectedRows.clone();
         sm.selectRange(row, firstColumn, row, lastColumn);
         this.selectedRows.or(tempSet);
         this.selectedRows.set(row);
      } else if (event.isShiftDown() && focusedRow >= 0 && focusedRow < rowCount) {
         sm.clearSelection();
         sm.selectRange(focusedRow, firstColumn, row, lastColumn);
         sm.getTableView().getFocusModel().focus(focusedRow, firstColumn);
         int min = Math.min(row, focusedRow);
         int max = Math.max(row, focusedRow);
         this.selectedRows.set(min, max + 1);
      } else {
         sm.clearSelection();
         sm.selectRange(row, firstColumn, row, lastColumn);
         sm.getTableView().getFocusModel().focus(row, firstColumn);
         this.selectedRows.set(row);
      }
   }

   private Label getPicker(Picker picker) {
      Label pickerLabel;
      if (this.pickerPile.isEmpty()) {
         pickerLabel = new Label();
         picker.getStyleClass().addListener(this.layout);
         pickerLabel.setOnMouseClicked(this.pickerMouseEvent);
      } else {
         pickerLabel = this.pickerPile.pop();
      }

      this.pickerUsed.push(pickerLabel);
      pickerLabel.getStyleClass().setAll(picker.getStyleClass());
      pickerLabel.getProperties().put("PickerIndex", picker);
      return pickerLabel;
   }

   private Rectangle getDragRect() {
      if (!this.dragRects.isEmpty() && this.dragRects.size() > this.dragRectCount) {
         return this.dragRects.get(this.dragRectCount++);
      } else {
         Rectangle rect = new Rectangle();
         rect.setWidth(this.getVerticalHeaderWidth());
         rect.setHeight(5.0);
         rect.setFill(Color.TRANSPARENT);
         rect.setSmooth(false);
         rect.setOnMousePressed(this.rectMousePressed);
         rect.setOnMouseDragged(this.rectMouseDragged);
         rect.setOnMouseReleased(this.rectMouseReleased);
         rect.setCursor(Cursor.V_RESIZE);
         this.dragRects.add(rect);
         this.dragRectCount++;
         return rect;
      }
   }

   private ContextMenu getRowContextMenu(final Integer row) {
      if (this.spreadsheetView.isRowFixable(row)) {
         ContextMenu contextMenu = new ContextMenu();
         final MenuItem fixItem = new MenuItem(Localization.localize(Localization.asKey("spreadsheet.verticalheader.menu.fix")));
         contextMenu.setOnShowing(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent event) {
               if (VerticalHeader.this.spreadsheetView.getFixedRows().contains(VerticalHeader.this.spreadsheetView.getFilteredSourceIndex(row))) {
                  fixItem.setText(Localization.localize(Localization.asKey("spreadsheet.verticalheader.menu.unfix")));
               } else {
                  fixItem.setText(Localization.localize(Localization.asKey("spreadsheet.verticalheader.menu.fix")));
               }
            }
         });
         fixItem.setGraphic(new ImageView(pinImage));
         fixItem.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent arg0) {
               Integer modelRow = VerticalHeader.this.spreadsheetView.getFilteredSourceIndex(row);
               if (VerticalHeader.this.spreadsheetView.getFixedRows().contains(modelRow)) {
                  VerticalHeader.this.spreadsheetView.getFixedRows().remove(modelRow);
               } else {
                  VerticalHeader.this.spreadsheetView.getFixedRows().add(modelRow);
               }
            }
         });
         contextMenu.getItems().add(fixItem);
         return contextMenu;
      } else {
         return this.blankContextMenu;
      }
   }

   private String getRowHeader(int index) {
      int newIndex = this.spreadsheetView.getFilteredSourceIndex(index);
      return this.spreadsheetView.getGrid().getRowHeaders().size() > newIndex
         ? (String)this.spreadsheetView.getGrid().getRowHeaders().get(newIndex)
         : String.valueOf(newIndex + 1);
   }
}
