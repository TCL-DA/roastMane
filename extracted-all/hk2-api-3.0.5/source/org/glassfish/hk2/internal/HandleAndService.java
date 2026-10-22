package org.glassfish.hk2.internal;

import org.glassfish.hk2.api.ServiceHandle;

public class HandleAndService {
   private final ServiceHandle<?> handle;
   private final Object service;

   public HandleAndService(ServiceHandle<?> handle, Object service) {
      this.handle = handle;
      this.service = service;
   }

   public ServiceHandle<?> getHandle() {
      return this.handle;
   }

   public Object getService() {
      return this.service;
   }
}
