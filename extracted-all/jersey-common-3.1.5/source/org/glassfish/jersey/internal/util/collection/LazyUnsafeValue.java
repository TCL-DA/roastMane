package org.glassfish.jersey.internal.util.collection;

public interface LazyUnsafeValue<T, E extends Throwable> extends UnsafeValue<T, E> {
   boolean isInitialized();
}
