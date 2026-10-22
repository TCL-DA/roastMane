package org.glassfish.jersey.server.internal.routing;

import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.PathSegment;
import jakarta.ws.rs.core.UriBuilder;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.regex.MatchResult;
import java.util.stream.Collectors;
import org.glassfish.jersey.internal.util.collection.ImmutableMultivaluedMap;
import org.glassfish.jersey.message.internal.TracingLogger;
import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.server.internal.ServerTraceEvent;
import org.glassfish.jersey.server.internal.process.Endpoint;
import org.glassfish.jersey.server.model.Resource;
import org.glassfish.jersey.server.model.ResourceMethod;
import org.glassfish.jersey.server.model.ResourceMethodInvoker;
import org.glassfish.jersey.server.model.RuntimeResource;
import org.glassfish.jersey.uri.UriComponent;
import org.glassfish.jersey.uri.UriTemplate;
import org.glassfish.jersey.uri.UriComponent.Type;
import org.glassfish.jersey.uri.internal.JerseyUriBuilder;

public class UriRoutingContext implements RoutingContext {
   private final LinkedList<MatchResult> matchResults = new LinkedList<>();
   private final LinkedList<Object> matchedResources = new LinkedList<>();
   private final LinkedList<UriTemplate> templates = new LinkedList<>();
   private final MultivaluedHashMap<String, String> encodedTemplateValues = new MultivaluedHashMap();
   private final ImmutableMultivaluedMap<String, String> encodedTemplateValuesView = new ImmutableMultivaluedMap(this.encodedTemplateValues);
   private final LinkedList<String> paths = new LinkedList<>();
   private final LinkedList<RuntimeResource> matchedRuntimeResources = new LinkedList<>();
   private final LinkedList<ResourceMethod> matchedLocators = new LinkedList<>();
   private final LinkedList<Resource> locatorSubResources = new LinkedList<>();
   private final TracingLogger tracingLogger;
   private volatile ResourceMethod matchedResourceMethod = null;
   private volatile Throwable mappedThrowable = null;
   private Endpoint endpoint;
   private MultivaluedHashMap<String, String> decodedTemplateValues;
   private ImmutableMultivaluedMap<String, String> decodedTemplateValuesView;
   private ImmutableMultivaluedMap<String, String> encodedQueryParamsView;
   private ImmutableMultivaluedMap<String, String> decodedQueryParamsView;
   private final ContainerRequest requestContext;
   private static final Function<String, String> PATH_DECODER = input -> UriComponent.decode(input, Type.PATH);

   public UriRoutingContext(ContainerRequest requestContext) {
      this.requestContext = requestContext;
      this.tracingLogger = TracingLogger.getInstance(requestContext);
   }

   @Override
   public void pushMatchResult(MatchResult matchResult) {
      this.matchResults.push(matchResult);
   }

   @Override
   public void pushMatchedResource(Object resource) {
      this.tracingLogger.log(ServerTraceEvent.MATCH_RESOURCE, new Object[]{resource});
      this.matchedResources.push(resource);
   }

   @Override
   public Object peekMatchedResource() {
      return this.matchedResources.peek();
   }

   @Override
   public void pushMatchedLocator(ResourceMethod resourceLocator) {
      this.tracingLogger.log(ServerTraceEvent.MATCH_LOCATOR, new Object[]{resourceLocator.getInvocable().getHandlingMethod()});
      this.matchedLocators.push(resourceLocator);
   }

   @Override
   public void pushLeftHandPath() {
      String rightHandPath = this.getFinalMatchingGroup();
      int rhpLength = rightHandPath != null ? rightHandPath.length() : 0;
      String encodedRequestPath = this.getPath(false);
      int length = encodedRequestPath.length() - rhpLength;
      if (length <= 0) {
         this.paths.addFirst("");
      } else {
         this.paths.addFirst(encodedRequestPath.substring(0, length));
      }
   }

   @Override
   public void pushTemplates(UriTemplate resourceTemplate, UriTemplate methodTemplate) {
      Iterator<MatchResult> matchResultIterator = this.matchResults.iterator();
      this.templates.push(resourceTemplate);
      if (methodTemplate != null) {
         this.templates.push(methodTemplate);
         matchResultIterator.next();
      }

      this.pushMatchedTemplateValues(resourceTemplate, matchResultIterator.next());
      if (methodTemplate != null) {
         this.pushMatchedTemplateValues(methodTemplate, this.matchResults.peek());
      }
   }

