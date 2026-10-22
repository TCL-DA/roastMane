package org.glassfish.jersey.server;

import java.util.Set;
import org.glassfish.jersey.ExtendedConfig;
import org.glassfish.jersey.server.model.Resource;

public interface ServerConfig extends ExtendedConfig {
   Set<Resource> getResources();
}
