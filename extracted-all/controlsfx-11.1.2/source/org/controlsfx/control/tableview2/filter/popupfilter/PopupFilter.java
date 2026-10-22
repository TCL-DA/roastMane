package org.controlsfx.control.tableview2.filter.popupfilter;

import impl.org.controlsfx.tableview2.filter.popupfilter.PopupFilterSkin;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.control.PopupControl;
import javafx.scene.control.Skin;
import javafx.stage.Window;
import org.controlsfx.control.tableview2.FilteredTableColumn;
import org.controlsfx.control.tableview2.filter.parser.Parser;

public abstract class PopupFilter<S, T> extends PopupControl {
   private final FilteredTableColumn<S, T> tableColumn;
   protected final StringProperty text = new SimpleStringProperty(this, "text");
   private static final String DEFAULT_STYLE_CLASS = "pop-up-filter";

   public PopupFilter(FilteredTableColumn<S, T> tableColumn) {
      this.tableColumn = tableColumn;
      this.setAutoHide(true);
      this.setAutoFix(true);
      this.setHideOnEscape(true);
      this.getStyleClass().add("pop-up-filter");
   }

   public final StringProperty textProperty() {
      return this.text;
   }

   public final String getText() {
      return (String)this.text.get();
   }

   public final void setText(String value) {
      this.text.set(value);
   }

   public void showPopup() {
      Node node = this.tableColumn.getGraphic().getParent().getParent();
      if (node.getScene() == null || node.getScene().getWindow() == null) {
         throw new IllegalStateException("Can not show popup. The node must be attached to a scene/window.");
      } else if (!this.isShowing()) {
         Window parent = node.getScene().getWindow();
         this.show(
            parent,
            parent.getX() + node.localToScene(0.0, 0.0).getX() + node.getScene().getX(),
            parent.getY() + node.localToScene(0.0, 0.0).getY() + node.getScene().getY() + node.getLayoutBounds().getHeight()
         );
      }
   }

   public abstract List<String> getOperations();

   public abstract Parser<T> getParser();

   public FilteredTableColumn<S, T> getTableColumn() {
      return this.tableColumn;
   }

   protected Skin<?> createDefaultSkin() {
      return new PopupFilterSkin<>(this);
   }
}
