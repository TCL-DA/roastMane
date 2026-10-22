package org.controlsfx.control.spreadsheet;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableSet;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.control.MenuItem;

public interface SpreadsheetCell {
   EventType<Event> EDITABLE_EVENT_TYPE = new EventType("EditableEventType" + UUID.randomUUID().toString());
   EventType<Event> WRAP_EVENT_TYPE = new EventType("WrapTextEventType" + UUID.randomUUID().toString());
   EventType<Event> CORNER_EVENT_TYPE = new EventType("CornerEventType" + UUID.randomUUID().toString());

   boolean match(Object var1);

   void setItem(Object var1);

   Object getItem();

   ObjectProperty<Object> itemProperty();

   boolean isEditable();

   void setEditable(boolean var1);

   boolean isWrapText();

   boolean isCellGraphic();

   void setCellGraphic(boolean var1);

   void setWrapText(boolean var1);

   List<Object> getOptionsForEditor();

   boolean hasPopup();

   void setHasPopup(boolean var1);

   List<MenuItem> getPopupItems();

   void setStyle(String var1);

   String getStyle();

   StringProperty styleProperty();

   void activateCorner(SpreadsheetCell.CornerPosition var1);

   void deactivateCorner(SpreadsheetCell.CornerPosition var1);

   boolean isCornerActivated(SpreadsheetCell.CornerPosition var1);

   StringProperty formatProperty();

   String getFormat();

   void setFormat(String var1);

   ReadOnlyStringProperty textProperty();

   String getText();

   SpreadsheetCellType getCellType();

   int getRow();

   int getColumn();

   int getRowSpan();

   void setRowSpan(int var1);

   int getColumnSpan();

   void setColumnSpan(int var1);

   ObservableSet<String> getStyleClass();

   ObjectProperty<Node> graphicProperty();

   void setGraphic(Node var1);

   Node getGraphic();

   Optional<String> getTooltip();

   void addEventHandler(EventType<Event> var1, EventHandler<Event> var2);

   void removeEventHandler(EventType<Event> var1, EventHandler<Event> var2);

   public static enum CornerPosition {
      TOP_LEFT,
      TOP_RIGHT,
      BOTTOM_RIGHT,
      BOTTOM_LEFT;
   }
}
