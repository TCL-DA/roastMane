package javafx.collections;

import java.util.Set;
import javafx.beans.Observable;

public interface ObservableSet<E> extends Set<E>, Observable {
   void addListener(SetChangeListener<? super E> var1);

   void removeListener(SetChangeListener<? super E> var1);
}
