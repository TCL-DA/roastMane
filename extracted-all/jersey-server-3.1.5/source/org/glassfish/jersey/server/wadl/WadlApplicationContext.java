package org.glassfish.jersey.server.wadl;

import com.sun.research.ws.wadl.Application;
import jakarta.ws.rs.core.UriInfo;
import jakarta.xml.bind.JAXBContext;
import org.glassfish.jersey.server.model.Resource;
import org.glassfish.jersey.server.wadl.internal.ApplicationDescription;

public interface WadlApplicationContext {
   ApplicationDescription getApplication(UriInfo var1, boolean var2);

   Application getApplication(UriInfo var1, Resource var2, boolean var3);

   JAXBContext getJAXBContext();

   void setWadlGenerationEnabled(boolean var1);

   boolean isWadlGenerationEnabled();
}
