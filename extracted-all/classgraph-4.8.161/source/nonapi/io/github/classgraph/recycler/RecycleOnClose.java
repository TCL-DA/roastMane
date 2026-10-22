package nonapi.io.github.classgraph.recycler;

public class RecycleOnClose<T, E extends Exception> implements AutoCloseable {
   private final Recycler<T, E> recycler;
   private final T instance;

   RecycleOnClose(Recycler<T, E> recycler, T instance) {
      this.recycler = recycler;
      this.instance = instance;
   }

   public T get() {
      return this.instance;
   }

   @Override
   public void close() {
      this.recycler.recycle(this.instance);
   }
}
