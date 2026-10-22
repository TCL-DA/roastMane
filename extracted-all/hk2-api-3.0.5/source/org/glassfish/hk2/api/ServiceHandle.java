package org.glassfish.hk2.api;

import java.io.Closeable;
import java.util.List;

public interface ServiceHandle<T> extends Closeable {
   T getService();

   ActiveDescriptor<T> getActiveDescriptor();

   boolean isActive();

   @Deprecated
   default void destroy() {
      this.close();
   }

   @Override
   default void close() {
      this.destroy();
   }

   void setServiceData(Object var1);

   Object getServiceData();

   List<ServiceHandle<?>> getSubHandles();
}
