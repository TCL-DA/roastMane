package impl.org.controlsfx.skin;

import impl.org.controlsfx.collections.ReadOnlyUnbackedObservableList;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.control.skin.ComboBoxListViewSkin;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyCombination.Modifier;
import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.IndexedCheckModel;

public class CheckComboBoxSkin<T> extends SkinBase<CheckComboBox<T>> {
   private final ComboBox<T> comboBox;
   private final ListCell<T> buttonCell;
   private final CheckComboBox<T> control;
   private final ObservableList<T> items;
   private final ReadOnlyUnbackedObservableList<Integer> selectedIndices;
   private final ReadOnlyUnbackedObservableList<T> selectedItems;

   public CheckComboBoxSkin(CheckComboBox<T> control) {
      super(control);
      this.control = control;
      this.items = control.getItems();
      this.selectedIndices = (ReadOnlyUnbackedObservableList<Integer>)control.getCheckModel().getCheckedIndices();
      this.selectedItems = (ReadOnlyUnbackedObservableList<T>)control.getCheckModel().getCheckedItems();
      this.comboBox = new ComboBox<T>(this.items) {
         protected Skin<?> createDefaultSkin() {
            return CheckComboBoxSkin.this.createComboBoxListViewSkin(this);
         }
      };
      this.comboBox.setFocusTraversable(false);
      this.comboBox.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
      Bindings.bindContent(control.getStyleClass(), this.comboBox.getStyleClass());
      this.comboBox.setCellFactory(listView -> {
         CheckBoxListCell<T> checkBoxListCell = new CheckBoxListCell(control::getItemBooleanProperty);
         checkBoxListCell.setOnMouseClicked(e -> {
            T item = (T)checkBoxListCell.getItem();
            if (control.getCheckModel().isChecked(item)) {
               control.getCheckModel().clearCheck(item);
            } else {
               control.getCheckModel().check(item);
            }
         });
         checkBoxListCell.converterProperty().bind(control.converterProperty());
         return checkBoxListCell;
      });
      this.buttonCell = new ListCell<T>() {
         protected void updateItem(T item, boolean empty) {
            this.setText(CheckComboBoxSkin.this.getTextString());
         }
      };
      this.comboBox.setButtonCell(this.buttonCell);
      this.comboBox.setValue(this.getTextString());
      if (control.getProperties().containsKey("comboBoxRowsToMeasureWidth")) {
         this.comboBox.getProperties().put("comboBoxRowsToMeasureWidth", control.getProperties().get("comboBoxRowsToMeasureWidth"));
      }

      this.selectedIndices.addListener(c -> this.buttonCell.updateIndex(0));
      this.getChildren().add(this.comboBox);
   }

   protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
      return this.comboBox.minWidth(height);
   }

   protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
      return this.comboBox.minHeight(width);
   }

   protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
      return this.comboBox.prefWidth(height);
   }

   protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
      return this.comboBox.prefHeight(width);
   }

   protected double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
      return ((CheckComboBox)this.getSkinnable()).prefWidth(height);
   }

   protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
      return ((CheckComboBox)this.getSkinnable()).prefHeight(width);
   }

   public void show() {
      this.comboBox.show();
   }

   public void hide() {
      this.comboBox.hide();
   }

   protected String getTextString() {
      if (this.control.getTitle() != null) {
         String vResult = this.control.getTitle();
         if (this.control.isShowCheckedCount()) {
            vResult = String.format("%s (%d/%d)", vResult, this.selectedItems.size(), this.items.size());
         }

         return vResult;
      } else {
         return this.buildString();
      }
   }

   private String buildString() {
      StringBuilder sb = new StringBuilder();
      int i = 0;

      for (int max = this.selectedItems.size(); i < max; i++) {
         T item = this.selectedItems.get(i);
         if (this.control.getConverter() == null) {
            sb.append(item);
         } else {
            sb.append(this.control.getConverter().toString(item));
         }

         if (i < max - 1) {
            sb.append(", ");
         }
      }

      return sb.toString();
   }

   private Skin<?> createComboBoxListViewSkin(ComboBox<T> comboBox) {
      ComboBoxListViewSkin<T> comboBoxListViewSkin = new ComboBoxListViewSkin(comboBox);
      comboBoxListViewSkin.setHideOnClick(false);
      ListView<T> listView = (ListView<T>)comboBoxListViewSkin.getPopupContent();
      listView.setOnKeyPressed(e -> {
         if (e.getCode() == KeyCode.SPACE) {
            T item = (T)listView.getSelectionModel().getSelectedItem();
            if (item != null) {
               IndexedCheckModel<T> checkModel = this.control.getCheckModel();
               if (checkModel != null) {
                  checkModel.toggleCheckState(item);
               }
            }
         } else if (e.getCode() == KeyCode.ESCAPE) {
            this.hide();
         } else if (e.getCode() == KeyCode.TAB || new KeyCodeCombination(KeyCode.TAB, new Modifier[]{KeyCombination.SHIFT_ANY}).match(e)) {
            e.consume();
            listView.requestFocus();
            this.hide();
            this.control.fireEvent(e);
         } else if (e.getCode() == KeyCode.LEFT || e.getCode() == KeyCode.RIGHT) {
            e.consume();
         }
      });
      return comboBoxListViewSkin;
   }
}
