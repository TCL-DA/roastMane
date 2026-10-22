package org.glassfish.jersey.server.internal.inject;

import jakarta.ws.rs.core.MultivaluedMap;

public interface MultivaluedParameterExtractor<T> {
   String getName();

   String getDefaultValueString();

   T extract(MultivaluedMap<String, String> var1);
}
