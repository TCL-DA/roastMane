package javafx.scene.control.skin;

import java.util.ArrayList;
import java.util.Map;
import java.util.WeakHashMap;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.WeakListChangeListener;
import javafx.event.EventHandler;
import javafx.geometry.NodeOrientation;
import javafx.scene.Cursor;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeTableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Callback;

public class NestedTableColumnHeader extends TableColumnHeader {
   static final String DEFAULT_STYLE_CLASS = "nested-column-header";
   private static final int DRAG_RECT_WIDTH = 4;
   private static final String TABLE_COLUMN_KEY = "TableColumn";
   private static final String TABLE_COLUMN_HEADER_KEY = "TableColumnHeader";
   private ObservableList<? extends TableColumnBase> columns;
   private TableColumnHeader label;
   private ObservableList<TableColumnHeader> columnHeaders;
   private ObservableList<TableColumnHeader> unmodifiableColumnHeaders;
   private double lastX = 0.0;
   private double dragAnchorX = 0.0;
   private Map<TableColumnBase<?, ?>, Rectangle> dragRects = new WeakHashMap<>();
   boolean updateColumns = true;
   private final ListChangeListener<TableColumnBase> columnsListener = var1x -> this.setHeadersNeedUpdate();
   private final WeakListChangeListener weakColumnsListener = new WeakListChangeListener(this.columnsListener);
   private static final EventHandler<MouseEvent> rectMousePressed = var0 -> {
      Rectangle var1 = (Rectangle)var0.getSource();
      TableColumnBase var2 = (TableColumnBase)var1.getProperties().get("TableColumn");
      NestedTableColumnHeader var3 = (NestedTableColumnHeader)var1.getProperties().get("TableColumnHeader");
      if (var3.isColumnResizingEnabled()) {
         if (!var3.getTableHeaderRow().columnDragLock) {
            if (!var0.isConsumed()) {
               var0.consume();
               if (var0.getClickCount() == 2 && var0.isPrimaryButtonDown()) {
                  TableHeaderRow var7 = var3.getTableHeaderRow();
                  TableColumnHeader var8 = var7.getColumnHeaderFor(var2);
                  if (var8 != null) {
                     var8.resizeColumnToFitContent(-1);
                  }
               } else {
                  Rectangle var4 = (Rectangle)var0.getSource();
                  double var5 = var3.getTableHeaderRow().sceneToLocal(var4.localToScene(var4.getBoundsInLocal())).getMinX() + 2.0;
                  var3.dragAnchorX = var0.getSceneX();
                  var3.columnResizingStarted(var5);
               }
            }
         }
      }
   };
   private static final EventHandler<MouseEvent> rectMouseDragged = var0 -> {
      Rectangle var1 = (Rectangle)var0.getSource();
      TableColumnBase var2 = (TableColumnBase)var1.getProperties().get("TableColumn");
      NestedTableColumnHeader var3 = (NestedTableColumnHeader)var1.getProperties().get("TableColumnHeader");
      if (var3.isColumnResizingEnabled()) {
         if (!var3.getTableHeaderRow().columnDragLock) {
            if (!var0.isConsumed()) {
               var0.consume();
               var3.columnResizing(var2, var0);
            }
         }
      }
   };
   private static final EventHandler<MouseEvent> rectMouseReleased = var0 -> {
      Rectangle var1 = (Rectangle)var0.getSource();
      TableColumnBase var2 = (TableColumnBase)var1.getProperties().get("TableColumn");
      NestedTableColumnHeader var3 = (NestedTableColumnHeader)var1.getProperties().get("TableColumnHeader");
      if (var3.isColumnResizingEnabled()) {
         if (!var3.getTableHeaderRow().columnDragLock) {
            if (!var0.isConsumed()) {
               var0.consume();
               var3.columnResizingComplete(var2, var0);
            }
         }
      }
   };
   private static final EventHandler<MouseEvent> rectCursorChangeListener = var0 -> {
      Rectangle var1 = (Rectangle)var0.getSource();
      TableColumnBase var2 = (TableColumnBase)var1.getProperties().get("TableColumn");
      NestedTableColumnHeader var3 = (NestedTableColumnHeader)var1.getProperties().get("TableColumnHeader");
      if (!var3.getTableHeaderRow().columnDragLock) {
         if (var3.getCursor() == null) {
            var1.setCursor(var3.isColumnResizingEnabled() && var1.isHover() && var2.isResizable() ? Cursor.H_RESIZE : null);
         }
      }
   };

