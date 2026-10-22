package org.apache.logging.log4j.util;

import java.lang.ref.WeakReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

final class LazyUtil {
   private static final Object NULL = new Object() {
      @Override
      public String toString() {
         return "null";
      }
   };

   static Object wrapNull(final Object value) {
      return value == null ? NULL : value;
   }

   static <T> T unwrapNull(final Object value) {
      return value == NULL ? null : Cast.cast(value);
   }

   static class Constant<T> implements Lazy<T> {
      private final T value;

      Constant(final T value) {
         this.value = value;
      }

      @Override
      public T value() {
         return this.value;
      }

      @Override
      public boolean isInitialized() {
         return true;
      }

      @Override
      public void set(final T newValue) {
         throw new UnsupportedOperationException();
      }

      @Override
      public String toString() {
         return String.valueOf(this.value);
      }
   }

   static class PureLazy<T> implements Lazy<T> {
      private final java.util.function.Supplier<T> supplier;
      private Object value;

      public PureLazy(final java.util.function.Supplier<T> supplier) {
         this.supplier = supplier;
      }

      @Override
      public T value() {
         Object value = this.value;
         if (value == null) {
            value = this.supplier.get();
            this.value = LazyUtil.wrapNull(value);
         }

         return LazyUtil.unwrapNull(value);
      }

      @Override
      public boolean isInitialized() {
         return this.value != null;
      }

      @Override
      public void set(final T newValue) {
         this.value = newValue;
      }
   }

   static class SafeLazy<T> implements Lazy<T> {
      private final Lock lock = new ReentrantLock();
      private final java.util.function.Supplier<T> supplier;
      private volatile Object value;

      SafeLazy(final java.util.function.Supplier<T> supplier) {
         this.supplier = supplier;
      }

      @Override
      public T value() {
         Object value = this.value;
         if (value == null) {
            this.lock.lock();

            try {
               value = this.value;
               if (value == null) {
                  value = this.supplier.get();
                  this.value = LazyUtil.wrapNull(value);
               }
            } finally {
               this.lock.unlock();
            }
         }

         return LazyUtil.unwrapNull(value);
      }

      @Override
      public void set(final T newValue) {
         this.value = newValue;
      }

      public void reset() {
         this.value = null;
      }

      @Override
      public boolean isInitialized() {
         return this.value != null;
      }

      @Override
      public String toString() {
         return this.isInitialized() ? String.valueOf(this.value) : "Lazy value not initialized";
      }
   }

   static class WeakConstant<T> implements Lazy<T> {
      private final WeakReference<T> reference;

      WeakConstant(final T value) {
         this.reference = new WeakReference<>(value);
      }

      @Override
      public T value() {
         return this.reference.get();
      }

      @Override
      public boolean isInitialized() {
         return true;
      }

      @Override
      public void set(final T newValue) {
         throw new UnsupportedOperationException();
      }

      @Override
      public String toString() {
         return String.valueOf(this.value());
      }
   }
}
