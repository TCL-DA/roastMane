package javafx.beans.value;

import java.lang.ref.WeakReference;
import javafx.beans.NamedArg;
import javafx.beans.WeakListener;

public final class WeakChangeListener<T> implements ChangeListener<T>, WeakListener {
   private final WeakReference<ChangeListener<T>> ref;

   public WeakChangeListener(@NamedArg("listener") ChangeListener<T> var1) {
      if (var1 == null) {
         throw new NullPointerException("Listener must be specified.");
      } else {
         this.ref = new WeakReference<>(var1);
      }
   }

   @Override
   public boolean wasGarbageCollected() {
      return this.ref.get() == null;
   }

   @Override
   public void changed(ObservableValue<? extends T> var1, T var2, T var3) {
      ChangeListener var4 = this.ref.get();
      if (var4 != null) {
         var4.changed(var1, var2, var3);
      } else {
         var1.removeListener(this);
      }
   }
}
