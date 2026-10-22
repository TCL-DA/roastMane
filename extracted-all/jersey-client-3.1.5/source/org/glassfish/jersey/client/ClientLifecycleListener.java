package org.glassfish.jersey.client;

import jakarta.ws.rs.ConstrainedTo;
import jakarta.ws.rs.RuntimeType;
import org.glassfish.jersey.spi.Contract;

@Contract
@ConstrainedTo(RuntimeType.CLIENT)
public interface ClientLifecycleListener {
   void onInit();

   void onClose();
}