   public NestedTableColumnHeader(TableColumnBase var1) {
      super(var1);
      this.setFocusTraversable(false);
      this.label = this.createTableColumnHeader(this.getTableColumn());
      this.label.setTableHeaderRow(this.getTableHeaderRow());
      this.label.setParentHeader(this.getParentHeader());
      this.label.setNestedColumnHeader(this);
      if (this.getTableColumn() != null) {
         this.changeListenerHandler
            .registerChangeListener(
               this.getTableColumn().textProperty(),
               var1x -> this.label.setVisible(this.getTableColumn().getText() != null && !this.getTableColumn().getText().isEmpty())
            );
      }
   }

   @Override
   void dispose() {
      super.dispose();
      if (this.label != null) {
         this.label.dispose();
      }

      if (this.getColumns() != null) {
         this.getColumns().removeListener(this.weakColumnsListener);
      }

      for (int var1 = 0; var1 < this.getColumnHeaders().size(); var1++) {
         TableColumnHeader var2 = (TableColumnHeader)this.getColumnHeaders().get(var1);
         var2.dispose();
      }

      for (Rectangle var4 : this.dragRects.values()) {
         if (var4 != null) {
            var4.visibleProperty().unbind();
         }
      }

      this.dragRects.clear();
      this.getChildren().clear();
      this.changeListenerHandler.dispose();
   }

   public final ObservableList<TableColumnHeader> getColumnHeaders() {
      if (this.columnHeaders == null) {
         this.columnHeaders = FXCollections.observableArrayList();
         this.unmodifiableColumnHeaders = FXCollections.unmodifiableObservableList(this.columnHeaders);
      }

      return this.unmodifiableColumnHeaders;
   }

   @Override
   protected void layoutChildren() {
      double var1 = this.getWidth() - this.snappedLeftInset() - this.snappedRightInset();
      double var3 = this.getHeight() - this.snappedTopInset() - this.snappedBottomInset();
      int var5 = 0;
      if (this.label.isVisible() && this.getTableColumn() != null) {
         var5 = (int)this.label.prefHeight(-1.0);
         this.label.resize(var1, (double)var5);
         this.label.relocate(this.snappedLeftInset(), this.snappedTopInset());
      }

      double var6 = this.snappedLeftInset();
      double var8 = this.snapSizeY(var3 - (double)var5);
      int var10 = 0;

      for (int var11 = this.getColumnHeaders().size(); var10 < var11; var10++) {
         TableColumnHeader var12 = (TableColumnHeader)this.getColumnHeaders().get(var10);
         if (var12.isVisible()) {
            double var13 = var12.prefWidth(var8);
            var12.resize(var13, var8);
            var12.relocate(var6, (double)var5 + this.snappedTopInset());
            var6 += var13;
            Rectangle var15 = this.dragRects.get(var12.getTableColumn());
            if (var15 != null) {
               var15.setHeight(var12.getDragRectHeight());
               var15.relocate(var6 - 2.0, this.snappedTopInset() + (double)var5);
            }
         }
      }
   }

   @Override
   protected double computePrefWidth(double var1) {
      this.checkState();
      double var3 = 0.0;
      if (this.getColumns() != null) {
         for (TableColumnHeader var6 : this.getColumnHeaders()) {
            if (var6.isVisible()) {
               var3 += var6.computePrefWidth(var1);
            }
         }
      }

      return var3;
   }

   @Override
   protected double computePrefHeight(double var1) {
      this.checkState();
      double var3 = 0.0;
      if (this.getColumnHeaders() != null) {
         for (TableColumnHeader var6 : this.getColumnHeaders()) {
            var3 = Math.max(var3, var6.prefHeight(-1.0));
         }
      }

      double var7 = 0.0;
      if (this.label.isVisible() && this.getTableColumn() != null) {
         var7 = this.label.prefHeight(-1.0);
      }

      return var3 + var7 + this.snappedTopInset() + this.snappedBottomInset();
   }

