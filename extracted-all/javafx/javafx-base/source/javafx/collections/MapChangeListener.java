package javafx.collections;

@FunctionalInterface
public interface MapChangeListener<K, V> {
   void onChanged(MapChangeListener.Change<? extends K, ? extends V> var1);

   public abstract static class Change<K, V> {
      private final ObservableMap<K, V> map;

      public Change(ObservableMap<K, V> var1) {
         this.map = var1;
      }

      public ObservableMap<K, V> getMap() {
         return this.map;
      }

      public abstract boolean wasAdded();

      public abstract boolean wasRemoved();

      public abstract K getKey();

      public abstract V getValueAdded();

      public abstract V getValueRemoved();
   }
}
