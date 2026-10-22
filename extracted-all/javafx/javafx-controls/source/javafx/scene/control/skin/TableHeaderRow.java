package javafx.scene.control.skin;

import com.sun.javafx.scene.control.skin.resources.ControlResources;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.beans.InvalidationListener;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.WeakListChangeListener;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumnBase;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

public class TableHeaderRow extends StackPane {
   private final String MENU_SEPARATOR = ControlResources.getString("TableView.nestedColumnControlMenuSeparator");
   private final VirtualFlow flow;
   final TableViewSkinBase<?, ?, ?, ?, ?> tableSkin;
   private Map<TableColumnBase, CheckMenuItem> columnMenuItems = new HashMap<>();
   private double scrollX;
   private double tableWidth;
   private Rectangle clip;
   private TableColumnHeader reorderingRegion;
   private StackPane dragHeader;
   private final Label dragHeaderLabel = new Label();
   private Region filler;
   private Pane cornerRegion;
   private ContextMenu columnPopupMenu;
   boolean columnDragLock = false;
   private InvalidationListener tableWidthListener = var1x -> this.updateTableWidth();
   private InvalidationListener tablePaddingListener = var1x -> this.updateTableWidth();
   private ListChangeListener visibleLeafColumnsListener = var1x -> this.getRootHeader().setHeadersNeedUpdate();
   private final ListChangeListener tableColumnsListener = var1x -> {
      while (var1x.next()) {
         this.updateTableColumnListeners(var1x.getAddedSubList(), var1x.getRemoved());
      }
   };
   private final InvalidationListener columnTextListener = var1x -> {
      TableColumnBase var2x = (TableColumnBase)((StringProperty)var1x).getBean();
      CheckMenuItem var3x = this.columnMenuItems.get(var2x);
      if (var3x != null) {
         var3x.setText(this.getText(var2x.getText(), var2x));
      }
   };
   private final WeakInvalidationListener weakTableWidthListener = new WeakInvalidationListener(this.tableWidthListener);
   private final WeakInvalidationListener weakTablePaddingListener = new WeakInvalidationListener(this.tablePaddingListener);
   private final WeakListChangeListener weakVisibleLeafColumnsListener = new WeakListChangeListener(this.visibleLeafColumnsListener);
   private final WeakListChangeListener weakTableColumnsListener = new WeakListChangeListener(this.tableColumnsListener);
   private final WeakInvalidationListener weakColumnTextListener = new WeakInvalidationListener(this.columnTextListener);
   private BooleanProperty reordering = new SimpleBooleanProperty(this, "reordering", false) {
      protected void invalidated() {
         TableColumnHeader var1 = TableHeaderRow.this.getReorderingRegion();
         if (var1 != null) {
            double var2 = var1.getNestedColumnHeader() != null
               ? var1.getNestedColumnHeader().getHeight()
               : TableHeaderRow.this.getReorderingRegion().getHeight();
            TableHeaderRow.this.dragHeader.resize(TableHeaderRow.this.dragHeader.getWidth(), var2);
            TableHeaderRow.this.dragHeader.setTranslateY(TableHeaderRow.this.getHeight() - var2);
         }

         TableHeaderRow.this.dragHeader.setVisible(TableHeaderRow.this.isReordering());
      }
   };
   private ReadOnlyObjectWrapper<NestedTableColumnHeader> rootHeader = new ReadOnlyObjectWrapper(this, "rootHeader");

   public TableHeaderRow(TableViewSkinBase var1) {
      this.tableSkin = var1;
      this.flow = var1.flow;
      this.getStyleClass().setAll(new String[]{"column-header-background"});
      this.clip = new Rectangle();
      this.clip.setSmooth(false);
      this.clip.heightProperty().bind(this.heightProperty());
      this.setClip(this.clip);
      this.updateTableWidth();
      this.tableSkin.getSkinnable().widthProperty().addListener(this.weakTableWidthListener);
      this.tableSkin.getSkinnable().paddingProperty().addListener(this.weakTablePaddingListener);
      TableSkinUtils.getVisibleLeafColumns(var1).addListener(this.weakVisibleLeafColumnsListener);
      this.columnPopupMenu = new ContextMenu();
      this.updateTableColumnListeners(TableSkinUtils.getColumns(this.tableSkin), Collections.emptyList());
      TableSkinUtils.getVisibleLeafColumns(var1).addListener(this.weakTableColumnsListener);
      TableSkinUtils.getColumns(this.tableSkin).addListener(this.weakTableColumnsListener);
      this.dragHeader = new StackPane();
      this.dragHeader.setVisible(false);
      this.dragHeader.getStyleClass().setAll(new String[]{"column-drag-header"});
      this.dragHeader.setManaged(false);
      this.dragHeader.setMouseTransparent(true);
      this.dragHeader.getChildren().add(this.dragHeaderLabel);
      NestedTableColumnHeader var2 = this.createRootHeader();
      this.setRootHeader(var2);
      var2.setFocusTraversable(false);
      var2.setTableHeaderRow(this);
      this.filler = new Region();
      this.filler.getStyleClass().setAll(new String[]{"filler"});
      this.setOnMousePressed(var1x -> var1.getSkinnable().requestFocus());
      final StackPane var3 = new StackPane();
      var3.setSnapToPixel(false);
      var3.getStyleClass().setAll(new String[]{"show-hide-column-image"});
      this.cornerRegion = new StackPane() {
         protected void layoutChildren() {
            double var1 = var3.snappedLeftInset() + var3.snappedRightInset();
            double var3x = var3.snappedTopInset() + var3.snappedBottomInset();
            var3.resize(var1, var3x);
            this.positionInArea(var3, 0.0, 0.0, this.getWidth(), this.getHeight() - 3.0, 0.0, HPos.CENTER, VPos.CENTER);
         }
      };
      this.cornerRegion.getStyleClass().setAll(new String[]{"show-hide-columns-button"});
      this.cornerRegion.getChildren().addAll(new Node[]{var3});
      BooleanProperty var4 = TableSkinUtils.tableMenuButtonVisibleProperty(var1);
      if (var4 != null) {
         this.cornerRegion.visibleProperty().bind(var4);
      }

      this.cornerRegion.setOnMousePressed(var1x -> {
         this.columnPopupMenu.show(this.cornerRegion, Side.BOTTOM, 0.0, 0.0);
         var1x.consume();
      });
      this.getChildren().addAll(new Node[]{this.filler, var2, this.cornerRegion, this.dragHeader});
   }

