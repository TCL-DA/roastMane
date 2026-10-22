package org.glassfish.jersey.spi;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.ext.ContextResolver;
import java.lang.reflect.Type;

public interface ContextResolvers {
   <T> ContextResolver<T> resolve(Type var1, MediaType var2);
}
