package impl.org.controlsfx.spreadsheet;

import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.binding.When;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WeakChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.SetChangeListener;
import javafx.collections.WeakSetChangeListener;
import javafx.collections.SetChangeListener.Change;
import javafx.event.EventHandler;
import javafx.event.WeakEventHandler;
import javafx.geometry.Side;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Control;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Skin;
import javafx.scene.control.TableCell;
import javafx.scene.control.TablePositionBase;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TableView.TableViewFocusModel;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Region;
import javafx.util.Duration;
import org.controlsfx.control.spreadsheet.CellGraphicFactory;
import org.controlsfx.control.spreadsheet.Filter;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetCellEditor;
import org.controlsfx.control.spreadsheet.SpreadsheetCellType;
import org.controlsfx.control.spreadsheet.SpreadsheetColumn;
import org.controlsfx.control.spreadsheet.SpreadsheetView;

public class CellView extends TableCell<ObservableList<SpreadsheetCell>, SpreadsheetCell> {
   final SpreadsheetHandle handle;
   private Tooltip tooltip;
   private EventHandler<DragEvent> dragOverHandler;
   private EventHandler<DragEvent> dragDropHandler;
   private boolean dirtyStyle = false;
   private static final String ANCHOR_PROPERTY_KEY = "table.anchor";
   private static final int TOOLTIP_MAX_WIDTH = 400;
   private static final Duration FADE_DURATION = Duration.millis(200.0);
   private final ChangeListener<Node> graphicListener = new ChangeListener<Node>() {
      public void changed(ObservableValue<? extends Node> arg0, Node arg1, Node newGraphic) {
         CellView.this.setCellGraphic((SpreadsheetCell)CellView.this.getItem());
      }
   };
   private final WeakChangeListener<Node> weakGraphicListener = new WeakChangeListener(this.graphicListener);
   private final SetChangeListener<String> styleClassListener = new SetChangeListener<String>() {
      public void onChanged(Change<? extends String> arg0) {
         if (arg0.wasAdded()) {
            CellView.this.getStyleClass().add((String)arg0.getElementAdded());
         } else if (arg0.wasRemoved()) {
            CellView.this.getStyleClass().remove(arg0.getElementRemoved());
         }

         if (CellView.this.getItem() != null && ((SpreadsheetCell)CellView.this.getItem()).isCellGraphic()) {
            CellView.this.dirtyStyle = true;
         }
      }
   };
   private final WeakSetChangeListener<String> weakStyleClassListener = new WeakSetChangeListener(this.styleClassListener);
   private ChangeListener<String> styleListener;
   private WeakChangeListener<String> weakStyleListener;
   private final EventHandler<MouseEvent> startFullDragEventHandler = new EventHandler<MouseEvent>() {
      public void handle(MouseEvent arg0) {
         if (CellView.this.handle.getGridView().getSelectionModel().getSelectionMode().equals(SelectionMode.MULTIPLE)) {
            CellView.setAnchor(CellView.this.getTableView(), CellView.this.getTableView().getFocusModel().getFocusedCell());
            CellView.this.startFullDrag();
         }
      }
   };
   private final EventHandler<MouseEvent> dragMouseEventHandler = new EventHandler<MouseEvent>() {
      public void handle(MouseEvent arg0) {
         CellView.this.dragSelect(arg0);
      }
   };
   private final ChangeListener<SpreadsheetCell> itemChangeListener = new ChangeListener<SpreadsheetCell>() {
      public void changed(ObservableValue<? extends SpreadsheetCell> arg0, SpreadsheetCell oldItem, SpreadsheetCell newItem) {
         if (oldItem != null) {
            oldItem.getStyleClass().removeListener(CellView.this.weakStyleClassListener);
            oldItem.graphicProperty().removeListener(CellView.this.weakGraphicListener);
            if (oldItem.styleProperty() != null) {
               oldItem.styleProperty().removeListener(CellView.this.weakStyleListener);
            }
         }

         if (newItem != null) {
            CellView.this.getStyleClass().clear();
            CellView.this.getStyleClass().setAll(newItem.getStyleClass());
            newItem.getStyleClass().addListener(CellView.this.weakStyleClassListener);
            CellView.this.setCellGraphic(newItem);
            newItem.graphicProperty().addListener(CellView.this.weakGraphicListener);
            if (newItem.styleProperty() != null) {
               CellView.this.initStyleListener();
               newItem.styleProperty().addListener(CellView.this.weakStyleListener);
               CellView.this.setStyle(newItem.getStyle());
            } else {
               CellView.this.setStyle(null);
            }

            if (CellView.this.getItem() != null && ((SpreadsheetCell)CellView.this.getItem()).isCellGraphic()) {
               CellView.this.dirtyStyle = true;
            }
         }
      }
   };
   private final EventHandler<MouseEvent> actionEventHandler = new EventHandler<MouseEvent>() {
      public void handle(MouseEvent event) {
         if (CellView.this.getItem() != null
            && ((SpreadsheetCell)CellView.this.getItem()).hasPopup()
            && MouseButton.PRIMARY.equals(event.getButton())
            && (CellView.this.getFilter() == null || !CellView.this.getFilter().getMenuButton().isShowing())) {
            ContextMenu menu = new ContextMenu();
            menu.getScene().getStylesheets().add(SpreadsheetView.class.getResource("spreadsheet.css").toExternalForm());
            menu.getStyleClass().add("popup-button");
            menu.getItems().setAll(((SpreadsheetCell)CellView.this.getItem()).getPopupItems());
            menu.show(CellView.this, Side.BOTTOM, 0.0, 0.0);
         }
      }
   };
   private final WeakEventHandler weakActionhandler = new WeakEventHandler(this.actionEventHandler);
   private ChangeListener<Parent> parentListener = new ChangeListener<Parent>() {
      public void changed(ObservableValue<? extends Parent> observable, Parent oldValue, Parent newParent) {
         if (newParent == null && CellView.this.getGraphic() != null) {
            CellView.this.releaseCellGraphic();
         }
      }
   };

