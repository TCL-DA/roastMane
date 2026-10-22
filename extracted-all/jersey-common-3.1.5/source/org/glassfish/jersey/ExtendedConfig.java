package org.glassfish.jersey;

import jakarta.ws.rs.core.Configuration;

public interface ExtendedConfig extends Configuration {
   boolean isProperty(String var1);
}
