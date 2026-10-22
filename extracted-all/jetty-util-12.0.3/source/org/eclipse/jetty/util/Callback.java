package org.eclipse.jetty.util;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import org.eclipse.jetty.util.thread.Invocable;

public interface Callback extends Invocable {
   Callback NOOP = new Callback() {
      @Override
      public Invocable.InvocationType getInvocationType() {
         return Invocable.InvocationType.NON_BLOCKING;
      }

      @Override
      public String toString() {
         return "Callback.NOOP";
      }
   };

   default void completeWith(CompletableFuture<?> completable) {
      completable.whenComplete((o, x) -> {
         if (x == null) {
            this.succeeded();
         } else {
            this.failed(x);
         }
      });
   }

   default void succeeded() {
   }

   default void failed(Throwable x) {
   }

   static Callback from(CompletableFuture<?> completable) {
      return from(completable, Invocable.InvocationType.NON_BLOCKING);
   }

   static Callback from(final CompletableFuture<?> completable, final Invocable.InvocationType invocation) {
      return completable instanceof Callback ? (Callback)completable : new Callback() {
         @Override
         public void succeeded() {
            completable.complete(null);
         }

         @Override
         public void failed(Throwable x) {
            completable.completeExceptionally(x);
         }

         @Override
         public Invocable.InvocationType getInvocationType() {
            return invocation;
         }
      };
   }

   static Callback from(Runnable success, Consumer<Throwable> failure) {
      return from(Invocable.InvocationType.BLOCKING, success, failure);
   }

   static Callback from(final Invocable.InvocationType invocationType, final Runnable success, final Consumer<Throwable> failure) {
      return new Callback() {
         @Override
         public void succeeded() {
            success.run();
         }

         @Override
         public void failed(Throwable x) {
            failure.accept(x);
         }

         @Override
         public Invocable.InvocationType getInvocationType() {
            return invocationType;
         }

         @Override
         public String toString() {
            return "Callback@%x{%s, %s,%s}".formatted(this.hashCode(), invocationType, success, failure);
         }
      };
   }

   static Callback from(Runnable completed) {
      return from(Invocable.getInvocationType(completed), completed);
   }

   static Callback from(final Invocable.InvocationType invocationType, final Runnable completed) {
      return new Callback.Completing() {
         @Override
         public void completed() {
            completed.run();
         }

         @Override
         public Invocable.InvocationType getInvocationType() {
            return invocationType;
         }

         @Override
         public String toString() {
            return "Callback.Completing@%x{%s,%s}".formatted(this.hashCode(), invocationType, completed);
         }
      };
   }

   static Callback from(final Callback callback, final Runnable completed) {
      return new Callback.Nested() {
         @Override
         public void completed() {
            completed.run();
         }
      };
   }

   static Callback from(final Callback callback, final Consumer<Throwable> completed) {
      return new Callback() {
         @Override
         public void succeeded() {
            try {
               callback.succeeded();
            } finally {
               completed.accept(null);
            }
         }

         @Override
         public void failed(Throwable x) {
            try {
               callback.failed(x);
            } finally {
               completed.accept(x);
            }
         }
      };
   }

   static Callback from(final Runnable completed, final Callback callback) {
      return new Callback() {
         @Override
         public void succeeded() {
            try {
               completed.run();
               callback.succeeded();
            } catch (Throwable var2) {
               callback.failed(var2);
            }
         }

         @Override
         public void failed(Throwable x) {
            try {
               completed.run();
            } catch (Throwable var3) {
               x.addSuppressed(var3);
            }

            callback.failed(x);
         }
      };
   }

   static Callback from(final Callback callback, final Throwable cause) {
      return new Callback() {
         @Override
         public void succeeded() {
            callback.failed(cause);
         }

         @Override
         public void failed(Throwable x) {
            cause.addSuppressed(x);
            callback.failed(cause);
         }
      };
   }

   static Callback from(Callback callback1, Callback callback2) {
      return combine(callback1, callback2);
   }

   static Callback combine(final Callback cb1, final Callback cb2) {
      if (cb1 == null || cb1 == cb2) {
         return cb2;
      } else {
         return cb2 == null ? cb1 : new Callback() {
            @Override
            public void succeeded() {
               try {
                  cb1.succeeded();
               } finally {
                  cb2.succeeded();
               }
            }

            @Override
            public void failed(Throwable x) {
               try {
                  cb1.failed(x);
               } catch (Throwable var6) {
                  ExceptionUtil.addSuppressedIfNotAssociated(x, var6);
               } finally {
                  cb2.failed(x);
               }
            }

            @Override
            public Invocable.InvocationType getInvocationType() {
               return Invocable.combine(Invocable.getInvocationType(cb1), Invocable.getInvocationType(cb2));
            }
         };
      }
   }

   public static class Completable extends CompletableFuture<Void> implements Callback {
      private final Invocable.InvocationType invocation;

      public static Callback.Completable with(Consumer<Callback.Completable> consumer) {
         Callback.Completable completable = new Callback.Completable();
         consumer.accept(completable);
         return completable;
      }

      public static Callback.Completable from(final Callback callback) {
         return new Callback.Completable(callback.getInvocationType()) {
            @Override
            public void succeeded() {
               callback.succeeded();
               super.succeeded();
            }

            @Override
            public void failed(Throwable x) {
               callback.failed(x);
               super.failed(x);
            }
         };
      }

      public Completable() {
         this(Invocable.InvocationType.NON_BLOCKING);
      }

      public Completable(Invocable.InvocationType invocation) {
         this.invocation = invocation;
      }

      @Override
      public void succeeded() {
         this.complete(null);
      }

      @Override
      public void failed(Throwable x) {
         this.completeExceptionally(x);
      }

      @Override
      public Invocable.InvocationType getInvocationType() {
         return this.invocation;
      }

      public Callback.Completable compose(Consumer<Callback.Completable> consumer) {
         Callback.Completable completable = new Callback.Completable();
         this.whenComplete((r, x) -> {
            if (x == null) {
               consumer.accept(completable);
            } else {
               completable.failed(x);
            }
         });
         return completable;
      }
   }

   public interface Completing extends Callback {
      void completed();

      @Override
      default void succeeded() {
         this.completed();
      }

      @Override
      default void failed(Throwable x) {
         this.completed();
      }
   }

   public static class Nested implements Callback.Completing {
      private final Callback callback;

      public Nested(Callback callback) {
         this.callback = Objects.requireNonNull(callback);
      }

      public Callback getCallback() {
         return this.callback;
      }

      @Override
      public void completed() {
      }

      @Override
      public void succeeded() {
         try {
            this.callback.succeeded();
         } finally {
            this.completed();
         }
      }

      @Override
      public void failed(Throwable x) {
         try {
            this.callback.failed(x);
         } finally {
            this.completed();
         }
      }

      @Override
      public Invocable.InvocationType getInvocationType() {
         return this.callback.getInvocationType();
      }

      @Override
      public String toString() {
         return "%s@%x:%s".formatted(this.getClass().getSimpleName(), this.hashCode(), this.callback);
      }
   }
}
