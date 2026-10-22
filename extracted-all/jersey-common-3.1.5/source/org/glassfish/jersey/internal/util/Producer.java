package org.glassfish.jersey.internal.util;

import java.util.concurrent.Callable;

public interface Producer<T> extends Callable<T> {
   @Override
   T call();
}
