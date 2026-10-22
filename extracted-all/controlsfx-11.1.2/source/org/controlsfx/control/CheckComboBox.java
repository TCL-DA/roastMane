package org.controlsfx.control;

import impl.org.controlsfx.skin.CheckComboBoxSkin;
import java.util.HashMap;
import java.util.Map;
import javafx.beans.DefaultProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Skin;
import javafx.util.StringConverter;

@DefaultProperty("items")
public class CheckComboBox<T> extends ControlsFXControl {
   public static final String COMBO_BOX_ROWS_TO_MEASURE_WIDTH_KEY = "comboBoxRowsToMeasureWidth";
   private final ObservableList<T> items;
   private final Map<T, BooleanProperty> itemBooleanMap;
   private CheckComboBoxSkin<T> checkComboBoxSkin;
   private ObjectProperty<IndexedCheckModel<T>> checkModel = new SimpleObjectProperty(this, "checkModel");
   private ObjectProperty<StringConverter<T>> converter = new SimpleObjectProperty(this, "converter");
   private StringProperty title = new SimpleStringProperty(null);
   private BooleanProperty showCheckedCount = new SimpleBooleanProperty(false);

   public CheckComboBox() {
      this(null);
   }

   public CheckComboBox(ObservableList<T> items) {
      int initialSize = items == null ? 32 : items.size();
      this.itemBooleanMap = new HashMap<>(initialSize);
      this.items = items == null ? FXCollections.observableArrayList() : items;
      this.setCheckModel(new CheckComboBox.CheckComboBoxBitSetCheckModel<>(this.items, this.itemBooleanMap));
   }

   public ObservableList<T> getItems() {
      return this.items;
   }

   public BooleanProperty getItemBooleanProperty(int index) {
      return index >= 0 && index < this.items.size() ? this.getItemBooleanProperty((T)this.getItems().get(index)) : null;
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

   public final ObjectProperty<StringConverter<T>> converterProperty() {
      return this.converter;
   }

   public final void setConverter(StringConverter<T> value) {
      this.converterProperty().set(value);
   }

   public final StringConverter<T> getConverter() {
      return (StringConverter<T>)this.converterProperty().get();
   }

   public final StringProperty titleProperty() {
      return this.title;
   }

   public final void setTitle(String value) {
      this.title.setValue(value);
   }

   public final String getTitle() {
      return this.title.getValue();
   }

   public void show() {
      if (this.checkComboBoxSkin != null) {
         this.checkComboBoxSkin.show();
      }
   }

   public void hide() {
      if (this.checkComboBoxSkin != null) {
         this.checkComboBoxSkin.hide();
      }
   }

   protected Skin<?> createDefaultSkin() {
      this.checkComboBoxSkin = new CheckComboBoxSkin<>(this);
      return this.checkComboBoxSkin;
   }

   public final BooleanProperty showCheckedCountProperty() {
      return this.showCheckedCount;
   }

   public final void setShowCheckedCount(boolean value) {
      this.showCheckedCount.setValue(value);
   }

   public final boolean isShowCheckedCount() {
      return this.showCheckedCount.getValue();
   }

   private static class CheckComboBoxBitSetCheckModel<T> extends CheckBitSetModelBase<T> {
      private final ObservableList<T> items;

      CheckComboBoxBitSetCheckModel(ObservableList<T> items, Map<T, BooleanProperty> itemBooleanMap) {
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
