package javafx.scene.control;

import com.sun.javafx.collections.NonIterableChange.GenericAddRemoveChange;
import com.sun.javafx.scene.control.MultipleAdditionAndRemovedChange;
import com.sun.javafx.scene.control.ReadOnlyUnbackedObservableList;
import com.sun.javafx.scene.control.SelectedItemsReadOnlyObservableList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;
import javafx.collections.ListChangeListener.Change;
import javafx.util.Callback;
import javafx.util.Pair;

abstract class MultipleSelectionModelBase<T> extends MultipleSelectionModel<T> {
   final MultipleSelectionModelBase<T>.SelectedIndicesList selectedIndices;
   private final ObservableListBase<T> selectedItems;
   Change selectedItemChange;

   public MultipleSelectionModelBase() {
      this.selectedIndexProperty().addListener(var1 -> this.setSelectedItem(this.getModelItem(this.getSelectedIndex())));
      this.selectedIndices = new MultipleSelectionModelBase.SelectedIndicesList();
      this.selectedItems = new SelectedItemsReadOnlyObservableList<T>(this.selectedIndices, () -> this.getItemCount()) {
         @Override
         protected T getModelItem(int var1) {
            return (T)MultipleSelectionModelBase.this.getModelItem(var1);
         }
      };
   }

   @Override
   public ObservableList<Integer> getSelectedIndices() {
      return this.selectedIndices;
   }

   @Override
   public ObservableList<T> getSelectedItems() {
      return this.selectedItems;
   }

   protected abstract int getItemCount();

   protected abstract T getModelItem(int var1);

   protected abstract void focus(int var1);

   protected abstract int getFocusedIndex();

   void shiftSelection(int var1, int var2, Callback<MultipleSelectionModelBase.ShiftParams, Void> var3) {
      this.shiftSelection(Arrays.asList(new Pair(var1, var2)), var3);
   }

   void shiftSelection(List<Pair<Integer, Integer>> var1, Callback<MultipleSelectionModelBase.ShiftParams, Void> var2) {
      int var3 = this.selectedIndices.size();
      if (var3 != 0) {
         int var4 = this.selectedIndices.bitsetSize();
         int[] var5 = new int[var4];
         Arrays.fill(var5, -1);
         Collections.sort(var1, (var0, var1x) -> Integer.compare((Integer)var1x.getKey(), (Integer)var0.getKey()));
         int var6 = (Integer)((Pair)var1.get(var1.size() - 1)).getKey();
         BitSet var7 = (BitSet)this.selectedIndices.bitset.clone();
         this.startAtomic();

         for (Pair var9 : var1) {
            this.doShift(var9, var2, var5);
         }

         this.stopAtomic();
         int[] var13 = Arrays.stream(var5).filter(var0 -> var0 > -1).toArray();
         boolean var14 = var13.length > 0;
         int var10 = this.getSelectedIndex();
         if (var10 >= var6 && var10 > -1) {
            int var11 = var1.stream().filter(var1x -> (Integer)var1x.getKey() <= var10).mapToInt(var0 -> (Integer)var0.getValue()).sum();
            int var12 = Math.max(0, var10 + var11);
            this.setSelectedIndex(var12);
            if (var14) {
               this.selectedIndices.set(var12, true);
            } else {
               this.select(var12);
            }
         }

         if (var14) {
            BitSet var15 = (BitSet)var7.clone();
            var15.andNot(this.selectedIndices.bitset);
            BitSet var16 = (BitSet)this.selectedIndices.bitset.clone();
            var16.andNot(var7);
            this.selectedIndices.reset();
            this.selectedIndices
               .callObservers(
                  new MultipleAdditionAndRemovedChange<>(
                     var16.stream().boxed().collect(Collectors.toList()), var15.stream().boxed().collect(Collectors.toList()), this.selectedIndices
                  )
               );
         }
      }
   }

