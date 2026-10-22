package javafx.scene.control;

import com.sun.javafx.scene.control.FakeFocusTextField;
import java.lang.ref.WeakReference;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.WeakListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.scene.AccessibleAttribute;
import javafx.scene.AccessibleRole;
import javafx.scene.Node;
import javafx.scene.control.skin.ComboBoxListViewSkin;
import javafx.util.Callback;
import javafx.util.StringConverter;

public class ComboBox<T> extends ComboBoxBase<T> {
   private ObjectProperty<ObservableList<T>> items = new SimpleObjectProperty(this, "items");
   private ObjectProperty<StringConverter<T>> converter = new SimpleObjectProperty(this, "converter", defaultStringConverter());
   private ObjectProperty<Callback<ListView<T>, ListCell<T>>> cellFactory = new SimpleObjectProperty(this, "cellFactory");
   private ObjectProperty<ListCell<T>> buttonCell = new SimpleObjectProperty(this, "buttonCell");
   private ObjectProperty<SingleSelectionModel<T>> selectionModel = new SimpleObjectProperty<SingleSelectionModel<T>>(this, "selectionModel") {
      private SingleSelectionModel<T> oldSM = null;

      protected void invalidated() {
         if (this.oldSM != null) {
            this.oldSM.selectedItemProperty().removeListener(ComboBox.this.selectedItemListener);
         }

         SingleSelectionModel var1 = (SingleSelectionModel)this.get();
         this.oldSM = var1;
         if (var1 != null) {
            var1.selectedItemProperty().addListener(ComboBox.this.selectedItemListener);
         }
      }
   };
   private IntegerProperty visibleRowCount = new SimpleIntegerProperty(this, "visibleRowCount", 10);
   private TextField textField;
   private ReadOnlyObjectWrapper<TextField> editor;
   private ObjectProperty<Node> placeholder;
   private ChangeListener<T> selectedItemListener = new ChangeListener<T>() {
      public void changed(ObservableValue<? extends T> var1, T var2, T var3) {
         if (!ComboBox.this.wasSetAllCalled || var3 != null) {
            ComboBox.this.updateValue(var3);
         }

         ComboBox.this.wasSetAllCalled = false;
      }
   };
   private static final String DEFAULT_STYLE_CLASS = "combo-box";
   private boolean wasSetAllCalled = false;
   private int previousItemCount = -1;

   private static <T> StringConverter<T> defaultStringConverter() {
      return new StringConverter<T>() {
         public String toString(T var1) {
            return var1 == null ? null : var1.toString();
         }

         public T fromString(String var1) {
            return (T)var1;
         }
      };
   }

   public ComboBox() {
      this(FXCollections.observableArrayList());
   }

   public ComboBox(ObservableList<T> var1) {
      this.getStyleClass().add("combo-box");
      this.setAccessibleRole(AccessibleRole.COMBO_BOX);
      this.setItems(var1);
      this.setSelectionModel(new ComboBox.ComboBoxSelectionModel<>(this));
      this.valueProperty().addListener((var1x, var2, var3) -> {
         if (this.getItems() != null) {
            SingleSelectionModel var4 = this.getSelectionModel();
            int var5 = this.getItems().indexOf(var3);
            if (var5 == -1) {
               Runnable var6 = () -> {
                  var4.setSelectedIndex(-1);
                  var4.setSelectedItem(var3);
               };
               if (var4 instanceof ComboBox.ComboBoxSelectionModel) {
                  ((ComboBox.ComboBoxSelectionModel)var4).doAtomic(var6);
               } else {
                  var6.run();
               }
            } else {
               Object var7 = var4.getSelectedItem();
               if (var7 == null || !var7.equals(this.getValue())) {
                  var4.clearAndSelect(var5);
               }
            }
         }
      });
      this.editableProperty().addListener(var1x -> {
         if (!this.isEditable() && this.getItems() != null && !this.getItems().contains(this.getValue())) {
            this.getSelectionModel().clearSelection();
         }
      });
      this.focusedProperty().addListener(var1x -> {
         if (!this.isFocused()) {
            this.commitValue();
         }
      });
   }

   public final void setItems(ObservableList<T> var1) {
      this.itemsProperty().set(var1);
   }

   public final ObservableList<T> getItems() {
      return (ObservableList<T>)this.items.get();
   }

   public ObjectProperty<ObservableList<T>> itemsProperty() {
      return this.items;
   }

   public ObjectProperty<StringConverter<T>> converterProperty() {
      return this.converter;
   }

   public final void setConverter(StringConverter<T> var1) {
      this.converterProperty().set(var1);
   }

