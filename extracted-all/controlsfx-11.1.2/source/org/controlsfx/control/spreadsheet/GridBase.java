package org.controlsfx.control.spreadsheet;

import com.sun.javafx.event.EventHandlerManager;
import impl.org.controlsfx.spreadsheet.RectangleSelection;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TreeSet;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.util.Callback;

public class GridBase implements Grid, EventTarget {
   private ObservableList<ObservableList<SpreadsheetCell>> rows;
   private int rowCount;
   private int columnCount;
   private Callback<Integer, Double> rowHeightFactory;
   private final BooleanProperty locked;
   private final EventHandlerManager eventHandlerManager = new EventHandlerManager(this);
   private final ObservableList<String> rowsHeader;
   private final ObservableList<String> columnsHeader;
   private BitSet resizableRow;
   private final TreeSet<Long> displaySelectionCells = new TreeSet<>();
   private final TreeSet<Long> noDisplaySelectionCells = new TreeSet<>();
   private final BooleanProperty displaySelection = new SimpleBooleanProperty(true);

   public GridBase(int rowCount, int columnCount) {
      this.rowCount = rowCount;
      this.columnCount = columnCount;
      this.rowsHeader = FXCollections.observableArrayList();
      this.columnsHeader = FXCollections.observableArrayList();
      this.locked = new SimpleBooleanProperty(false);
      this.rowHeightFactory = new GridBase.MapBasedRowHeightFactory(new HashMap<>());
      this.rows = FXCollections.observableArrayList();
      this.rows.addListener(observable -> this.setRowCount(this.rows.size()));
      this.resizableRow = new BitSet(rowCount);
      this.resizableRow.set(0, rowCount, true);
   }

   @Override
   public ObservableList<ObservableList<SpreadsheetCell>> getRows() {
      return this.rows;
   }

   @Override
   public void setCellValue(int modelRow, int column, Object value) {
      if (modelRow < this.getRowCount() && column < this.columnCount && !this.isLocked()) {
         SpreadsheetCell cell = (SpreadsheetCell)((ObservableList)this.getRows().get(modelRow)).get(column);
         Object previousItem = cell.getItem();
         Object convertedValue = cell.getCellType().convertValue(value);
         cell.setItem(convertedValue);
         if (!Objects.equals(previousItem, cell.getItem())) {
            GridChange cellChange = new GridChange(cell.getRow(), cell.getColumn(), previousItem, convertedValue);
            Event.fireEvent(this, cellChange);
         }
      }
   }

   @Override
   public int getRowCount() {
      return this.rowCount;
   }

   @Override
   public int getColumnCount() {
      return this.columnCount;
   }

   @Override
   public double getRowHeight(int row) {
      return (Double)this.rowHeightFactory.call(row);
   }

   public void setRowHeightCallback(Callback<Integer, Double> rowHeight) {
      this.rowHeightFactory = rowHeight;
   }

   @Override
   public ObservableList<String> getRowHeaders() {
      return this.rowsHeader;
   }

   @Override
   public ObservableList<String> getColumnHeaders() {
      return this.columnsHeader;
   }

   public BooleanProperty lockedProperty() {
      return this.locked;
   }

   public boolean isLocked() {
      return this.locked.get();
   }

   public void setLocked(Boolean lock) {
      this.locked.setValue(lock);
   }

   @Override
   public void spanRow(int count, int rowIndex, int colIndex) {
      if (count > 0 && count <= this.getRowCount() && rowIndex < this.getRowCount() && colIndex < this.columnCount) {
         SpreadsheetCell cell = (SpreadsheetCell)((ObservableList)this.rows.get(rowIndex)).get(colIndex);
         int colSpan = cell.getColumnSpan();
         int rowSpan = count;
         cell.setRowSpan(count);

         for (int row = rowIndex; row < rowIndex + rowSpan && row < this.getRowCount(); row++) {
            for (int col = colIndex; col < colIndex + colSpan && col < this.columnCount; col++) {
               if (row != rowIndex || col != colIndex) {
                  ((ObservableList)this.rows.get(row)).set(col, cell);
               }
            }
         }
      }
   }

   @Override
   public void spanColumn(int count, int rowIndex, int colIndex) {
      if (count > 0 && count <= this.columnCount && rowIndex < this.getRowCount() && colIndex < this.columnCount) {
         SpreadsheetCell cell = (SpreadsheetCell)((ObservableList)this.rows.get(rowIndex)).get(colIndex);
         int colSpan = count;
         int rowSpan = cell.getRowSpan();
         cell.setColumnSpan(count);

         for (int row = rowIndex; row < rowIndex + rowSpan && row < this.getRowCount(); row++) {
            for (int col = colIndex; col < colIndex + colSpan && col < this.columnCount; col++) {
               if (row != rowIndex || col != colIndex) {
                  ((ObservableList)this.rows.get(row)).set(col, cell);
               }
            }
         }
      }
   }

   @Override
   public void setRows(Collection<ObservableList<SpreadsheetCell>> rows) {
      this.rows.clear();
      this.rows.addAll(rows);
      this.setRowCount(rows.size());
      this.setColumnCount(this.rowCount == 0 ? 0 : ((ObservableList)this.rows.get(0)).size());
   }

   public void setResizableRows(BitSet resizableRow) {
      this.resizableRow = resizableRow;
   }

   @Override
   public boolean isRowResizable(int row) {
      return this.resizableRow.get(row);
   }

   @Override
   public boolean isDisplaySelection() {
      return this.displaySelection.get();
   }

   @Override
   public void setDisplaySelection(boolean value) {
      this.displaySelection.setValue(value);
   }

   @Override
   public BooleanProperty displaySelectionProperty() {
      return this.displaySelection;
   }

   @Override
   public void setCellDisplaySelection(int row, int column, boolean displaySelection) {
      Long key = RectangleSelection.SelectionRange.key(row, column);
      if (displaySelection) {
         this.displaySelectionCells.add(key);
         this.noDisplaySelectionCells.remove(key);
      } else {
         this.displaySelectionCells.remove(key);
         this.noDisplaySelectionCells.add(key);
      }
   }

   @Override
   public boolean isCellDisplaySelection(int row, int column) {
      Long key = RectangleSelection.SelectionRange.key(row, column);
      if (this.displaySelectionCells.contains(key)) {
         return true;
      } else {
         return this.noDisplaySelectionCells.contains(key) ? false : this.isDisplaySelection();
      }
   }

   @Override
   public <E extends GridChange> void addEventHandler(EventType<E> eventType, EventHandler<E> eventHandler) {
      this.eventHandlerManager.addEventHandler(eventType, eventHandler);
   }

   @Override
   public <E extends GridChange> void removeEventHandler(EventType<E> eventType, EventHandler<E> eventHandler) {
      this.eventHandlerManager.removeEventHandler(eventType, eventHandler);
   }

   public EventDispatchChain buildEventDispatchChain(EventDispatchChain tail) {
      return tail.append(this.eventHandlerManager);
   }

   private void setRowCount(int rowCount) {
      this.rowCount = rowCount;
   }

   private void setColumnCount(int columnCount) {
      this.columnCount = columnCount;
   }

   public static class MapBasedRowHeightFactory implements Callback<Integer, Double> {
      private final Map<Integer, Double> rowHeightMap;

      public MapBasedRowHeightFactory(Map<Integer, Double> rowHeightMap) {
         this.rowHeightMap = rowHeightMap;
      }

      public Double call(Integer index) {
         Double value = this.rowHeightMap.get(index);
         return value == null ? -1.0 : value;
      }
   }
}
