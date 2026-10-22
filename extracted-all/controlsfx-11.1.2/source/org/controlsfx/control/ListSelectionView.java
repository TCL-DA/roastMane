package org.controlsfx.control;

import impl.org.controlsfx.i18n.Localization;
import impl.org.controlsfx.skin.ListSelectionViewSkin;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Skin;
import javafx.scene.input.KeyCombination;
import javafx.util.Callback;
import org.controlsfx.control.action.Action;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;

public class ListSelectionView<T> extends ControlsFXControl {
   private static final String DEFAULT_STYLECLASS = "list-selection-view";
   private final ObjectProperty<Node> sourceHeader = new SimpleObjectProperty(this, "sourceHeader");
   private final ObjectProperty<Node> sourceFooter = new SimpleObjectProperty(this, "sourceFooter");
   private final ObjectProperty<Node> targetHeader = new SimpleObjectProperty(this, "targetHeader");
   private final ObjectProperty<Node> targetFooter = new SimpleObjectProperty(this, "targetFooter");
   private ObjectProperty<ObservableList<T>> sourceItems;
   private ObjectProperty<ObservableList<T>> targetItems;
   private final ObjectProperty<Orientation> orientation = new SimpleObjectProperty(this, "orientation", Orientation.HORIZONTAL);
   private ObjectProperty<Callback<ListView<T>, ListCell<T>>> cellFactory;
   private ObservableList<Action> actions = FXCollections.observableArrayList(
      new Action[]{
         new ListSelectionView.MoveToTarget(),
         new ListSelectionView.MoveToTargetAll(),
         new ListSelectionView.MoveToSource(),
         new ListSelectionView.MoveToSourceAll()
      }
   );
   private ObservableList<Action> sourceActions = FXCollections.observableArrayList();
   private ObservableList<Action> targetActions = FXCollections.observableArrayList();

   public ListSelectionView() {
      this.getStyleClass().add("list-selection-view");
      Label sourceHeader = new Label(Localization.localize(Localization.asKey("listSelectionView.header.source")));
      sourceHeader.getStyleClass().add("list-header-label");
      sourceHeader.setId("source-header-label");
      this.setSourceHeader(sourceHeader);
      Label targetHeader = new Label(Localization.localize(Localization.asKey("listSelectionView.header.target")));
      targetHeader.getStyleClass().add("list-header-label");
      targetHeader.setId("target-header-label");
      this.setTargetHeader(targetHeader);
   }

   protected Skin<ListSelectionView<T>> createDefaultSkin() {
      return new ListSelectionViewSkin<T>(this);
   }

   public String getUserAgentStylesheet() {
      return this.getUserAgentStylesheet(ListSelectionView.class, "listselectionview.css");
   }

   public final ObjectProperty<Node> sourceHeaderProperty() {
      return this.sourceHeader;
   }

   public final Node getSourceHeader() {
      return (Node)this.sourceHeader.get();
   }

   public final void setSourceHeader(Node node) {
      this.sourceHeader.set(node);
   }

   public final ObjectProperty<Node> sourceFooterProperty() {
      return this.sourceFooter;
   }

   public final Node getSourceFooter() {
      return (Node)this.sourceFooter.get();
   }

   public final void setSourceFooter(Node node) {
      this.sourceFooter.set(node);
   }

   public final ObjectProperty<Node> targetHeaderProperty() {
      return this.targetHeader;
   }

   public final Node getTargetHeader() {
      return (Node)this.targetHeader.get();
   }

   public final void setTargetHeader(Node node) {
      this.targetHeader.set(node);
   }

   public final ObjectProperty<Node> targetFooterProperty() {
      return this.targetFooter;
   }

   public final Node getTargetFooter() {
      return (Node)this.targetFooter.get();
   }

   public final void setTargetFooter(Node node) {
      this.targetFooter.set(node);
   }

   public final void setSourceItems(ObservableList<T> value) {
      this.sourceItemsProperty().set(value);
   }

