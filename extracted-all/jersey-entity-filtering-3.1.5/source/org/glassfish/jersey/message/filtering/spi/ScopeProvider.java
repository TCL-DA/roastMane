package org.glassfish.jersey.message.filtering.spi;

import java.lang.annotation.Annotation;
import java.util.Set;
import org.glassfish.jersey.message.filtering.EntityFiltering;
import org.glassfish.jersey.spi.Contract;

@Contract
public interface ScopeProvider {
   String DEFAULT_SCOPE = EntityFiltering.class.getName();

   Set<String> getFilteringScopes(Annotation[] var1, boolean var2);
}
