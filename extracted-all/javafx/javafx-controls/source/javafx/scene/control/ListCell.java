package javafx.scene.control;

import java.lang.ref.WeakReference;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WeakChangeListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.WeakListChangeListener;
import javafx.scene.AccessibleAction;
import javafx.scene.AccessibleAttribute;
import javafx.scene.AccessibleRole;
import javafx.scene.control.skin.ListCellSkin;

public class ListCell<T> extends IndexedCell<T> {
   private final InvalidationListener editingListener = var1 -> this.updateEditing();
   private boolean updateEditingIndex = true;
   private final ListChangeListener<Integer> selectedListener = var1 -> this.updateSelection();
   private final ChangeListener<MultipleSelectionModel<T>> selectionModelPropertyListener = new ChangeListener<MultipleSelectionModel<T>>() {
      public void changed(ObservableValue<? extends MultipleSelectionModel<T>> var1, MultipleSelectionModel<T> var2, MultipleSelectionModel<T> var3) {
         if (var2 != null) {
            var2.getSelectedIndices().removeListener(ListCell.this.weakSelectedListener);
         }

         if (var3 != null) {
            var3.getSelectedIndices().addListener(ListCell.this.weakSelectedListener);
         }

         ListCell.this.updateSelection();
      }
   };
   private final ListChangeListener<T> itemsListener = var1 -> {
      boolean var2 = false;

      while (var1.next()) {
         int var3 = this.getIndex();
         ListView var4 = this.getListView();
         ObservableList var5 = var4 == null ? null : var4.getItems();
         int var6 = var5 == null ? 0 : var5.size();
         boolean var7 = var3 >= var1.getFrom();
         boolean var8 = var3 < var1.getTo() || var3 == var6;
         boolean var9 = var7 && var8;
         var2 = var9 || var7 && !var1.wasReplaced() && (var1.wasRemoved() || var1.wasAdded());
      }

      if (var2) {
         this.updateItem(-1);
      }
   };
   private final InvalidationListener itemsPropertyListener = new InvalidationListener() {
      private WeakReference<ObservableList<T>> weakItemsRef = new WeakReference<>(null);

      public void invalidated(Observable var1) {
         ObservableList var2 = this.weakItemsRef.get();
         if (var2 != null) {
            var2.removeListener(ListCell.this.weakItemsListener);
         }

         ListView var3 = ListCell.this.getListView();
         ObservableList var4 = var3 == null ? null : var3.getItems();
         this.weakItemsRef = new WeakReference<>(var4);
         if (var4 != null) {
            var4.addListener(ListCell.this.weakItemsListener);
         }

         ListCell.this.updateItem(-1);
      }
   };
   private final InvalidationListener focusedListener = var1 -> this.updateFocus();
   private final ChangeListener<FocusModel<T>> focusModelPropertyListener = new ChangeListener<FocusModel<T>>() {
      public void changed(ObservableValue<? extends FocusModel<T>> var1, FocusModel<T> var2, FocusModel<T> var3) {
         if (var2 != null) {
            var2.focusedIndexProperty().removeListener(ListCell.this.weakFocusedListener);
         }

         if (var3 != null) {
            var3.focusedIndexProperty().addListener(ListCell.this.weakFocusedListener);
         }

         ListCell.this.updateFocus();
      }
   };
   private final WeakInvalidationListener weakEditingListener = new WeakInvalidationListener(this.editingListener);
   private final WeakListChangeListener<Integer> weakSelectedListener = new WeakListChangeListener(this.selectedListener);
   private final WeakChangeListener<MultipleSelectionModel<T>> weakSelectionModelPropertyListener = new WeakChangeListener(this.selectionModelPropertyListener);
   private final WeakListChangeListener<T> weakItemsListener = new WeakListChangeListener(this.itemsListener);
   private final WeakInvalidationListener weakItemsPropertyListener = new WeakInvalidationListener(this.itemsPropertyListener);
   private final WeakInvalidationListener weakFocusedListener = new WeakInvalidationListener(this.focusedListener);
   private final WeakChangeListener<FocusModel<T>> weakFocusModelPropertyListener = new WeakChangeListener(this.focusModelPropertyListener);
   private ReadOnlyObjectWrapper<ListView<T>> listView = new ReadOnlyObjectWrapper<ListView<T>>(this, "listView") {
      private WeakReference<ListView<T>> weakListViewRef = new WeakReference<>(null);

      protected void invalidated() {
         ListView var1 = (ListView)this.get();
         ListView var2 = this.weakListViewRef.get();
         if (var1 != var2) {
            if (var2 != null) {
               MultipleSelectionModel var3 = var2.getSelectionModel();
               if (var3 != null) {
                  var3.getSelectedIndices().removeListener(ListCell.this.weakSelectedListener);
               }

               FocusModel var4 = var2.getFocusModel();
               if (var4 != null) {
                  var4.focusedIndexProperty().removeListener(ListCell.this.weakFocusedListener);
               }

               ObservableList var5 = var2.getItems();
               if (var5 != null) {
                  var5.removeListener(ListCell.this.weakItemsListener);
               }

               var2.editingIndexProperty().removeListener(ListCell.this.weakEditingListener);
               var2.itemsProperty().removeListener(ListCell.this.weakItemsPropertyListener);
               var2.focusModelProperty().removeListener(ListCell.this.weakFocusModelPropertyListener);
               var2.selectionModelProperty().removeListener(ListCell.this.weakSelectionModelPropertyListener);
            }

            if (var1 != null) {
               MultipleSelectionModel var6 = var1.getSelectionModel();
               if (var6 != null) {
                  var6.getSelectedIndices().addListener(ListCell.this.weakSelectedListener);
               }

               FocusModel var7 = var1.getFocusModel();
               if (var7 != null) {
                  var7.focusedIndexProperty().addListener(ListCell.this.weakFocusedListener);
               }

               ObservableList var8 = var1.getItems();
               if (var8 != null) {
                  var8.addListener(ListCell.this.weakItemsListener);
               }

               var1.editingIndexProperty().addListener(ListCell.this.weakEditingListener);
               var1.itemsProperty().addListener(ListCell.this.weakItemsPropertyListener);
               var1.focusModelProperty().addListener(ListCell.this.weakFocusModelPropertyListener);
               var1.selectionModelProperty().addListener(ListCell.this.weakSelectionModelPropertyListener);
               this.weakListViewRef = new WeakReference<>(var1);
            }

            ListCell.this.updateItem(-1);
            ListCell.this.updateSelection();
            ListCell.this.updateFocus();
            ListCell.this.requestLayout();
         }
      }
   };
   private int indexAtStartEdit;
   private boolean firstRun = true;
   private static final String DEFAULT_STYLE_CLASS = "list-cell";