   private void pushMatchedTemplateValues(UriTemplate template, MatchResult matchResult) {
      int i = 1;

      for (String templateVariable : template.getTemplateVariables()) {
         String value = matchResult.group(i++);
         this.encodedTemplateValues.addFirst(templateVariable, value);
         if (this.decodedTemplateValues != null) {
            this.decodedTemplateValues.addFirst(UriComponent.decode(templateVariable, Type.PATH_SEGMENT), UriComponent.decode(value, Type.PATH));
         }
      }
   }

   @Override
   public String getFinalMatchingGroup() {
      MatchResult mr = this.matchResults.peek();
      if (mr == null) {
         return null;
      } else {
         String finalGroup = mr.group(mr.groupCount());
         return finalGroup == null ? "" : finalGroup;
      }
   }

   public LinkedList<MatchResult> getMatchedResults() {
      return this.matchResults;
   }

   @Override
   public void setEndpoint(Endpoint endpoint) {
      this.endpoint = endpoint;
   }

   @Override
   public Endpoint getEndpoint() {
      return this.endpoint;
   }

   @Override
   public void setMatchedResourceMethod(ResourceMethod resourceMethod) {
      this.tracingLogger.log(ServerTraceEvent.MATCH_RESOURCE_METHOD, new Object[]{resourceMethod.getInvocable().getHandlingMethod()});
      this.matchedResourceMethod = resourceMethod;
   }

   @Override
   public void pushMatchedRuntimeResource(RuntimeResource runtimeResource) {
      if (this.tracingLogger.isLogEnabled(ServerTraceEvent.MATCH_RUNTIME_RESOURCE)) {
         this.tracingLogger
            .log(
               ServerTraceEvent.MATCH_RUNTIME_RESOURCE,
               new Object[]{
                  runtimeResource.getResources().get(0).getPath(),
                  runtimeResource.getResources().get(0).getPathPattern().getRegex(),
                  this.matchResults.peek().group().substring(0, this.matchResults.peek().group().length() - this.getFinalMatchingGroup().length()),
                  this.matchResults.peek().group()
               }
            );
      }

      this.matchedRuntimeResources.push(runtimeResource);
   }

   @Override
   public void pushLocatorSubResource(Resource subResourceFromLocator) {
      this.locatorSubResources.push(subResourceFromLocator);
   }

   public URI getAbsolutePath() {
      return this.requestContext.getAbsolutePath();
   }

   public UriBuilder getAbsolutePathBuilder() {
      return new JerseyUriBuilder().uri(this.getAbsolutePath());
   }

   public URI getBaseUri() {
      return this.requestContext.getBaseUri();
   }

   public UriBuilder getBaseUriBuilder() {
      return new JerseyUriBuilder().uri(this.getBaseUri());
   }

   public List<Object> getMatchedResources() {
      return Collections.unmodifiableList(this.matchedResources);
   }

   public List<String> getMatchedURIs() {
      return this.getMatchedURIs(true);
   }

   public List<String> getMatchedURIs(boolean decode) {
      List<String> result;
      if (decode) {
         result = this.paths.stream().map(PATH_DECODER).collect(Collectors.toList());
      } else {
         result = this.paths;
      }

      return Collections.unmodifiableList(result);
   }

   public String getPath() {
      return this.requestContext.getPath(true);
   }

   public String getPath(boolean decode) {
      return this.requestContext.getPath(decode);
   }

   public MultivaluedMap<String, String> getPathParameters() {
      return this.getPathParameters(true);
   }

   public MultivaluedMap<String, String> getPathParameters(boolean decode) {
      if (decode) {
         if (this.decodedTemplateValuesView != null) {
            return this.decodedTemplateValuesView;
         } else {
            if (this.decodedTemplateValues == null) {
               this.decodedTemplateValues = new MultivaluedHashMap();

               for (Entry<String, List<String>> e : this.encodedTemplateValues.entrySet()) {
                  this.decodedTemplateValues
                     .put(
                        UriComponent.decode(e.getKey(), Type.PATH_SEGMENT),
                        e.getValue().stream().map(s -> UriComponent.decode(s, Type.PATH)).collect(Collectors.toCollection(ArrayList::new))
                     );
               }
            }

            this.decodedTemplateValuesView = new ImmutableMultivaluedMap(this.decodedTemplateValues);
            return this.decodedTemplateValuesView;
         }
      } else {
         return this.encodedTemplateValuesView;
      }
   }

   public List<PathSegment> getPathSegments() {
      return this.getPathSegments(true);
   }

   public List<PathSegment> getPathSegments(boolean decode) {
      String requestPath = this.requestContext.getPath(false);
      return Collections.unmodifiableList(UriComponent.decodePath(requestPath, decode));
   }

   public MultivaluedMap<String, String> getQueryParameters() {
      return this.getQueryParameters(true);
   }