   private void doShift(Pair<Integer, Integer> var1, Callback<MultipleSelectionModelBase.ShiftParams, Void> var2, int[] var3) {
      int var4 = (Integer)var1.getKey();
      int var5 = (Integer)var1.getValue();
      if (var4 >= 0) {
         if (var5 != 0) {
            int var6 = (int)Arrays.stream(var3).filter(var0 -> var0 > -1).count();
            int var7 = this.selectedIndices.bitsetSize() - var6;
            if (var5 > 0) {
               for (int var8 = var7 - 1; var8 >= var4 && var8 >= 0; var8--) {
                  boolean var9 = this.selectedIndices.isSelected(var8);
                  if (var2 == null) {
                     this.selectedIndices.clear(var8);
                     this.selectedIndices.set(var8 + var5, var9);
                  } else {
                     var2.call(new MultipleSelectionModelBase.ShiftParams(var8, var8 + var5, var9));
                  }

                  if (var9) {
                     var3[var6++] = var8 + 1;
                  }
               }

               this.selectedIndices.clear(var4);
            } else if (var5 < 0) {
               for (int var10 = var4; var10 < var7; var10++) {
                  if (var10 + var5 >= 0 && var10 + 1 + var5 >= var4) {
                     boolean var11 = this.selectedIndices.isSelected(var10 + 1);
                     if (var2 == null) {
                        this.selectedIndices.clear(var10 + 1);
                        this.selectedIndices.set(var10 + 1 + var5, var11);
                     } else {
                        var2.call(new MultipleSelectionModelBase.ShiftParams(var10 + 1, var10 + 1 + var5, var11));
                     }

                     if (var11) {
                        var3[var6++] = var10;
                     }
                  }
               }
            }
         }
      }
   }

   void startAtomic() {
      this.selectedIndices.startAtomic();
   }

   void stopAtomic() {
      this.selectedIndices.stopAtomic();
   }

   boolean isAtomic() {
      return this.selectedIndices.isAtomic();
   }

   @Override
   public void clearAndSelect(int var1) {
      if (var1 >= 0 && var1 < this.getItemCount()) {
         boolean var2 = this.isSelected(var1);
         if (!var2 || this.getSelectedIndices().size() != 1 || this.getSelectedItem() != this.getModelItem(var1)) {
            BitSet var3 = new BitSet();
            var3.or(this.selectedIndices.bitset);
            var3.clear(var1);
            MultipleSelectionModelBase.SelectedIndicesList var4 = new MultipleSelectionModelBase.SelectedIndicesList(var3);
            this.startAtomic();
            this.clearSelection();
            this.select(var1);
            this.stopAtomic();
            Object var5;
            if (var2) {
               var5 = ControlUtils.buildClearAndSelectChange(this.selectedIndices, var4, var1, Comparator.naturalOrder());
            } else {
               int var6 = Math.max(0, this.selectedIndices.indexOf(var1));
               var5 = new GenericAddRemoveChange(var6, var6 + 1, var4, this.selectedIndices);
            }

            this.selectedIndices.callObservers((Change<Integer>)var5);
         }
      } else {
         this.clearSelection();
      }
   }

   @Override
   public void select(int var1) {
      if (var1 == -1) {
         this.clearSelection();
      } else if (var1 >= 0 && var1 < this.getItemCount()) {
         boolean var2 = var1 == this.getSelectedIndex();
         Object var3 = this.getSelectedItem();
         Object var4 = this.getModelItem(var1);
         boolean var5 = var4 != null && var4.equals(var3);
         boolean var6 = var2 && !var5;
         this.focus(var1);
         if (!this.selectedIndices.isSelected(var1)) {
            if (this.getSelectionMode() == SelectionMode.SINGLE) {
               this.startAtomic();
               this.quietClearSelection();
               this.stopAtomic();
            }

            this.selectedIndices.set(var1);
         }

         this.setSelectedIndex(var1);
         if (var6) {
            this.setSelectedItem((T)var4);
         }
      }
   }

