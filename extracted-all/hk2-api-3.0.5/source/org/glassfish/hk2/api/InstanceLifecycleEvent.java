package org.glassfish.hk2.api;

import java.util.Map;

public interface InstanceLifecycleEvent {
   InstanceLifecycleEventType getEventType();

   ActiveDescriptor<?> getActiveDescriptor();

   Object getLifecycleObject();

   Map<Injectee, Object> getKnownInjectees();
}
