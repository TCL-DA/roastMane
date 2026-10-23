package com.cropster.roastingintelligence.ui.list;

import com.cropster.roastingintelligence.ui.JavaFxUtil;
import com.cropster.roastingintelligence.ui.components.RandomShortcutPane;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class EmptyStateList<T> extends StackPane implements Initializable {
   @FXML
   private VBox infoBox;
   @FXML
   private Label emptyStateLabel;
   @FXML
   private Label emptyStateNote;
   @FXML
   private RandomShortcutPane randomShortcutPane;
   @FXML
   private ScrollPane listViewScroller;
   @FXML
   private ListView<T> listView;
   private final ListChangeListener<T> listChangeListener = c -> this.infoBox.setVisible(this.listView.getItems().isEmpty());
   private final BooleanProperty showRandomShortcuts = new SimpleBooleanProperty(true);

   public EmptyStateList() {
      JavaFxUtil.loadComponent(this);
   }

   public void initialize(URL url, ResourceBundle resourceBundle) {
      JavaFxUtil.setDefaultLineSpacing(this);
      if (this.listView.getItems() != null) {
         this.listView.getItems().addListener(this.listChangeListener);
      }

      this.listView.getStyleClass().add("transparent-list");
      this.infoBox.visibleProperty().addListener((s, o, n) -> {
         if (n) {
            this.randomShortcutPane.update();
         }
      });
      this.infoBox.setVisible(this.listView.getItems() != null && this.listView.getItems().isEmpty());
      this.emptyStateNote.visibleProperty().bind(this.emptyStateNote.textProperty().isNotEmpty());
      this.randomShortcutPane.visibleProperty().bind(this.showRandomShortcuts);
   }

   public StringProperty emptyStateMessageProperty() {
      return this.emptyStateLabel.textProperty();
   }

   public String getEmptyStateMessage() {
      return this.emptyStateLabel.getText();
   }

   public void setEmptyStateMessage(String message) {
      this.emptyStateLabel.setText(message);
   }

   public StringProperty emptyStateNoteProperty() {
      return this.emptyStateNote.textProperty();
   }

   public String getEmptyStateNote() {
      return this.emptyStateNote.getText();
   }

   public void setEmptyStateNote(String message) {
      this.emptyStateNote.setText(message);
   }

   public ListView<T> getListView() {
      return this.listView;
   }

   public void setListViewItems(ObservableList<T> items) {
      ObservableList<T> oldItems = this.listView.getItems();
      if (oldItems != null) {
         oldItems.removeListener(this.listChangeListener);
      }

      this.listView.setItems(items);
      ObservableList<T> newItems = this.listView.getItems();
      if (newItems != null) {
         newItems.addListener(this.listChangeListener);
         this.infoBox.setVisible(newItems.isEmpty());
      } else {
         this.infoBox.setVisible(true);
      }
   }

   public ScrollPane getListViewScroller() {
      return this.listViewScroller;
   }

   public RandomShortcutPane getRandomShortcutPane() {
      return this.randomShortcutPane;
   }

   public BooleanProperty showRandomShortcutsProperty() {
      return this.showRandomShortcuts;
   }

   public boolean isShowRandomShortcuts() {
      return this.showRandomShortcuts.get();
   }

   public void setShowRandomShortcuts(boolean showRandomShortcuts) {
      this.showRandomShortcuts.set(showRandomShortcuts);
   }

   public ReadOnlyBooleanProperty listEmptyProperty() {
      return this.infoBox.visibleProperty();
   }

   public boolean isListEmpty() {
      return this.infoBox.isVisible();
   }
}
