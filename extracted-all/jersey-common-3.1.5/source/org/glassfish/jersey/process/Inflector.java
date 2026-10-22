package org.glassfish.jersey.process;

public interface Inflector<DATA, RESULT> {
   RESULT apply(DATA var1);
}
