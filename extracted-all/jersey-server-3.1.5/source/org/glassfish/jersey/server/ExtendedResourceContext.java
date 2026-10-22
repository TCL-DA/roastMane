package org.glassfish.jersey.server;

import jakarta.ws.rs.container.ResourceContext;
import org.glassfish.jersey.server.model.ResourceModel;

public interface ExtendedResourceContext extends ResourceContext {
   ResourceModel getResourceModel();
}
