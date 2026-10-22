package org.eclipse.jetty.util.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StopLifeCycle extends AbstractLifeCycle implements LifeCycle.Listener {
   private static final Logger LOG = LoggerFactory.getLogger(StopLifeCycle.class);
   private final LifeCycle _lifecycle;

   public StopLifeCycle(LifeCycle lifecycle) {
      this._lifecycle = lifecycle;
      this.addEventListener(this);
   }

   @Override
   public void lifeCycleStarted(LifeCycle lifecycle) {
      try {
         this._lifecycle.stop();
      } catch (Exception var3) {
         LOG.warn("Unable to stop", var3);
      }
   }
}
