package org.eclipse.jetty.util.thread;

import java.util.concurrent.TimeUnit;
import org.eclipse.jetty.util.component.LifeCycle;

public interface Scheduler extends LifeCycle {
   Scheduler.Task schedule(Runnable var1, long var2, TimeUnit var4);

   public interface Task {
      boolean cancel();
   }
}
