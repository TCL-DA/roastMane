package org.controlsfx.control.spreadsheet;

import java.io.Serializable;
import java.util.UUID;
import javafx.event.Event;
import javafx.event.EventType;

public class GridChange extends Event implements Serializable {
   public static final EventType<GridChange> GRID_CHANGE_EVENT = new EventType(Event.ANY, "GridChange" + UUID.randomUUID().toString());
   private static final long serialVersionUID = 210644901287223524L;
   private final int modelRow;
   private final int column;
   private final Object oldValue;
   private final Object newValue;

   public GridChange(int modelRow, int column, Object oldValue, Object newValue) {
      super(GRID_CHANGE_EVENT);
      this.modelRow = modelRow;
      this.column = column;
      this.oldValue = oldValue;
      this.newValue = newValue;
   }

   public int getRow() {
      return this.modelRow;
   }

   public int getColumn() {
      return this.column;
   }

   public Object getOldValue() {
      return this.oldValue;
   }

   public Object getNewValue() {
      return this.newValue;
   }
}