   static TablePositionBase<?> getAnchor(Control table, TablePositionBase<?> focusedCell) {
      return hasAnchor(table) ? (TablePositionBase)table.getProperties().get("table.anchor") : focusedCell;
   }

   static boolean hasAnchor(Control table) {
      return table.getProperties().get("table.anchor") != null;
   }

   static void setAnchor(Control table, TablePositionBase anchor) {
      if (table != null && anchor == null) {
         removeAnchor(table);
      } else {
         table.getProperties().put("table.anchor", anchor);
      }
   }

   static void removeAnchor(Control table) {
      table.getProperties().remove("table.anchor");
   }

   public CellView(SpreadsheetHandle handle) {
      this.handle = handle;
      this.addEventHandler(MouseEvent.DRAG_DETECTED, new WeakEventHandler(this.startFullDragEventHandler));
      this.setOnMouseDragEntered(new WeakEventHandler(this.dragMouseEventHandler));
      this.itemProperty().addListener(this.itemChangeListener);
   }

   public void startEdit() {
      if (this.getParent() == null) {
         this.updateTableView(null);
         this.updateTableRow(null);
         this.updateTableColumn(null);
      } else if (!this.isEditable()) {
         this.getTableView().edit(-1, null);
      } else {
         int column = this.getTableView().getColumns().indexOf(this.getTableColumn());
         int row = this.getIndex();
         SpreadsheetView spv = this.handle.getView();
         SpreadsheetView.SpanType type = spv.getSpanType(row, column);
         if (type == SpreadsheetView.SpanType.NORMAL_CELL || type == SpreadsheetView.SpanType.ROW_VISIBLE) {
            if (!this.getTableRow().isManaged()) {
               return;
            }

            GridCellEditor editor = this.getEditor((SpreadsheetCell)this.getItem(), spv);
            if (editor != null) {
               super.startEdit();
               this.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
               editor.startEdit();
            } else {
               this.getTableView().edit(-1, null);
            }
         }
      }
   }

   Filter getFilter() {
      Filter filter = this.getItem() != null && ((SpreadsheetCell)this.getItem()).getColumn() < this.handle.getView().getColumns().size()
         ? ((SpreadsheetColumn)this.handle.getView().getColumns().get(((SpreadsheetCell)this.getItem()).getColumn())).getFilter()
         : null;
      if (filter != null && ((SpreadsheetCell)this.getItem()).getRowSpan() > 1) {
         int rowSpan = this.handle.getView().getRowSpan((SpreadsheetCell)this.getItem(), this.getIndex());
         int row = ((SpreadsheetCell)this.getItem()).getRow();

         for (int i = row; i < row + rowSpan; i++) {
            if (this.handle.getView().getFilteredRow() == this.handle.getView().getModelRow(i)) {
               return filter;
            }
         }

         return null;
      } else {
         return filter != null && this.handle.getView().getFilteredRow() == this.handle.getView().getModelRow(this.getIndex()) ? filter : null;
      }
   }