   public final void setReordering(boolean var1) {
      this.reordering.set(var1);
   }

   public final boolean isReordering() {
      return this.reordering.get();
   }

   public final BooleanProperty reorderingProperty() {
      return this.reordering;
   }

   private final ReadOnlyObjectProperty<NestedTableColumnHeader> rootHeaderProperty() {
      return this.rootHeader.getReadOnlyProperty();
   }

   public final NestedTableColumnHeader getRootHeader() {
      return (NestedTableColumnHeader)this.rootHeader.get();
   }

   private final void setRootHeader(NestedTableColumnHeader var1) {
      this.rootHeader.set(var1);
   }

   protected void layoutChildren() {
      double var1 = this.scrollX;
      double var3 = this.snapSizeX(this.getRootHeader().prefWidth(-1.0));
      double var5 = this.getHeight() - this.snappedTopInset() - this.snappedBottomInset();
      double var7 = this.snapSizeX(this.flow.getVbar().prefWidth(-1.0));
      this.getRootHeader().resizeRelocate(var1, this.snappedTopInset(), var3, var5);
      Control var9 = this.tableSkin.getSkinnable();
      if (var9 != null) {
         BooleanProperty var10 = TableSkinUtils.tableMenuButtonVisibleProperty(this.tableSkin);
         double var11 = var9.snappedLeftInset() + var9.snappedRightInset();
         double var13 = this.tableWidth - var3 + this.filler.getInsets().getLeft() - var11;
         var13 -= var10 != null && var10.get() ? var7 : 0.0;
         this.filler.setVisible(var13 > 0.0);
         if (var13 > 0.0) {
            this.filler.resizeRelocate(var1 + var3, this.snappedTopInset(), var13, var5);
         }

         this.cornerRegion.resizeRelocate(this.tableWidth - var7, this.snappedTopInset(), var7, var5);
      }
   }

   protected double computePrefWidth(double var1) {
      return this.getRootHeader().prefWidth(var1);
   }

   protected double computeMinHeight(double var1) {
      return this.computePrefHeight(var1);
   }

   protected double computePrefHeight(double var1) {
      double var3 = this.getRootHeader().prefHeight(var1);
      var3 = var3 == 0.0 ? 24.0 : var3;
      return this.snappedTopInset() + var3 + this.snappedBottomInset();
   }

   protected void updateScrollX() {
      this.scrollX = this.flow.getHbar().isVisible() ? -this.flow.getHbar().getValue() : 0.0;
      this.requestLayout();
      this.layout();
   }

   protected void updateTableWidth() {
      Control var1 = this.tableSkin.getSkinnable();
      if (var1 == null) {
         this.tableWidth = 0.0;
      } else {
         Insets var2 = var1.getInsets() == null ? Insets.EMPTY : var1.getInsets();
         double var3 = this.snapSizeX(var2.getLeft()) + this.snapSizeX(var2.getRight());
         this.tableWidth = this.snapSizeX(var1.getWidth()) - var3;
      }

      this.clip.setWidth(this.tableWidth);
   }

   protected NestedTableColumnHeader createRootHeader() {
      return new NestedTableColumnHeader(null);
   }

   protected TableColumnHeader getReorderingRegion() {
      return this.reorderingRegion;
   }

   void setReorderingColumn(TableColumnBase var1) {
      this.dragHeaderLabel.setText(var1 == null ? "" : var1.getText());
   }

