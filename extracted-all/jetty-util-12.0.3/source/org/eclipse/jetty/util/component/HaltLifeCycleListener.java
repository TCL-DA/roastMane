package org.eclipse.jetty.util.component;

public class HaltLifeCycleListener implements LifeCycle.Listener {
   @Override
   public void lifeCycleStarted(LifeCycle lifecycle) {
      Runtime.getRuntime().halt(0);
   }
}
