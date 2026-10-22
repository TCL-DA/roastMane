package javafx.beans.property;

import com.sun.javafx.binding.MapExpressionHelper;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;

public abstract class ReadOnlyMapPropertyBase<K, V> extends ReadOnlyMapProperty<K, V> {
   private MapExpressionHelper<K, V> helper;

   @Override
   public void addListener(InvalidationListener var1) {
      this.helper = MapExpressionHelper.addListener(this.helper, this, var1);
   }

   @Override
   public void removeListener(InvalidationListener var1) {
      this.helper = MapExpressionHelper.removeListener(this.helper, var1);
   }

   @Override
   public void addListener(ChangeListener<? super ObservableMap<K, V>> var1) {
      this.helper = MapExpressionHelper.addListener(this.helper, this, var1);
   }

   @Override
   public void removeListener(ChangeListener<? super ObservableMap<K, V>> var1) {
      this.helper = MapExpressionHelper.removeListener(this.helper, var1);
   }

   @Override
   public void addListener(MapChangeListener<? super K, ? super V> var1) {
      this.helper = MapExpressionHelper.addListener(this.helper, this, var1);
   }

   @Override
   public void removeListener(MapChangeListener<? super K, ? super V> var1) {
      this.helper = MapExpressionHelper.removeListener(this.helper, var1);
   }

   protected void fireValueChangedEvent() {
      MapExpressionHelper.fireValueChangedEvent(this.helper);
   }

   protected void fireValueChangedEvent(MapChangeListener.Change<? extends K, ? extends V> var1) {
      MapExpressionHelper.fireValueChangedEvent(this.helper, var1);
   }
}
