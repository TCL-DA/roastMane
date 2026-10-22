package org.glassfish.jersey.internal.util.collection;

public interface UnsafeValue<T, E extends Throwable> {
   T get() throws E;
}
