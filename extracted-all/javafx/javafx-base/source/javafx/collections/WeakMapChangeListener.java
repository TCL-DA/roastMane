package javafx.collections;

import java.lang.ref.WeakReference;
import javafx.beans.NamedArg;
import javafx.beans.WeakListener;

public final class WeakMapChangeListener<K, V> implements MapChangeListener<K, V>, WeakListener {
   private final WeakReference<MapChangeListener<K, V>> ref;

   public WeakMapChangeListener(@NamedArg("listener") MapChangeListener<K, V> var1) {
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
   public void onChanged(MapChangeListener.Change<? extends K, ? extends V> var1) {
      MapChangeListener var2 = this.ref.get();
      if (var2 != null) {
         var2.onChanged(var1);
      } else {
         var1.getMap().removeListener(this);
      }
   }
}