   @Override
   public void select(T var1) {
      if (var1 == null && this.getSelectionMode() == SelectionMode.SINGLE) {
         this.clearSelection();
      } else {
         Object var2 = null;
         int var3 = 0;

         for (int var4 = this.getItemCount(); var3 < var4; var3++) {
            var2 = this.getModelItem(var3);
            if (var2 != null && var2.equals(var1)) {
               if (this.isSelected(var3)) {
                  return;
               }

               if (this.getSelectionMode() == SelectionMode.SINGLE) {
                  this.quietClearSelection();
               }

               this.select(var3);
               return;
            }
         }

         this.setSelectedIndex(-1);
         this.setSelectedItem((T)var1);
      }
   }

   @Override
   public void selectIndices(int var1, int... var2) {
      if (var2 != null && var2.length != 0) {
         int var3 = this.getItemCount();
         if (this.getSelectionMode() == SelectionMode.SINGLE) {
            this.quietClearSelection();

            for (int var4 = var2.length - 1; var4 >= 0; var4--) {
               int var5 = var2[var4];
               if (var5 >= 0 && var5 < var3) {
                  this.selectedIndices.set(var5);
                  this.select(var5);
                  break;
               }
            }

            if (this.selectedIndices.isEmpty() && var1 > 0 && var1 < var3) {
               this.selectedIndices.set(var1);
               this.select(var1);
            }
         } else {
            this.selectedIndices.set(var1, var2);
            IntStream.concat(IntStream.of(var1), IntStream.of(var2))
               .filter(var1x -> var1x >= 0 && var1x < var3)
               .reduce((var0, var1x) -> var1x)
               .ifPresent(var1x -> {
                  this.setSelectedIndex(var1x);
                  this.focus(var1x);
                  this.setSelectedItem(this.getModelItem(var1x));
               });
         }
      } else {
         this.select(var1);
      }
   }

   @Override
   public void selectAll() {
      if (this.getSelectionMode() != SelectionMode.SINGLE) {
         if (this.getItemCount() > 0) {
            int var1 = this.getItemCount();
            int var2 = this.getFocusedIndex();
            this.clearSelection();
            this.selectedIndices.set(0, var1, true);
            if (var2 == -1) {
               this.setSelectedIndex(var1 - 1);
               this.focus(var1 - 1);
            } else {
               this.setSelectedIndex(var2);
               this.focus(var2);
            }
         }
      }
   }

   @Override
   public void selectFirst() {
      if (this.getSelectionMode() == SelectionMode.SINGLE) {
         this.quietClearSelection();
      }

      if (this.getItemCount() > 0) {
         this.select(0);
      }
   }

   @Override
   public void selectLast() {
      if (this.getSelectionMode() == SelectionMode.SINGLE) {
         this.quietClearSelection();
      }

      int var1 = this.getItemCount();
      if (var1 > 0 && this.getSelectedIndex() < var1 - 1) {
         this.select(var1 - 1);
      }
   }

   @Override
   public void clearSelection(int var1) {
      if (var1 >= 0) {
         boolean var2 = this.selectedIndices.isEmpty();
         this.selectedIndices.clear(var1);
         if (!var2 && this.selectedIndices.isEmpty()) {
            this.clearSelection();
         }
      }
   }

   @Override
   public void clearSelection() {
      this.quietClearSelection();
      if (!this.isAtomic()) {
         this.setSelectedIndex(-1);
         this.focus(-1);
      }
   }

   private void quietClearSelection() {
      this.selectedIndices.clear();
   }

   @Override
   public boolean isSelected(int var1) {
      return var1 >= 0 && var1 < this.selectedIndices.bitsetSize() ? this.selectedIndices.isSelected(var1) : false;
   }

   @Override
   public boolean isEmpty() {
      return this.selectedIndices.isEmpty();
   }

