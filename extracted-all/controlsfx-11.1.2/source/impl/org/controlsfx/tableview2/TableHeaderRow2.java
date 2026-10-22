package impl.org.controlsfx.tableview2;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Objects;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.scene.control.TableColumn;
import javafx.scene.control.skin.NestedTableColumnHeader;
import javafx.scene.control.skin.TableColumnHeader;
import javafx.scene.control.skin.TableHeaderRow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import org.controlsfx.control.tableview2.TableView2;

public class TableHeaderRow2 extends TableHeaderRow {
   private static final String FIXED_STYLE = "fixed";
   private static final String SELECTED_STYLE = "selected";
   private static final String LEAF_STYLE = "leaf-header";
   final TableView2Skin<?> skin;
   final TableView2<?> control;
   private boolean working = true;
   protected BitSet selectedColumns = new BitSet();
   private final List<TableColumnHeader> visibleLeafHeaders;
   private double scrollX;
   private double tableWidth;
   private final NestedTableColumnHeader header;
   private NestedTableColumnHeader2 rootHeader2;
   private final Region filler;
   private final Pane cornerRegion;
   private final StackPane dragHeader;
   private TableColumnHeader reorderingRegion;
   private final SouthTableHeaderRow southHeaderRow;
   private final InvalidationListener rowHeaderListener = o -> this.updateTableWidth();
   private final InvalidationListener columnFixingEnabledHeaderListener = o -> this.updateFixedColumnsStyle();
   private final ListChangeListener<TableColumn> fixedColumnsListener = change -> {
      while (change.next()) {
         change.getRemoved().forEach(this::unfixColumnStyle);
         change.getAddedSubList().forEach(this::fixColumnStyle);
      }

      this.updateHighlightSelection();
   };
   private final InvalidationListener selectionListener = o -> this.updateHighlightSelection();
   private final EventHandler<KeyEvent> cancelDrag = e -> {
      if (this.isReordering() && e.getCode() == KeyCode.ESCAPE) {
         this.setReordering(false);
         TableColumnHeader columnHeader = this.getReorderingRegion();
         if (columnHeader != null) {
            columnHeader.getTableColumn().setReorderable(false);
         }
      }
   };

   public TableHeaderRow2(TableView2Skin<?> skin) {
      super(skin);
      this.skin = skin;
      this.control = (TableView2<?>)skin.getSkinnable();
      this.visibleLeafHeaders = new ArrayList<>();
      this.buildVisibleLeafColumnHeaders(this.getRootHeader().getColumnHeaders(), this.visibleLeafHeaders);
      this.filler = (Region)this.getChildrenUnmodifiable().get(0);
      this.header = (NestedTableColumnHeader)this.getChildrenUnmodifiable().get(1);
      this.cornerRegion = (Pane)this.getChildrenUnmodifiable().get(2);
      this.dragHeader = (StackPane)this.getChildrenUnmodifiable().get(3);
      this.southHeaderRow = new SouthTableHeaderRow(skin);
      this.getChildren().add(0, this.southHeaderRow);
   }

   public void init() {
      this.updateColumnHeaderVisibility(this.skin.isColumnHeaderVisible());
      this.control.rowHeaderVisibleProperty().addListener(this.rowHeaderListener);
      if (this.skin.rowHeader != null) {
         this.skin.rowHeader.rowHeaderWidthProperty().addListener(this.rowHeaderListener);
      }

      this.control.columnFixingEnabledProperty().addListener(this.columnFixingEnabledHeaderListener);
      this.control.itemsProperty().addListener(this.columnFixingEnabledHeaderListener);
      this.skin.getSelectedColumns().addListener(this.selectionListener);
      this.control.getFixedColumns().addListener(this.fixedColumnsListener);
      Platform.runLater(() -> {
         this.updateFixedColumnsStyle();
         this.requestLayout();
         this.updateVisibleLeafColumnHeaders();
         this.updateVisibleLeafStyle();
      });
      this.getRootHeader().getColumnHeaders().addListener(o -> {
         this.updateFixedColumnsStyle();
         this.updateHighlightSelection();
         this.updateVisibleLeafStyle();
      });
      this.control.addEventHandler(KeyEvent.KEY_PRESSED, this.cancelDrag);
      this.southHeaderRow.heightProperty().addListener(o -> this.updateVisibleLeafStyle());
      this.control.southHeaderBlendedProperty().addListener(o -> this.updateVisibleLeafStyle());
   }

   public SouthTableHeaderRow getSouthHeaderRow() {
      return this.southHeaderRow;
   }

