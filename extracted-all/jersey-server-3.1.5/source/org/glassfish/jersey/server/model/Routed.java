package org.glassfish.jersey.server.model;

import org.glassfish.jersey.uri.PathPattern;

public interface Routed {
   String getPath();

   PathPattern getPathPattern();
}
