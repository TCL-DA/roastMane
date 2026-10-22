package org.controlsfx.control;

import impl.org.controlsfx.collections.MappingChange;
import impl.org.controlsfx.collections.NonIterableChange;
import impl.org.controlsfx.collections.ReadOnlyUnbackedObservableList;
import java.util.BitSet;
import java.util.Map;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;

abstract class CheckBitSetModelBase<T> implements IndexedCheckModel<T> {
   private final Map<T, BooleanProperty> itemBooleanMap;
   private final BitSet checkedIndices;
   private final ReadOnlyUnbackedObservableList<Integer> checkedIndicesList;
   private final ReadOnlyUnbackedObservableList<T> checkedItemsList;

   CheckBitSetModelBase(Map<T, BooleanProperty> itemBooleanMap) {
      this.itemBooleanMap = itemBooleanMap;
      this.checkedIndices = new BitSet();
      this.checkedIndicesList = new ReadOnlyUnbackedObservableList<Integer>() {
         public Integer get(int index) {
            if (index >= 0 && index < CheckBitSetModelBase.this.getItemCount()) {
               int pos = 0;

               for (int val = CheckBitSetModelBase.this.checkedIndices.nextSetBit(0);
                  val >= 0 || pos == index;
                  val = CheckBitSetModelBase.this.checkedIndices.nextSetBit(val + 1)
               ) {
                  if (pos == index) {
                     return val;
                  }

                  pos++;
               }

               return -1;
            } else {
               return -1;
            }
         }

         @Override
         public int size() {
            return CheckBitSetModelBase.this.checkedIndices.cardinality();
         }

         @Override
         public boolean contains(Object o) {
            if (!(o instanceof Number)) {
               return false;
            } else {
               Number n = (Number)o;
               int index = n.intValue();
               return index >= 0 && index < CheckBitSetModelBase.this.checkedIndices.length() && CheckBitSetModelBase.this.checkedIndices.get(index);
            }
         }
      };
      this.checkedItemsList = new ReadOnlyUnbackedObservableList<T>() {
         @Override
         public T get(int i) {
            int pos = CheckBitSetModelBase.this.checkedIndicesList.get(i);
            return (T)(pos >= 0 && pos < CheckBitSetModelBase.this.getItemCount() ? CheckBitSetModelBase.this.getItem(pos) : null);
         }

         @Override
         public int size() {
            return CheckBitSetModelBase.this.checkedIndices.cardinality();
         }
      };
      MappingChange.Map<Integer, T> map = f -> this.getItem(f);
      this.checkedIndicesList.addListener(c -> {
         boolean hasRealChangeOccurred = false;

         while (c.next() && !hasRealChangeOccurred) {
            hasRealChangeOccurred = c.wasAdded() || c.wasRemoved();
         }

         if (hasRealChangeOccurred) {
            c.reset();
            this.checkedItemsList.callObservers(new MappingChange<>(c, map, this.checkedItemsList));
         }

         c.reset();
      });
      this.getCheckedItems().addListener(c -> {
         while (c.next()) {
            if (c.wasAdded()) {
               for (T item : c.getAddedSubList()) {
                  BooleanProperty p = this.getItemBooleanProperty(item);
                  if (p != null) {
                     p.set(true);
                  }
               }
            }

            if (c.wasRemoved()) {
               for (T itemx : c.getRemoved()) {
                  BooleanProperty p = this.getItemBooleanProperty(itemx);
                  if (p != null) {
                     p.set(false);
                  }
               }
            }
         }
      });
   }

   @Override
   public abstract T getItem(int var1);

   @Override
   public abstract int getItemCount();

   @Override
   public abstract int getItemIndex(T var1);

   BooleanProperty getItemBooleanProperty(T item) {
      return this.itemBooleanMap.get(item);
   }

   @Override
   public ObservableList<Integer> getCheckedIndices() {
      return this.checkedIndicesList;
   }

   @Override
   public ObservableList<T> getCheckedItems() {
      return this.checkedItemsList;
   }

   @Override
   public void checkAll() {
      for (int i = 0; i < this.getItemCount(); i++) {
         this.check(i);
      }
   }

   @Override
   public void checkIndices(int... indices) {
      for (int i = 0; i < indices.length; i++) {
         this.check(indices[i]);
      }
   }

   @Override
   public void clearCheck(T item) {
      int index = this.getItemIndex(item);
      this.clearCheck(index);
   }

   @Override
   public void clearChecks() {
      for (int index = 0; index < this.checkedIndices.length(); index++) {
         this.clearCheck(index);
      }
   }

   @Override
   public void clearCheck(int index) {
      if (index >= 0 && index < this.getItemCount()) {
         this.checkedIndices.clear(index);
         int changeIndex = this.checkedIndicesList.indexOf(index);
         this.checkedIndicesList.callObservers(new NonIterableChange.SimpleRemovedChange<>(changeIndex, changeIndex, index, this.checkedIndicesList));
      }
   }

   @Override
   public boolean isEmpty() {
      return this.checkedIndices.isEmpty();
   }

   @Override
   public boolean isChecked(T item) {
      int index = this.getItemIndex(item);
      return this.isChecked(index);
   }

   @Override
   public boolean isChecked(int index) {
      return this.checkedIndices.get(index);
   }

   @Override
   public void toggleCheckState(T item) {
      int index = this.getItemIndex(item);
      this.toggleCheckState(index);
   }

   @Override
   public void toggleCheckState(int index) {
      if (this.isChecked(index)) {
         this.clearCheck(index);
      } else {
         this.check(index);
      }
   }

   @Override
   public void check(int index) {
      if (index >= 0 && index < this.getItemCount()) {
         this.checkedIndices.set(index);
         int changeIndex = this.checkedIndicesList.indexOf(index);
         this.checkedIndicesList.callObservers(new NonIterableChange.SimpleAddChange<>(changeIndex, changeIndex + 1, this.checkedIndicesList));
      }
   }

   @Override
   public void check(T item) {
      int index = this.getItemIndex(item);
      this.check(index);
   }

   protected void updateMap() {
      this.itemBooleanMap.clear();

      for (final int i = 0; i < this.getItemCount(); i++) {
         T item = this.getItem(i);
         final BooleanProperty booleanProperty = new SimpleBooleanProperty(item, "selected", false);
         this.itemBooleanMap.put(item, booleanProperty);
         booleanProperty.addListener(
            new InvalidationListener() {
               public void invalidated(Observable o) {
                  if (booleanProperty.get()) {
                     CheckBitSetModelBase.this.checkedIndices.set(i);
                     int changeIndex = CheckBitSetModelBase.this.checkedIndicesList.indexOf(i);
                     CheckBitSetModelBase.this.checkedIndicesList
                        .callObservers(new NonIterableChange.SimpleAddChange<>(changeIndex, changeIndex + 1, CheckBitSetModelBase.this.checkedIndicesList));
                  } else {
                     int changeIndex = CheckBitSetModelBase.this.checkedIndicesList.indexOf(i);
                     CheckBitSetModelBase.this.checkedIndices.clear(i);
                     CheckBitSetModelBase.this.checkedIndicesList
                        .callObservers(new NonIterableChange.SimpleRemovedChange<>(changeIndex, changeIndex, i, CheckBitSetModelBase.this.checkedIndicesList));
                  }
               }
            }
         );
      }
   }
}