   public ListCell() {
      this.getStyleClass().addAll(new String[]{"list-cell"});
      this.setAccessibleRole(AccessibleRole.LIST_ITEM);
   }

   private void setListView(ListView<T> var1) {
      this.listView.set(var1);
   }

   public final ListView<T> getListView() {
      return (ListView<T>)this.listView.get();
   }

   public final ReadOnlyObjectProperty<ListView<T>> listViewProperty() {
      return this.listView.getReadOnlyProperty();
   }

   @Override
   void indexChanged(int var1, int var2) {
      super.indexChanged(var1, var2);
      if (!this.isEditing() || var2 != var1) {
         this.updateItem(var1);
         this.updateSelection();
         this.updateFocus();
         this.updateEditing();
      }
   }

   @Override
   protected Skin<?> createDefaultSkin() {
      return new ListCellSkin<>(this);
   }

   @Override
   public void startEdit() {
      ListView var1 = this.getListView();
      if (this.isEditable() && (var1 == null || var1.isEditable())) {
         super.startEdit();
         if (var1 != null) {
            var1.fireEvent(new ListView.EditEvent(var1, ListView.editStartEvent(), null, this.getIndex()));
            var1.edit(this.getIndex());
            var1.requestFocus();
         }

         this.indexAtStartEdit = this.getIndex();
      }
   }

