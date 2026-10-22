package javafx.collections;

import java.util.Map;
import javafx.beans.Observable;

public interface ObservableMap<K, V> extends Map<K, V>, Observable {
   void addListener(MapChangeListener<? super K, ? super V> var1);

   void removeListener(MapChangeListener<? super K, ? super V> var1);
}