   public final StringConverter<T> getConverter() {
      return (StringConverter<T>)this.converterProperty().get();
   }

   public final void setCellFactory(Callback<ListView<T>, ListCell<T>> var1) {
      this.cellFactoryProperty().set(var1);
   }

   public final Callback<ListView<T>, ListCell<T>> getCellFactory() {
      return (Callback<ListView<T>, ListCell<T>>)this.cellFactoryProperty().get();
   }

   public ObjectProperty<Callback<ListView<T>, ListCell<T>>> cellFactoryProperty() {
      return this.cellFactory;
   }

   public ObjectProperty<ListCell<T>> buttonCellProperty() {
      return this.buttonCell;
   }

   public final void setButtonCell(ListCell<T> var1) {
      this.buttonCellProperty().set(var1);
   }

   public final ListCell<T> getButtonCell() {
      return (ListCell<T>)this.buttonCellProperty().get();
   }

   public final void setSelectionModel(SingleSelectionModel<T> var1) {
      this.selectionModel.set(var1);
   }

   public final SingleSelectionModel<T> getSelectionModel() {
      return (SingleSelectionModel<T>)this.selectionModel.get();
   }

   public final ObjectProperty<SingleSelectionModel<T>> selectionModelProperty() {
      return this.selectionModel;
   }

   public final void setVisibleRowCount(int var1) {
      this.visibleRowCount.set(var1);
   }

   public final int getVisibleRowCount() {
      return this.visibleRowCount.get();
   }

   public final IntegerProperty visibleRowCountProperty() {
      return this.visibleRowCount;
   }

   public final TextField getEditor() {
      return (TextField)this.editorProperty().get();
   }

   public final ReadOnlyObjectProperty<TextField> editorProperty() {
      if (this.editor == null) {
         this.editor = new ReadOnlyObjectWrapper(this, "editor");
         this.textField = new FakeFocusTextField();
         this.editor.set(this.textField);
      }

      return this.editor.getReadOnlyProperty();
   }

   public final ObjectProperty<Node> placeholderProperty() {
      if (this.placeholder == null) {
         this.placeholder = new SimpleObjectProperty(this, "placeholder");
      }

      return this.placeholder;
   }

   public final void setPlaceholder(Node var1) {
      this.placeholderProperty().set(var1);
   }

   public final Node getPlaceholder() {
      return this.placeholder == null ? null : (Node)this.placeholder.get();
   }

   @Override
   protected Skin<?> createDefaultSkin() {
      return new ComboBoxListViewSkin<>(this);
   }

   public final void commitValue() {
      if (this.isEditable()) {
         String var1 = this.getEditor().getText();
         StringConverter var2 = this.getConverter();
         if (var2 != null) {
            Object var3 = var2.fromString(var1);
            this.setValue((T)var3);
         }
      }
   }

   public final void cancelEdit() {
      if (this.isEditable()) {
         Object var1 = this.getValue();
         StringConverter var2 = this.getConverter();
         if (var2 != null) {
            String var3 = var2.toString(var1);
            this.getEditor().setText(var3);
         }
      }
   }

   private void updateValue(T var1) {
      if (!this.valueProperty().isBound()) {
         this.setValue((T)var1);
      }
   }

   @Override
   public Object queryAccessibleAttribute(AccessibleAttribute var1, Object... var2) {
      switch (var1) {
         case TEXT:
            String var3 = this.getAccessibleText();
            if (var3 != null && !var3.isEmpty()) {
               return var3;
            } else {
               Object var4 = super.queryAccessibleAttribute(var1, var2);
               if (var4 != null) {
                  return var4;
               } else {
                  StringConverter var5 = this.getConverter();
                  if (var5 == null) {
                     return this.getValue() != null ? this.getValue().toString() : "";
                  }

                  return var5.toString(this.getValue());
               }
            }
         default:
            return super.queryAccessibleAttribute(var1, var2);
      }
   }

