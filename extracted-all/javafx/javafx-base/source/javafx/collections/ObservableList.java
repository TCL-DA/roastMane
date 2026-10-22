package javafx.collections;

import java.text.Collator;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import javafx.beans.Observable;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

public interface ObservableList<E> extends List<E>, Observable {
   void addListener(ListChangeListener<? super E> var1);

   void removeListener(ListChangeListener<? super E> var1);

   boolean addAll(E... var1);

   boolean setAll(E... var1);

   boolean setAll(Collection<? extends E> var1);

   boolean removeAll(E... var1);

   boolean retainAll(E... var1);

   void remove(int var1, int var2);

   default FilteredList<E> filtered(Predicate<E> var1) {
      return new FilteredList<>(this, var1);
   }

   default SortedList<E> sorted(Comparator<E> var1) {
      return new SortedList<>(this, var1);
   }

   default SortedList<E> sorted() {
      Comparator var1 = new Comparator<E>() {
         @Override
         public int compare(E var1, E var2) {
            if (var1 == null && var2 == null) {
               return 0;
            } else if (var1 == null) {
               return -1;
            } else if (var2 == null) {
               return 1;
            } else {
               return var1 instanceof Comparable ? ((Comparable)var1).compareTo(var2) : Collator.getInstance().compare(var1.toString(), var2.toString());
            }
         }
      };
      return this.sorted(var1);
   }
}