   protected void layoutChildren() {
      double x = this.scrollX;
      double headerWidth = this.snapSize(this.header.prefWidth(-1.0));
      double southTableHeaderRowHeight = this.southHeaderRow.prefHeight(headerWidth);
      this.southHeaderRow.resizeRelocate(x, this.getHeight() - this.snappedBottomInset() - southTableHeaderRowHeight, headerWidth, southTableHeaderRowHeight);
      double prefHeight = this.getHeight() - this.snappedTopInset() - this.snappedBottomInset();
      double cornerWidth = this.snapSize(this.skin.getFlow().getVerticalBar().prefWidth(-1.0));
      this.header.resizeRelocate(x, this.snappedTopInset(), headerWidth, prefHeight - southTableHeaderRowHeight);
      if (this.control != null) {
         double controlInsets = this.control.snappedLeftInset() + this.control.snappedRightInset();
         double fillerWidth = this.tableWidth - headerWidth + this.filler.getInsets().getLeft() - controlInsets;
         fillerWidth -= this.control.tableMenuButtonVisibleProperty().get() ? cornerWidth : 0.0;
         this.filler.setVisible(fillerWidth > 0.0);
         if (fillerWidth > 0.0) {
            this.filler.resizeRelocate(x + headerWidth, this.snappedTopInset(), fillerWidth, prefHeight);
         }

         this.cornerRegion.resizeRelocate(this.tableWidth - cornerWidth, this.snappedTopInset(), cornerWidth, prefHeight);
      }
   }

   void clearSelectedColumns() {
      this.selectedColumns.clear();
   }

   protected void updateTableWidth() {
      super.updateTableWidth();
      double padding = 0.0;
      if (this.working && this.skin != null && this.control != null && this.control.rowHeaderVisibleProperty().get() && this.skin.rowHeader != null) {
         padding += this.skin.rowHeader.getRowHeaderWidth();
      }

      Rectangle clip = (Rectangle)this.getClip();
      this.tableWidth = clip.getWidth();
      clip.setWidth(clip.getWidth() == 0.0 ? 0.0 : clip.getWidth() - padding);
   }

   protected void updateScrollX() {
      this.scrollX = this.skin.getFlow().getHorizontalBar().isVisible() ? -this.skin.getFlow().getHorizontalBar().getValue() : 0.0;
      this.requestLayout();
      this.layout();
      if (this.working) {
         this.requestLayout();
         if (this.rootHeader2 == null) {
            this.createRootHeader();
         }

         this.rootHeader2.layoutFixedColumns();
      }

      this.skin.getSouthHeader().updateScrollX();
   }

   protected NestedTableColumnHeader createRootHeader() {
      this.rootHeader2 = new NestedTableColumnHeader2(null);
      return this.rootHeader2;
   }

   protected void updateVisibleLeafColumnHeaders() {
      this.visibleLeafHeaders.clear();
      this.buildVisibleLeafColumnHeaders(this.getRootHeader().getColumnHeaders(), this.visibleLeafHeaders);
      this.updateHighlightSelection();
   }

   public TableColumnHeader getReorderingRegion() {
      return this.reorderingRegion;
   }

   public void setReorderingRegion(final TableColumnHeader reorderingRegion) {
      this.reorderingRegion = reorderingRegion;
      if (reorderingRegion != null) {
         this.dragHeader.resize(reorderingRegion.getWidth(), reorderingRegion.getHeight());
         this.reorderingProperty()
            .addListener(
               new InvalidationListener() {
                  public void invalidated(Observable observable) {
                     TableHeaderRow2.this.dragHeader.setLayoutX(-TableHeaderRow2.this.skin.rowHeader.getRowHeaderWidth());
                     TableHeaderRow2.this.dragHeader
                        .setTranslateY(TableHeaderRow2.this.dragHeader.getTranslateY() - TableHeaderRow2.this.southHeaderRow.getHeight());
                     TableHeaderRow2.this.dragHeader
                        .resize(reorderingRegion.getWidth(), TableHeaderRow2.this.dragHeader.getHeight() + TableHeaderRow2.this.southHeaderRow.getHeight());
                     TableHeaderRow2.this.reorderingProperty().removeListener(this);
                  }
               }
            );
      }
   }

   private void fixColumnStyle(TableColumn column) {
      int i = this.skin.getViewColumn(this.control.getColumns().indexOf(column));
      if (this.getRootHeader().getColumnHeaders().size() > i) {
         if (this.control.isColumnFixingEnabled() && this.control.getItems() != null && column.isVisible()) {
            this.addStyleHeader((TableColumnHeader)this.getRootHeader().getColumnHeaders().get(i), "fixed");
            this.addStyleSouthHeader(column, "fixed");
         } else {
            this.removeStyleHeader((TableColumnHeader)this.getRootHeader().getColumnHeaders().get(i), "fixed");
            this.removeStyleSouthHeader(column, "fixed");
         }
      }
   }

   private void unfixColumnStyle(TableColumn column) {
      int i = this.skin.getViewColumn(this.control.getColumns().indexOf(column));
      if (this.getRootHeader().getColumnHeaders().size() > i) {
         this.removeStyleHeader((TableColumnHeader)this.getRootHeader().getColumnHeaders().get(i), "fixed");
      }

      this.removeStyleSouthHeader(column, "fixed");
   }