   @Override
   public void selectPrevious() {
      int var1 = this.getFocusedIndex();
      if (this.getSelectionMode() == SelectionMode.SINGLE) {
         this.quietClearSelection();
      }

      if (var1 == -1) {
         this.select(this.getItemCount() - 1);
      } else if (var1 > 0) {
         this.select(var1 - 1);
      }
   }

   @Override
   public void selectNext() {
      int var1 = this.getFocusedIndex();
      if (this.getSelectionMode() == SelectionMode.SINGLE) {
         this.quietClearSelection();
      }

      if (var1 == -1) {
         this.select(0);
      } else if (var1 != this.getItemCount() - 1) {
         this.select(var1 + 1);
      }
   }

   class SelectedIndicesList extends ReadOnlyUnbackedObservableList<Integer> {
      private final BitSet bitset;
      private int lastGetIndex = -1;
      private int lastGetValue = -1;
      private int atomicityCount = 0;

      public SelectedIndicesList() {
         this(new BitSet());
      }

      public SelectedIndicesList(BitSet var2) {
         this.bitset = var2;
      }

      boolean isAtomic() {
         return this.atomicityCount > 0;
      }

      void startAtomic() {
         this.atomicityCount++;
      }

      void stopAtomic() {
         this.atomicityCount = Math.max(0, this.atomicityCount - 1);
      }

      public Integer get(int var1) {
         int var2 = this.size();
         if (var1 >= 0 && var1 < var2) {
            if (var1 == this.lastGetIndex + 1 && this.lastGetValue < var2) {
               this.lastGetIndex++;
               this.lastGetValue = this.bitset.nextSetBit(this.lastGetValue + 1);
               return this.lastGetValue;
            } else if (var1 == this.lastGetIndex - 1 && this.lastGetValue > 0) {
               this.lastGetIndex--;
               this.lastGetValue = this.bitset.previousSetBit(this.lastGetValue - 1);
               return this.lastGetValue;
            } else {
               this.lastGetIndex = 0;

               for (this.lastGetValue = this.bitset.nextSetBit(0);
                  this.lastGetValue >= 0 || this.lastGetIndex == var1;
                  this.lastGetValue = this.bitset.nextSetBit(this.lastGetValue + 1)
               ) {
                  if (this.lastGetIndex == var1) {
                     return this.lastGetValue;
                  }

                  this.lastGetIndex++;
               }

               return -1;
            }
         } else {
            throw new IndexOutOfBoundsException(var1 + " >= " + var2);
         }
      }

      public void set(int var1) {
         if (this.isValidIndex(var1) && !this.isSelected(var1)) {
            this._beginChange();
            this.bitset.set(var1);
            int var2 = this.indexOf(var1);
            this._nextAdd(var2, var2 + 1);
            this._endChange();
         }
      }

      private boolean isValidIndex(int var1) {
         return var1 >= 0 && var1 < MultipleSelectionModelBase.this.getItemCount();
      }

      public void set(int var1, boolean var2) {
         if (var2) {
            this.set(var1);
         } else {
            this.clear(var1);
         }
      }

      public void set(int var1, int var2, boolean var3) {
         this._beginChange();
         if (var3) {
            this.bitset.set(var1, var2, var3);
            int var4 = this.indexOf(var1);
            int var5 = var2 - var1;
            this._nextAdd(var4, var4 + var5);
         } else {
            this.bitset.set(var1, var2, var3);
         }

         this._endChange();
      }