   public void commitEdit(SpreadsheetCell newValue) {
      FadeTransition fadeTransition = new FadeTransition(FADE_DURATION, this);
      fadeTransition.setFromValue(0.0);
      fadeTransition.setToValue(1.0);
      fadeTransition.play();
      if (this.isEditing()) {
         super.commitEdit(newValue);
         this.setContentDisplay(ContentDisplay.LEFT);
         this.updateItem(newValue, false);
         if (this.getTableView() != null) {
            this.getTableView().requestFocus();
         }
      }
   }

   public void cancelEdit() {
      if (this.isEditing()) {
         super.cancelEdit();
         this.setContentDisplay(ContentDisplay.LEFT);
         this.updateItem((SpreadsheetCell)this.getItem(), false);
         GridCellEditor editor = this.handle.getCellsViewSkin().getSpreadsheetCellEditorImpl();
         if (editor.isEditing()) {
            editor.endEdit(false);
         }

         if (this.getTableView() != null) {
            this.getTableView().requestFocus();
         }
      }
   }

   public void updateItem(SpreadsheetCell item, boolean empty) {
      boolean emptyRow = this.getTableView().getItems().size() < this.getIndex() + 1;
      if (!this.isEditing()) {
         super.updateItem(item, empty && emptyRow);
      }

      if (empty && this.isSelected()) {
         this.updateSelected(false);
      }

      if (empty && emptyRow) {
         this.textProperty().unbind();
         this.setText(null);
         this.releaseCellGraphic();
      } else if (!this.isEditing() && item != null) {
         this.show(item);
         if (item.getGraphic() == null && !item.isCellGraphic()) {
            this.setGraphic(null);
         }
      }
   }

   public void releaseCellGraphic() {
      CellGraphicFactory browserImpl = this.handle.getView().getCellGraphicFactory();
      if (browserImpl != null && this.getGraphic() != null && browserImpl.getType().isAssignableFrom(this.getGraphic().getClass())) {
         browserImpl.setUnusedNode(this.getGraphic());
         this.setGraphic(null);
         this.parentProperty().removeListener(this.parentListener);
      }
   }

   protected void layoutChildren() {
      super.layoutChildren();
      if (this.dirtyStyle && ((SpreadsheetCell)this.getItem()).isCellGraphic()) {
         CellGraphicFactory browserImpl = this.handle.getView().getCellGraphicFactory();
         if (browserImpl != null) {
            Node node = this.getGraphic() != null && browserImpl.getType().isAssignableFrom(this.getGraphic().getClass()) ? this.getGraphic() : null;
            browserImpl.loadStyle(node, (SpreadsheetCell)this.getItem(), this.getFont(), this.getTextFill(), this.getAlignment(), this.getBackground());
            this.dirtyStyle = false;
         }
      }
   }

   public void show(SpreadsheetCell cell) {
      this.textProperty().bind(cell.textProperty());
      this.setCellGraphic(cell);
      Optional<String> tooltipText = cell.getTooltip();
      String trimTooltip = tooltipText.isPresent() ? tooltipText.get().trim() : null;
      if (trimTooltip != null && !trimTooltip.isEmpty()) {
         Tooltip localTooltip = this.getAvailableTooltip();
         if (localTooltip != null) {
            if (!Objects.equals(localTooltip.getText(), trimTooltip)) {
               this.getTooltip().setText(trimTooltip);
            }
         } else {
            getValue(() -> {
               Tooltip newTooltip = new Tooltip(tooltipText.get());
               newTooltip.setWrapText(true);
               newTooltip.setMaxWidth(400.0);
               this.setTooltip(newTooltip);
            });
         }
      } else {
         if (this.getTooltip() != null) {
            this.tooltip = this.getTooltip();
         }

         this.setTooltip(null);
      }

      this.setWrapText(cell.isWrapText());
      this.setEditable(cell.hasPopup() ? false : cell.isEditable());
      if (cell.hasPopup()) {
         this.setOnMouseClicked(this.weakActionhandler);
         this.setCursor(Cursor.HAND);
      } else {
         this.setOnMouseClicked(null);
         this.setCursor(Cursor.DEFAULT);
      }

      if (cell.getCellType().acceptDrop()) {
         this.setOnDragOver(this.getDragOverHandler());
         this.setOnDragDropped(this.getDragDropHandler());
      } else {
         this.setOnDragOver(null);
         this.setOnDragDropped(null);
      }
   }

