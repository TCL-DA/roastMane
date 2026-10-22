package org.glassfish.hk2.utilities;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.jvnet.hk2.annotations.Contract;

@Contract
public interface ImmediateErrorHandler {
   void postConstructFailed(ActiveDescriptor<?> var1, Throwable var2);

   void preDestroyFailed(ActiveDescriptor<?> var1, Throwable var2);
}
