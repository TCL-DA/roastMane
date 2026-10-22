package org.eclipse.jetty.server.handler;

import java.util.function.Predicate;
import org.eclipse.jetty.http.pathmap.PathSpec;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.InetAddressPattern;

public class InetAccessHandler extends ConditionalHandler.Abstract {
   public InetAccessHandler() {
      this(null);
   }

   public InetAccessHandler(Handler handler) {
      super(handler);
   }

   @Override
   protected boolean onConditionsMet(Request request, Response response, Callback callback) throws Exception {
      return this.nextHandler(request, response, callback);
   }

   @Override
   protected boolean onConditionsNotMet(Request request, Response response, Callback callback) throws Exception {
      Response.writeError(request, response, callback, 403);
      return true;
   }

   public void include(String pattern) {
      this.includeExclude(true, pattern);
   }

   public void include(String... patterns) {
      for (String pattern : patterns) {
         this.include(pattern);
      }
   }

   public void include(String connectorName, String addressPattern, PathSpec pathSpec) {
      this.include(new Predicate[]{from(connectorName, InetAddressPattern.from(addressPattern), null, pathSpec)});
   }

   public void exclude(String pattern) {
      this.includeExclude(false, pattern);
   }

   public void exclude(String... patterns) {
      for (String pattern : patterns) {
         this.exclude(pattern);
      }
   }

   public void exclude(String connectorName, String addressPattern, PathSpec pathSpec) {
      this.exclude(new Predicate[]{from(connectorName, InetAddressPattern.from(addressPattern), null, pathSpec)});
   }

   private void includeExclude(boolean include, String pattern) {
      String path = null;
      int pathIndex = pattern.indexOf(124);
      if (pathIndex >= 0) {
         path = pattern.substring(pathIndex + 1);
         pattern = pattern.substring(0, pathIndex);
      }

      String method = null;
      int methodIndex = pattern.indexOf(62);
      if (methodIndex >= 0) {
         method = pattern.substring(methodIndex + 1);
         pattern = pattern.substring(0, methodIndex);
      }

      String connector = null;
      int connectorIndex = pattern.indexOf(64);
      if (connectorIndex >= 0) {
         connector = pattern.substring(0, connectorIndex);
      }

      String addr = null;
      int addrStart = connectorIndex < 0 ? 0 : connectorIndex + 1;
      int addrEnd = pathIndex < 0 ? pattern.length() : pathIndex;
      if (addrStart != addrEnd) {
         addr = pattern.substring(addrStart, addrEnd);
      }

      if (include) {
         this.include(new Predicate[]{from(connector, addr, method, path)});
      } else {
         this.exclude(new Predicate[]{from(connector, addr, method, path)});
      }
   }
}
