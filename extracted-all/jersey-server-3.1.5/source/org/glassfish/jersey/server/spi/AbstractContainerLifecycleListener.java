package org.glassfish.jersey.server.spi;

public abstract class AbstractContainerLifecycleListener implements ContainerLifecycleListener {
   @Override
   public void onStartup(Container container) {
   }

   @Override
   public void onReload(Container container) {
   }

   @Override
   public void onShutdown(Container container) {
   }
}
