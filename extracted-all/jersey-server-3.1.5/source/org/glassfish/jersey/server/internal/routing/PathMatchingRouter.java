package org.glassfish.jersey.server.internal.routing;

import java.util.List;
import java.util.regex.MatchResult;
import org.glassfish.jersey.message.internal.TracingLogger;
import org.glassfish.jersey.server.internal.ServerTraceEvent;
import org.glassfish.jersey.server.internal.process.RequestProcessingContext;
import org.glassfish.jersey.uri.PathPattern;

final class PathMatchingRouter implements Router {
   private final List<Route> acceptedRoutes;

   PathMatchingRouter(List<Route> routes) {
      this.acceptedRoutes = routes;
   }

   // $VF: Could not properly define all variable types!
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public Router.Continuation apply(RequestProcessingContext context) {
      RoutingContext rc = context.routingContext();
      String path = rc.getFinalMatchingGroup();
      TracingLogger tracingLogger = TracingLogger.getInstance(context.request());
      tracingLogger.log(ServerTraceEvent.MATCH_PATH_FIND, new Object[]{path});
      Router.Continuation result = null;
      MatchResult matchResultCandidate = null;
      Route acceptedRouteCandidate = null;

      for (Route acceptedRoute : this.acceptedRoutes) {
         PathPattern routePattern = acceptedRoute.routingPattern();
         MatchResult matchResult = routePattern.match(path);
         if (matchResult != null) {
            if (isLocator(acceptedRoute) && matchResultCandidate != null) {
               result = this.matchPathSelected(context, acceptedRouteCandidate, matchResultCandidate, tracingLogger);
               break;
            }

            if (isLocator(acceptedRoute) || designatorMatch(acceptedRoute, context)) {
               result = this.matchPathSelected(context, acceptedRoute, matchResult, tracingLogger);
               break;
            }

            if (matchResultCandidate == null) {
               matchResultCandidate = matchResult;
               acceptedRouteCandidate = acceptedRoute;
            }
         } else {
            tracingLogger.log(ServerTraceEvent.MATCH_PATH_NOT_MATCHED, new Object[]{routePattern.getRegex()});
         }
      }

      <unknown> iterator;
      if (tracingLogger.isLogEnabled(ServerTraceEvent.MATCH_PATH_SKIPPED)) {
         while (iterator.hasNext()) {
            tracingLogger.log(ServerTraceEvent.MATCH_PATH_SKIPPED, new Object[]{((Route)iterator.next()).routingPattern().getRegex()});
         }
      }

      if (result == null && acceptedRouteCandidate != null) {
         result = this.matchPathSelected(context, acceptedRouteCandidate, matchResultCandidate, tracingLogger);
      }

      return result == null ? Router.Continuation.of(context) : result;
   }

   private Router.Continuation matchPathSelected(RequestProcessingContext context, Route acceptedRoute, MatchResult matchResult, TracingLogger tracingLogger) {
      context.routingContext().pushMatchResult(matchResult);
      Router.Continuation result = Router.Continuation.of(context, acceptedRoute.next());
      tracingLogger.log(ServerTraceEvent.MATCH_PATH_SELECTED, new Object[]{acceptedRoute.routingPattern().getRegex()});
      return result;
   }

   private static boolean designatorMatch(Route route, RequestProcessingContext context) {
      String httpMethod = context.request().getMethod();
      return route.getHttpMethods().contains(httpMethod) ? true : "HEAD".equals(httpMethod) && route.getHttpMethods().contains("GET");
   }

   private static boolean isLocator(Route route) {
      return route.getHttpMethods() == null;
   }
}
