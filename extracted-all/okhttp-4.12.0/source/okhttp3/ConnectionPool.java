package okhttp3

import java.util.concurrent.TimeUnit
import okhttp3.internal.concurrent.TaskRunner
import okhttp3.internal.connection.RealConnectionPool

class ConnectionPool internal constructor(delegate: RealConnectionPool) {
   internal final val delegate: RealConnectionPool

   init {
      this.delegate = delegate;
   }

   public constructor(maxIdleConnections: Int, keepAliveDuration: Long, timeUnit: TimeUnit) : this(
         new RealConnectionPool(TaskRunner.INSTANCE, maxIdleConnections, keepAliveDuration, timeUnit)
      )
   public constructor() : this(5, 5L, TimeUnit.MINUTES)
   public fun idleConnectionCount(): Int {
      return this.delegate.idleConnectionCount();
   }

   public fun connectionCount(): Int {
      return this.delegate.connectionCount();
   }

   public fun evictAll() {
      this.delegate.evictAll();
   }
}
