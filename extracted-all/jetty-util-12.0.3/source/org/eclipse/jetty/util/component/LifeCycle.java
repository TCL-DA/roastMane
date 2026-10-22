package org.eclipse.jetty.util.component;

import java.util.EventListener;
import org.eclipse.jetty.util.annotation.ManagedObject;
import org.eclipse.jetty.util.annotation.ManagedOperation;

@ManagedObject("Lifecycle Interface for startable components")
public interface LifeCycle {
   @ManagedOperation(
      value = "Starts the instance",
      impact = "ACTION"
   )
   void start() throws Exception;

   static void start(Object object) {
      if (object instanceof LifeCycle) {
         try {
            ((LifeCycle)object).start();
         } catch (Exception var2) {
            throw new RuntimeException(var2);
         }
      }
   }

   @ManagedOperation(
      value = "Stops the instance",
      impact = "ACTION"
   )
   void stop() throws Exception;

   static void stop(Object object) {
      if (object instanceof LifeCycle) {
         try {
            ((LifeCycle)object).stop();
         } catch (Exception var2) {
            throw new RuntimeException(var2);
         }
      }
   }

   boolean isRunning();

   boolean isStarted();

   boolean isStarting();

   boolean isStopping();

   boolean isStopped();

   boolean isFailed();

   boolean addEventListener(EventListener var1);

   boolean removeEventListener(EventListener var1);

   public interface Listener extends EventListener {
      default void lifeCycleStarting(LifeCycle event) {
      }

      default void lifeCycleStarted(LifeCycle event) {
      }

      default void lifeCycleFailure(LifeCycle event, Throwable cause) {
      }

      default void lifeCycleStopping(LifeCycle event) {
      }

      default void lifeCycleStopped(LifeCycle event) {
      }
   }
}
