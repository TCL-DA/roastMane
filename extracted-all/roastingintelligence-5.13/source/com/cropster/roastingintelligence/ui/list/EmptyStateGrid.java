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
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class EmptyStateGrid<T> extends StackPane implements Initializable {
   @FXML
   private VBox infoBox;
   @FXML
   private Label emptyStateLabel;
   @FXML
   private Label emptyStateNote;
   @FXML
   private RandomShortcutPane randomShortcutPane;
   @FXML
   private ScrollPane gridViewScroller;
   @FXML
   private GridView<T> gridView;
   private final ListChangeListener<T> listChangeListener = c -> this.infoBox.setVisible(this.gridView.getItems().isEmpty());
   private final BooleanProperty showRandomShortcuts = new SimpleBooleanProperty(true);

   public EmptyStateGrid() {
      JavaFxUtil.loadComponent(this);
   }

   public void initialize(URL url, ResourceBundle resourceBundle) {
      JavaFxUtil.setDefaultLineSpacing(this);
      if (this.gridView.getItems() != null) {
         this.gridView.getItems().addListener(this.listChangeListener);
      }

      this.gridView.getStyleClass().add("transparent-list");
      this.infoBox.visibleProperty().addListener((s, o, n) -> {
         if (n) {
            this.randomShortcutPane.update();
         }
      });
      this.infoBox.setVisible(this.gridView.getItems() != null && this.gridView.getItems().isEmpty());
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

   public GridView<T> getGridView() {
      return this.gridView;
   }

   public void setListViewItems(ObservableList<T> items) {
      ObservableList<T> oldItems = this.gridView.getItems();
      if (oldItems != null) {
         oldItems.removeListener(this.listChangeListener);
      }

      this.gridView.setItems(items);
      if (items != null) {
         items.addListener(this.listChangeListener);
         this.infoBox.setVisible(items.isEmpty());
      } else {
         this.infoBox.setVisible(true);
      }
   }

   public ScrollPane getGridViewScroller() {
      return this.gridViewScroller;
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
