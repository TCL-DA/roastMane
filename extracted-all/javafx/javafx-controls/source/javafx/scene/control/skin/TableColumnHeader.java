package javafx.scene.control.skin;

import com.sun.javafx.scene.control.LambdaMultiplePropertyChangeListenerHandler;
import com.sun.javafx.scene.control.Properties;
import com.sun.javafx.scene.control.TableColumnBaseHelper;
import com.sun.javafx.scene.control.TableColumnSortTypeWrapper;
import com.sun.javafx.scene.control.TreeTableViewBackingList;
import com.sun.javafx.scene.control.skin.Utils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.WeakListChangeListener;
import javafx.css.CssMetaData;
import javafx.css.PseudoClass;
import javafx.css.Styleable;
import javafx.css.StyleableDoubleProperty;
import javafx.css.StyleableProperty;
import javafx.css.converter.SizeConverter;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.AccessibleAttribute;
import javafx.scene.AccessibleRole;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.util.Callback;

public class TableColumnHeader extends Region {
   static final String DEFAULT_STYLE_CLASS = "column-header";
   static final double DEFAULT_COLUMN_WIDTH = 80.0;
   private boolean autoSizeComplete = false;
   private double dragOffset;
   private NestedTableColumnHeader nestedColumnHeader;
   private TableHeaderRow tableHeaderRow;
   private NestedTableColumnHeader parentHeader;
   Label label;
   int sortPos = -1;
   private Region arrow;
   private Label sortOrderLabel;
   private HBox sortOrderDots;
   private Node sortArrow;
   private boolean isSortColumn;
   private boolean isSizeDirty = false;
   boolean isLastVisibleColumn = false;
   int columnIndex = -1;
   private int newColumnPos;
   Region columnReorderLine;
   final LambdaMultiplePropertyChangeListenerHandler changeListenerHandler;
   private ListChangeListener<TableColumnBase<?, ?>> sortOrderListener = var1x -> this.updateSortPosition();
   private ListChangeListener<TableColumnBase<?, ?>> visibleLeafColumnsListener = var1x -> {
      this.updateColumnIndex();
      this.updateSortPosition();
   };
   private ListChangeListener<String> styleClassListener = var1x -> {
      while (var1x.next()) {
         if (var1x.wasRemoved()) {
            this.getStyleClass().removeAll(var1x.getRemoved());
         }

         if (var1x.wasAdded()) {
            this.getStyleClass().addAll(var1x.getAddedSubList());
         }
      }
   };
   private WeakListChangeListener<TableColumnBase<?, ?>> weakSortOrderListener = new WeakListChangeListener(this.sortOrderListener);
   private final WeakListChangeListener<TableColumnBase<?, ?>> weakVisibleLeafColumnsListener = new WeakListChangeListener(this.visibleLeafColumnsListener);
   private final WeakListChangeListener<String> weakStyleClassListener = new WeakListChangeListener(this.styleClassListener);
   private static final EventHandler<MouseEvent> mousePressedHandler = var0 -> {
      TableColumnHeader var1 = (TableColumnHeader)var0.getSource();
      TableColumnBase var2 = var1.getTableColumn();
      ContextMenu var3 = var2.getContextMenu();
      if (var3 != null && var3.isShowing()) {
         var3.hide();
      }

      if (!var0.isConsumed()) {
         var0.consume();
         var1.getTableHeaderRow().columnDragLock = true;
         var1.getTableSkin().getSkinnable().requestFocus();
         if (var0.isPrimaryButtonDown() && var1.isColumnReorderingEnabled()) {
            var1.columnReorderingStarted(var0.getX());
         }
      }
   };
   private static final EventHandler<MouseEvent> mouseDraggedHandler = var0 -> {
      if (!var0.isConsumed()) {
         var0.consume();
         TableColumnHeader var1 = (TableColumnHeader)var0.getSource();
         if (var0.isPrimaryButtonDown() && var1.isColumnReorderingEnabled()) {
            var1.columnReordering(var0.getSceneX(), var0.getSceneY());
         }
      }
   };
   private static final EventHandler<MouseEvent> mouseReleasedHandler = var0 -> {
      TableColumnHeader var1 = (TableColumnHeader)var0.getSource();
      var1.getTableHeaderRow().columnDragLock = false;
      if (!var0.isPopupTrigger()) {
         if (!var0.isConsumed()) {
            var0.consume();
            if (var1.getTableHeaderRow().isReordering() && var1.isColumnReorderingEnabled()) {
               var1.columnReorderingComplete();
            } else if (var0.isStillSincePress()) {
               var1.sortColumn(var0.isShiftDown());
            }
         }
      }
   };
   private static final EventHandler<ContextMenuEvent> contextMenuRequestedHandler = var0 -> {
      TableColumnHeader var1 = (TableColumnHeader)var0.getSource();
      TableColumnBase var2 = var1.getTableColumn();
      ContextMenu var3 = var2.getContextMenu();
      if (var3 != null) {
         var3.show(var1, var0.getScreenX(), var0.getScreenY());
         var0.consume();
      }
   };
   private DoubleProperty size;
   private ReadOnlyObjectWrapper<TableColumnBase<?, ?>> tableColumn = new ReadOnlyObjectWrapper(this, "tableColumn");
   private static final PseudoClass PSEUDO_CLASS_LAST_VISIBLE = PseudoClass.getPseudoClass("last-visible");

