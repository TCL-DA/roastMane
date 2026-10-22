package com.sun.javafx.collections;

import java.util.Comparator;
import java.util.List;

public interface SortableList<E> extends List<E> {
   void sort();

   @Override
   void sort(Comparator<? super E> var1);
}
