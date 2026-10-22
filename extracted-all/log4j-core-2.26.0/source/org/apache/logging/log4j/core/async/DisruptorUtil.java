package org.apache.logging.log4j.core.async;

import com.lmax.disruptor.ExceptionHandler;
import com.lmax.disruptor.WaitStrategy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.util.Constants;
import org.apache.logging.log4j.core.util.Integers;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.LoaderUtil;
import org.apache.logging.log4j.util.PropertiesUtil;

final class DisruptorUtil {
   private static final Logger LOGGER = StatusLogger.getLogger();
   private static final int RINGBUFFER_MIN_SIZE = 128;
   private static final int RINGBUFFER_DEFAULT_SIZE = 262144;
   private static final int RINGBUFFER_NO_GC_DEFAULT_SIZE = 4096;
   public static final String LOGGER_EXCEPTION_HANDLER_PROPERTY = "AsyncLogger.ExceptionHandler";
   public static final String LOGGER_CONFIG_EXCEPTION_HANDLER_PROPERTY = "AsyncLoggerConfig.ExceptionHandler";
   static final boolean ASYNC_LOGGER_SYNCHRONIZE_ENQUEUE_WHEN_QUEUE_FULL = PropertiesUtil.getProperties()
      .getBooleanProperty("AsyncLogger.SynchronizeEnqueueWhenQueueFull", true);
   static final boolean ASYNC_CONFIG_SYNCHRONIZE_ENQUEUE_WHEN_QUEUE_FULL = PropertiesUtil.getProperties()
      .getBooleanProperty("AsyncLoggerConfig.SynchronizeEnqueueWhenQueueFull", true);
   static final int DISRUPTOR_MAJOR_VERSION = detectDisruptorMajorVersion();

   private static int detectDisruptorMajorVersion() {
      try {
         Class.forName("com.lmax.disruptor.SequenceReportingEventHandler", true, DisruptorUtil.class.getClassLoader());
         return 3;
      } catch (ClassNotFoundException var1) {
         return 4;
      }
   }

   private DisruptorUtil() {
   }

   static WaitStrategy createWaitStrategy(final String propertyName, final AsyncWaitStrategyFactory asyncWaitStrategyFactory) {
      if (asyncWaitStrategyFactory == null) {
         LOGGER.debug("No AsyncWaitStrategyFactory was configured in the configuration, using default factory...");
         return new DefaultAsyncWaitStrategyFactory(propertyName).createWaitStrategy();
      } else {
         LOGGER.debug("Using configured AsyncWaitStrategyFactory {}", asyncWaitStrategyFactory.getClass().getName());
         return asyncWaitStrategyFactory.createWaitStrategy();
      }
   }

   static int calculateRingBufferSize(final String propertyName) {
      int ringBufferSize = Constants.ENABLE_THREADLOCALS ? 4096 : 262144;
      String userPreferredRBSize = PropertiesUtil.getProperties().getStringProperty(propertyName, String.valueOf(ringBufferSize));

      try {
         int size = Integers.parseInt(userPreferredRBSize);
         if (size < 128) {
            size = 128;
            LOGGER.warn("Invalid RingBufferSize {}, using minimum size {}.", userPreferredRBSize, 128);
         }

         ringBufferSize = size;
      } catch (Exception var4) {
         LOGGER.warn("Invalid RingBufferSize {}, using default size {}.", userPreferredRBSize, ringBufferSize);
      }

      return Integers.ceilingNextPowerOfTwo(ringBufferSize);
   }

   static ExceptionHandler<RingBufferLogEvent> getAsyncLoggerExceptionHandler() {
      try {
         return (ExceptionHandler<RingBufferLogEvent>)LoaderUtil.newCheckedInstanceOfProperty(
            "AsyncLogger.ExceptionHandler", ExceptionHandler.class, AsyncLoggerDefaultExceptionHandler::new
         );
      } catch (ReflectiveOperationException var1) {
         LOGGER.debug("Invalid AsyncLogger.ExceptionHandler value: {}", var1.getMessage(), var1);
         return new AsyncLoggerDefaultExceptionHandler();
      }
   }

   static ExceptionHandler<AsyncLoggerConfigDisruptor.Log4jEventWrapper> getAsyncLoggerConfigExceptionHandler() {
      try {
         return (ExceptionHandler<AsyncLoggerConfigDisruptor.Log4jEventWrapper>)LoaderUtil.newCheckedInstanceOfProperty(
            "AsyncLoggerConfig.ExceptionHandler", ExceptionHandler.class, AsyncLoggerConfigDefaultExceptionHandler::new
         );
      } catch (ReflectiveOperationException var1) {
         LOGGER.debug("Invalid AsyncLogger.ExceptionHandler value: {}", var1.getMessage(), var1);
         return new AsyncLoggerConfigDefaultExceptionHandler();
      }
   }

   public static long getExecutorThreadId(final ExecutorService executor) {
      Future<Long> result = executor.submit(() -> Thread.currentThread().getId());

      try {
         return result.get();
      } catch (Exception var4) {
         String msg = "Could not obtain executor thread Id. Giving up to avoid the risk of application deadlock.";
         throw new IllegalStateException("Could not obtain executor thread Id. Giving up to avoid the risk of application deadlock.", var4);
      }
   }
}
