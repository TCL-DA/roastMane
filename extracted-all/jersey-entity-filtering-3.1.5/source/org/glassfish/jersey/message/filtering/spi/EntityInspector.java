package org.glassfish.jersey.message.filtering.spi;

import org.glassfish.jersey.spi.Contract;

@Contract
public interface EntityInspector {
   void inspect(Class<?> var1, boolean var2);
}
