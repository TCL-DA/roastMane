package org.apache.logging.log4j.util;

@FunctionalInterface
@InternalApi
public interface Supplier<T> extends java.util.function.Supplier<T> {
   @Override
   T get();
}
