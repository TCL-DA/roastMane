package org.glassfish.jersey.server.internal.routing;

import java.util.List;
import java.util.Set;
import org.glassfish.jersey.uri.PathPattern;

final class Route {
   private final PathPattern routingPattern;
   private final List<Router> routers;
   private Set<String> httpMethods;

   static Route of(PathPattern routingPattern, List<Router> routers) {
      return new Route(routingPattern, routers);
   }

   private Route(PathPattern routingPattern, List<Router> routers) {
      this.routingPattern = routingPattern;
      this.routers = routers;
   }

   public PathPattern routingPattern() {
      return this.routingPattern;
   }

   public List<Router> next() {
      return this.routers;
   }

   Set<String> getHttpMethods() {
      return this.httpMethods;
   }

   void setHttpMethods(Set<String> httpMethods) {
      this.httpMethods = httpMethods;
   }
}
