package org.controlsfx.control.tableview2;

import impl.org.controlsfx.tableview2.SortUtils;
import impl.org.controlsfx.tableview2.TableView2Skin;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ListChangeListener.Change;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Skin;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;

public class TableView2<S> extends TableView<S> {
   private static final double DEFAULT_ROW_HEADER_WIDTH = 30.0;
   private final ObservableList<Integer> fixedRows = FXCollections.observableArrayList();
   private final ObservableList<TableColumn> fixedColumns = FXCollections.observableArrayList();
   private final BooleanProperty rowFixingEnabled = new SimpleBooleanProperty(true);
   private final BooleanProperty columnFixingEnabled = new SimpleBooleanProperty(true);
   private final BooleanProperty rowHeaderVisible = new SimpleBooleanProperty(true, "showRowHeader", false);
   private BitSet rowFix;
   private final DoubleProperty rowHeaderWidth = new SimpleDoubleProperty(30.0);
   private String stylesheet;
   private final ObjectProperty<TableColumn<S, ?>> rowHeader = new SimpleObjectProperty(this, "rowHeader");
   private ObjectProperty<BiFunction<Integer, S, ContextMenu>> rowHeaderContextMenuFactory;
   private final BooleanProperty southHeaderBlended = new SimpleBooleanProperty(this, "southHeaderBlended", true);
   private final ListChangeListener<Integer> fixedRowsListener = c -> {
      while (c.next()) {
         if (c.wasAdded()) {
            List<? extends Integer> newRows = c.getAddedSubList();
            if (!this.areRowsFixable(newRows)) {
               throw new IllegalArgumentException(this.computeReason(newRows));
            }

            FXCollections.sort(this.fixedRows);
         }

         if (c.wasRemoved()) {
         }
      }
   };
   private final ListChangeListener<TableColumn> fixedColumnsListener = new ListChangeListener<TableColumn>() {
      public void onChanged(Change<? extends TableColumn> c) {
         while (c.next()) {
            if (c.wasAdded()) {
               List<? extends TableColumn> newColumns = c.getAddedSubList();
               if (!TableView2.this.areTableViewColumnsFixable(newColumns)) {
                  List<Integer> newList = new ArrayList<>();

                  for (TableColumn column : newColumns) {
                     if (column != null) {
                        newList.add(TableView2.this.getColumns().indexOf(column));
                     }
                  }

                  throw new IllegalArgumentException(this.computeReason(newList));
               }
            }
         }
      }

      private String computeReason(List<Integer> list) {
         String reason = "\n This column cannot be fixed.";

         for (Integer columnIndex : list) {
            if (!TableView2.this.isColumnFixable(columnIndex)) {
               int maxSpan = 1;
               int count = columnIndex + maxSpan - 1;

               for (int index = columnIndex + 1; index < count; index++) {
                  if (!list.contains(index)) {
                     reason = reason
                        + "One cell on the column "
                        + columnIndex
                        + " has a column span of "
                        + maxSpan
                        + ". But the column "
                        + index
                        + " contained within that span is not fixed.\n";
                  }
               }
            }
         }

         return reason;
      }
   };

   public TableView2() {
      this(FXCollections.observableArrayList());
   }

   public TableView2(ObservableList<S> items) {
      super(items);
      this.getStyleClass().add("table-view2");
      this.fixedRows.addListener(this.fixedRowsListener);
      this.fixedColumns.addListener(this.fixedColumnsListener);
      this.skinProperty().addListener(new InvalidationListener() {
         public void invalidated(Observable observable) {
            ((TableView2Skin)TableView2.this.getSkin()).sizeProperty().addListener(o -> TableView2.this.initRowFix());
            TableView2.this.skinProperty().removeListener(this);
         }
      });
      this.getItems().addListener(o -> this.initRowFix());
      this.initRowFix();
   }

   public int getRowSpan(TablePosition<?, ?> pos, int index) {
      return 1;
   }

   public int getColumnSpan(TablePosition<?, ?> pos) {
      return 1;
   }

   public final ObservableList<Integer> getFixedRows() {
      return this.fixedRows;
   }

   public final boolean isRowFixable(int row) {
      return row >= 0 && row < this.rowFix.size() && this.isRowFixingEnabled() ? this.rowFix.get(row) : false;
   }

