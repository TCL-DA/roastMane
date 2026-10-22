package javafx.collections;

@FunctionalInterface
public interface SetChangeListener<E> {
   void onChanged(SetChangeListener.Change<? extends E> var1);

   public abstract static class Change<E> {
      private ObservableSet<E> set;

      public Change(ObservableSet<E> var1) {
         this.set = var1;
      }

      public ObservableSet<E> getSet() {
         return this.set;
      }

      public abstract boolean wasAdded();

      public abstract boolean wasRemoved();

      public abstract E getElementAdded();

      public abstract E getElementRemoved();
   }
}
