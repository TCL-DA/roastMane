package org.glassfish.jersey.server.internal.routing;

import java.util.Arrays;
import java.util.List;
import org.glassfish.jersey.server.model.ResourceMethod;

final class MethodRouting {
   final ResourceMethod method;
   final List<Router> routers;

   MethodRouting(ResourceMethod method, Router... routers) {
      this.method = method;
      this.routers = Arrays.asList(routers);
   }

   @Override
   public String toString() {
      return "{" + this.method + " -> " + this.routers + '}';
   }
}
