package org.glassfish.jersey.server.internal.inject;

import org.glassfish.jersey.model.Parameter;

public interface MultivaluedParameterExtractorProvider {
   MultivaluedParameterExtractor<?> get(Parameter var1);
}