   private Tooltip getAvailableTooltip() {
      if (this.getTooltip() != null) {
         return this.getTooltip();
      } else if (this.tooltip != null) {
         this.setTooltip(this.tooltip);
         return this.tooltip;
      } else {
         return null;
      }
   }

   public boolean isOriginalCell() {
      return this.handle.getView().getReverseRowSpan((SpreadsheetCell)this.getItem(), this.getIndex()) <= 1;
   }

   private void setBrowserGraphic(SpreadsheetCell item) {
      CellGraphicFactory browserImpl = this.handle.getView().getCellGraphicFactory();
      if (browserImpl != null) {
         this.textProperty().unbind();
         this.setText(null);
         if (this.getGraphic() != null && browserImpl.getType().isAssignableFrom(this.getGraphic().getClass())) {
            browserImpl.load(this.getGraphic(), item);
         } else {
            this.dirtyStyle = true;
            this.setGraphic(browserImpl.getNode(item));
            if (!this.handle.getView().getFixedRows().contains(this.getIndex()) && !this.handle.getView().getFixedColumns().contains(item.getColumn())) {
               this.parentProperty().addListener(this.parentListener);
            }
         }
      }
   }

   private void setCellGraphic(SpreadsheetCell item) {
      if (!this.isEditing()) {
         if (item.isCellGraphic() && item.getItem() != null) {
            this.setBrowserGraphic(item);
         } else {
            if (this.getGraphic() != null) {
               this.releaseCellGraphic();
            }

            Node graphic = item.getGraphic();
            if (graphic != null) {
               if (graphic instanceof ImageView) {
                  ImageView image = (ImageView)graphic;
                  image.setCache(true);
                  image.setPreserveRatio(true);
                  image.setSmooth(true);
                  if (image.getImage() != null) {
                     image.fitHeightProperty()
                        .bind(
                           new When(this.heightProperty().greaterThan(image.getImage().getHeight()))
                              .then(image.getImage().getHeight())
                              .otherwise(this.heightProperty())
                        );
                     image.fitWidthProperty()
                        .bind(
                           new When(this.widthProperty().greaterThan(image.getImage().getWidth()))
                              .then(image.getImage().getWidth())
                              .otherwise(this.widthProperty())
                        );
                  }
               } else if (graphic instanceof Region && item.getItem() == null) {
                  Region region = (Region)graphic;
                  region.minHeightProperty().bind(this.heightProperty());
                  region.minWidthProperty().bind(this.widthProperty());
               }

               this.setGraphic(graphic);
               if (!this.getChildren().contains(graphic)) {
                  this.getChildren().add(graphic);
               }
            } else {
               this.setGraphic(null);
            }
         }
      }
   }

   private GridCellEditor getEditor(SpreadsheetCell cell, SpreadsheetView spv) {
      SpreadsheetCellType<?> cellType = cell.getCellType();
      Optional<SpreadsheetCellEditor> cellEditor = spv.getEditor(cellType);
      if (cellEditor.isPresent()) {
         GridCellEditor editor = this.handle.getCellsViewSkin().getSpreadsheetCellEditorImpl();
         if (editor.isEditing()) {
            if (editor.getModelCell() != null) {
               StringBuilder builder = new StringBuilder();
               builder.append("The cell at row ")
                  .append(editor.getModelCell().getRow())
                  .append(" and column ")
                  .append(editor.getModelCell().getColumn())
                  .append(" was in edition and cell at row ")
                  .append(cell.getRow())
                  .append(" and column ")
                  .append(cell.getColumn())
                  .append(" requested edition. This situation should not happen as the previous cell should not be in edition.");
               Logger.getLogger("root").warning(builder.toString());
            }

            editor.endEdit(false);
         }

         editor.updateSpreadsheetCell(this);
         editor.updateDataCell(cell);
         editor.updateSpreadsheetCellEditor(cellEditor.get());
         return editor;
      } else {
         return null;
      }
   }

