package org.apache.logging.log4j.util;

import java.util.function.Function;

public interface Lazy<T> extends java.util.function.Supplier<T> {
   T value();

   @Override
   default T get() {
      return this.value();
   }

   default <R> Lazy<R> map(final Function<? super T, ? extends R> function) {
      return lazy(() -> (T)function.apply(this.value()));
   }

   boolean isInitialized();

   void set(final T newValue);

   static <T> Lazy<T> lazy(final java.util.function.Supplier<T> supplier) {
      return new LazyUtil.SafeLazy<>(supplier);
   }

   static <T> Lazy<T> value(final T value) {
      return new LazyUtil.Constant<>(value);
   }

   static <T> Lazy<T> weak(final T value) {
      return new LazyUtil.WeakConstant<>(value);
   }

   static <T> Lazy<T> pure(final java.util.function.Supplier<T> supplier) {
      return new LazyUtil.PureLazy<>(supplier);
   }
}