   protected void setReorderingRegion(TableColumnHeader var1) {
      this.reorderingRegion = var1;
      if (var1 != null) {
         this.dragHeader.resize(var1.getWidth(), this.dragHeader.getHeight());
      }
   }

   void setDragHeaderX(double var1) {
      this.dragHeader.setTranslateX(var1);
   }

   TableColumnHeader getColumnHeaderFor(TableColumnBase<?, ?> var1) {
      if (var1 == null) {
         return null;
      } else {
         ArrayList var2 = new ArrayList();
         var2.add(var1);

         for (TableColumnBase var3 = var1.getParentColumn(); var3 != null; var3 = var3.getParentColumn()) {
            var2.add(0, var3);
         }

         Object var4 = this.getRootHeader();

         for (int var5 = 0; var5 < var2.size(); var5++) {
            TableColumnBase var6 = (TableColumnBase)var2.get(var5);
            var4 = this.getColumnHeaderFor(var6, (TableColumnHeader)var4);
         }

         return (TableColumnHeader)var4;
      }
   }

   private TableColumnHeader getColumnHeaderFor(TableColumnBase<?, ?> var1, TableColumnHeader var2) {
      if (var2 instanceof NestedTableColumnHeader) {
         ObservableList var3 = ((NestedTableColumnHeader)var2).getColumnHeaders();

         for (int var4 = 0; var4 < var3.size(); var4++) {
            TableColumnHeader var5 = (TableColumnHeader)var3.get(var4);
            if (var5.getTableColumn() == var1) {
               return var5;
            }
         }
      }

      return null;
   }

   private void updateTableColumnListeners(List<? extends TableColumnBase<?, ?>> var1, List<? extends TableColumnBase<?, ?>> var2) {
      for (TableColumnBase var4 : var2) {
         this.remove(var4);
      }

      this.rebuildColumnMenu();
   }

   private void remove(TableColumnBase<?, ?> var1) {
      if (var1 != null) {
         CheckMenuItem var2 = this.columnMenuItems.remove(var1);
         if (var2 != null) {
            var1.textProperty().removeListener(this.weakColumnTextListener);
            var2.selectedProperty().unbindBidirectional(var1.visibleProperty());
            this.columnPopupMenu.getItems().remove(var2);
         }

         if (!var1.getColumns().isEmpty()) {
            for (TableColumnBase var4 : var1.getColumns()) {
               this.remove(var4);
            }
         }
      }
   }

   private void rebuildColumnMenu() {
      this.columnPopupMenu.getItems().clear();

      for (TableColumnBase var2 : TableSkinUtils.getColumns(this.tableSkin)) {
         if (var2.getColumns().isEmpty()) {
            this.createMenuItem(var2);
         } else {
            for (TableColumnBase var5 : this.getLeafColumns(var2)) {
               this.createMenuItem(var5);
            }
         }
      }
   }

   private List<TableColumnBase<?, ?>> getLeafColumns(TableColumnBase<?, ?> var1) {
      ArrayList var2 = new ArrayList();

      for (TableColumnBase var4 : var1.getColumns()) {
         if (var4.getColumns().isEmpty()) {
            var2.add(var4);
         } else {
            var2.addAll(this.getLeafColumns(var4));
         }
      }

      return var2;
   }

   private void createMenuItem(TableColumnBase<?, ?> var1) {
      CheckMenuItem var2 = this.columnMenuItems.get(var1);
      if (var2 == null) {
         var2 = new CheckMenuItem();
         this.columnMenuItems.put(var1, var2);
      }

      var2.setText(this.getText(var1.getText(), var1));
      var1.textProperty().addListener(this.weakColumnTextListener);
      var2.setDisable(var1.visibleProperty().isBound());
      var2.setSelected(var1.isVisible());
      CheckMenuItem var3 = var2;
      var2.selectedProperty().addListener(var2x -> {
         if (!var1.visibleProperty().isBound()) {
            var1.setVisible(var3.isSelected());
         }
      });
      var1.visibleProperty().addListener(var2x -> var3.setSelected(var1.isVisible()));
      this.columnPopupMenu.getItems().add(var2);
   }

   private String getText(String var1, TableColumnBase var2) {
      String var3 = var1;

      for (TableColumnBase var4 = var2.getParentColumn(); var4 != null; var4 = var4.getParentColumn()) {
         if (this.isColumnVisibleInHeader(var4, TableSkinUtils.getColumns(this.tableSkin))) {
            var3 = var4.getText() + this.MENU_SEPARATOR + var3;
         }
      }

      return var3;
   }

   private boolean isColumnVisibleInHeader(TableColumnBase var1, List var2) {
      if (var1 == null) {
         return false;
      } else {
         for (int var3 = 0; var3 < var2.size(); var3++) {
            TableColumnBase var4 = (TableColumnBase)var2.get(var3);
            if (var1.equals(var4)) {
               return true;
            }

            if (!var4.getColumns().isEmpty()) {
               boolean var5 = this.isColumnVisibleInHeader(var1, var4.getColumns());
               if (var5) {
                  return true;
               }
            }
         }

         return false;
      }
   }
}