   public TableColumnHeader(TableColumnBase var1) {
      this.setTableColumn(var1);
      this.setFocusTraversable(false);
      this.initStyleClasses();
      this.initUI();
      this.changeListenerHandler = new LambdaMultiplePropertyChangeListenerHandler();
      this.changeListenerHandler.registerChangeListener(this.sceneProperty(), var1x -> this.updateScene());
      if (this.getTableColumn() != null) {
         this.changeListenerHandler.registerChangeListener(var1.idProperty(), var2 -> this.setId(var1.getId()));
         this.changeListenerHandler.registerChangeListener(var1.styleProperty(), var2 -> this.setStyle(var1.getStyle()));
         this.changeListenerHandler.registerChangeListener(var1.widthProperty(), var1x -> {
            this.isSizeDirty = true;
            this.requestLayout();
         });
         this.changeListenerHandler.registerChangeListener(var1.visibleProperty(), var1x -> this.setVisible(this.getTableColumn().isVisible()));
         this.changeListenerHandler.registerChangeListener(var1.sortNodeProperty(), var1x -> this.updateSortGrid());
         this.changeListenerHandler.registerChangeListener(var1.sortableProperty(), var1x -> {
            if (TableSkinUtils.getSortOrder(this.getTableSkin()).contains(this.getTableColumn())) {
               NestedTableColumnHeader var2 = this.getTableHeaderRow().getRootHeader();
               this.updateAllHeaders(var2);
            }
         });
         this.changeListenerHandler.registerChangeListener(var1.textProperty(), var2 -> this.label.setText(var1.getText()));
         this.changeListenerHandler.registerChangeListener(var1.graphicProperty(), var2 -> this.label.setGraphic(var1.getGraphic()));
         this.setId(var1.getId());
         this.setStyle(var1.getStyle());
         this.setAccessibleRole(AccessibleRole.TABLE_COLUMN);
      }
   }

   private final double getSize() {
      return this.size == null ? 20.0 : this.size.doubleValue();
   }

   private final DoubleProperty sizeProperty() {
      if (this.size == null) {
         this.size = new StyleableDoubleProperty(20.0) {
            protected void invalidated() {
               double var1 = this.get();
               if (var1 <= 0.0) {
                  if (this.isBound()) {
                     this.unbind();
                  }

                  this.set(20.0);
                  throw new IllegalArgumentException("Size cannot be 0 or negative");
               }
            }

            public Object getBean() {
               return TableColumnHeader.this;
            }

            public String getName() {
               return "size";
            }

            public CssMetaData<TableColumnHeader, Number> getCssMetaData() {
               return TableColumnHeader.StyleableProperties.SIZE;
            }
         };
      }

      return this.size;
   }

   private final void setTableColumn(TableColumnBase<?, ?> var1) {
      this.tableColumn.set(var1);
   }

   public final TableColumnBase<?, ?> getTableColumn() {
      return (TableColumnBase<?, ?>)this.tableColumn.get();
   }

   public final ReadOnlyObjectProperty<TableColumnBase<?, ?>> tableColumnProperty() {
      return this.tableColumn.getReadOnlyProperty();
   }

