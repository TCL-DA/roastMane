package org.glassfish.hk2.api;

import org.jvnet.hk2.annotations.Contract;

@Contract
public interface Factory<T> {
   T provide();

   void dispose(T var1);
}
