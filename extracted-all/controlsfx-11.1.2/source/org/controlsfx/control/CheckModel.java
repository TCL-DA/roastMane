package org.controlsfx.control;

import javafx.collections.ObservableList;

public interface CheckModel<T> {
   int getItemCount();

   ObservableList<T> getCheckedItems();

   void checkAll();

   void clearCheck(T var1);

   void clearChecks();

   boolean isEmpty();

   boolean isChecked(T var1);

   void check(T var1);

   void toggleCheckState(T var1);
}
