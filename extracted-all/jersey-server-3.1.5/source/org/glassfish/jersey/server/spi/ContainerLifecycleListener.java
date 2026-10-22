package org.glassfish.jersey.server.spi;

import jakarta.ws.rs.ConstrainedTo;
import jakarta.ws.rs.RuntimeType;
import org.glassfish.jersey.spi.Contract;

@Contract
@ConstrainedTo(RuntimeType.SERVER)
public interface ContainerLifecycleListener {
   void onStartup(Container var1);

   void onReload(Container var1);

   void onShutdown(Container var1);
}
