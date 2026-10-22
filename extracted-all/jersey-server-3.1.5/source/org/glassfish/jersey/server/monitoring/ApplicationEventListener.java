package org.glassfish.jersey.server.monitoring;

import jakarta.ws.rs.ConstrainedTo;
import jakarta.ws.rs.RuntimeType;
import org.glassfish.jersey.spi.Contract;

@Contract
@ConstrainedTo(RuntimeType.SERVER)
public interface ApplicationEventListener {
   void onEvent(ApplicationEvent var1);

   RequestEventListener onRequest(RequestEvent var1);
}
