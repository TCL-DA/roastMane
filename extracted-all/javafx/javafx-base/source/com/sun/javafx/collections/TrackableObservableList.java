package com.sun.javafx.collections;

import java.util.ArrayList;
import java.util.List;
import javafx.collections.ListChangeListener;

public abstract class TrackableObservableList<T> extends ObservableListWrapper<T> {
   public TrackableObservableList(List<T> var1) {
      super(var1);
   }

   public TrackableObservableList() {
      super(new ArrayList<>());
      this.addListener(var1 -> this.onChanged((ListChangeListener.Change<T>)var1));
   }

   protected abstract void onChanged(ListChangeListener.Change<T> var1);
}
