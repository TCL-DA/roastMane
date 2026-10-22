package org.eclipse.jetty.util.thread.strategy;

import java.util.concurrent.Executor;
import org.eclipse.jetty.util.thread.ExecutionStrategy;

@Deprecated(
   forRemoval = true
)
public class EatWhatYouKill extends AdaptiveExecutionStrategy {
   public EatWhatYouKill(ExecutionStrategy.Producer producer, Executor executor) {
      super(producer, executor);
   }
}
