package org.glassfish.jersey.server;

import jakarta.ws.rs.core.PathSegment;
import jakarta.ws.rs.core.UriInfo;
import java.util.List;
import java.util.regex.MatchResult;
import org.glassfish.jersey.server.model.Resource;
import org.glassfish.jersey.server.model.ResourceMethod;
import org.glassfish.jersey.server.model.RuntimeResource;
import org.glassfish.jersey.uri.UriTemplate;

public interface ExtendedUriInfo extends UriInfo {
   Throwable getMappedThrowable();

   List<MatchResult> getMatchedResults();

   List<UriTemplate> getMatchedTemplates();

   List<PathSegment> getPathSegments(String var1);

   List<PathSegment> getPathSegments(String var1, boolean var2);

   List<RuntimeResource> getMatchedRuntimeResources();

   ResourceMethod getMatchedResourceMethod();

   Resource getMatchedModelResource();

   List<ResourceMethod> getMatchedResourceLocators();

   List<Resource> getLocatorSubResources();
}
