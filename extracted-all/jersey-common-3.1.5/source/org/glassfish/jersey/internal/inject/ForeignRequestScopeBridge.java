package org.glassfish.jersey.internal.inject;

import java.util.Set;

public interface ForeignRequestScopeBridge {
   Set<Class<?>> getRequestScopedComponents();
}
