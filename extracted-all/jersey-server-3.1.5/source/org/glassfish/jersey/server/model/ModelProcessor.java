package org.glassfish.jersey.server.model;

import jakarta.ws.rs.ConstrainedTo;
import jakarta.ws.rs.RuntimeType;
import jakarta.ws.rs.core.Configuration;
import org.glassfish.jersey.spi.Contract;

@Contract
@ConstrainedTo(RuntimeType.SERVER)
public interface ModelProcessor {
   ResourceModel processResourceModel(ResourceModel var1, Configuration var2);

   ResourceModel processSubResource(ResourceModel var1, Configuration var2);
}