   private void removeStyleHeader(TableColumnHeader header, String style) {
      if (header instanceof NestedTableColumnHeader) {
         ((NestedTableColumnHeader)header)
            .getChildrenUnmodifiable()
            .stream()
            .filter(TableColumnHeader.class::isInstance)
            .map(TableColumnHeader.class::cast)
            .forEach(t -> this.removeStyleHeader(t, style));
      } else if (header.getStyleClass().contains(style)) {
         header.getStyleClass().remove(style);
      }
   }

   private void addStyleHeader(TableColumnHeader header, String style) {
      if (header instanceof NestedTableColumnHeader) {
         ((NestedTableColumnHeader)header)
            .getChildrenUnmodifiable()
            .stream()
            .filter(TableColumnHeader.class::isInstance)
            .map(TableColumnHeader.class::cast)
            .forEach(t -> this.addStyleHeader(t, style));
      } else if (!header.getStyleClass().contains(style)) {
         header.getStyleClass().add(style);
      }
   }

   private void addStyleSouthHeader(TableColumn<?, ?> column, String style) {
      SouthTableColumnHeader southHeader = this.southHeaderRow.getSouthColumnHeaderFor(column);
      if (southHeader != null) {
         if (!southHeader.getStyleClass().contains(style)) {
            southHeader.getStyleClass().add(style);
         }
      } else {
         column.getColumns().forEach(col -> this.addStyleSouthHeader(col, style));
      }
   }

   private void removeStyleSouthHeader(TableColumn<?, ?> column, String style) {
      SouthTableColumnHeader southHeader = this.southHeaderRow.getSouthColumnHeaderFor(column);
      if (southHeader != null) {
         if (southHeader.getStyleClass().contains(style)) {
            southHeader.getStyleClass().remove(style);
         }
      } else {
         column.getColumns().forEach(col -> this.removeStyleSouthHeader(col, style));
      }
   }

   private void updateHighlightSelection() {
      this.removeStyleHeader(this.getRootHeader(), "selected");
      this.visibleLeafHeaders.forEach(h -> this.removeStyleSouthHeader((TableColumn<?, ?>)h.getTableColumn(), "selected"));
      this.skin.getSelectedColumns().forEach(i -> {
         if (this.visibleLeafHeaders.size() > i && !this.visibleLeafHeaders.get(i).getStyleClass().contains("selected")) {
            this.visibleLeafHeaders.get(i).getStyleClass().add("selected");
            this.addStyleSouthHeader((TableColumn<?, ?>)this.visibleLeafHeaders.get(i).getTableColumn(), "selected");
         }
      });
   }

   private void buildVisibleLeafColumnHeaders(List<TableColumnHeader> colHeaders, List<TableColumnHeader> vlch) {
      colHeaders.stream().filter(Objects::nonNull).forEachOrdered(c -> {
         if (c instanceof NestedTableColumnHeader) {
            this.buildVisibleLeafColumnHeaders(((NestedTableColumnHeader)c).getColumnHeaders(), vlch);
         } else if (c.isVisible()) {
            vlch.add(c);
         }
      });
   }

   private void updateColumnHeaderVisibility(boolean visible) {
      this.working = visible;
      this.setManaged(this.working);
      if (!visible) {
         this.getStyleClass().add("invisible");
      } else {
         this.getStyleClass().remove("invisible");
         this.requestLayout();
         if (this.rootHeader2 == null) {
            this.createRootHeader();
         }

         this.rootHeader2.layoutFixedColumns();
         this.updateHighlightSelection();
      }
   }

   private void updateFixedColumnsStyle() {
      Platform.runLater(() -> this.control.getFixedColumns().forEach(this::fixColumnStyle));
   }

   private void updateVisibleLeafStyle() {
      this.removeStyleHeader(this.getRootHeader(), "leaf-header");
      if (this.southHeaderRow.getHeight() > 0.0) {
         this.visibleLeafHeaders.forEach(h -> h.getStyleClass().add("leaf-header"));
      }

      this.removeStyleHeader(this.getRootHeader(), "south");
      if (this.southHeaderRow.getHeight() > 0.0 && this.control.isSouthHeaderBlended()) {
         this.visibleLeafHeaders.forEach(h -> h.getStyleClass().add("south"));
      }
   }

   protected double computePrefHeight(double width) {
      if (!this.skin.isColumnHeaderVisible()) {
         return 0.0;
      } else {
         double headerPrefHeight = this.getRootHeader().prefHeight(width);
         headerPrefHeight = headerPrefHeight == 0.0 ? 24.0 : headerPrefHeight;
         double southTableHeaderRowHeight = this.southHeaderRow.prefHeight(width);
         return this.snappedTopInset() + headerPrefHeight + southTableHeaderRowHeight + this.snappedBottomInset();
      }
   }
}
