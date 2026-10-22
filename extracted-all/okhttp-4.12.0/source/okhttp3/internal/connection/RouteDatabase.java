package okhttp3.internal.connection

import java.util.LinkedHashSet
import okhttp3.Route

class RouteDatabase {
   private final val failedRoutes: MutableSet<Route> = (new LinkedHashSet()) as java.util.Set


   @Synchronized
   public fun failed(failedRoute: Route) {
      this.failedRoutes.add(failedRoute);
   }

   @Synchronized
   public fun connected(route: Route) {
      this.failedRoutes.remove(route);
   }

   @Synchronized
   public fun shouldPostpone(route: Route): Boolean {
      return this.failedRoutes.contains(route);
   }
}