   public MultivaluedMap<String, String> getQueryParameters(boolean decode) {
      if (decode) {
         if (this.decodedQueryParamsView != null) {
            return this.decodedQueryParamsView;
         } else {
            this.decodedQueryParamsView = new ImmutableMultivaluedMap(UriComponent.decodeQuery(this.getRequestUri(), true));
            return this.decodedQueryParamsView;
         }
      } else if (this.encodedQueryParamsView != null) {
         return this.encodedQueryParamsView;
      } else {
         this.encodedQueryParamsView = new ImmutableMultivaluedMap(UriComponent.decodeQuery(this.getRequestUri(), false));
         return this.encodedQueryParamsView;
      }
   }

   public void invalidateUriComponentViews() {
      this.decodedQueryParamsView = null;
      this.encodedQueryParamsView = null;
   }

   public URI getRequestUri() {
      return this.requestContext.getRequestUri();
   }

   public UriBuilder getRequestUriBuilder() {
      return UriBuilder.fromUri(this.getRequestUri());
   }

   @Override
   public Throwable getMappedThrowable() {
      return this.mappedThrowable;
   }

   @Override
   public void setMappedThrowable(Throwable mappedThrowable) {
      this.mappedThrowable = mappedThrowable;
   }

   @Override
   public List<UriTemplate> getMatchedTemplates() {
      return Collections.unmodifiableList(this.templates);
   }

   @Override
   public List<PathSegment> getPathSegments(String name) {
      return this.getPathSegments(name, true);
   }

   @Override
   public List<PathSegment> getPathSegments(String name, boolean decode) {
      int[] bounds = this.getPathParameterBounds(name);
      if (bounds != null) {
         String path = this.matchResults.getLast().group();
         int segmentsStart = 0;

         for (int x = 0; x < bounds[0]; x++) {
            if (path.charAt(x) == '/') {
               segmentsStart++;
            }
         }

         int segmentsEnd = segmentsStart;

         for (int xx = bounds[0]; xx < bounds[1]; xx++) {
            if (path.charAt(xx) == '/') {
               segmentsEnd++;
            }
         }

         return this.getPathSegments(decode).subList(segmentsStart - 1, segmentsEnd);
      } else {
         return Collections.emptyList();
      }
   }

   private int[] getPathParameterBounds(String name) {
      Iterator<UriTemplate> templatesIterator = this.templates.iterator();
      Iterator<MatchResult> matchResultsIterator = this.matchResults.iterator();

      while (templatesIterator.hasNext()) {
         MatchResult mr = matchResultsIterator.next();
         int pIndex = this.getLastPathParameterIndex(name, templatesIterator.next());
         if (pIndex != -1) {
            int pathLength = mr.group().length();
            int segmentIndex = mr.end(pIndex + 1);
            int groupLength = segmentIndex - mr.start(pIndex + 1);

            while (matchResultsIterator.hasNext()) {
               mr = matchResultsIterator.next();
               segmentIndex += mr.group().length() - pathLength;
               pathLength = mr.group().length();
            }

            return new int[]{segmentIndex - groupLength, segmentIndex};
         }
      }

      return null;
   }

   private int getLastPathParameterIndex(String name, UriTemplate t) {
      int i = 0;
      int pIndex = -1;

      for (String parameterName : t.getTemplateVariables()) {
         if (parameterName.equals(name)) {
            pIndex = i;
         }

         i++;
      }

      return pIndex;
   }

   public Method getResourceMethod() {
      return this.endpoint instanceof ResourceMethodInvoker ? ((ResourceMethodInvoker)this.endpoint).getResourceMethod() : null;
   }

   public Class<?> getResourceClass() {
      return this.endpoint instanceof ResourceMethodInvoker ? ((ResourceMethodInvoker)this.endpoint).getResourceClass() : null;
   }

   @Override
   public List<RuntimeResource> getMatchedRuntimeResources() {
      return this.matchedRuntimeResources;
   }

   @Override
   public ResourceMethod getMatchedResourceMethod() {
      return this.matchedResourceMethod;
   }

   @Override
   public List<ResourceMethod> getMatchedResourceLocators() {
      return this.matchedLocators;
   }

   @Override
   public List<Resource> getLocatorSubResources() {
      return this.locatorSubResources;
   }

   @Override
   public Resource getMatchedModelResource() {
      return this.matchedResourceMethod == null ? null : this.matchedResourceMethod.getParent();
   }

   public URI resolve(URI uri) {
      return UriTemplate.resolve(this.getBaseUri(), uri);
   }

   public URI relativize(URI uri) {
      if (!uri.isAbsolute()) {
         uri = this.resolve(uri);
      }

      return UriTemplate.relativize(this.getRequestUri(), uri);
   }
}
