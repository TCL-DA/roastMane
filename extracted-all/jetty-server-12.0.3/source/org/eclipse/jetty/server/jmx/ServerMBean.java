package org.eclipse.jetty.server.jmx;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import org.eclipse.jetty.util.annotation.ManagedObject;

@ManagedObject
public class ServerMBean extends Handler.AbstractMBean {
   private final Instant startup = Instant.now();

   public ServerMBean(Object managedObject) {
      super(managedObject);
   }

   public Server getManagedObject() {
      return (Server)super.getManagedObject();
   }

   @ManagedAttribute("The contexts on this server")
   public List<ContextHandler> getContexts() {
      return this.getManagedObject().getDescendants(ContextHandler.class);
   }

   @ManagedAttribute("The UTC startup instant")
   public String getStartupTime() {
      return this.startup.toString();
   }

   @ManagedAttribute("The uptime duration in d:HH:mm:ss.SSS")
   public String getUpTime() {
      Duration upTime = Duration.between(this.startup, Instant.now());
      return "%d:%02d:%02d:%02d.%03d"
         .formatted(upTime.toDaysPart(), upTime.toHoursPart(), upTime.toMinutesPart(), upTime.toSecondsPart(), upTime.toMillisPart());
   }
}
