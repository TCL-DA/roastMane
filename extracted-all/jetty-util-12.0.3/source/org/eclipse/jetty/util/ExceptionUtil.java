package org.eclipse.jetty.util;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.eclipse.jetty.util.thread.Invocable;

public class ExceptionUtil {
   public static <T extends Throwable> T as(Class<T> type, Throwable throwable) throws IllegalArgumentException {
      if (throwable == null) {
         return null;
      } else if (type.isInstance(throwable)) {
         return (T)throwable;
      } else {
         try {
            Constructor<T> constructor = type.getConstructor(Throwable.class);
            return constructor.newInstance(throwable);
         } catch (Exception var4) {
            IllegalArgumentException iae = new IllegalArgumentException(var4);
            if (areNotAssociated(iae, throwable)) {
               iae.addSuppressed(throwable);
            }

            throw iae;
         } catch (Throwable var5) {
            if (areNotAssociated(var5, throwable)) {
               var5.addSuppressed(throwable);
            }

            throw var5;
         }
      }
   }

   public static void ifExceptionThrow(Throwable throwable) throws Error, Exception {
      if (throwable != null) {
         if (throwable instanceof Error error) {
            throw error;
         } else if (throwable instanceof Exception exception) {
            throw exception;
         } else {
            throw new RuntimeException(throwable);
         }
      }
   }

   public static void ifExceptionThrowUnchecked(Throwable throwable) throws Error, RuntimeException {
      if (throwable != null) {
         if (throwable instanceof RuntimeException runtimeException) {
            throw runtimeException;
         } else if (throwable instanceof Error error) {
            throw error;
         } else {
            throw new RuntimeException(throwable);
         }
      }
   }

   public static <T extends Throwable> void ifExceptionThrowAs(Class<T> type, Throwable throwable) throws Error, RuntimeException, T, IllegalArgumentException {
      if (throwable != null) {
         if (throwable instanceof Error error) {
            throw error;
         } else if (throwable instanceof RuntimeException runtimeException) {
            throw runtimeException;
         } else {
            throw as(type, throwable);
         }
      }
   }

   public static <T extends Throwable> void ifExceptionThrowAllAs(Class<T> type, Throwable throwable) throws T {
      if (throwable != null) {
         throw as(type, throwable);
      }
   }

   public static boolean areNotAssociated(Throwable t1, Throwable t2) {
      if (t1 != null && t2 != null) {
         while (t1 != null) {
            for (Throwable two = t2; two != null; two = two.getCause()) {
               if (t1 == two) {
                  return false;
               }

               if (t1.getCause() == two) {
                  return false;
               }

               if (Arrays.asList(t1.getSuppressed()).contains(two)) {
                  return false;
               }

               if (Arrays.asList(two.getSuppressed()).contains(t1)) {
                  return false;
               }
            }

            t1 = t1.getCause();
         }

         return true;
      } else {
         return false;
      }
   }

   public static void addSuppressedIfNotAssociated(Throwable throwable, Throwable suppressed) {
      if (areNotAssociated(throwable, suppressed)) {
         throwable.addSuppressed(suppressed);
      }
   }

   public static <T extends Throwable> T withSuppressed(T t, List<Throwable> errors) {
      if (errors != null) {
         errors.stream().filter(e -> areNotAssociated(t, e)).forEach(t::addSuppressed);
      }

      return t;
   }

   public static Throwable combine(Throwable t1, Throwable t2) {
      if (t1 == null) {
         return t2;
      } else {
         if (areNotAssociated(t1, t2)) {
            t1.addSuppressed(t2);
         }

         return t1;
      }
   }

   private ExceptionUtil() {
   }

   public static <T> T get(CompletableFuture<T> completableFuture) {
      try {
         return completableFuture.get();
      } catch (InterruptedException var2) {
         throw new RuntimeException(var2);
      } catch (ExecutionException var3) {
         throw new RuntimeException(var3.getCause());
      }
   }

   public static class MultiException {
      private Throwable _multiException;

      public void add(Throwable t) {
         this._multiException = ExceptionUtil.combine(this._multiException, t);
      }

      public void ifExceptionThrow() throws Exception {
         ExceptionUtil.ifExceptionThrow(this._multiException);
      }

      public void ifExceptionThrowRuntime() {
         ExceptionUtil.ifExceptionThrowUnchecked(this._multiException);
      }

      public <T extends Throwable> void ifExceptionThrowAs(Class<T> type) throws T {
         ExceptionUtil.ifExceptionThrowAs(type, this._multiException);
      }

      public void callAndCatch(Invocable.Callable task) {
         try {
            if (task != null) {
               task.call();
            }
         } catch (Throwable var3) {
            this.add(var3);
         }
      }
   }
}
