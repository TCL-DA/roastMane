package org.glassfish.jersey.client.inject;

import org.glassfish.jersey.model.Parameter;

public interface ParameterUpdaterProvider {
   ParameterUpdater<?, ?> get(Parameter var1);
}