      public void set(int var1, int... var2) {
         if (var2 != null && var2.length != 0) {
            this.startAtomic();
            List var3 = IntStream.concat(IntStream.of(var1), IntStream.of(var2))
               .distinct()
               .filter(this::isValidIndex)
               .filter(this::isNotSelected)
               .sorted()
               .boxed()
               .peek(this::set)
               .collect(Collectors.toList());
            this.stopAtomic();
            int var4 = var3.size();
            if (var4 != 0) {
               if (var4 == 1) {
                  this._beginChange();
                  int var5 = (Integer)var3.get(0);
                  int var6 = this.indexOf(var5);
                  this._nextAdd(var6, var6 + 1);
                  this._endChange();
               } else {
                  this._beginChange();
                  int var11 = 0;
                  int var13 = 0;
                  int var7 = 0;
                  int var8 = (Integer)var3.get(var11++);
                  var13 = this.indexOf(var8);
                  var7 = var13 + 1;
                  int var9 = var8;

                  while (var11 < var4) {
                     int var10 = var9;
                     var9 = (Integer)var3.get(var11++);
                     var7++;
                     if (var10 != var9 - 1) {
                        this._nextAdd(var13, var7);
                        var13 = var7;
                     } else if (var11 == var4) {
                        this._nextAdd(var13, var13 + var11);
                     }
                  }

                  this._endChange();
               }
            }
         } else {
            this.set(var1);
         }
      }

      @Override
      public void clear() {
         this._beginChange();
         List var1 = this.bitset.stream().boxed().collect(Collectors.toList());
         this.bitset.clear();
         this._nextRemove(0, var1);
         this._endChange();
      }

      public void clear(int var1) {
         if (this.bitset.get(var1)) {
            int var2 = this.indexOf(var1);
            this._beginChange();
            this.bitset.clear(var1);
            this._nextRemove(var2, var1);
            this._endChange();
         }
      }

      public boolean isSelected(int var1) {
         return this.bitset.get(var1);
      }

      public boolean isNotSelected(int var1) {
         return !this.isSelected(var1);
      }

      @Override
      public int size() {
         return this.bitset.cardinality();
      }

      public int bitsetSize() {
         return this.bitset.size();
      }

      @Override
      public int indexOf(Object var1) {
         this.reset();
         return super.indexOf(var1);
      }

      @Override
      public boolean contains(Object var1) {
         if (!(var1 instanceof Number)) {
            return false;
         } else {
            Number var2 = (Number)var1;
            int var3 = var2.intValue();
            return var3 >= 0 && var3 < this.bitset.length() && this.bitset.get(var3);
         }
      }

      public void reset() {
         this.lastGetIndex = -1;
         this.lastGetValue = -1;
      }

      @Override
      public void _beginChange() {
         if (!this.isAtomic()) {
            super._beginChange();
         }
      }

      @Override
      public void _endChange() {
         if (!this.isAtomic()) {
            super._endChange();
         }
      }

      @Override
      public final void _nextUpdate(int var1) {
         if (!this.isAtomic()) {
            this.nextUpdate(var1);
         }
      }

      public final void _nextSet(int var1, Integer var2) {
         if (!this.isAtomic()) {
            this.nextSet(var1, var2);
         }
      }

      @Override
      public final void _nextReplace(int var1, int var2, List<? extends Integer> var3) {
         if (!this.isAtomic()) {
            this.nextReplace(var1, var2, var3);
         }
      }

      @Override
      public final void _nextRemove(int var1, List<? extends Integer> var2) {
         if (!this.isAtomic()) {
            this.nextRemove(var1, var2);
         }
      }

      public final void _nextRemove(int var1, Integer var2) {
         if (!this.isAtomic()) {
            this.nextRemove(var1, var2);
         }
      }

      @Override
      public final void _nextPermutation(int var1, int var2, int[] var3) {
         if (!this.isAtomic()) {
            this.nextPermutation(var1, var2, var3);
         }
      }

      @Override
      public final void _nextAdd(int var1, int var2) {
         if (!this.isAtomic()) {
            this.nextAdd(var1, var2);
         }
      }
   }

   static class ShiftParams {
      private final int clearIndex;
      private final int setIndex;
      private final boolean selected;

      ShiftParams(int var1, int var2, boolean var3) {
         this.clearIndex = var1;
         this.setIndex = var2;
         this.selected = var3;
      }

      public final int getClearIndex() {
         return this.clearIndex;
      }

      public final int getSetIndex() {
         return this.setIndex;
      }

      public final boolean isSelected() {
         return this.selected;
      }
   }
}
