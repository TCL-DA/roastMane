package org.glassfish.jersey.model;

import java.lang.annotation.Annotation;
import java.util.Collection;

public interface NameBound {
   boolean isNameBound();

   Collection<Class<? extends Annotation>> getNameBindings();
}
