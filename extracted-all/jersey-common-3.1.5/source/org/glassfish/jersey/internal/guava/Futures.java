package org.glassfish.jersey.internal.guava;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Futures {
   private Futures() {
   }

   public static <V> ListenableFuture<V> immediateFuture(V value) {
      return new Futures.ImmediateSuccessfulFuture<>(value);
   }

   public static <V> ListenableFuture<V> immediateFailedFuture(Throwable throwable) {
      Preconditions.checkNotNull(throwable);
      return new Futures.ImmediateFailedFuture<>(throwable);
   }

   public static <I, O> ListenableFuture<O> transform(ListenableFuture<I> input, Function<? super I, ? extends O> function) {
      Preconditions.checkNotNull(function);
      Futures.ChainingListenableFuture<I, O> output = new Futures.ChainingListenableFuture<>(asAsyncFunction(function), input);
      input.addListener(output, MoreExecutors.directExecutor());
      return output;
   }

   private static <I, O> AsyncFunction<I, O> asAsyncFunction(final Function<? super I, ? extends O> function) {
      return new AsyncFunction<I, O>() {
         @Override
         public ListenableFuture<O> apply(I input) {
            O output = (O)function.apply(input);
            return Futures.immediateFuture(output);
         }
      };
   }

   private static class ChainingListenableFuture<I, O> extends AbstractFuture<O> implements Runnable {
      private AsyncFunction<? super I, ? extends O> function;
      private ListenableFuture<? extends I> inputFuture;
      private volatile ListenableFuture<? extends O> outputFuture;

      private ChainingListenableFuture(AsyncFunction<? super I, ? extends O> function, ListenableFuture<? extends I> inputFuture) {
         this.function = Preconditions.checkNotNull(function);
         this.inputFuture = Preconditions.checkNotNull(inputFuture);
      }

      @Override
      public boolean cancel(boolean mayInterruptIfRunning) {
         if (super.cancel(mayInterruptIfRunning)) {
            this.cancel(this.inputFuture, mayInterruptIfRunning);
            this.cancel(this.outputFuture, mayInterruptIfRunning);
            return true;
         } else {
            return false;
         }
      }

      private void cancel(Future<?> future, boolean mayInterruptIfRunning) {
         if (future != null) {
            future.cancel(mayInterruptIfRunning);
         }
      }

      @Override
      public void run() {
         try {
            try {
               I sourceResult;
               try {
                  sourceResult = Uninterruptibles.getUninterruptibly((Future<I>)this.inputFuture);
               } catch (CancellationException var9) {
                  this.cancel(false);
                  return;
               } catch (ExecutionException var10) {
                  this.setException(var10.getCause());
                  return;
               }

               final ListenableFuture<? extends O> outputFuture = this.outputFuture = Preconditions.checkNotNull(
                  this.function.apply(sourceResult), "AsyncFunction may not return null."
               );
               if (this.isCancelled()) {
                  outputFuture.cancel(this.wasInterrupted());
                  this.outputFuture = null;
                  return;
               }

               outputFuture.addListener(new Runnable() {
                  @Override
                  public void run() {
                     try {
                        ChainingListenableFuture.this.set(Uninterruptibles.getUninterruptibly((Future<O>)outputFuture));
                     } catch (CancellationException var6) {
                        ChainingListenableFuture.this.cancel(false);
                     } catch (ExecutionException var7) {
                        ChainingListenableFuture.this.setException(var7.getCause());
                     } finally {
                        ChainingListenableFuture.this.outputFuture = null;
                     }
                  }
               }, MoreExecutors.directExecutor());
            } catch (UndeclaredThrowableException var11) {
               this.setException(var11.getCause());
            } catch (Throwable var12) {
               this.setException(var12);
            }
         } finally {
            this.function = null;
            this.inputFuture = null;
         }
      }
   }

   private static class ImmediateFailedFuture<V> extends Futures.ImmediateFuture<V> {
      private final Throwable thrown;

      ImmediateFailedFuture(Throwable thrown) {
         this.thrown = thrown;
      }

      @Override
      public V get() throws ExecutionException {
         throw new ExecutionException(this.thrown);
      }
   }

   private abstract static class ImmediateFuture<V> implements ListenableFuture<V> {
      private static final Logger log = Logger.getLogger(Futures.ImmediateFuture.class.getName());

      private ImmediateFuture() {
      }

      @Override
      public void addListener(Runnable listener, Executor executor) {
         Preconditions.checkNotNull(listener, "Runnable was null.");
         Preconditions.checkNotNull(executor, "Executor was null.");

         try {
            executor.execute(listener);
         } catch (RuntimeException var4) {
            log.log(Level.SEVERE, "RuntimeException while executing runnable " + listener + " with executor " + executor, (Throwable)var4);
         }
      }

      @Override
      public boolean cancel(boolean mayInterruptIfRunning) {
         return false;
      }

      @Override
      public abstract V get() throws ExecutionException;

      @Override
      public V get(long timeout, TimeUnit unit) throws ExecutionException {
         Preconditions.checkNotNull(unit);
         return this.get();
      }

      @Override
      public boolean isCancelled() {
         return false;
      }

      @Override
      public boolean isDone() {
         return true;
      }
   }

   private static class ImmediateSuccessfulFuture<V> extends Futures.ImmediateFuture<V> {
      private final V value;

      ImmediateSuccessfulFuture(V value) {
         this.value = value;
      }

      @Override
      public V get() {
         return this.value;
      }
   }
}
