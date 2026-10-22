package org.eclipse.jetty.util.thread;

public interface Invocable {
   Runnable NOOP = () -> {
   };
   ThreadLocal<Boolean> __nonBlocking = new ThreadLocal<>();

   static Invocable.Task from(Invocable.InvocationType type, Runnable task) {
      return new Invocable.ReadyTask(type, task);
   }

   static boolean isNonBlockingInvocation() {
      return Boolean.TRUE.equals(__nonBlocking.get());
   }

   static void invokeNonBlocking(Runnable task) {
      Boolean wasNonBlocking = __nonBlocking.get();

      try {
         __nonBlocking.set(Boolean.TRUE);
         task.run();
      } finally {
         __nonBlocking.set(wasNonBlocking);
      }
   }

   static Invocable.InvocationType combine(Invocable.InvocationType it1, Invocable.InvocationType it2) {
      if (it1 != null && it2 != null) {
         if (it1 == it2) {
            return it1;
         }

         if (it1 == Invocable.InvocationType.EITHER) {
            return it2;
         }

         if (it2 == Invocable.InvocationType.EITHER) {
            return it1;
         }
      }

      return Invocable.InvocationType.BLOCKING;
   }

   static Invocable.InvocationType getInvocationType(Object o) {
      return o instanceof Invocable ? ((Invocable)o).getInvocationType() : Invocable.InvocationType.BLOCKING;
   }

   default Invocable.InvocationType getInvocationType() {
      return Invocable.InvocationType.BLOCKING;
   }

   public interface Callable extends Invocable {
      void call() throws Exception;
   }

   public static enum InvocationType {
      BLOCKING,
      NON_BLOCKING,
      EITHER;
   }

   public static class ReadyTask implements Invocable.Task {
      private final Invocable.InvocationType type;
      private final Runnable task;

      public ReadyTask(Invocable.InvocationType type, Runnable task) {
         this.type = type;
         this.task = task;
      }

      @Override
      public void run() {
         this.task.run();
      }

      @Override
      public Invocable.InvocationType getInvocationType() {
         return this.type;
      }

      @Override
      public String toString() {
         return String.format("%s@%x[%s|%s]", this.getClass().getSimpleName(), this.hashCode(), this.type, this.task);
      }
   }

   public interface Task extends Invocable, Runnable {
   }
}