   protected void layoutChildren() {
      if (this.isSizeDirty) {
         this.resize(this.getTableColumn().getWidth(), this.getHeight());
         this.isSizeDirty = false;
      }

      double var1 = 0.0;
      double var3 = this.snapSizeX(this.getWidth()) - (this.snappedLeftInset() + this.snappedRightInset());
      double var5 = this.getHeight() - (this.snappedTopInset() + this.snappedBottomInset());
      if (this.arrow != null) {
         this.arrow.setMaxSize(this.arrow.prefWidth(-1.0), this.arrow.prefHeight(-1.0));
      }

      if (this.sortArrow != null && this.sortArrow.isVisible()) {
         var1 = this.sortArrow.prefWidth(-1.0);
         double var7 = var3 - var1;
         this.sortArrow.resize(var1, this.sortArrow.prefHeight(-1.0));
         this.positionInArea(this.sortArrow, var7, this.snappedTopInset(), var1, var5, 0.0, HPos.CENTER, VPos.CENTER);
      }

      if (this.label != null) {
         double var9 = var3 - var1;
         this.label.resizeRelocate(this.snappedLeftInset(), 0.0, var9, this.getHeight());
      }
   }

   protected double computePrefWidth(double var1) {
      if (this.getNestedColumnHeader() != null) {
         double var3 = this.getNestedColumnHeader().prefWidth(var1);
         if (this.getTableColumn() != null) {
            TableColumnBaseHelper.setWidth(this.getTableColumn(), var3);
         }

         return var3;
      } else {
         return this.getTableColumn() != null && this.getTableColumn().isVisible() ? this.snapSizeX(this.getTableColumn().getWidth()) : 0.0;
      }
   }

   protected double computeMinHeight(double var1) {
      return this.label == null ? 0.0 : this.label.minHeight(var1);
   }

   protected double computePrefHeight(double var1) {
      return this.getTableColumn() == null ? 0.0 : Math.max(this.getSize(), this.label.prefHeight(-1.0));
   }

