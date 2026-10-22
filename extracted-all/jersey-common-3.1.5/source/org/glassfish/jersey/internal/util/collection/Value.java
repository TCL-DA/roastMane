package org.glassfish.jersey.internal.util.collection;

import java.util.function.Supplier;

public interface Value<T> extends Supplier<T> {
   @Override
   T get();
}
