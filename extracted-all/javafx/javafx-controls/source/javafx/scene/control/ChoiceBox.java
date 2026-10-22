package javafx.scene.control;

import javafx.beans.DefaultProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.WeakListChangeListener;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.AccessibleAction;
import javafx.scene.AccessibleAttribute;
import javafx.scene.AccessibleRole;
import javafx.scene.control.skin.ChoiceBoxSkin;
import javafx.util.StringConverter;

@DefaultProperty("items")
public class ChoiceBox<T> extends Control {
   public static final EventType<Event> ON_SHOWING = new EventType(Event.ANY, "CHOICE_BOX_ON_SHOWING");
   public static final EventType<Event> ON_SHOWN = new EventType(Event.ANY, "CHOICE_BOX_ON_SHOWN");
   public static final EventType<Event> ON_HIDING = new EventType(Event.ANY, "CHOICE_BOX_ON_HIDING");
   public static final EventType<Event> ON_HIDDEN = new EventType(Event.ANY, "CHOICE_BOX_ON_HIDDEN");
   private ObjectProperty<SingleSelectionModel<T>> selectionModel = new SimpleObjectProperty<SingleSelectionModel<T>>(this, "selectionModel") {
      private SelectionModel<T> oldSM = null;

      protected void invalidated() {
         if (this.oldSM != null) {
            this.oldSM.selectedItemProperty().removeListener(ChoiceBox.this.selectedItemListener);
         }

         SelectionModel var1 = (SelectionModel)this.get();
         this.oldSM = var1;
         if (var1 != null) {
            var1.selectedItemProperty().addListener(ChoiceBox.this.selectedItemListener);
            if (!ChoiceBox.this.valueProperty().isBound()) {
               ChoiceBox.this.setValue(var1.getSelectedItem());
            }
         }
      }
   };
   private ChangeListener<T> selectedItemListener = (var1x, var2, var3) -> {
      if (!this.valueProperty().isBound()) {
         this.setValue((T)var3);
      }
   };
   private ReadOnlyBooleanWrapper showing = new ReadOnlyBooleanWrapper() {
      protected void invalidated() {
         ChoiceBox.this.pseudoClassStateChanged(ChoiceBox.SHOWING_PSEUDOCLASS_STATE, this.get());
         ChoiceBox.this.notifyAccessibleAttributeChanged(AccessibleAttribute.EXPANDED);
      }

      public Object getBean() {
         return ChoiceBox.this;
      }

      public String getName() {
         return "showing";
      }
   };
   private ObjectProperty<ObservableList<T>> items = new ObjectPropertyBase<ObservableList<T>>() {
      ObservableList<T> old;

      protected void invalidated() {
         ObservableList var1 = (ObservableList)this.get();
         if (this.old != var1) {
            if (this.old != null) {
               this.old.removeListener(ChoiceBox.this.itemsListener);
            }

            if (var1 != null) {
               var1.addListener(ChoiceBox.this.itemsListener);
            }

            SingleSelectionModel var2 = ChoiceBox.this.getSelectionModel();
            if (var2 != null) {
               if (var1 != null && var1.isEmpty()) {
                  var2.clearSelection();
               } else if (var2.getSelectedIndex() == -1 && var2.getSelectedItem() != null) {
                  int var3 = ChoiceBox.this.getItems().indexOf(var2.getSelectedItem());
                  if (var3 != -1) {
                     var2.setSelectedIndex(var3);
                  }
               } else {
                  var2.clearSelection();
               }
            }

            this.old = var1;
         }
      }

      public Object getBean() {
         return ChoiceBox.this;
      }

      public String getName() {
         return "items";
      }
   };
   private final ListChangeListener<T> itemsListener = var1x -> {
      SingleSelectionModel var2 = this.getSelectionModel();
      if (var2 != null) {
         if (this.getItems() != null && !this.getItems().isEmpty()) {
            int var3 = this.getItems().indexOf(var2.getSelectedItem());
            var2.setSelectedIndex(var3);
         } else {
            var2.clearSelection();
         }
      }

      if (var2 != null) {
         Object var4 = var2.getSelectedItem();

         while (var1x.next()) {
            if (var4 != null && var1x.getRemoved().contains(var4)) {
               var2.clearSelection();
               break;
            }
         }
      }
   };
   private ObjectProperty<StringConverter<T>> converter = new SimpleObjectProperty(this, "converter", null);
   private ObjectProperty<T> value = new SimpleObjectProperty<T>(this, "value") {
      protected void invalidated() {
         super.invalidated();
         ChoiceBox.this.fireEvent(new ActionEvent());
         SingleSelectionModel var1 = ChoiceBox.this.getSelectionModel();
         if (var1 != null) {
            var1.select(super.getValue());
         }

         ChoiceBox.this.notifyAccessibleAttributeChanged(AccessibleAttribute.TEXT);
      }
   };
   private ObjectProperty<EventHandler<ActionEvent>> onAction = new ObjectPropertyBase<EventHandler<ActionEvent>>() {
      protected void invalidated() {
         ChoiceBox.this.setEventHandler(ActionEvent.ACTION, (EventHandler)this.get());
      }

      public Object getBean() {
         return ChoiceBox.this;
      }

      public String getName() {
         return "onAction";
      }
   };
   private ObjectProperty<EventHandler<Event>> onShowing = new ObjectPropertyBase<EventHandler<Event>>() {
      protected void invalidated() {
         ChoiceBox.this.setEventHandler(ChoiceBox.ON_SHOWING, (EventHandler)this.get());
      }

      public Object getBean() {
         return ChoiceBox.this;
      }

      public String getName() {
         return "onShowing";
      }
   };
   private ObjectProperty<EventHandler<Event>> onShown = new ObjectPropertyBase<EventHandler<Event>>() {
      protected void invalidated() {
         ChoiceBox.this.setEventHandler(ChoiceBox.ON_SHOWN, (EventHandler)this.get());
      }

      public Object getBean() {
         return ChoiceBox.this;
      }

      public String getName() {
         return "onShown";
      }
   };
   private ObjectProperty<EventHandler<Event>> onHiding = new ObjectPropertyBase<EventHandler<Event>>() {
      protected void invalidated() {
         ChoiceBox.this.setEventHandler(ChoiceBox.ON_HIDING, (EventHandler)this.get());
      }

      public Object getBean() {
         return ChoiceBox.this;
      }

      public String getName() {
         return "onHiding";
      }
   };
   private ObjectProperty<EventHandler<Event>> onHidden = new ObjectPropertyBase<EventHandler<Event>>() {
      protected void invalidated() {
         ChoiceBox.this.setEventHandler(ChoiceBox.ON_HIDDEN, (EventHandler)this.get());
      }

      public Object getBean() {
         return ChoiceBox.this;
      }

      public String getName() {
         return "onHidden";
      }
   };
   private static final PseudoClass SHOWING_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("showing");

