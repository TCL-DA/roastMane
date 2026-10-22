package org.glassfish.jersey.server.internal.routing;

import jakarta.ws.rs.container.ResourceInfo;
import java.util.regex.MatchResult;
import org.glassfish.jersey.server.ExtendedUriInfo;
import org.glassfish.jersey.server.internal.process.Endpoint;
import org.glassfish.jersey.server.model.Resource;
import org.glassfish.jersey.server.model.ResourceMethod;
import org.glassfish.jersey.server.model.RuntimeResource;
import org.glassfish.jersey.uri.UriTemplate;

public interface RoutingContext extends ResourceInfo, ExtendedUriInfo {
   void pushMatchResult(MatchResult var1);

   void pushMatchedResource(Object var1);

   Object peekMatchedResource();

   void pushTemplates(UriTemplate var1, UriTemplate var2);

   String getFinalMatchingGroup();

   void pushLeftHandPath();

   void setEndpoint(Endpoint var1);

   Endpoint getEndpoint();

   void setMatchedResourceMethod(ResourceMethod var1);

   void pushMatchedLocator(ResourceMethod var1);

   void pushMatchedRuntimeResource(RuntimeResource var1);

   void pushLocatorSubResource(Resource var1);

   void setMappedThrowable(Throwable var1);
}
