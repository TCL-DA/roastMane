package nonapi.io.github.classgraph.recycler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class Recycler<T, E extends Exception> implements AutoCloseable {
   private final Set<T> usedInstances = Collections.newSetFromMap(new ConcurrentHashMap<>());
   private final Queue<T> unusedInstances = new ConcurrentLinkedQueue();

   public abstract T newInstance() throws E;

   public T acquire() throws E {
      T recycledInstance = this.unusedInstances.poll();
      T instance;
      if (recycledInstance == null) {
         T newInstance = this.newInstance();
         if (newInstance == null) {
            throw new NullPointerException("Failed to allocate a new recyclable instance");
         }

         instance = newInstance;
      } else {
         instance = recycledInstance;
      }

      this.usedInstances.add(instance);
      return instance;
   }

   public RecycleOnClose<T, E> acquireRecycleOnClose() throws E {
      return new RecycleOnClose<>(this, this.acquire());
   }

   public final void recycle(T instance) {
      if (instance != null) {
         if (!this.usedInstances.remove(instance)) {
            throw new IllegalArgumentException("Tried to recycle an instance that was not in use");
         }

         if (instance instanceof Resettable) {
            ((Resettable)instance).reset();
         }

         if (!this.unusedInstances.add(instance)) {
            throw new IllegalArgumentException("Tried to recycle an instance twice");
         }
      }
   }

   @Override
   public void close() {
      T unusedInstance;
      while ((unusedInstance = this.unusedInstances.poll()) != null) {
         if (unusedInstance instanceof AutoCloseable) {
            try {
               ((AutoCloseable)unusedInstance).close();
            } catch (Exception var3) {
            }
         }
      }
   }

   public void forceClose() {
      for (T usedInstance : new ArrayList<>(this.usedInstances)) {
         if (this.usedInstances.remove(usedInstance)) {
            this.unusedInstances.add(usedInstance);
         }
      }

      this.close();
   }
}
