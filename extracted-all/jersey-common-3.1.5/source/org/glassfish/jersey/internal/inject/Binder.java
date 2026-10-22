package org.glassfish.jersey.internal.inject;

import java.util.Collection;

public interface Binder {
   Collection<Binding> getBindings();
}
