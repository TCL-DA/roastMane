package org.glassfish.jersey.message.filtering.spi;

import java.util.Set;
import org.glassfish.jersey.spi.Contract;

@Contract
public interface EntityGraphProvider {
   EntityGraph getOrCreateEntityGraph(Class<?> var1, boolean var2);

   EntityGraph getOrCreateEmptyEntityGraph(Class<?> var1, boolean var2);

   boolean containsEntityGraph(Class<?> var1, boolean var2);

   ObjectGraph createObjectGraph(Class<?> var1, Set<String> var2, boolean var3);

   EntityGraph putIfAbsent(Class<?> var1, EntityGraph var2, boolean var3);
}