   public final ObservableList<T> getSourceItems() {
      return (ObservableList<T>)this.sourceItemsProperty().get();
   }

   public final ObjectProperty<ObservableList<T>> sourceItemsProperty() {
      if (this.sourceItems == null) {
         this.sourceItems = new SimpleObjectProperty(this, "sourceItems", FXCollections.observableArrayList());
      }

      return this.sourceItems;
   }

   public final void setTargetItems(ObservableList<T> value) {
      this.targetItemsProperty().set(value);
   }

   public final ObservableList<T> getTargetItems() {
      return (ObservableList<T>)this.targetItemsProperty().get();
   }

   public final ObjectProperty<ObservableList<T>> targetItemsProperty() {
      if (this.targetItems == null) {
         this.targetItems = new SimpleObjectProperty(this, "targetItems", FXCollections.observableArrayList());
      }

      return this.targetItems;
   }

   public final ObjectProperty<Orientation> orientationProperty() {
      return this.orientation;
   }

   public final void setOrientation(Orientation value) {
      this.orientationProperty().set(value);
   }

   public final Orientation getOrientation() {
      return (Orientation)this.orientation.get();
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

   public final ObservableList<Action> getActions() {
      return this.actions;
   }

   public final ObservableList<Action> getSourceActions() {
      return this.sourceActions;
   }

   public final ObservableList<Action> getTargetActions() {
      return this.targetActions;
   }

   private static Glyph getGlyph(FontAwesome.Glyph angleDoubleDown) {
      return new FontAwesome().create(angleDoubleDown);
   }

   private static <T> void moveToTarget(ListView<T> sourceListView, ListView<T> targetListView) {
      move(sourceListView, targetListView);
      sourceListView.getSelectionModel().clearSelection();
   }

   private static <T> void moveToTargetAll(ListView<T> sourceListView, ListView<T> targetListView) {
      move(sourceListView, targetListView, new ArrayList<>(sourceListView.getItems()));
      sourceListView.getSelectionModel().clearSelection();
   }

   private static <T> void moveToSource(ListView<T> sourceListView, ListView<T> targetListView) {
      move(targetListView, sourceListView);
      targetListView.getSelectionModel().clearSelection();
   }

   private static <T> void moveToSourceAll(ListView<T> sourceListView, ListView<T> targetListView) {
      move(targetListView, sourceListView, new ArrayList<>(targetListView.getItems()));
      targetListView.getSelectionModel().clearSelection();
   }

   private static <T> void move(ListView<T> source, ListView<T> target) {
      List<T> selectedItems = new ArrayList<>(source.getSelectionModel().getSelectedItems());
      move(source, target, selectedItems);
   }

   private static <T> void move(ListView<T> source, ListView<T> target, List<T> items) {
      source.getItems().removeAll(items);
      target.getItems().addAll(items);
   }

   public abstract static class ListSelectionAction<T> extends Action {
      public ListSelectionAction(Node graphic) {
         this(graphic, "");
      }

      public ListSelectionAction(Node graphic, String text) {
         super(text);
         this.setGraphic(graphic);
      }

      public abstract void initialize(ListView<T> var1, ListView<T> var2);

      @Override
      protected final void setEventHandler(Consumer<ActionEvent> eventHandler) {
         super.setEventHandler(eventHandler);
      }
   }

   public class MoveToSource extends ListSelectionView.ListSelectionAction<T> {
      public MoveToSource() {
         super(ListSelectionView.getGlyph(FontAwesome.Glyph.ANGLE_LEFT));
         this.getStyleClass().add("move-to-source-button");
         this.graphicProperty()
            .bind(
               Bindings.createObjectBinding(
                  () -> ListSelectionView.this.getOrientation() == Orientation.HORIZONTAL
                        ? ListSelectionView.getGlyph(FontAwesome.Glyph.ANGLE_LEFT)
                        : ListSelectionView.getGlyph(FontAwesome.Glyph.ANGLE_UP),
                  new Observable[]{ListSelectionView.this.orientationProperty()}
               )
            );
         this.setAccelerator(KeyCombination.keyCombination("CTRL+LEFT"));
      }

      @Override
      public void initialize(ListView<T> sourceListView, ListView<T> targetListView) {
         this.disabledProperty().bind(Bindings.isEmpty(targetListView.getSelectionModel().getSelectedItems()));
         this.setEventHandler(ae -> ListSelectionView.moveToSource(sourceListView, targetListView));
      }
   }

   public class MoveToSourceAll extends ListSelectionView.ListSelectionAction<T> {
      public MoveToSourceAll() {
         super(ListSelectionView.getGlyph(FontAwesome.Glyph.ANGLE_DOUBLE_LEFT));
         this.getStyleClass().add("move-to-source-all-button");
         this.graphicProperty()
            .bind(
               Bindings.createObjectBinding(
                  () -> ListSelectionView.this.getOrientation() == Orientation.HORIZONTAL
                        ? ListSelectionView.getGlyph(FontAwesome.Glyph.ANGLE_DOUBLE_LEFT)
                        : ListSelectionView.getGlyph(FontAwesome.Glyph.ANGLE_DOUBLE_UP),
                  new Observable[]{ListSelectionView.this.orientationProperty()}
               )
            );
         this.setAccelerator(KeyCombination.keyCombination("CTRL+SHIFT+LEFT"));
      }

      @Override
      public void initialize(ListView<T> sourceListView, ListView<T> targetListView) {
         this.disabledProperty().bind(Bindings.isEmpty(targetListView.getItems()));
         this.setEventHandler(ae -> ListSelectionView.moveToSourceAll(sourceListView, targetListView));
      }
   }

   public class MoveToTarget extends ListSelectionView.ListSelectionAction<T> {
      public MoveToTarget() {
         super(ListSelectionView.getGlyph(FontAwesome.Glyph.ANGLE_RIGHT));
         this.getStyleClass().add("move-to-target-button");
         this.setAccelerator(KeyCombination.keyCombination("CTRL+RIGHT"));
         this.graphicProperty()
            .bind(
               Bindings.createObjectBinding(
                  () -> ListSelectionView.this.getOrientation() == Orientation.HORIZONTAL
                        ? ListSelectionView.getGlyph(FontAwesome.Glyph.ANGLE_RIGHT)
                        : ListSelectionView.getGlyph(FontAwesome.Glyph.ANGLE_DOWN),
                  new Observable[]{ListSelectionView.this.orientationProperty()}
               )
            );
      }

      @Override
      public void initialize(ListView<T> sourceListView, ListView<T> targetListView) {
         this.disabledProperty().bind(Bindings.isEmpty(sourceListView.getSelectionModel().getSelectedItems()));
         this.setEventHandler(ae -> ListSelectionView.moveToTarget(sourceListView, targetListView));
      }
   }

   public class MoveToTargetAll extends ListSelectionView.ListSelectionAction<T> {
      public MoveToTargetAll() {
         super(ListSelectionView.getGlyph(FontAwesome.Glyph.ANGLE_DOUBLE_RIGHT));
         this.getStyleClass().add("move-to-target-all-button");
         this.graphicProperty()
            .bind(
               Bindings.createObjectBinding(
                  () -> ListSelectionView.this.getOrientation() == Orientation.HORIZONTAL
                        ? ListSelectionView.getGlyph(FontAwesome.Glyph.ANGLE_DOUBLE_RIGHT)
                        : ListSelectionView.getGlyph(FontAwesome.Glyph.ANGLE_DOUBLE_DOWN),
                  new Observable[]{ListSelectionView.this.orientationProperty()}
               )
            );
         this.setAccelerator(KeyCombination.keyCombination("CTRL+SHIFT+RIGHT"));
      }

      @Override
      public void initialize(ListView<T> sourceListView, ListView<T> targetListView) {
         this.disabledProperty().bind(Bindings.isEmpty(sourceListView.getItems()));
         this.setEventHandler(ae -> ListSelectionView.moveToTargetAll(sourceListView, targetListView));
      }
   }
}
