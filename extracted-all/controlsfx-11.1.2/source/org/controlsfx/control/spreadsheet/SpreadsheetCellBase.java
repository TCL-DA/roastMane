package org.controlsfx.control.spreadsheet;

import com.sun.javafx.event.EventHandlerManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.control.MenuItem;

public class SpreadsheetCellBase implements SpreadsheetCell, EventTarget {
   private static final int EDITABLE_BIT_POSITION = 4;
   private static final int WRAP_BIT_POSITION = 5;
   private static final int POPUP_BIT_POSITION = 6;
   private static final int IS_BROWSER_POSITION = 7;
   private final SpreadsheetCellType type;
   private final int row;
   private final int column;
   private int rowSpan;
   private int columnSpan;
   private final StringProperty format;
   private final StringProperty text;
   private final StringProperty styleProperty;
   private final ObjectProperty<Node> graphic;
   private String tooltip;
   private int propertyContainer = 0;
   private final EventHandlerManager eventHandlerManager = new EventHandlerManager(this);
   private ObservableSet<String> styleClass;
   private List<MenuItem> actionsList;
   private final ObjectProperty<Object> item = new SimpleObjectProperty<Object>(this, "item") {
      protected void invalidated() {
         SpreadsheetCellBase.this.updateText();
      }
   };

   public SpreadsheetCellBase(int row, int column, int rowSpan, int columnSpan) {
      this(row, column, rowSpan, columnSpan, SpreadsheetCellType.OBJECT);
   }

