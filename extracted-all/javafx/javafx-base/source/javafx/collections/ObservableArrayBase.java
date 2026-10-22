package javafx.collections;

import com.sun.javafx.collections.ArrayListenerHelper;
import javafx.beans.InvalidationListener;

public abstract class ObservableArrayBase<T extends ObservableArray<T>> implements ObservableArray<T> {
   private ArrayListenerHelper<T> listenerHelper;

   @Override
   public final void addListener(InvalidationListener var1) {
      this.listenerHelper = ArrayListenerHelper.addListener(this.listenerHelper, this, var1);
   }

   @Override
   public final void removeListener(InvalidationListener var1) {
      this.listenerHelper = ArrayListenerHelper.removeListener(this.listenerHelper, var1);
   }

   @Override
   public final void addListener(ArrayChangeListener<T> var1) {
      this.listenerHelper = ArrayListenerHelper.addListener(this.listenerHelper, this, var1);
   }

   @Override
   public final void removeListener(ArrayChangeListener<T> var1) {
      this.listenerHelper = ArrayListenerHelper.removeListener(this.listenerHelper, var1);
   }

   protected final void fireChange(boolean var1, int var2, int var3) {
      ArrayListenerHelper.fireValueChangedEvent(this.listenerHelper, var1, var2, var3);
   }
}
