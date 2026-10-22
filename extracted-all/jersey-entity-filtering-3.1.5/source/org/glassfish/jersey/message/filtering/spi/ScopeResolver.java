package org.glassfish.jersey.message.filtering.spi;

import java.lang.annotation.Annotation;
import java.util.Set;
import org.glassfish.jersey.spi.Contract;

@Contract
public interface ScopeResolver {
   Set<String> resolve(Annotation[] var1);
}
