package org.glassfish.jersey.server.model;

import jakarta.ws.rs.core.MediaType;
import java.util.List;

public interface Consuming {
   List<MediaType> getConsumedTypes();
}
