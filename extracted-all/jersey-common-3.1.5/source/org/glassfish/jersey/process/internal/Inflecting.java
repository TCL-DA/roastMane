package org.glassfish.jersey.process.internal;

import org.glassfish.jersey.process.Inflector;

public interface Inflecting<DATA, RESULT> {
   Inflector<DATA, RESULT> inflector();
}
