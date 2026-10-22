package org.glassfish.jersey.model;

import java.lang.annotation.Annotation;

public interface Scoped {
   Class<? extends Annotation> getScope();
}