   public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
      return getClassCssMetaData();
   }

   public Object queryAccessibleAttribute(AccessibleAttribute var1, Object... var2) {
      switch (var1) {
         case INDEX:
            return this.getIndex(this.getTableColumn());
         case TEXT:
            return this.getTableColumn() != null ? this.getTableColumn().getText() : null;
         default:
            return super.queryAccessibleAttribute(var1, var2);
      }
   }

   void initStyleClasses() {
      this.getStyleClass().setAll(new String[]{"column-header"});
      this.installTableColumnStyleClassListener();
   }

   void installTableColumnStyleClassListener() {
      TableColumnBase var1 = this.getTableColumn();
      if (var1 != null) {
         this.getStyleClass().addAll(var1.getStyleClass());
         var1.getStyleClass().addListener(this.weakStyleClassListener);
      }
   }

   NestedTableColumnHeader getNestedColumnHeader() {
      return this.nestedColumnHeader;
   }

   void setNestedColumnHeader(NestedTableColumnHeader var1) {
      this.nestedColumnHeader = var1;
   }

   protected TableHeaderRow getTableHeaderRow() {
      return this.tableHeaderRow;
   }

   void setTableHeaderRow(TableHeaderRow var1) {
      this.tableHeaderRow = var1;
      this.updateTableSkin();
   }

   private void updateTableSkin() {
      TableViewSkinBase var1 = this.getTableSkin();
      if (var1 != null) {
         this.updateColumnIndex();
         this.columnReorderLine = var1.getColumnReorderLine();
         if (this.getTableColumn() != null) {
            this.updateSortPosition();
            TableSkinUtils.getSortOrder(var1).addListener(this.weakSortOrderListener);
            TableSkinUtils.getVisibleLeafColumns(var1).addListener(this.weakVisibleLeafColumnsListener);
         }
      }
   }

   protected TableViewSkinBase<?, ?, ?, ?, ?> getTableSkin() {
      return this.tableHeaderRow == null ? null : this.tableHeaderRow.tableSkin;
   }

   NestedTableColumnHeader getParentHeader() {
      return this.parentHeader;
   }

   void setParentHeader(NestedTableColumnHeader var1) {
      this.parentHeader = var1;
   }

   private void updateAllHeaders(TableColumnHeader var1) {
      if (var1 instanceof NestedTableColumnHeader) {
         ObservableList var2 = ((NestedTableColumnHeader)var1).getColumnHeaders();

         for (int var3 = 0; var3 < var2.size(); var3++) {
            this.updateAllHeaders((TableColumnHeader)var2.get(var3));
         }
      } else {
         var1.updateSortPosition();
      }
   }

   private void updateScene() {
      if (!this.autoSizeComplete) {
         if (this.getTableColumn() == null || this.getTableColumn().getWidth() != 80.0 || this.getScene() == null) {
            return;
         }

         this.doColumnAutoSize(30);
         this.autoSizeComplete = true;
      }
   }

   void dispose() {
      TableViewSkinBase var1 = this.getTableSkin();
      if (var1 != null) {
         TableSkinUtils.getVisibleLeafColumns(var1).removeListener(this.weakVisibleLeafColumnsListener);
         TableSkinUtils.getSortOrder(var1).removeListener(this.weakSortOrderListener);
      }

      this.changeListenerHandler.dispose();
   }

   private boolean isSortingEnabled() {
      return true;
   }

   private boolean isColumnReorderingEnabled() {
      return !Properties.IS_TOUCH_SUPPORTED && TableSkinUtils.getVisibleLeafColumns(this.getTableSkin()).size() > 1;
   }

   private void initUI() {
      if (this.getTableColumn() != null) {
         this.setOnMousePressed(mousePressedHandler);
         this.setOnMouseDragged(mouseDraggedHandler);
         this.setOnDragDetected(var0 -> var0.consume());
         this.setOnContextMenuRequested(contextMenuRequestedHandler);
         this.setOnMouseReleased(mouseReleasedHandler);
         this.label = new Label();
         this.label.setText(this.getTableColumn().getText());
         this.label.setGraphic(this.getTableColumn().getGraphic());
         this.label.setVisible(this.getTableColumn().isVisible());
         if (this.isSortingEnabled()) {
            this.updateSortGrid();
         }
      }
   }

   private void doColumnAutoSize(int var1) {
      double var2 = this.getTableColumn().getPrefWidth();
      if (var2 == 80.0) {
         this.resizeColumnToFitContent(var1);
      }
   }

   protected void resizeColumnToFitContent(int var1) {
      TableColumnBase var2 = this.getTableColumn();
      if (var2.isResizable()) {
         Control var3 = this.getTableSkin().getSkinnable();
         if (var3 instanceof TableView) {
            this.resizeColumnToFitContent((TableView)var3, (TableColumn)var2, this.getTableSkin(), var1);
         } else if (var3 instanceof TreeTableView) {
            this.resizeColumnToFitContent((TreeTableView)var3, (TreeTableColumn)var2, this.getTableSkin(), var1);
         }
      }
   }

   private <T, S> void resizeColumnToFitContent(TableView<T> var1, TableColumn<T, S> var2, TableViewSkinBase var3, int var4) {
      ObservableList var5 = var1.getItems();
      if (var5 != null && !var5.isEmpty()) {
         Callback var6 = var2.getCellFactory();
         if (var6 != null) {
            TableCell var7 = (TableCell)var6.call(var2);
            if (var7 != null) {
               var7.getProperties().put("deferToParentPrefWidth", Boolean.TRUE);
               double var8 = 10.0;
               Node var10 = var7.getSkin() == null ? null : var7.getSkin().getNode();
               if (var10 instanceof Region) {
                  Region var11 = (Region)var10;
                  var8 = var11.snappedLeftInset() + var11.snappedRightInset();
               }

               int var24 = var4 == -1 ? var5.size() : Math.min(var5.size(), var4);
               double var12 = 0.0;

               for (int var14 = 0; var14 < var24; var14++) {
                  var7.updateTableColumn(var2);
                  var7.updateTableView(var1);
                  var7.updateIndex(var14);
                  if (var7.getText() != null && !var7.getText().isEmpty() || var7.getGraphic() != null) {
                     var3.getChildren().add(var7);
                     var7.applyCss();
                     var12 = Math.max(var12, var7.prefWidth(-1.0));
                     var3.getChildren().remove(var7);
                  }
               }

               var7.updateIndex(-1);
               TableColumnHeader var27 = var3.getTableHeaderRow().getColumnHeaderFor(var2);
               double var15 = Utils.computeTextWidth(var27.label.getFont(), var2.getText(), -1.0);
               Node var17 = var27.label.getGraphic();
               double var18 = var17 == null ? 0.0 : var17.prefWidth(-1.0) + var27.label.getGraphicTextGap();
               double var20 = var15 + var18 + 10.0 + var27.snappedLeftInset() + var27.snappedRightInset();
               var12 = Math.max(var12, var20);
               var12 += var8;
               if (var1.getColumnResizePolicy() == TableView.CONSTRAINED_RESIZE_POLICY && var1.getWidth() > 0.0) {
                  if (var12 > var2.getMaxWidth()) {
                     var12 = var2.getMaxWidth();
                  }

                  int var22 = var2.getColumns().size();
                  if (var22 > 0) {
                     TableColumnHeader var23 = this.getTableHeaderRow().getColumnHeaderFor((TableColumnBase<?, ?>)var2.getColumns().get(var22 - 1));
                     if (var23 != null) {
                        var23.resizeColumnToFitContent(var4);
                     }

                     return;
                  }

                  TableSkinUtils.resizeColumn(var3, var2, (double)Math.round(var12 - var2.getWidth()));
               } else {
                  TableColumnBaseHelper.setWidth(var2, var12);
               }
            }
         }
      }
   }

   private <T, S> void resizeColumnToFitContent(TreeTableView<T> var1, TreeTableColumn<T, S> var2, TableViewSkinBase var3, int var4) {
      TreeTableViewBackingList var5 = new TreeTableViewBackingList(var1);
      if (var5 != null && !var5.isEmpty()) {
         Callback var6 = var2.getCellFactory();
         if (var6 != null) {
            TreeTableCell var7 = (TreeTableCell)var6.call(var2);
            if (var7 != null) {
               var7.getProperties().put("deferToParentPrefWidth", Boolean.TRUE);
               double var8 = 10.0;
               Node var10 = var7.getSkin() == null ? null : var7.getSkin().getNode();
               if (var10 instanceof Region) {
                  Region var11 = (Region)var10;
                  var8 = var11.snappedLeftInset() + var11.snappedRightInset();
               }

               TreeTableRow var25 = new TreeTableRow();
               var25.updateTreeTableView(var1);
               int var12 = var4 == -1 ? var5.size() : Math.min(var5.size(), var4);
               double var13 = 0.0;

               for (int var15 = 0; var15 < var12; var15++) {
                  var25.updateIndex(var15);
                  var25.updateTreeItem(var1.getTreeItem(var15));
                  var7.updateTableColumn(var2);
                  var7.updateTreeTableView(var1);
                  var7.updateTableRow(var25);
                  var7.updateIndex(var15);
                  if (var7.getText() != null && !var7.getText().isEmpty() || var7.getGraphic() != null) {
                     var3.getChildren().add(var7);
                     var7.applyCss();
                     double var16 = var7.prefWidth(-1.0);
                     var13 = Math.max(var13, var16);
                     var3.getChildren().remove(var7);
                  }
               }

               var7.updateIndex(-1);
               TableColumnHeader var28 = var3.getTableHeaderRow().getColumnHeaderFor(var2);
               double var29 = Utils.computeTextWidth(var28.label.getFont(), var2.getText(), -1.0);
               Node var18 = var28.label.getGraphic();
               double var19 = var18 == null ? 0.0 : var18.prefWidth(-1.0) + var28.label.getGraphicTextGap();
               double var21 = var29 + var19 + 10.0 + var28.snappedLeftInset() + var28.snappedRightInset();
               var13 = Math.max(var13, var21);
               var13 += var8;
               if (var1.getColumnResizePolicy() == TreeTableView.CONSTRAINED_RESIZE_POLICY && var1.getWidth() > 0.0) {
                  if (var13 > var2.getMaxWidth()) {
                     var13 = var2.getMaxWidth();
                  }

                  int var23 = var2.getColumns().size();
                  if (var23 > 0) {
                     TableColumnHeader var24 = this.getTableHeaderRow().getColumnHeaderFor((TableColumnBase<?, ?>)var2.getColumns().get(var23 - 1));
                     if (var24 != null) {
                        var24.resizeColumnToFitContent(var4);
                     }

                     return;
                  }

                  TableSkinUtils.resizeColumn(var3, var2, (double)Math.round(var13 - var2.getWidth()));
               } else {
                  TableColumnBaseHelper.setWidth(var2, var13);
               }
            }
         }
      }
   }

   private void updateSortPosition() {
      this.sortPos = !this.getTableColumn().isSortable() ? -1 : this.getSortPosition();
      this.updateSortGrid();
   }

   private void updateSortGrid() {
      if (!(this instanceof NestedTableColumnHeader)) {
         this.getChildren().clear();
         this.getChildren().add(this.label);
         if (this.isSortingEnabled()) {
            this.isSortColumn = this.sortPos != -1;
            if (!this.isSortColumn) {
               if (this.sortArrow != null) {
                  this.sortArrow.setVisible(false);
               }
            } else {
               int var1 = TableSkinUtils.getVisibleLeafIndex(this.getTableSkin(), this.getTableColumn());
               if (var1 != -1) {
                  int var2 = this.getVisibleSortOrderColumnCount();
                  boolean var3 = this.sortPos <= 3 && var2 > 1;
                  Object var4 = null;
                  if (this.getTableColumn().getSortNode() != null) {
                     var4 = this.getTableColumn().getSortNode();
                     this.getChildren().add(var4);
                  } else {
                     GridPane var5 = new GridPane();
                     var4 = var5;
                     var5.setPadding(new Insets(0.0, 3.0, 0.0, 0.0));
                     this.getChildren().add(var5);
                     if (this.arrow == null) {
                        this.arrow = new Region();
                        this.arrow.getStyleClass().setAll(new String[]{"arrow"});
                        this.arrow.setVisible(true);
                        this.arrow.setRotate(TableColumnSortTypeWrapper.isAscending(this.getTableColumn()) ? 180.0 : 0.0);
                        this.changeListenerHandler.registerChangeListener(TableColumnSortTypeWrapper.getSortTypeProperty(this.getTableColumn()), var1x -> {
                           this.updateSortGrid();
                           if (this.arrow != null) {
                              this.arrow.setRotate(TableColumnSortTypeWrapper.isAscending(this.getTableColumn()) ? 180.0 : 0.0);
                           }
                        });
                     }

                     this.arrow.setVisible(this.isSortColumn);
                     if (this.sortPos > 2) {
                        if (this.sortOrderLabel == null) {
                           this.sortOrderLabel = new Label();
                           this.sortOrderLabel.getStyleClass().add("sort-order");
                        }

                        this.sortOrderLabel.setText(this.sortPos + 1 + "");
                        this.sortOrderLabel.setVisible(var2 > 1);
                        var5.add(this.arrow, 1, 1);
                        GridPane.setHgrow(this.arrow, Priority.NEVER);
                        GridPane.setVgrow(this.arrow, Priority.NEVER);
                        var5.add(this.sortOrderLabel, 2, 1);
                     } else if (var3) {
                        if (this.sortOrderDots == null) {
                           this.sortOrderDots = new HBox(0.0);
                           this.sortOrderDots.getStyleClass().add("sort-order-dots-container");
                        }

                        boolean var6 = TableColumnSortTypeWrapper.isAscending(this.getTableColumn());
                        int var7 = var6 ? 1 : 2;
                        int var8 = var6 ? 2 : 1;
                        var5.add(this.arrow, 1, var7);
                        GridPane.setHalignment(this.arrow, HPos.CENTER);
                        var5.add(this.sortOrderDots, 1, var8);
                        this.updateSortOrderDots(this.sortPos);
                     } else {
                        var5.add(this.arrow, 1, 1);
                        GridPane.setHgrow(this.arrow, Priority.NEVER);
                        GridPane.setVgrow(this.arrow, Priority.ALWAYS);
                     }
                  }

                  this.sortArrow = (Node)var4;
                  if (this.sortArrow != null) {
                     this.sortArrow.setVisible(this.isSortColumn);
                  }

                  this.requestLayout();
               }
            }
         }
      }
   }

   private void updateSortOrderDots(int var1) {
      double var2 = this.arrow.prefWidth(-1.0);
      this.sortOrderDots.getChildren().clear();

      for (int var4 = 0; var4 <= var1; var4++) {
         Region var5 = new Region();
         var5.getStyleClass().add("sort-order-dot");
         String var6 = TableColumnSortTypeWrapper.getSortTypeName(this.getTableColumn());
         if (var6 != null && !var6.isEmpty()) {
            var5.getStyleClass().add(var6.toLowerCase(Locale.ROOT));
         }

         this.sortOrderDots.getChildren().add(var5);
         if (var4 < var1) {
            Region var7 = new Region();
            double var8 = var1 == 1 ? 1.0 : 0.0;
            var7.setPadding(new Insets(0.0, 1.0, 0.0, var8));
            this.sortOrderDots.getChildren().add(var7);
         }
      }

      this.sortOrderDots.setAlignment(Pos.TOP_CENTER);
      this.sortOrderDots.setMaxWidth(var2);
   }

   void moveColumn(TableColumnBase var1, int var2) {
      if (var1 != null && var2 >= 0) {
         ObservableList var3 = this.getColumns(var1);
         int var4 = var3.size();
         int var5 = var3.indexOf(var1);
         int var6 = var2;
         int var7 = var2;
         int var8 = 0;

         for (int var9 = 0; var9 < var4 && var8 != var7 + 1; var9++) {
            if (((TableColumnBase)var3.get(var9)).isVisible()) {
               var8++;
            } else {
               var6++;
            }
         }

         if (var6 >= var4) {
            var6 = var4 - 1;
         } else if (var6 < 0) {
            var6 = 0;
         }

         if (var6 != var5) {
            ArrayList var10 = new ArrayList(var3);
            var10.remove(var1);
            var10.add(var6, var1);
            var3.setAll(var10);
         }
      }
   }

   private ObservableList<TableColumnBase<?, ?>> getColumns(TableColumnBase var1) {
      return var1.getParentColumn() == null ? TableSkinUtils.getColumns(this.getTableSkin()) : var1.getParentColumn().getColumns();
   }

   private int getIndex(TableColumnBase<?, ?> var1) {
      if (var1 == null) {
         return -1;
      } else {
         ObservableList var2 = this.getColumns(var1);
         int var3 = -1;

         for (int var4 = 0; var4 < var2.size(); var4++) {
            TableColumnBase var5 = (TableColumnBase)var2.get(var4);
            if (var5.isVisible()) {
               var3++;
               if (var1.equals(var5)) {
                  break;
               }
            }
         }

         return var3;
      }
   }

   private void updateColumnIndex() {
      TableColumnBase var1 = this.getTableColumn();
      TableViewSkinBase var2 = this.getTableSkin();
      this.columnIndex = var2 != null && var1 != null ? TableSkinUtils.getVisibleLeafIndex(var2, var1) : -1;
      this.isLastVisibleColumn = this.getTableColumn() != null
         && this.columnIndex != -1
         && this.columnIndex == TableSkinUtils.getVisibleLeafColumns(var2).size() - 1;
      this.pseudoClassStateChanged(PSEUDO_CLASS_LAST_VISIBLE, this.isLastVisibleColumn);
   }

   private void sortColumn(boolean var1) {
      if (this.isSortingEnabled()) {
         if (this.getTableColumn() != null
            && this.getTableColumn().getColumns().size() == 0
            && this.getTableColumn().getComparator() != null
            && this.getTableColumn().isSortable()) {
            ObservableList var2 = TableSkinUtils.getSortOrder(this.getTableSkin());
            if (var1) {
               if (!this.isSortColumn) {
                  TableColumnSortTypeWrapper.setSortType(this.getTableColumn(), TableColumn.SortType.ASCENDING);
                  var2.add(this.getTableColumn());
               } else if (TableColumnSortTypeWrapper.isAscending(this.getTableColumn())) {
                  TableColumnSortTypeWrapper.setSortType(this.getTableColumn(), TableColumn.SortType.DESCENDING);
               } else {
                  int var3 = var2.indexOf(this.getTableColumn());
                  if (var3 != -1) {
                     var2.remove(var3);
                  }
               }
            } else if (this.isSortColumn && var2.size() == 1) {
               if (TableColumnSortTypeWrapper.isAscending(this.getTableColumn())) {
                  TableColumnSortTypeWrapper.setSortType(this.getTableColumn(), TableColumn.SortType.DESCENDING);
               } else {
                  var2.remove(this.getTableColumn());
               }
            } else if (this.isSortColumn) {
               if (TableColumnSortTypeWrapper.isAscending(this.getTableColumn())) {
                  TableColumnSortTypeWrapper.setSortType(this.getTableColumn(), TableColumn.SortType.DESCENDING);
               } else if (TableColumnSortTypeWrapper.isDescending(this.getTableColumn())) {
                  TableColumnSortTypeWrapper.setSortType(this.getTableColumn(), TableColumn.SortType.ASCENDING);
               }

               ArrayList var4 = new ArrayList(var2);
               var4.remove(this.getTableColumn());
               var4.add(0, this.getTableColumn());
               var2.setAll(new TableColumnBase[]{this.getTableColumn()});
            } else {
               TableColumnSortTypeWrapper.setSortType(this.getTableColumn(), TableColumn.SortType.ASCENDING);
               var2.setAll(new TableColumnBase[]{this.getTableColumn()});
            }
         }
      }
   }

   private int getSortPosition() {
      if (this.getTableColumn() == null) {
         return -1;
      } else {
         List var1 = this.getVisibleSortOrderColumns();
         int var2 = 0;

         for (int var3 = 0; var3 < var1.size(); var3++) {
            TableColumnBase var4 = (TableColumnBase)var1.get(var3);
            if (this.getTableColumn().equals(var4)) {
               return var2;
            }

            var2++;
         }

         return -1;
      }
   }

   private List<TableColumnBase> getVisibleSortOrderColumns() {
      ObservableList var1 = TableSkinUtils.getSortOrder(this.getTableSkin());
      ArrayList var2 = new ArrayList();

      for (int var3 = 0; var3 < var1.size(); var3++) {
         TableColumnBase var4 = (TableColumnBase)var1.get(var3);
         if (var4 != null && var4.isSortable() && var4.isVisible()) {
            var2.add(var4);
         }
      }

      return var2;
   }

   private int getVisibleSortOrderColumnCount() {
      return this.getVisibleSortOrderColumns().size();
   }

   void columnReorderingStarted(double var1) {
      if (this.getTableColumn().isReorderable()) {
         this.dragOffset = var1;
         this.getTableHeaderRow().setReorderingColumn(this.getTableColumn());
         this.getTableHeaderRow().setReorderingRegion(this);
      }
   }

   void columnReordering(double var1, double var3) {
      if (this.getTableColumn().isReorderable()) {
         this.getTableHeaderRow().setReordering(true);
         TableColumnHeader var5 = null;
         double var6 = this.getParentHeader().sceneToLocal(var1, var3).getX();
         double var8 = this.getTableSkin().getSkinnable().sceneToLocal(var1, var3).getX() - this.dragOffset;
         this.getTableHeaderRow().setDragHeaderX(var8);
         double var10 = 0.0;
         double var12 = 0.0;
         double var14 = 0.0;
         this.newColumnPos = 0;

         for (TableColumnHeader var17 : this.getParentHeader().getColumnHeaders()) {
            if (var17.isVisible()) {
               double var18 = var17.prefWidth(-1.0);
               var14 += var18;
               var10 = var17.getBoundsInParent().getMinX();
               var12 = var10 + var18;
               if (var6 >= var10 && var6 < var12) {
                  var5 = var17;
                  break;
               }

               this.newColumnPos++;
            }
         }

         if (var5 == null) {
            this.newColumnPos = var6 > var14 ? this.getParentHeader().getColumns().size() - 1 : 0;
         } else {
            double var22 = var10 + (var12 - var10) / 2.0;
            boolean var23 = var6 <= var22;
            int var19 = this.getIndex(this.getTableColumn());
            this.newColumnPos = this.newColumnPos + (this.newColumnPos > var19 && var23 ? -1 : (this.newColumnPos < var19 && !var23 ? 1 : 0));
            double var20 = this.getTableHeaderRow().sceneToLocal(var5.localToScene(var5.getBoundsInLocal())).getMinX();
            var20 += var23 ? 0.0 : var5.getWidth();
            if (var20 >= -0.5 && var20 <= this.getTableSkin().getSkinnable().getWidth()) {
               this.columnReorderLine.setTranslateX(var20);
               this.columnReorderLine.setVisible(true);
            }

            this.getTableHeaderRow().setReordering(true);
         }
      }
   }

   void columnReorderingComplete() {
      if (this.getTableColumn().isReorderable()) {
         this.moveColumn(this.getTableColumn(), this.newColumnPos);
         this.columnReorderLine.setTranslateX(0.0);
         this.columnReorderLine.setLayoutX(0.0);
         this.newColumnPos = 0;
         this.getTableHeaderRow().setReordering(false);
         this.columnReorderLine.setVisible(false);
         this.getTableHeaderRow().setReorderingColumn(null);
         this.getTableHeaderRow().setReorderingRegion(null);
         this.dragOffset = 0.0;
      }
   }

   double getDragRectHeight() {
      return this.getHeight();
   }

   boolean represents(TableColumnBase<?, ?> var1) {
      return !var1.getColumns().isEmpty() ? false : var1 == this.getTableColumn();
   }

   public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
      return TableColumnHeader.StyleableProperties.STYLEABLES;
   }

   private static class StyleableProperties {
      private static final CssMetaData<TableColumnHeader, Number> SIZE = new CssMetaData<TableColumnHeader, Number>(
         "-fx-size", SizeConverter.getInstance(), 20.0
      ) {
         public boolean isSettable(TableColumnHeader var1) {
            return var1.size == null || !var1.size.isBound();
         }

         public StyleableProperty<Number> getStyleableProperty(TableColumnHeader var1) {
            return (StyleableProperty<Number>)var1.sizeProperty();
         }
      };
      private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

      static {
         ArrayList var0 = new ArrayList(Region.getClassCssMetaData());
         var0.add(SIZE);
         STYLEABLES = Collections.unmodifiableList(var0);
      }
   }
}