   protected TableColumnHeader createTableColumnHeader(TableColumnBase var1) {
      return (TableColumnHeader)(var1 != null && !var1.getColumns().isEmpty() && var1 != this.getTableColumn()
         ? new NestedTableColumnHeader(var1)
         : new TableColumnHeader(var1));
   }

   @Override
   void initStyleClasses() {
      this.getStyleClass().setAll(new String[]{"nested-column-header"});
      this.installTableColumnStyleClassListener();
   }

   @Override
   void setTableHeaderRow(TableHeaderRow var1) {
      super.setTableHeaderRow(var1);
      if (this.getTableSkin() != null) {
         this.changeListenerHandler.registerChangeListener(TableSkinUtils.columnResizePolicyProperty(this.getTableSkin()), var1x -> this.updateContent());
      }

      this.label.setTableHeaderRow(var1);

      for (TableColumnHeader var3 : this.getColumnHeaders()) {
         var3.setTableHeaderRow(var1);
      }
   }

   @Override
   void setParentHeader(NestedTableColumnHeader var1) {
      super.setParentHeader(var1);
      this.label.setParentHeader(var1);
   }

   ObservableList<? extends TableColumnBase> getColumns() {
      return this.columns;
   }

   void setColumns(ObservableList<? extends TableColumnBase> var1) {
      if (this.columns != null) {
         this.columns.removeListener(this.weakColumnsListener);
      }

      this.columns = var1;
      if (this.columns != null) {
         this.columns.addListener(this.weakColumnsListener);
      }
   }

   void updateTableColumnHeaders() {
      if (this.getTableColumn() == null && this.getTableSkin() != null) {
         this.setColumns(TableSkinUtils.getColumns(this.getTableSkin()));
      } else if (this.getTableColumn() != null) {
         this.setColumns(this.getTableColumn().getColumns());
      }

      if (this.getColumns().isEmpty()) {
         for (int var1 = 0; var1 < this.getColumnHeaders().size(); var1++) {
            TableColumnHeader var2 = (TableColumnHeader)this.getColumnHeaders().get(var1);
            var2.dispose();
         }

         NestedTableColumnHeader var8 = this.getParentHeader();
         if (var8 != null) {
            ObservableList var11 = var8.getColumnHeaders();
            int var3 = var11.indexOf(this);
            if (var3 >= 0 && var3 < var11.size()) {
               var11.set(var3, this.createColumnHeader(this.getTableColumn()));
            }
         } else {
            this.columnHeaders.clear();
         }
      } else {
         ArrayList var9 = new ArrayList(this.getColumnHeaders());
         ArrayList var12 = new ArrayList();

         for (int var14 = 0; var14 < this.getColumns().size(); var14++) {
            TableColumnBase var4 = (TableColumnBase)this.getColumns().get(var14);
            if (var4 != null && var4.isVisible()) {
               boolean var5 = false;

               for (int var6 = 0; var6 < var9.size(); var6++) {
                  TableColumnHeader var7 = (TableColumnHeader)var9.get(var6);
                  if (var7.represents(var4)) {
                     var12.add(var7);
                     var5 = true;
                     break;
                  }
               }

               if (!var5) {
                  var12.add(this.createColumnHeader(var4));
               }
            }
         }

         this.columnHeaders.setAll(var12);
         var9.removeAll(var12);

         for (int var15 = 0; var15 < var9.size(); var15++) {
            ((TableColumnHeader)var9.get(var15)).dispose();
         }
      }

      this.updateContent();

      for (TableColumnHeader var13 : this.getColumnHeaders()) {
         var13.applyCss();
      }
   }

