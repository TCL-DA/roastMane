package org.glassfish.jersey.model.internal.spi;

import java.util.Map;
import org.glassfish.jersey.model.Parameter;

public interface ParameterServiceProvider {
   Map<Class, Parameter.ParamAnnotationHelper> getParameterAnnotationHelperMap();

   Parameter.ParamCreationFactory<? extends Parameter> getParameterCreationFactory();
}
