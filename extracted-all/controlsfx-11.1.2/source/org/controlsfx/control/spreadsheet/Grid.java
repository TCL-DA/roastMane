package org.controlsfx.control.spreadsheet;

import java.util.Collection;
import javafx.beans.property.BooleanProperty;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.event.EventType;

public interface Grid {
   double AUTOFIT = -1.0;

   int getRowCount();

   int getColumnCount();

   ObservableList<ObservableList<SpreadsheetCell>> getRows();

   void setCellValue(int var1, int var2, Object var3);

   double getRowHeight(int var1);

   boolean isRowResizable(int var1);

   ObservableList<String> getRowHeaders();

   ObservableList<String> getColumnHeaders();

   void spanRow(int var1, int var2, int var3);

   void spanColumn(int var1, int var2, int var3);

   void setRows(Collection<ObservableList<SpreadsheetCell>> var1);

   boolean isDisplaySelection();

   void setDisplaySelection(boolean var1);

   BooleanProperty displaySelectionProperty();

   void setCellDisplaySelection(int var1, int var2, boolean var3);

   boolean isCellDisplaySelection(int var1, int var2);

   <E extends GridChange> void addEventHandler(EventType<E> var1, EventHandler<E> var2);

   <E extends GridChange> void removeEventHandler(EventType<E> var1, EventHandler<E> var2);
}
