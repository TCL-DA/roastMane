package org.glassfish.jersey.internal.util.collection;

public interface LazyValue<T> extends Value<T> {
   boolean isInitialized();
}