   public ChoiceBox() {
      this(FXCollections.observableArrayList());
   }

   public ChoiceBox(ObservableList<T> var1) {
      this.getStyleClass().setAll(new String[]{"choice-box"});
      this.setAccessibleRole(AccessibleRole.COMBO_BOX);
      this.setItems(var1);
      this.setSelectionModel(new ChoiceBox.ChoiceBoxSelectionModel<>(this));
      this.valueProperty().addListener((var1x, var2, var3) -> {
         if (this.getItems() != null) {
            int var4 = this.getItems().indexOf(var3);
            if (var4 > -1) {
               this.getSelectionModel().select(var4);
            }
         }
      });
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

   public final boolean isShowing() {
      return this.showing.get();
   }

   public final ReadOnlyBooleanProperty showingProperty() {
      return this.showing.getReadOnlyProperty();
   }

   private void setShowing(boolean var1) {
      Event.fireEvent(this, var1 ? new Event(ON_SHOWING) : new Event(ON_HIDING));
      this.showing.set(var1);
      Event.fireEvent(this, var1 ? new Event(ON_SHOWN) : new Event(ON_HIDDEN));
   }

   public final void setItems(ObservableList<T> var1) {
      this.items.set(var1);
   }

   public final ObservableList<T> getItems() {
      return (ObservableList<T>)this.items.get();
   }

   public final ObjectProperty<ObservableList<T>> itemsProperty() {
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

   public ObjectProperty<T> valueProperty() {
      return this.value;
   }

   public final void setValue(T var1) {
      this.valueProperty().set(var1);
   }

   public final T getValue() {
      return (T)this.valueProperty().get();
   }

   public final ObjectProperty<EventHandler<ActionEvent>> onActionProperty() {
      return this.onAction;
   }

   public final void setOnAction(EventHandler<ActionEvent> var1) {
      this.onActionProperty().set(var1);
   }

   public final EventHandler<ActionEvent> getOnAction() {
      return (EventHandler<ActionEvent>)this.onActionProperty().get();
   }

   public final ObjectProperty<EventHandler<Event>> onShowingProperty() {
      return this.onShowing;
   }

   public final void setOnShowing(EventHandler<Event> var1) {
      this.onShowingProperty().set(var1);
   }

   public final EventHandler<Event> getOnShowing() {
      return (EventHandler<Event>)this.onShowingProperty().get();
   }

   public final ObjectProperty<EventHandler<Event>> onShownProperty() {
      return this.onShown;
   }

   public final void setOnShown(EventHandler<Event> var1) {
      this.onShownProperty().set(var1);
   }

   public final EventHandler<Event> getOnShown() {
      return (EventHandler<Event>)this.onShownProperty().get();
   }

   public final ObjectProperty<EventHandler<Event>> onHidingProperty() {
      return this.onHiding;
   }

   public final void setOnHiding(EventHandler<Event> var1) {
      this.onHidingProperty().set(var1);
   }

   public final EventHandler<Event> getOnHiding() {
      return (EventHandler<Event>)this.onHidingProperty().get();
   }

   public final ObjectProperty<EventHandler<Event>> onHiddenProperty() {
      return this.onHidden;
   }

   public final void setOnHidden(EventHandler<Event> var1) {
      this.onHiddenProperty().set(var1);
   }

   public final EventHandler<Event> getOnHidden() {
      return (EventHandler<Event>)this.onHiddenProperty().get();
   }

   public void show() {
      if (!this.isDisabled()) {
         this.setShowing(true);
      }
   }

   public void hide() {
      this.setShowing(false);
   }

   @Override
   protected Skin<?> createDefaultSkin() {
      return new ChoiceBoxSkin<>(this);
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
         case EXPANDED:
            return this.isShowing();
         default:
            return super.queryAccessibleAttribute(var1, var2);
      }
   }

   @Override
   public void executeAccessibleAction(AccessibleAction var1, Object... var2) {
      switch (var1) {
         case COLLAPSE:
            this.hide();
            break;
         case EXPAND:
            this.show();
            break;
         default:
            super.executeAccessibleAction(var1);
      }
   }

   static class ChoiceBoxSelectionModel<T> extends SingleSelectionModel<T> {
      private final ChoiceBox<T> choiceBox;
      private ChangeListener<ObservableList<T>> itemsObserver;
      private ListChangeListener<T> itemsContentObserver;
      private WeakListChangeListener<T> weakItemsContentObserver;

      public ChoiceBoxSelectionModel(ChoiceBox<T> var1) {
         if (var1 == null) {
            throw new NullPointerException("ChoiceBox can not be null");
         } else {
            this.choiceBox = var1;
            this.itemsContentObserver = var1x -> {
               if (this.choiceBox.getItems() != null && !this.choiceBox.getItems().isEmpty()) {
                  if (this.getSelectedIndex() == -1 && this.getSelectedItem() != null) {
                     int var2 = this.choiceBox.getItems().indexOf(this.getSelectedItem());
                     if (var2 != -1) {
                        this.setSelectedIndex(var2);
                     }
                  }
               } else {
                  this.setSelectedIndex(-1);
               }
            };
            this.weakItemsContentObserver = new WeakListChangeListener(this.itemsContentObserver);
            if (this.choiceBox.getItems() != null) {
               this.choiceBox.getItems().addListener(this.weakItemsContentObserver);
            }

            this.itemsObserver = (var1x, var2, var3) -> {
               if (var2 != null) {
                  var2.removeListener(this.weakItemsContentObserver);
               }

               if (var3 != null) {
                  var3.addListener(this.weakItemsContentObserver);
               }

               this.setSelectedIndex(-1);
               if (this.getSelectedItem() != null) {
                  int var4 = this.choiceBox.getItems().indexOf(this.getSelectedItem());
                  if (var4 != -1) {
                     this.setSelectedIndex(var4);
                  }
               }
            };
            this.choiceBox.itemsProperty().addListener(new WeakChangeListener(this.itemsObserver));
         }
      }

      @Override
      protected T getModelItem(int var1) {
         ObservableList var2 = this.choiceBox.getItems();
         if (var2 == null) {
            return null;
         } else {
            return (T)(var1 >= 0 && var1 < var2.size() ? var2.get(var1) : null);
         }
      }

      @Override
      protected int getItemCount() {
         ObservableList var1 = this.choiceBox.getItems();
         return var1 == null ? 0 : var1.size();
      }

      @Override
      public void select(int var1) {
         super.select(var1);
         if (this.choiceBox.isShowing()) {
            this.choiceBox.hide();
         }
      }

      @Override
      public void select(T var1) {
         super.select((T)var1);
         if (var1 != null && !this.choiceBox.getItems().contains(var1)) {
            this.setSelectedIndex(-1);
         }
      }

      @Override
      public void selectPrevious() {
         for (int var1 = this.getSelectedIndex() - 1; var1 >= 0; var1--) {
            Object var2 = this.getModelItem(var1);
            if (!(var2 instanceof Separator)) {
               this.select(var1);
               break;
            }
         }
      }

      @Override
      public void selectNext() {
         for (int var1 = this.getSelectedIndex() + 1; var1 < this.getItemCount(); var1++) {
            Object var2 = this.getModelItem(var1);
            if (!(var2 instanceof Separator)) {
               this.select(var1);
               break;
            }
         }
      }
   }
}
