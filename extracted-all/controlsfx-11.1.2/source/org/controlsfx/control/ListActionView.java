package org.controlsfx.control;

import impl.org.controlsfx.skin.ListActionViewSkin;
import java.util.function.Consumer;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Skin;
import javafx.util.Callback;
import org.controlsfx.control.action.Action;

public class ListActionView<T> extends ControlsFXControl {
   private static final String DEFAULT_STYLE = "list-action-view";
   private final ObservableList<T> items = FXCollections.observableArrayList();
   private final ObservableList<Action> actions = FXCollections.observableArrayList();
   private ObjectProperty<Side> side;
   private ObjectProperty<Callback<ListView<T>, ListCell<T>>> cellFactory;

   public ListActionView() {
      this.getStyleClass().add("list-action-view");
   }

   public final ObservableList<T> getItems() {
      return this.items;
   }

   public final ObservableList<Action> getActions() {
      return this.actions;
   }

   public Side getSide() {
      return this.side == null ? null : (Side)this.side.get();
   }

   public ObjectProperty<Side> sideProperty() {
      if (this.side == null) {
         this.side = new SimpleObjectProperty(this, "side", Side.LEFT);
      }

      return this.side;
   }

   public void setSide(Side side) {
      this.side.set(side);
   }

   public final void setCellFactory(Callback<ListView<T>, ListCell<T>> value) {
      this.cellFactoryProperty().set(value);
   }

   public final Callback<ListView<T>, ListCell<T>> getCellFactory() {
      return this.cellFactory == null ? null : (Callback)this.cellFactory.get();
   }

   public final ObjectProperty<Callback<ListView<T>, ListCell<T>>> cellFactoryProperty() {
      if (this.cellFactory == null) {
         this.cellFactory = new SimpleObjectProperty(this, "cellFactory");
      }

      return this.cellFactory;
   }

   protected Skin<?> createDefaultSkin() {
      return new ListActionViewSkin<T>(this);
   }

   public String getUserAgentStylesheet() {
      return this.getUserAgentStylesheet(ListActionView.class, "listactionview.css");
   }

   public abstract static class ListAction<T> extends Action {
      public ListAction(Node graphic) {
         this(graphic, "");
      }

      public ListAction(Node graphic, String text) {
         super(text);
         this.setGraphic(graphic);
      }

      public abstract void initialize(ListView<T> var1);

      @Override
      protected final void setEventHandler(Consumer<ActionEvent> eventHandler) {
         super.setEventHandler(eventHandler);
      }
   }
}
