package javafx.collections;

import java.lang.ref.WeakReference;
import javafx.beans.NamedArg;
import javafx.beans.WeakListener;

public final class WeakSetChangeListener<E> implements SetChangeListener<E>, WeakListener {
   private final WeakReference<SetChangeListener<E>> ref;

   public WeakSetChangeListener(@NamedArg("listener") SetChangeListener<E> var1) {
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
   public void onChanged(SetChangeListener.Change<? extends E> var1) {
      SetChangeListener var2 = this.ref.get();
      if (var2 != null) {
         var2.onChanged(var1);
      } else {
         var1.getSet().removeListener(this);
      }
   }
}
