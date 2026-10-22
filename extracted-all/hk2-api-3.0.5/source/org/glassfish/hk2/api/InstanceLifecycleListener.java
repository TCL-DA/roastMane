package org.glassfish.hk2.api;

import org.jvnet.hk2.annotations.Contract;

@Contract
public interface InstanceLifecycleListener {
   Filter getFilter();

   void lifecycleEvent(InstanceLifecycleEvent var1);
}
