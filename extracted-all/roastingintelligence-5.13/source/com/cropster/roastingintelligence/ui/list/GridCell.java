package com.cropster.roastingintelligence.ui.list;

import com.cropster.roastingintelligence.ui.list.skin.GridCellSkin;
import java.lang.ref.WeakReference;
import java.util.List;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.WeakListChangeListener;
import javafx.geometry.Pos;
import javafx.scene.control.IndexedCell;
import javafx.scene.control.Skin;

public abstract class GridCell<T> extends IndexedCell<T> {
   private static final String DEFAULT_STYLE_CLASS = "grid-cell";
   private final ListChangeListener<T> itemsListener = c -> {
      boolean doUpdate = false;

      while (c.next()) {
         int currentIndex = this.getIndex();
         GridView<T> gv = this.getGridView();
         List<T> items = gv == null ? null : gv.getItems();
         int itemCount = items == null ? 0 : items.size();
         boolean indexAfterChangeFromIndex = currentIndex >= c.getFrom();
         boolean indexBeforeChangeToIndex = currentIndex < c.getTo() || currentIndex == itemCount;
         boolean indexInRange = indexAfterChangeFromIndex && indexBeforeChangeToIndex;
         doUpdate = indexInRange || indexAfterChangeFromIndex && !c.wasReplaced() && (c.wasRemoved() || c.wasAdded());
      }

      if (doUpdate) {
         this.updateItem(-1);
      }
   };
   private final InvalidationListener itemsPropertyListener = new InvalidationListener() {
      private WeakReference<ObservableList<T>> weakItemsRef = new WeakReference<>(null);

      public void invalidated(Observable observable) {
         ObservableList<T> oldItems = this.weakItemsRef.get();
         if (oldItems != null) {
            oldItems.removeListener(GridCell.this.weakItemsListener);
         }

         GridView<T> gv = GridCell.this.getGridView();
         ObservableList<T> items = gv == null ? null : gv.getItems();
         this.weakItemsRef = new WeakReference<>(items);
         if (items != null) {
            items.addListener(GridCell.this.weakItemsListener);
         }

         GridCell.this.updateItem(-1);
      }
   };
   private final WeakListChangeListener<T> weakItemsListener = new WeakListChangeListener(this.itemsListener);
   private final WeakInvalidationListener weakItemsPropertyListener = new WeakInvalidationListener(this.itemsPropertyListener);
   private boolean firstRun = true;
   private final ReadOnlyObjectWrapper<GridView<T>> gridView = new ReadOnlyObjectWrapper<GridView<T>>(this, "gridView") {
      private WeakReference<GridView<T>> weakGridViewRef = new WeakReference<>(null);

      protected void invalidated() {
         GridView<T> currentGridView = (GridView<T>)this.get();
         GridView<T> oldGridView = this.weakGridViewRef.get();
         if (currentGridView != oldGridView) {
            if (oldGridView != null) {
               ObservableList<T> items = oldGridView.getItems();
               if (items != null) {
                  items.removeListener(GridCell.this.weakItemsListener);
               }

               oldGridView.itemsProperty().removeListener(GridCell.this.weakItemsPropertyListener);
            }

            if (currentGridView != null) {
               ObservableList<T> items = currentGridView.getItems();
               if (items != null) {
                  items.addListener(GridCell.this.weakItemsListener);
               }

               currentGridView.itemsProperty().addListener(GridCell.this.weakItemsPropertyListener);
               this.weakGridViewRef = new WeakReference<>(currentGridView);
            }

            GridCell.this.updateItem(-1);
            GridCell.this.requestLayout();
         }
      }
   };

   public GridCell() {
      this.setAlignment(Pos.CENTER);
      this.setMaxHeight(Double.MAX_VALUE);
      this.setMaxWidth(Double.MAX_VALUE);
      this.getStyleClass().addAll(new String[]{"grid-cell"});
      this.indexProperty().addListener((s, o, n) -> {
         if (o != null) {
            int oldIndex = o.intValue();
            if (!this.isEditing() || n == null || n.intValue() != oldIndex) {
               this.updateItem(oldIndex);
            }
         }
      });
      this.setEditable(false);
      this.editableProperty().addListener((s, o, n) -> {
         if (n != null && n) {
            throw new UnsupportedOperationException("Editing is not supported in GridCell");
         }
      });
   }

   public final GridView<T> getGridView() {
      return (GridView<T>)this.gridView.get();
   }

   public final ReadOnlyObjectProperty<GridView<T>> gridViewProperty() {
      return this.gridView.getReadOnlyProperty();
   }

   private void setGridView(GridView<T> view) {
      this.gridView.set(view);
   }

   public final void updateGridView(GridView<T> gridView) {
      this.setGridView(gridView);
   }

   protected Skin<?> createDefaultSkin() {
      return new GridCellSkin<T>(this);
   }

   protected abstract void initViewer(T var1);

   protected void updateItem(T item, boolean empty) {
      super.updateItem(item, empty);
      if (!empty && item != null) {
         this.initViewer(item);
      } else {
         this.setGraphic(null);
      }

      this.setText(null);
   }

   private void updateItem(int oldIndex) {
      GridView<T> gv = this.getGridView();
      List<T> items = gv == null ? null : gv.getItems();
      int index = this.getIndex();
      int itemCount = items == null ? -1 : items.size();
      boolean valid = items != null && index >= 0 && index < itemCount;
      T oldValue = (T)this.getItem();
      boolean isEmpty = this.isEmpty();
      if (valid) {
         T newValue = items.get(index);
         if (oldIndex != index || this.isItemChanged(oldValue, newValue)) {
            this.updateItem(newValue, false);
         }
      } else if (!isEmpty || this.firstRun) {
         this.updateItem(null, true);
         this.firstRun = false;
      }
   }
}