   @Override
   public void commitEdit(T var1) {
      if (this.isEditing()) {
         ListView var2 = this.getListView();
         if (var2 != null) {
            var2.fireEvent(new ListView.EditEvent<>(var2, ListView.editCommitEvent(), var1, var2.getEditingIndex()));
         }

         super.commitEdit((T)var1);
         this.updateItem((T)var1, false);
         if (var2 != null) {
            var2.edit(-1);
            ControlUtils.requestFocusOnControlOnlyIfCurrentFocusOwnerIsChild(var2);
         }
      }
   }

   @Override
   public void cancelEdit() {
      if (this.isEditing()) {
         super.cancelEdit();
         ListView var1 = this.getListView();
         if (var1 != null) {
            if (this.updateEditingIndex) {
               var1.edit(-1);
            }

            ControlUtils.requestFocusOnControlOnlyIfCurrentFocusOwnerIsChild(var1);
            var1.fireEvent(new ListView.EditEvent(var1, ListView.editCancelEvent(), null, this.indexAtStartEdit));
         }
      }
   }

   private void updateItem(int var1) {
      ListView var2 = this.getListView();
      ObservableList var3 = var2 == null ? null : var2.getItems();
      int var4 = this.getIndex();
      int var5 = var3 == null ? -1 : var3.size();
      boolean var6 = var3 != null && var4 >= 0 && var4 < var5;
      Object var7 = this.getItem();
      boolean var8 = this.isEmpty();
      if (var6) {
         Object var9 = var3.get(var4);
         if (var1 != var4 || this.isItemChanged((T)var7, (T)var9)) {
            this.updateItem((T)var9, false);
         }
      } else if (!var8 || this.firstRun) {
         this.updateItem(null, true);
         this.firstRun = false;
      }
   }

   public final void updateListView(ListView<T> var1) {
      this.setListView(var1);
   }

   private void updateSelection() {
      if (!this.isEmpty()) {
         int var1 = this.getIndex();
         ListView var2 = this.getListView();
         if (var1 != -1 && var2 != null) {
            MultipleSelectionModel var3 = var2.getSelectionModel();
            if (var3 == null) {
               this.updateSelected(false);
            } else {
               boolean var4 = var3.isSelected(var1);
               if (this.isSelected() != var4) {
                  this.updateSelected(var4);
               }
            }
         }
      }
   }

   private void updateFocus() {
      int var1 = this.getIndex();
      ListView var2 = this.getListView();
      if (var1 != -1 && var2 != null) {
         FocusModel var3 = var2.getFocusModel();
         if (var3 == null) {
            this.setFocused(false);
         } else {
            this.setFocused(var3.isFocused(var1));
         }
      }
   }

   private void updateEditing() {
      int var1 = this.getIndex();
      ListView var2 = this.getListView();
      int var3 = var2 == null ? -1 : var2.getEditingIndex();
      boolean var4 = this.isEditing();
      boolean var5 = var2 != null && var1 != -1 && var1 == var3;
      if (var5 && !var4) {
         this.startEdit();
      } else if (!var5 && var4) {
         try {
            this.updateEditingIndex = false;
            this.cancelEdit();
         } finally {
            this.updateEditingIndex = true;
         }
      }
   }

   @Override
   public Object queryAccessibleAttribute(AccessibleAttribute var1, Object... var2) {
      switch (var1) {
         case INDEX:
            return this.getIndex();
         case SELECTED:
            return this.isSelected();
         default:
            return super.queryAccessibleAttribute(var1, var2);
      }
   }

   @Override
   public void executeAccessibleAction(AccessibleAction var1, Object... var2) {
      switch (var1) {
         case REQUEST_FOCUS:
            ListView var3 = this.getListView();
            if (var3 != null) {
               FocusModel var4 = var3.getFocusModel();
               if (var4 != null) {
                  var4.focus(this.getIndex());
               }
            }
            break;
         default:
            super.executeAccessibleAction(var1, var2);
      }
   }
}