   public SpreadsheetCellBase(int row, int column, int rowSpan, int columnSpan, SpreadsheetCellType<?> type) {
      this.row = row;
      this.column = column;
      this.rowSpan = rowSpan;
      this.columnSpan = columnSpan;
      this.type = type;
      this.text = new SimpleStringProperty("");
      this.format = new SimpleStringProperty("");
      this.graphic = new SimpleObjectProperty();
      this.format.addListener(new ChangeListener<String>() {
         public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
            SpreadsheetCellBase.this.updateText();
         }
      });
      this.setEditable(true);
      this.getStyleClass().add("spreadsheet-cell");
      this.styleProperty = new SimpleStringProperty();
   }

   @Override
   public boolean match(Object value) {
      return this.type.match(value, this.getOptionsForEditor());
   }

   @Override
   public final void setItem(Object value) {
      if (this.isEditable()) {
         this.item.set(value);
      }
   }

   @Override
   public final Object getItem() {
      return this.item.get();
   }

   @Override
   public final ObjectProperty<Object> itemProperty() {
      return this.item;
   }

   @Override
   public final boolean isEditable() {
      return this.isSet(4);
   }

   @Override
   public final void setEditable(boolean editable) {
      if (this.setMask(editable, 4)) {
         Event.fireEvent(this, new Event(EDITABLE_EVENT_TYPE));
      }
   }

   @Override
   public boolean isWrapText() {
      return this.isSet(5);
   }

   @Override
   public boolean isCellGraphic() {
      return this.isSet(7);
   }

   @Override
   public void setCellGraphic(boolean isBrowser) {
      this.setMask(isBrowser, 7);
   }

   @Override
   public void setWrapText(boolean wrapText) {
      if (this.setMask(wrapText, 5)) {
         Event.fireEvent(this, new Event(WRAP_EVENT_TYPE));
      }
   }

   @Override
   public List<Object> getOptionsForEditor() {
      return Collections.emptyList();
   }

   @Override
   public boolean hasPopup() {
      return this.isSet(6);
   }

   @Override
   public void setHasPopup(boolean value) {
      this.setMask(value, 6);
      Event.fireEvent(this, new Event(CORNER_EVENT_TYPE));
   }

   @Override
   public List<MenuItem> getPopupItems() {
      if (this.actionsList == null) {
         this.actionsList = new ArrayList<>();
      }

      return this.actionsList;
   }

   @Override
   public final StringProperty formatProperty() {
      return this.format;
   }

   @Override
   public final String getFormat() {
      return (String)this.format.get();
   }

   @Override
   public final void setFormat(String format) {
      this.formatProperty().set(format);
      this.updateText();
   }

   @Override
   public final ReadOnlyStringProperty textProperty() {
      return this.text;
   }

   @Override
   public final String getText() {
      return (String)this.text.get();
   }

   @Override
   public final SpreadsheetCellType getCellType() {
      return this.type;
   }

   @Override
   public final int getRow() {
      return this.row;
   }

   @Override
   public final int getColumn() {
      return this.column;
   }

   @Override
   public final int getRowSpan() {
      return this.rowSpan;
   }

   @Override
   public final void setRowSpan(int rowSpan) {
      this.rowSpan = rowSpan;
   }

   @Override
   public final int getColumnSpan() {
      return this.columnSpan;
   }

   @Override
   public final void setColumnSpan(int columnSpan) {
      this.columnSpan = columnSpan;
   }

   @Override
   public final ObservableSet<String> getStyleClass() {
      if (this.styleClass == null) {
         this.styleClass = FXCollections.observableSet(new String[0]);
      }

      return this.styleClass;
   }

   @Override
   public void setStyle(String style) {
      this.styleProperty.set(style);
   }

   @Override
   public String getStyle() {
      return (String)this.styleProperty.get();
   }

   @Override
   public StringProperty styleProperty() {
      return this.styleProperty;
   }

   @Override
   public ObjectProperty<Node> graphicProperty() {
      return this.graphic;
   }

   @Override
   public void setGraphic(Node graphic) {
      this.graphic.set(graphic);
   }

   @Override
   public Node getGraphic() {
      return (Node)this.graphic.get();
   }

   @Override
   public Optional<String> getTooltip() {
      return Optional.ofNullable(this.tooltip);
   }

   public void setTooltip(String tooltip) {
      this.tooltip = tooltip;
   }

   @Override
   public void activateCorner(SpreadsheetCell.CornerPosition position) {
      if (this.setMask(true, this.getCornerBitNumber(position))) {
         Event.fireEvent(this, new Event(CORNER_EVENT_TYPE));
      }
   }

   @Override
   public void deactivateCorner(SpreadsheetCell.CornerPosition position) {
      if (this.setMask(false, this.getCornerBitNumber(position))) {
         Event.fireEvent(this, new Event(CORNER_EVENT_TYPE));
      }
   }

   @Override
   public boolean isCornerActivated(SpreadsheetCell.CornerPosition position) {
      return this.isSet(this.getCornerBitNumber(position));
   }

   public EventDispatchChain buildEventDispatchChain(EventDispatchChain tail) {
      return tail.append(this.eventHandlerManager);
   }

   @Override
   public String toString() {
      return "cell[" + this.row + "][" + this.column + "]" + this.rowSpan + "-" + this.columnSpan;
   }

   @Override
   public final boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (!(obj instanceof SpreadsheetCell)) {
         return false;
      } else {
         SpreadsheetCell otherCell = (SpreadsheetCell)obj;
         return otherCell.getRow() == this.row
            && otherCell.getColumn() == this.column
            && Objects.equals(otherCell.getText(), this.getText())
            && this.rowSpan == otherCell.getRowSpan()
            && this.columnSpan == otherCell.getColumnSpan()
            && Objects.equals(this.getStyleClass(), otherCell.getStyleClass());
      }
   }

   @Override
   public final int hashCode() {
      int prime = 31;
      int result = 1;
      result = 31 * result + this.column;
      result = 31 * result + this.row;
      result = 31 * result + this.rowSpan;
      result = 31 * result + this.columnSpan;
      result = 31 * result + Objects.hashCode(this.getText());
      return 31 * result + Objects.hashCode(this.getStyleClass());
   }

   @Override
   public void addEventHandler(EventType<Event> eventType, EventHandler<Event> eventHandler) {
      this.eventHandlerManager.addEventHandler(eventType, eventHandler);
   }

   @Override
   public void removeEventHandler(EventType<Event> eventType, EventHandler<Event> eventHandler) {
      this.eventHandlerManager.removeEventHandler(eventType, eventHandler);
   }

   protected void updateText() {
      if (this.getItem() == null) {
         this.text.setValue("");
      } else if (!"".equals(this.getFormat())) {
         this.text.setValue(this.type.toString(this.getItem(), this.getFormat()));
      } else {
         this.text.setValue(this.type.toString(this.getItem()));
      }
   }

   private int getCornerBitNumber(SpreadsheetCell.CornerPosition position) {
      switch (position) {
         case TOP_LEFT:
            return 0;
         case TOP_RIGHT:
            return 1;
         case BOTTOM_RIGHT:
            return 2;
         case BOTTOM_LEFT:
         default:
            return 3;
      }
   }

   private boolean setMask(boolean flag, int position) {
      int oldCorner = this.propertyContainer;
      if (flag) {
         this.propertyContainer |= 1 << position;
      } else {
         this.propertyContainer &= ~(1 << position);
      }

      return this.propertyContainer != oldCorner;
   }

   private boolean isSet(int position) {
      return (this.propertyContainer & 1 << position) != 0;
   }
}
