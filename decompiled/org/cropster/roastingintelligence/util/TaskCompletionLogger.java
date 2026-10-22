package org.cropster.roastingintelligence.util;

import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskCompletionLogger extends Thread {
   private static final String LOG_MESSAGE = "Error executing task";
   private final Logger logger = LoggerFactory.getLogger(TaskCompletionLogger.class);
   private final CompletionService<?> _completionService;

   public TaskCompletionLogger(CompletionService<?> completionService) {
      super(TaskCompletionLogger.class.getSimpleName());
      this.setDaemon(true);
      this._completionService = completionService;
   }

   @Override
   public void run() {
      while (!this.isInterrupted()) {
         try {
            Future<?> future = this._completionService.take();
            if (future != null) {
               future.get();
            }
         } catch (CancellationException var2) {
            this.logger.warn("Task cancelled", var2);
         } catch (InterruptedException var3) {
            this.logger.warn("Task Completion logger interrupted", var3);
         } catch (ExecutionException var4) {
            if (this.logger.isDebugEnabled()) {
               this.logger.debug("Error executing task", var4);
            } else if (this.logger.isInfoEnabled()) {
               this.logger.info("Error executing task", var4);
            } else if (this.logger.isWarnEnabled()) {
               this.logger.warn("Error executing task", var4);
            }
         }
      }
   }
}