   public final boolean areRowsFixable(List<? extends Integer> list) {
      if (list != null && !list.isEmpty() && this.isRowFixingEnabled() && this.getItems() != null) {
         for (Integer row : list) {
            if (row == null || row < 0 || row >= this.getItems().size()) {
               return false;
            }

            if (!this.isRowFixable(row)) {
               int maxSpan = 1;

               for (TableColumn c : this.getColumns()) {
                  if (!list.contains(row)) {
                     return false;
                  }

                  if (this.getRowSpan(new TablePosition(this, row, c), row) > maxSpan) {
                     maxSpan = 1;
                  }
               }

               int count = row + maxSpan - 1;

               for (int index = row + 1; index <= count; index++) {
                  if (!list.contains(index)) {
                     return false;
                  }
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public final boolean isRowFixingEnabled() {
      return this.rowFixingEnabled.get();
   }

   public final void setRowFixingEnabled(boolean b) {
      this.rowFixingEnabled.set(b);
   }

   public final ReadOnlyBooleanProperty rowFixingEnabledProperty() {
      return this.rowFixingEnabled;
   }

   public final ObservableList<TableColumn> getFixedColumns() {
      return this.fixedColumns;
   }

   public final boolean isColumnFixable(int columnIndex) {
      if (columnIndex >= 0 && columnIndex < this.getColumns().size() && this.isColumnFixingEnabled()) {
         TableColumn<S, ?> column = (TableColumn<S, ?>)this.getColumns().get(columnIndex);
         return column.getParentColumn() == null;
      } else {
         return false;
      }
   }

   private boolean areTableViewColumnsFixable(List<? extends TableColumn> list) {
      return this.areColumnsFixable(list.stream().filter(Objects::nonNull).map(this.getColumns()::indexOf).collect(Collectors.toList()));
   }

   private boolean areColumnsFixable(List<? extends Integer> list) {
      if (list != null && !list.isEmpty() && this.isRowFixingEnabled()) {
         for (Integer columnIndex : list) {
            if (columnIndex == null || columnIndex < 0 || columnIndex >= this.getColumns().size()) {
               return false;
            }

            if (!this.isColumnFixable(columnIndex)) {
               int maxSpan = 1;
               int count = columnIndex + maxSpan - 1;

               for (int index = columnIndex + 1; index <= count; index++) {
                  if (!list.contains(index)) {
                     return false;
                  }
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public final boolean isColumnFixingEnabled() {
      return this.getItems() != null && this.columnFixingEnabled.get();
   }

   public final void setColumnFixingEnabled(boolean b) {
      this.columnFixingEnabled.set(b);
   }

   public final ReadOnlyBooleanProperty columnFixingEnabledProperty() {
      return this.columnFixingEnabled;
   }

   public final void setRowHeaderVisible(boolean b) {
      this.rowHeaderVisible.setValue(b);
   }

   public final boolean isRowHeaderVisible() {
      return this.rowHeaderVisible.get();
   }

   public final BooleanProperty rowHeaderVisibleProperty() {
      return this.rowHeaderVisible;
   }

   public final DoubleProperty rowHeaderWidthProperty() {
      return this.rowHeaderWidth;
   }

   public final void setRowHeaderWidth(double value) {
      this.rowHeaderWidth.setValue(value);
   }

   public final double getRowHeaderWidth() {
      return this.rowHeaderWidth.get();
   }

   public final void setRowHeader(TableColumn<S, ?> value) {
      this.rowHeader.set(value);
   }

   public final TableColumn<S, ?> getRowHeader() {
      return (TableColumn<S, ?>)this.rowHeader.get();
   }

   public final ObjectProperty<TableColumn<S, ?>> rowHeaderProperty() {
      return this.rowHeader;
   }

   public final void setRowHeaderContextMenuFactory(BiFunction<Integer, S, ContextMenu> value) {
      this.rowHeaderContextMenuFactoryProperty().set(value);
   }

   public final BiFunction<Integer, S, ContextMenu> getRowHeaderContextMenuFactory() {
      return this.rowHeaderContextMenuFactory == null ? null : (BiFunction)this.rowHeaderContextMenuFactory.get();
   }

   public final ObjectProperty<BiFunction<Integer, S, ContextMenu>> rowHeaderContextMenuFactoryProperty() {
      if (this.rowHeaderContextMenuFactory == null) {
         this.rowHeaderContextMenuFactory = new SimpleObjectProperty(this, "rowHeaderContextMenu");
      }

      return this.rowHeaderContextMenuFactory;
   }

   public final void setSouthHeaderBlended(boolean value) {
      this.southHeaderBlended.set(value);
   }

   public final boolean isSouthHeaderBlended() {
      return this.southHeaderBlended.get();
   }

   public final BooleanProperty southHeaderBlendedProperty() {
      return this.southHeaderBlended;
   }

   public TableView2.SpanType getSpanType(int rowIndex, int modelColumn) {
      if (this.getSkin() == null) {
         return TableView2.SpanType.NORMAL_CELL;
      } else if (rowIndex >= 0
         && modelColumn >= 0
         && (this.getItems() == null || rowIndex < this.getItems().size())
         && modelColumn < this.getVisibleLeafColumns().size()) {
         TablePosition<S, ?> pos = new TablePosition(this, rowIndex, (TableColumn)this.getVisibleLeafColumns().get(modelColumn));
         if (pos.getColumn() < 0) {
            return TableView2.SpanType.COLUMN_SPAN_INVISIBLE;
         } else {
            int cellRowSpan = 1;
            if (modelColumn == modelColumn && cellRowSpan == 1) {
               return TableView2.SpanType.NORMAL_CELL;
            } else {
               int cellColumnSpan = this.getColumnSpan(pos);
               boolean containsRowMinusOne = this.getSkin() == null ? true : ((TableView2Skin)this.getSkin()).containsRow(rowIndex - 1);
               boolean containsSameCellMinusOne = false;
               if (containsRowMinusOne && cellColumnSpan > 1 && modelColumn != modelColumn && cellRowSpan > 1) {
               }

               if (cellRowSpan > 1 && modelColumn == modelColumn) {
                  return TableView2.SpanType.ROW_VISIBLE;
               } else if (cellColumnSpan <= 1) {
                  return TableView2.SpanType.NORMAL_CELL;
               } else {
                  return modelColumn == modelColumn ? TableView2.SpanType.NORMAL_CELL : TableView2.SpanType.COLUMN_SPAN_INVISIBLE;
               }
            }
         }
      } else {
         return TableView2.SpanType.NORMAL_CELL;
      }
   }

   public void sort() {
      SortUtils.SortStartedEvent<TableView<S>> sortStartedEvent = new SortUtils.SortStartedEvent<>(this, this);
      this.fireEvent(sortStartedEvent);
      super.sort();
      SortUtils.SortEndedEvent<TableView<S>> sortEndedEvent = new SortUtils.SortEndedEvent<>(this, this);
      this.fireEvent(sortEndedEvent);
   }

   private void initRowFix() {
      this.rowFix = new BitSet(this.getItems() != null ? this.getItems().size() : 0);

      for (int r = 0; r < this.rowFix.size(); r++) {
         this.rowFix.set(r);
      }
   }

   private String computeReason(List<? extends Integer> list) {
      String reason = "\n A row cannot be fixed. \n";

      for (Integer row : list) {
         if (!this.isRowFixable(row)) {
            int maxSpan = 1;
            int count = row + maxSpan - 1;

            for (int index = row + 1; index < count; index++) {
               if (!list.contains(index)) {
                  reason = reason
                     + "One cell on the row "
                     + row
                     + " has a row span of "
                     + maxSpan
                     + ". But the row "
                     + index
                     + " contained within that span is not fixed.\n";
               }
            }
         }
      }

      return reason;
   }

   protected Skin<?> createDefaultSkin() {
      return new TableView2Skin<S>(this);
   }

   public String getUserAgentStylesheet() {
      if (this.stylesheet == null) {
         this.stylesheet = TableView2.class.getResource("tableview2.css").toExternalForm();
      }

      return this.stylesheet;
   }

   public static enum SpanType {
      NORMAL_CELL,
      COLUMN_SPAN_INVISIBLE,
      ROW_SPAN_INVISIBLE,
      ROW_VISIBLE,
      BOTH_INVISIBLE;
   }
}
