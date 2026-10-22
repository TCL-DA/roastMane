package org.glassfish.jersey.message.filtering.spi;

import java.util.Map;
import java.util.Set;

public interface EntityGraph {
   EntityGraph addField(String var1);

   EntityGraph addField(String var1, String... var2);

   EntityGraph addField(String var1, Set<String> var2);

   EntityGraph addSubgraph(String var1, Class<?> var2);

   EntityGraph addSubgraph(String var1, Class<?> var2, String... var3);

   EntityGraph addSubgraph(String var1, Class<?> var2, Set<String> var3);

   EntityGraph addFilteringScopes(Set<String> var1);

   boolean presentInScope(String var1, String var2);

   boolean presentInScopes(String var1);

   Class<?> getEntityClass();

   Set<String> getFields(String var1);

   Set<String> getFields(String... var1);

   Set<String> getFields(Set<String> var1);

   Set<String> getFilteringScopes();

   Set<String> getClassFilteringScopes();

   Map<String, Class<?>> getSubgraphs(String var1);

   Map<String, Class<?>> getSubgraphs(String... var1);

   Map<String, Class<?>> getSubgraphs(Set<String> var1);

   EntityGraph remove(String var1);
}
