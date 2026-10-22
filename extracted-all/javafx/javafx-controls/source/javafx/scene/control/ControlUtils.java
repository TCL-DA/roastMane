package javafx.scene.control;

import com.sun.javafx.scene.control.skin.Utils;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import javafx.collections.ObservableList;
import javafx.collections.ListChangeListener.Change;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;

class ControlUtils {
   private ControlUtils() {
   }

   public static void scrollToIndex(Control var0, int var1) {
      Utils.executeOnceWhenPropertyIsNonNull(
         var0.skinProperty(), var2 -> Event.fireEvent(var0, new ScrollToEvent<>(var0, var0, ScrollToEvent.scrollToTopIndex(), var1))
      );
   }

   public static void scrollToColumn(Control var0, TableColumnBase<?, ?> var1) {
      Utils.executeOnceWhenPropertyIsNonNull(var0.skinProperty(), var2 -> var0.fireEvent(new ScrollToEvent<>(var0, var0, ScrollToEvent.scrollToColumn(), var1)));
   }

   static void requestFocusOnControlOnlyIfCurrentFocusOwnerIsChild(Control var0) {
      Scene var1 = var0.getScene();
      Node var2 = var1 == null ? null : var1.getFocusOwner();
      if (var2 == null) {
         var0.requestFocus();
      } else if (!var0.equals(var2)) {
         for (Parent var3 = var2.getParent(); var3 != null; var3 = var3.getParent()) {
            if (var0.equals(var3)) {
               var0.requestFocus();
               break;
            }
         }
      }
   }

   static <T> Change<T> buildClearAndSelectChange(ObservableList<T> var0, final List<T> var1, final T var2, final Comparator<T> var3) {
      return new Change<T>(var0) {
         private final int[] EMPTY_PERM = new int[0];
         private final int removedSize = var1.size();
         private final List<T> firstRemovedRange;
         private final List<T> secondRemovedRange;
         private boolean invalid = true;
         private boolean atFirstRange = true;
         private int from = -1;

         {
            int var5 = Collections.binarySearch(var1, var2, var3);
            if (var5 >= 0) {
               this.firstRemovedRange = var1;
               this.secondRemovedRange = Collections.emptyList();
            } else {
               int var6 = -var5 - 1;
               this.firstRemovedRange = var1.subList(0, var6);
               this.secondRemovedRange = var1.subList(var6, this.removedSize);
            }
         }

         public int getFrom() {
            this.checkState();
            return this.from;
         }

         public int getTo() {
            return this.getFrom();
         }

         public List<T> getRemoved() {
            this.checkState();
            return this.atFirstRange ? this.firstRemovedRange : this.secondRemovedRange;
         }

         public int getRemovedSize() {
            this.checkState();
            return this.atFirstRange ? this.firstRemovedRange.size() : this.secondRemovedRange.size();
         }

         protected int[] getPermutation() {
            this.checkState();
            return this.EMPTY_PERM;
         }

         public boolean next() {
            if (this.invalid) {
               this.invalid = false;
               this.from = this.atFirstRange ? 0 : 1;
               return true;
            } else if (this.atFirstRange && !this.secondRemovedRange.isEmpty()) {
               this.atFirstRange = false;
               this.from = 1;
               return true;
            } else {
               return false;
            }
         }

         public void reset() {
            this.invalid = true;
            this.atFirstRange = !this.firstRemovedRange.isEmpty();
         }

         private void checkState() {
            if (this.invalid) {
               throw new IllegalStateException("Invalid Change state: next() must be called before inspecting the Change.");
            }
         }
      };
   }

   public static <S> void updateSelectedIndices(MultipleSelectionModelBase<S> var0, Change<? extends TablePositionBase<?>> var1) {
      var0.selectedIndices._beginChange();

      while (var1.next()) {
         var0.startAtomic();
         List var2 = var1.getRemoved().stream().map(TablePositionBase::getRow).distinct().peek(var0.selectedIndices::clear).collect(Collectors.toList());
         int var3 = (int)var1.getAddedSubList().stream().map(TablePositionBase::getRow).distinct().peek(var0.selectedIndices::set).count();
         var0.stopAtomic();
         int var4 = var1.getFrom() + var3;
         if (var1.wasReplaced()) {
            var0.selectedIndices._nextReplace(var1.getFrom(), var4, var2);
         } else if (var1.wasRemoved()) {
            var0.selectedIndices._nextRemove(var1.getFrom(), var2);
         } else if (var1.wasAdded()) {
            var0.selectedIndices._nextAdd(var1.getFrom(), var4);
         }
      }

      var1.reset();
      var0.selectedIndices.reset();
      if (!var0.isAtomic()) {
         if (var0.getSelectedItems().isEmpty() && var0.getSelectedItem() != null) {
            var0.setSelectedItem(null);
         }

         var0.selectedIndices._endChange();
      }
   }
}