   private EventHandler<DragEvent> getDragOverHandler() {
      if (this.dragOverHandler == null) {
         this.dragOverHandler = new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
               Dragboard db = event.getDragboard();
               if (db.hasFiles()) {
                  event.acceptTransferModes(TransferMode.ANY);
               } else {
                  event.consume();
               }
            }
         };
      }

      return this.dragOverHandler;
   }

   private EventHandler<DragEvent> getDragDropHandler() {
      if (this.dragDropHandler == null) {
         this.dragDropHandler = new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
               Dragboard db = event.getDragboard();
               boolean success = false;
               if (db.hasFiles()
                  && db.getFiles().size() == 1
                  && ((SpreadsheetCell)CellView.this.getItem())
                     .getCellType()
                     .match(db.getFiles().get(0), ((SpreadsheetCell)CellView.this.getItem()).getOptionsForEditor())) {
                  CellView.this.handle
                     .getView()
                     .getGrid()
                     .setCellValue(
                        ((SpreadsheetCell)CellView.this.getItem()).getRow(),
                        ((SpreadsheetCell)CellView.this.getItem()).getColumn(),
                        ((SpreadsheetCell)CellView.this.getItem()).getCellType().convertValue(db.getFiles().get(0))
                     );
                  success = true;
               }

               event.setDropCompleted(success);
               event.consume();
            }
         };
      }

      return this.dragDropHandler;
   }

   private void dragSelect(MouseEvent e) {
      if (this.contains(e.getX(), e.getY())) {
         TableView<ObservableList<SpreadsheetCell>> tableView = this.getTableView();
         if (tableView != null) {
            int count = tableView.getItems().size();
            if (this.getIndex() < count) {
               TableViewSelectionModel<ObservableList<SpreadsheetCell>> sm = tableView.getSelectionModel();
               if (sm != null) {
                  int row = this.getIndex();
                  int column = tableView.getVisibleLeafIndex(this.getTableColumn());
                  SpreadsheetCell cell = (SpreadsheetCell)this.getItem();
                  int rowCell = this.getIndex() + this.handle.getView().getRowSpan(cell, this.getIndex()) - 1;
                  int columnCell = this.handle.getView().getViewColumn(cell.getColumn()) + this.handle.getView().getColumnSpan(cell) - 1;
                  TableViewFocusModel<?> fm = tableView.getFocusModel();
                  if (fm != null) {
                     TablePositionBase<?> focusedCell = fm.getFocusedCell();
                     MouseButton button = e.getButton();
                     if (button == MouseButton.PRIMARY) {
                        TablePositionBase<?> anchor = getAnchor(tableView, focusedCell);
                        int minRow = Math.min(anchor.getRow(), row);
                        minRow = Math.min(minRow, rowCell);
                        int maxRow = Math.max(anchor.getRow(), row);
                        maxRow = Math.max(maxRow, rowCell);
                        int minColumn = Math.min(anchor.getColumn(), column);
                        minColumn = Math.min(minColumn, columnCell);
                        int maxColumn = Math.max(anchor.getColumn(), column);
                        maxColumn = Math.max(maxColumn, columnCell);
                        if (!e.isShortcutDown()) {
                           sm.clearSelection();
                        }

                        if (minColumn != -1 && maxColumn != -1) {
                           sm.selectRange(minRow, tableView.getVisibleLeafColumn(minColumn), maxRow, tableView.getVisibleLeafColumn(maxColumn));
                        }

                        setAnchor(tableView, anchor);
                     }
                  }
               }
            }
         }
      }
   }

   public static void getValue(Runnable runnable) {
      if (Platform.isFxApplicationThread()) {
         runnable.run();
      } else {
         Platform.runLater(runnable);
      }
   }

   protected Skin<?> createDefaultSkin() {
      return new CellViewSkin(this);
   }

   private void initStyleListener() {
      if (this.styleListener == null) {
         this.styleListener = (observable, oldValue, newValue) -> {
            this.styleProperty().set(newValue);
            if (this.getItem() != null && ((SpreadsheetCell)this.getItem()).isCellGraphic()) {
               this.dirtyStyle = true;
            }
         };
      }

      this.weakStyleListener = new WeakChangeListener(this.styleListener);
   }
}