   static class ComboBoxSelectionModel<T> extends SingleSelectionModel<T> {
      private final ComboBox<T> comboBox;
      private boolean atomic = false;
      private final ListChangeListener<T> itemsContentObserver = new ListChangeListener<T>() {
         public void onChanged(Change<? extends T> var1) {
            if (ComboBoxSelectionModel.this.comboBox.getItems() != null && !ComboBoxSelectionModel.this.comboBox.getItems().isEmpty()) {
               if (ComboBoxSelectionModel.this.getSelectedIndex() == -1 && ComboBoxSelectionModel.this.getSelectedItem() != null) {
                  int var2 = ComboBoxSelectionModel.this.comboBox.getItems().indexOf(ComboBoxSelectionModel.this.getSelectedItem());
                  if (var2 != -1) {
                     ComboBoxSelectionModel.this.setSelectedIndex(var2);
                  }
               }
            } else {
               ComboBoxSelectionModel.this.setSelectedIndex(-1);
            }

            int var5 = 0;

            while (var1.next()) {
               ComboBoxSelectionModel.this.comboBox.wasSetAllCalled = ComboBoxSelectionModel.this.comboBox.previousItemCount == var1.getRemovedSize();
               if (!var1.wasReplaced()
                  && (var1.wasAdded() || var1.wasRemoved())
                  && var1.getFrom() <= ComboBoxSelectionModel.this.getSelectedIndex()
                  && ComboBoxSelectionModel.this.getSelectedIndex() != -1) {
                  var5 += var1.wasAdded() ? var1.getAddedSize() : -var1.getRemovedSize();
               }
            }

            if (var5 != 0) {
               ComboBoxSelectionModel.this.clearAndSelect(ComboBoxSelectionModel.this.getSelectedIndex() + var5);
            } else if (ComboBoxSelectionModel.this.comboBox.wasSetAllCalled
               && ComboBoxSelectionModel.this.getSelectedIndex() >= 0
               && ComboBoxSelectionModel.this.getSelectedItem() != null) {
               Object var3 = ComboBoxSelectionModel.this.getSelectedItem();

               for (int var4 = 0; var4 < ComboBoxSelectionModel.this.comboBox.getItems().size(); var4++) {
                  if (var3.equals(ComboBoxSelectionModel.this.comboBox.getItems().get(var4))) {
                     ComboBoxSelectionModel.this.comboBox.setValue(null);
                     ComboBoxSelectionModel.this.setSelectedItem(null);
                     ComboBoxSelectionModel.this.setSelectedIndex(var4);
                     break;
                  }
               }
            }

            ComboBoxSelectionModel.this.comboBox.previousItemCount = ComboBoxSelectionModel.this.getItemCount();
         }
      };
      private final InvalidationListener itemsObserver;
      private WeakListChangeListener<T> weakItemsContentObserver = new WeakListChangeListener(this.itemsContentObserver);

      private void doAtomic(Runnable var1) {
         this.atomic = true;
         var1.run();
         this.atomic = false;
      }

      public ComboBoxSelectionModel(ComboBox<T> var1) {
         if (var1 == null) {
            throw new NullPointerException("ComboBox can not be null");
         } else {
            this.comboBox = var1;
            this.comboBox.previousItemCount = this.getItemCount();
            this.selectedIndexProperty().addListener(var1x -> {
               if (!this.atomic) {
                  this.setSelectedItem(this.getModelItem(this.getSelectedIndex()));
               }
            });
            this.itemsObserver = new InvalidationListener() {
               private WeakReference<ObservableList<T>> weakItemsRef = new WeakReference<>(ComboBoxSelectionModel.this.comboBox.getItems());

               public void invalidated(Observable var1) {
                  ObservableList var2 = this.weakItemsRef.get();
                  this.weakItemsRef = new WeakReference<>(ComboBoxSelectionModel.this.comboBox.getItems());
                  ComboBoxSelectionModel.this.updateItemsObserver(var2, ComboBoxSelectionModel.this.comboBox.getItems());
                  ComboBoxSelectionModel.this.comboBox.previousItemCount = ComboBoxSelectionModel.this.getItemCount();
               }
            };
            this.comboBox.itemsProperty().addListener(new WeakInvalidationListener(this.itemsObserver));
            if (this.comboBox.getItems() != null) {
               this.comboBox.getItems().addListener(this.weakItemsContentObserver);
            }
         }
      }

      private void updateItemsObserver(ObservableList<T> var1, ObservableList<T> var2) {
         if (var1 != null) {
            var1.removeListener(this.weakItemsContentObserver);
         }

         if (var2 != null) {
            var2.addListener(this.weakItemsContentObserver);
         }

         int var3 = -1;
         if (var2 != null) {
            Object var4 = this.comboBox.getValue();
            if (var4 != null) {
               var3 = var2.indexOf(var4);
            }
         }

         this.setSelectedIndex(var3);
      }

      @Override
      protected T getModelItem(int var1) {
         ObservableList var2 = this.comboBox.getItems();
         if (var2 == null) {
            return null;
         } else {
            return (T)(var1 >= 0 && var1 < var2.size() ? var2.get(var1) : null);
         }
      }

      @Override
      protected int getItemCount() {
         ObservableList var1 = this.comboBox.getItems();
         return var1 == null ? 0 : var1.size();
      }
   }
}
