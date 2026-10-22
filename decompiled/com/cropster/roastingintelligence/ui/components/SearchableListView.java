package com.cropster.roastingintelligence.ui.components;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.ScrollToEvent;
import javafx.scene.control.TextField;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.controlsfx.control.textfield.TextFields;

public class SearchableListView<T> extends VBox {
   public static final String[] DEFAULT_LIST_STYLE_CLASSES = new String[]{"selectable-list-view", "bordered-list"};
   private final ListView<T> listView;
   private final TextField searchTextField = TextFields.createClearableTextField();

   public SearchableListView() {
      this.searchTextField.setPromptText(org.cropster.roastingintelligence.util.Messages.m_("Select or type to search"));
      this.getChildren().add(this.searchTextField);
      this.listView = new ListView();
      this.listView.getStyleClass().addAll(DEFAULT_LIST_STYLE_CLASSES);
      this.listView.setEditable(false);
      this.listView.editableProperty().addListener((s, o, n) -> {
         if (n != null && n) {
            throw new UnsupportedOperationException("The inner ListView cannot be editable");
         }
      });
      setVgrow(this.listView, Priority.ALWAYS);
      this.getChildren().add(this.listView);
   }

   public SearchableListView(ObservableList<T> items) {
      this();
      this.listView.setItems(items);
   }

   public ListView<T> getListView() {
      return this.listView;
   }

   public TextField getSearchTextField() {
      return this.searchTextField;
   }

   public ObjectProperty<ObservableList<T>> itemsProperty() {
      return this.listView.itemsProperty();
   }

   public ObservableList<T> getItems() {
      return this.listView.getItems();
   }

   public void setItems(ObservableList<T> value) {
      this.listView.setItems(value);
   }

   public ObjectProperty<Node> placeholderProperty() {
      return this.listView.placeholderProperty();
   }

   public Node getPlaceholder() {
      return this.listView.getPlaceholder();
   }

   public void setPlaceholder(Node value) {
      this.listView.setPlaceholder(value);
   }

   public ObjectProperty<MultipleSelectionModel<T>> selectionModelProperty() {
      return this.listView.selectionModelProperty();
   }

   public MultipleSelectionModel<T> getSelectionModel() {
      return this.listView.getSelectionModel();
   }

   public void setSelectionModel(MultipleSelectionModel<T> value) {
      this.listView.setSelectionModel(value);
   }

   public ObjectProperty<Callback<ListView<T>, ListCell<T>>> cellFactoryProperty() {
      return this.listView.cellFactoryProperty();
   }

   public Callback<ListView<T>, ListCell<T>> getCellFactory() {
      return this.listView.getCellFactory();
   }

   public void setCellFactory(Callback<ListView<T>, ListCell<T>> value) {
      this.listView.setCellFactory(value);
   }

   public final DoubleProperty fixedCellSizeProperty() {
      return this.listView.fixedCellSizeProperty();
   }

   public final double getFixedCellSize() {
      return this.listView.getFixedCellSize();
   }

   public final void setFixedCellSize(double value) {
      this.listView.setFixedCellSize(value);
   }

   public void scrollTo(int index) {
      this.listView.scrollTo(index);
   }

   public void scrollTo(T object) {
      this.listView.scrollTo(object);
   }

   public ObjectProperty<EventHandler<ScrollToEvent<Integer>>> onScrollToProperty() {
      return this.listView.onScrollToProperty();
   }

   public EventHandler<ScrollToEvent<Integer>> getOnScrollTo() {
      return this.listView.getOnScrollTo();
   }

   public void setOnScrollTo(EventHandler<ScrollToEvent<Integer>> value) {
      this.listView.setOnScrollTo(value);
   }

   public void refresh() {
      this.listView.refresh();
   }
}
