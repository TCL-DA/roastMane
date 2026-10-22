package org.controlsfx.control;

import javafx.collections.ObservableList;

public interface IndexedCheckModel<T> extends CheckModel<T> {
   T getItem(int var1);

   int getItemIndex(T var1);

   ObservableList<Integer> getCheckedIndices();

   void checkIndices(int... var1);

   void clearCheck(int var1);

   boolean isChecked(int var1);

   void check(int var1);

   void toggleCheckState(int var1);
}
