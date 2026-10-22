package org.glassfish.jersey.message.filtering.spi;

import java.util.Map;
import java.util.Set;

public interface ObjectGraph {
   Class<?> getEntityClass();

   Set<String> getFields();

   Set<String> getFields(String var1);

   Map<String, ObjectGraph> getSubgraphs();

   Map<String, ObjectGraph> getSubgraphs(String var1);
}
