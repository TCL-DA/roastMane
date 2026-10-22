package org.glassfish.hk2.api;

import java.util.concurrent.Executor;
import org.jvnet.hk2.annotations.Contract;

@Contract
public interface ImmediateController {
   Executor getExecutor();

   void setExecutor(Executor var1) throws IllegalStateException;

   long getThreadInactivityTimeout();

   void setThreadInactivityTimeout(long var1) throws IllegalArgumentException;

   ImmediateController.ImmediateServiceState getImmediateState();

   void setImmediateState(ImmediateController.ImmediateServiceState var1);

   public static enum ImmediateServiceState {
      SUSPENDED,
      RUNNING;
   }
}