   @Override
   boolean represents(TableColumnBase<?, ?> var1) {
      if (var1.getColumns().isEmpty()) {
         return false;
      } else if (var1 != this.getTableColumn()) {
         return false;
      } else {
         int var2 = var1.getColumns().size();
         int var3 = this.getColumnHeaders().size();
         if (var2 != var3) {
            return false;
         } else {
            for (int var4 = 0; var4 < var2; var4++) {
               TableColumnBase var5 = (TableColumnBase)var1.getColumns().get(var4);
               TableColumnHeader var6 = (TableColumnHeader)this.getColumnHeaders().get(var4);
               if (!var6.represents(var5)) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   @Override
   double getDragRectHeight() {
      return this.label.prefHeight(-1.0);
   }

   void setHeadersNeedUpdate() {
      this.updateColumns = true;

      for (int var1 = 0; var1 < this.getColumnHeaders().size(); var1++) {
         TableColumnHeader var2 = (TableColumnHeader)this.getColumnHeaders().get(var1);
         if (var2 instanceof NestedTableColumnHeader) {
            ((NestedTableColumnHeader)var2).setHeadersNeedUpdate();
         }
      }

      this.requestLayout();
   }

   private void updateContent() {
      ArrayList var1 = new ArrayList();
      var1.add(this.label);
      var1.addAll(this.getColumnHeaders());
      if (this.isColumnResizingEnabled()) {
         this.rebuildDragRects();
         var1.addAll(this.dragRects.values());
      }

      this.getChildren().setAll(var1);
   }

   private void rebuildDragRects() {
      if (this.isColumnResizingEnabled()) {
         this.getChildren().removeAll(this.dragRects.values());

         for (Rectangle var2 : this.dragRects.values()) {
            var2.visibleProperty().unbind();
         }

         this.dragRects.clear();
         ObservableList var8 = this.getColumns();
         if (var8 != null) {
            boolean var9 = false;
            TableViewSkinBase var3 = this.getTableSkin();
            Callback var4 = (Callback)TableSkinUtils.columnResizePolicyProperty(var3).get();
            if (var4 != null) {
               var9 = var3 instanceof TableViewSkin
                  ? TableView.CONSTRAINED_RESIZE_POLICY.equals(var4)
                  : (var3 instanceof TreeTableViewSkin ? TreeTableView.CONSTRAINED_RESIZE_POLICY.equals(var4) : false);
            }

            if (!var9 || TableSkinUtils.getVisibleLeafColumns(var3).size() != 1) {
               for (int var5 = 0; var5 < var8.size() && (!var9 || var5 != this.getColumns().size() - 1); var5++) {
                  TableColumnBase var6 = (TableColumnBase)var8.get(var5);
                  Rectangle var7 = new Rectangle();
                  var7.getProperties().put("TableColumn", var6);
                  var7.getProperties().put("TableColumnHeader", this);
                  var7.setWidth(4.0);
                  var7.setHeight(this.getHeight() - this.label.getHeight());
                  var7.setFill(Color.TRANSPARENT);
                  var7.visibleProperty().bind(var6.visibleProperty().and(var6.resizableProperty()));
                  var7.setOnMousePressed(rectMousePressed);
                  var7.setOnMouseDragged(rectMouseDragged);
                  var7.setOnMouseReleased(rectMouseReleased);
                  var7.setOnMouseEntered(rectCursorChangeListener);
                  var7.setOnMouseExited(rectCursorChangeListener);
                  this.dragRects.put(var6, var7);
               }
            }
         }
      }
   }

   private void checkState() {
      if (this.updateColumns) {
         this.updateTableColumnHeaders();
         this.updateColumns = false;
      }
   }

   private TableColumnHeader createColumnHeader(TableColumnBase var1) {
      TableColumnHeader var2 = this.createTableColumnHeader(var1);
      var2.setTableHeaderRow(this.getTableHeaderRow());
      var2.setParentHeader(this);
      return var2;
   }

   private boolean isColumnResizingEnabled() {
      return true;
   }

   private void columnResizingStarted(double var1) {
      this.setCursor(Cursor.H_RESIZE);
      this.columnReorderLine.setLayoutX(var1);
   }

   private void columnResizing(TableColumnBase var1, MouseEvent var2) {
      double var3 = var2.getSceneX() - this.dragAnchorX;
      if (this.getEffectiveNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT) {
         var3 = -var3;
      }

      double var5 = var3 - this.lastX;
      boolean var7 = TableSkinUtils.resizeColumn(this.getTableSkin(), var1, var5);
      if (var7) {
         this.lastX = var3;
      }
   }

   private void columnResizingComplete(TableColumnBase var1, MouseEvent var2) {
      this.setCursor(null);
      this.columnReorderLine.setTranslateX(0.0);
      this.columnReorderLine.setLayoutX(0.0);
      this.lastX = 0.0;
   }
}
