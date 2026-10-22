package org.controlsfx.control;

import java.util.HashMap;
import java.util.Map;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class CheckListView<T> extends ListView<T> {
   private final Map<T, BooleanProperty> itemBooleanMap;
   private ObjectProperty<IndexedCheckModel<T>> checkModel = new SimpleObjectProperty(this, "checkModel");

   public CheckListView() {
      this(FXCollections.observableArrayList());
   }

   public CheckListView(ObservableList<T> items) {
      super(items);
      this.itemBooleanMap = new HashMap<>();
      this.setCheckModel(new CheckListView.CheckListViewBitSetCheckModel<>(this.getItems(), this.itemBooleanMap));
      this.itemsProperty().addListener(ov -> this.setCheckModel(new CheckListView.CheckListViewBitSetCheckModel<>(this.getItems(), this.itemBooleanMap)));
      this.setCellFactory(listView -> {
         CheckBoxListCell<T> checkBoxListCell = new CheckBoxListCell(item -> this.getItemBooleanProperty((T)item));
         checkBoxListCell.focusedProperty().addListener((o, ov, nv) -> {
            if (nv) {
               Parent parent = checkBoxListCell.getParent();
               if (parent != null) {
                  parent.requestFocus();
               }
            }
         });
         return checkBoxListCell;
      });
      this.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
         if (e.getCode() == KeyCode.SPACE) {
            T item = (T)this.getSelectionModel().getSelectedItem();
            if (item != null) {
               IndexedCheckModel<T> checkModel = this.getCheckModel();
               if (checkModel != null) {
                  if (checkModel.isChecked(item)) {
                     checkModel.clearCheck(item);
                  } else {
                     checkModel.check(item);
                  }
               }
            }
         }
      });
   }

   public BooleanProperty getItemBooleanProperty(int index) {
      return index >= 0 && index < this.getItems().size() ? this.getItemBooleanProperty((T)this.getItems().get(index)) : null;
   }

   public BooleanProperty getItemBooleanProperty(T item) {
      return this.itemBooleanMap.get(item);
   }

   public final void setCheckModel(IndexedCheckModel<T> value) {
      this.checkModelProperty().set(value);
   }

   public final IndexedCheckModel<T> getCheckModel() {
      return this.checkModel == null ? null : (IndexedCheckModel)this.checkModel.get();
   }

   public final ObjectProperty<IndexedCheckModel<T>> checkModelProperty() {
      return this.checkModel;
   }

   private static class CheckListViewBitSetCheckModel<T> extends CheckBitSetModelBase<T> {
      private final ObservableList<T> items;

      CheckListViewBitSetCheckModel(ObservableList<T> items, Map<T, BooleanProperty> itemBooleanMap) {
         super(itemBooleanMap);
         this.items = items;
         this.items.addListener(c -> this.updateMap());
         this.updateMap();
      }

      @Override
      public T getItem(int index) {
         return (T)this.items.get(index);
      }

      @Override
      public int getItemCount() {
         return this.items.size();
      }

      @Override
      public int getItemIndex(T item) {
         return this.items.indexOf(item);
      }
   }
}
