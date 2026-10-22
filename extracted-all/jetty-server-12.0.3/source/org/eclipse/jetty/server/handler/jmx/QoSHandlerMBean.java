package org.eclipse.jetty.server.handler.jmx;

import java.time.Duration;
import org.eclipse.jetty.server.handler.QoSHandler;
import org.eclipse.jetty.server.jmx.Handler;
import org.eclipse.jetty.util.annotation.ManagedAttribute;

public class QoSHandlerMBean extends Handler.AbstractMBean {
   public QoSHandlerMBean(Object managedObject) {
      super(managedObject);
   }

   public QoSHandler getManagedObject() {
      return (QoSHandler)super.getManagedObject();
   }

   @ManagedAttribute("The maximum request suspend time in milliseconds")
   public long getMaxSuspendMillis() {
      return this.getManagedObject().getMaxSuspend().toMillis();
   }

   public void setMaxSuspendMillis(long maxSuspend) {
      this.getManagedObject().setMaxSuspend(Duration.ofMillis(maxSuspend));
   }
}
