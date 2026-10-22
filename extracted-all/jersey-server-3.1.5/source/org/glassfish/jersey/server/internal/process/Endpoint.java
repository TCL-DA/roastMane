package org.glassfish.jersey.server.internal.process;

import org.glassfish.jersey.process.Inflector;
import org.glassfish.jersey.server.ContainerResponse;

public interface Endpoint extends Inflector<RequestProcessingContext, ContainerResponse> {
}
