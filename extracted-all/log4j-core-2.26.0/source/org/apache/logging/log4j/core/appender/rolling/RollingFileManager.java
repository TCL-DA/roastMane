package org.apache.logging.log4j.core.appender.rolling;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LifeCycle;
import org.apache.logging.log4j.core.LifeCycle2;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConfigurationFactoryData;
import org.apache.logging.log4j.core.appender.FileManager;
import org.apache.logging.log4j.core.appender.rolling.action.AbstractAction;
import org.apache.logging.log4j.core.appender.rolling.action.Action;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.internal.annotation.SuppressFBWarnings;
import org.apache.logging.log4j.core.util.Constants;
import org.apache.logging.log4j.core.util.FileUtils;
import org.apache.logging.log4j.core.util.Log4jThreadFactory;

public class RollingFileManager extends FileManager {
   private static final int MAX_TRIES = 3;
   private static final int MIN_DURATION = 100;
   private static final FileTime EPOCH = FileTime.fromMillis(0L);
   protected long size;
   private long initialTime;
   private volatile PatternProcessor patternProcessor;
   private final Semaphore semaphore = new Semaphore(1);
   private final Log4jThreadFactory threadFactory = Log4jThreadFactory.createThreadFactory("RollingFileManager");
   private volatile TriggeringPolicy triggeringPolicy;
   private volatile RolloverStrategy rolloverStrategy;
   private volatile boolean renameEmptyFiles;
   private volatile boolean initialized;
   private volatile String fileName;
   private final boolean directWrite;
   private final CopyOnWriteArrayList<RolloverListener> rolloverListeners = new CopyOnWriteArrayList<>();
   private final ExecutorService asyncExecutor = new ThreadPoolExecutor(
      0, Integer.MAX_VALUE, 0L, TimeUnit.MILLISECONDS, new RollingFileManager.EmptyQueue(), this.threadFactory
   );
   private static final AtomicReferenceFieldUpdater<RollingFileManager, TriggeringPolicy> triggeringPolicyUpdater = AtomicReferenceFieldUpdater.newUpdater(
      RollingFileManager.class, TriggeringPolicy.class, "triggeringPolicy"
   );
   private static final AtomicReferenceFieldUpdater<RollingFileManager, RolloverStrategy> rolloverStrategyUpdater = AtomicReferenceFieldUpdater.newUpdater(
      RollingFileManager.class, RolloverStrategy.class, "rolloverStrategy"
   );
   private static final AtomicReferenceFieldUpdater<RollingFileManager, PatternProcessor> patternProcessorUpdater = AtomicReferenceFieldUpdater.newUpdater(
      RollingFileManager.class, PatternProcessor.class, "patternProcessor"
   );

   @Deprecated
   protected RollingFileManager(
      final String fileName,
      final String pattern,
      final OutputStream os,
      final boolean append,
      final long size,
      final long initialTime,
      final TriggeringPolicy triggeringPolicy,
      final RolloverStrategy rolloverStrategy,
      final String advertiseURI,
      final Layout<? extends Serializable> layout,
      final int bufferSize,
      final boolean writeHeader
   ) {
      this(
         fileName,
         pattern,
         os,
         append,
         size,
         initialTime,
         triggeringPolicy,
         rolloverStrategy,
         advertiseURI,
         layout,
         writeHeader,
         ByteBuffer.wrap(new byte[Constants.ENCODER_BYTE_BUFFER_SIZE])
      );
   }

   @Deprecated
   protected RollingFileManager(
      final String fileName,
      final String pattern,
      final OutputStream os,
      final boolean append,
      final long size,
      final long initialTime,
      final TriggeringPolicy triggeringPolicy,
      final RolloverStrategy rolloverStrategy,
      final String advertiseURI,
      final Layout<? extends Serializable> layout,
      final boolean writeHeader,
      final ByteBuffer buffer
   ) {
      super(fileName != null ? fileName : pattern, os, append, false, advertiseURI, layout, writeHeader, buffer);
      this.size = size;
      this.initialTime = initialTime;
      this.triggeringPolicy = triggeringPolicy;
      this.rolloverStrategy = rolloverStrategy;
      this.patternProcessor = new PatternProcessor(pattern);
      this.patternProcessor.setPrevFileTime(initialTime);
      this.fileName = fileName;
      this.directWrite = rolloverStrategy instanceof DirectWriteRolloverStrategy;
   }

   @Deprecated
   protected RollingFileManager(
      final LoggerContext loggerContext,
      final String fileName,
      final String pattern,
      final OutputStream os,
      final boolean append,
      final boolean createOnDemand,
      final long size,
      final long initialTime,
      final TriggeringPolicy triggeringPolicy,
      final RolloverStrategy rolloverStrategy,
      final String advertiseURI,
      final Layout<? extends Serializable> layout,
      final boolean writeHeader,
      final ByteBuffer buffer
   ) {
      super(loggerContext, fileName != null ? fileName : pattern, os, append, false, createOnDemand, advertiseURI, layout, writeHeader, buffer);
      this.size = size;
      this.initialTime = initialTime;
      this.triggeringPolicy = triggeringPolicy;
      this.rolloverStrategy = rolloverStrategy;
      this.patternProcessor = new PatternProcessor(pattern);
      this.patternProcessor.setPrevFileTime(initialTime);
      this.fileName = fileName;
      this.directWrite = rolloverStrategy instanceof DirectWriteRolloverStrategy;
   }

   protected RollingFileManager(
      final LoggerContext loggerContext,
      final String fileName,
      final String pattern,
      final OutputStream os,
      final boolean append,
      final boolean createOnDemand,
      final long size,
      final long initialTime,
      final TriggeringPolicy triggeringPolicy,
      final RolloverStrategy rolloverStrategy,
      final String advertiseURI,
      final Layout<? extends Serializable> layout,
      final String filePermissions,
      final String fileOwner,
      final String fileGroup,
      final boolean writeHeader,
      final ByteBuffer buffer
   ) {
      super(
         loggerContext,
         fileName != null ? fileName : pattern,
         os,
         append,
         false,
         createOnDemand,
         advertiseURI,
         layout,
         filePermissions,
         fileOwner,
         fileGroup,
         writeHeader,
         buffer
      );
      this.size = size;
      this.initialTime = initialTime;
      this.patternProcessor = new PatternProcessor(pattern);
      this.patternProcessor.setPrevFileTime(initialTime);
      this.triggeringPolicy = triggeringPolicy;
      this.rolloverStrategy = rolloverStrategy;
      this.fileName = fileName;
      this.directWrite = rolloverStrategy instanceof DirectFileRolloverStrategy;
   }

   @SuppressFBWarnings(
      value = {"PATH_TRAVERSAL_IN"},
      justification = "The name of the accessed files is based on a configuration value."
   )
   public void initialize() {
      if (!this.initialized) {
         LOGGER.debug("Initializing triggering policy {}", this.triggeringPolicy);
         this.initialized = true;
         if (this.directWrite) {
            File file = new File(this.getFileName());
            if (file.exists()) {
               this.size = file.length();
            } else {
               ((DirectFileRolloverStrategy)this.rolloverStrategy).clearCurrentFileName();
            }
         }

         this.triggeringPolicy.initialize(this);
         if (this.triggeringPolicy instanceof LifeCycle) {
            ((LifeCycle)this.triggeringPolicy).start();
         }

         if (this.directWrite) {
            File file = new File(this.getFileName());
            if (file.exists()) {
               this.size = file.length();
            } else {
               ((DirectFileRolloverStrategy)this.rolloverStrategy).clearCurrentFileName();
            }
         }
      }
   }

   public static RollingFileManager getFileManager(
      final String fileName,
      final String pattern,
      final boolean append,
      final boolean bufferedIO,
      final TriggeringPolicy policy,
      final RolloverStrategy strategy,
      final String advertiseURI,
      final Layout<? extends Serializable> layout,
      final int bufferSize,
      final boolean immediateFlush,
      final boolean createOnDemand,
      final String filePermissions,
      final String fileOwner,
      final String fileGroup,
      final Configuration configuration
   ) {
      if (strategy instanceof DirectWriteRolloverStrategy && fileName != null) {
         LOGGER.error("The fileName attribute must not be specified with the DirectWriteRolloverStrategy");
         return null;
      } else {
         String actualName = fileName == null ? pattern : fileName;
         int actualBufferSize = bufferedIO ? bufferSize : Constants.ENCODER_BYTE_BUFFER_SIZE;
         return narrow(
            RollingFileManager.class,
            getManager(
               actualName,
               (name, data) -> {
                  long size = 0L;
                  File file = null;
                  if (fileName != null) {
                     file = new File(fileName);

                     try {
                        FileUtils.makeParentDirs(file);
                        boolean created = createOnDemand ? false : file.createNewFile();
                        LOGGER.trace("New file '{}' created = {}", name, created);
                     } catch (IOException var20) {
                        LOGGER.error("Unable to create file {}", name, var20);
                        return null;
                     }

                     size = append ? file.length() : 0L;
                  }

                  try {
                     ByteBuffer buffer = ByteBuffer.allocate(actualBufferSize);
                     OutputStream os = !createOnDemand && fileName != null ? new FileOutputStream(fileName, append) : null;
                     long initialTime = file != null && file.exists() ? initialFileTime(file) : 0L;
                     boolean writeHeader = file != null && file.exists() && file.length() == 0L;
                     RollingFileManager rm = new RollingFileManager(
                        data.getLoggerContext(),
                        fileName,
                        data.getPattern(),
                        os,
                        append,
                        createOnDemand,
                        size,
                        initialTime,
                        data.getTriggeringPolicy(),
                        data.getRolloverStrategy(),
                        advertiseURI,
                        layout,
                        filePermissions,
                        fileOwner,
                        fileGroup,
                        writeHeader,
                        buffer
                     );
                     if (os != null && rm.isAttributeViewEnabled()) {
                        rm.defineAttributeView(file.toPath());
                     }

                     return rm;
                  } catch (IOException var21) {
                     LOGGER.error("RollingFileManager ({}): {}", name, var21.getMessage(), var21);
                     return null;
                  }
               },
               new RollingFileManager.FactoryData(pattern, policy, strategy, configuration)
            )
         );
      }
   }

   public void addRolloverListener(final RolloverListener listener) {
      this.rolloverListeners.add(listener);
   }

   public void removeRolloverListener(final RolloverListener listener) {
      this.rolloverListeners.remove(listener);
   }

   @Override
   public String getFileName() {
      if (this.directWrite) {
         this.fileName = ((DirectFileRolloverStrategy)this.rolloverStrategy).getCurrentFileName(this);
      }

      return this.fileName;
   }

   @Override
   protected void createParentDir(File file) {
      if (this.directWrite) {
         File parent = file.getParentFile();
         if (parent != null) {
            parent.mkdirs();
         }
      }
   }

   public boolean isDirectWrite() {
      return this.directWrite;
   }

   public FileExtension getFileExtension() {
      return this.patternProcessor.getFileExtension();
   }

   @Override
   protected synchronized void write(final byte[] bytes, final int offset, final int length, final boolean immediateFlush) {
      super.write(bytes, offset, length, immediateFlush);
   }

   @Override
   protected synchronized void writeToDestination(final byte[] bytes, final int offset, final int length) {
      this.size += (long)length;
      super.writeToDestination(bytes, offset, length);
   }

   public boolean isRenameEmptyFiles() {
      return this.renameEmptyFiles;
   }

   public void setRenameEmptyFiles(final boolean renameEmptyFiles) {
      this.renameEmptyFiles = renameEmptyFiles;
   }

   public long getFileSize() {
      return this.size + (long)this.byteBuffer.position();
   }

   public long getFileTime() {
      return this.initialTime;
   }

   public synchronized void checkRollover(final LogEvent event) {
      if (this.triggeringPolicy.isTriggeringEvent(event)) {
         this.rollover();
      }
   }

   @Override
   public boolean releaseSub(final long timeout, final TimeUnit timeUnit) {
      LOGGER.debug("Shutting down RollingFileManager {}", this.getName());
      boolean stopped = true;
      if (this.triggeringPolicy instanceof LifeCycle2) {
         stopped &= ((LifeCycle2)this.triggeringPolicy).stop(timeout, timeUnit);
      } else if (this.triggeringPolicy instanceof LifeCycle) {
         ((LifeCycle)this.triggeringPolicy).stop();
         stopped &= true;
      }

      boolean status = super.releaseSub(timeout, timeUnit) && stopped;
      this.asyncExecutor.shutdown();

      try {
         long millis = timeUnit.toMillis(timeout);
         long waitInterval = 100L < millis ? millis : 100L;

         for (int count = 1; count <= 3 && !this.asyncExecutor.isTerminated(); count++) {
            this.asyncExecutor.awaitTermination(waitInterval * (long)count, TimeUnit.MILLISECONDS);
         }

         if (this.asyncExecutor.isTerminated()) {
            LOGGER.debug("All asynchronous threads have terminated");
         } else {
            this.asyncExecutor.shutdownNow();

            try {
               this.asyncExecutor.awaitTermination(timeout, timeUnit);
               if (this.asyncExecutor.isTerminated()) {
                  LOGGER.debug("All asynchronous threads have terminated");
               } else {
                  LOGGER.debug("RollingFileManager shutting down but some asynchronous services may not have completed");
               }
            } catch (InterruptedException var12) {
               LOGGER.warn("RollingFileManager stopped but some asynchronous services may not have completed.");
            }
         }
      } catch (InterruptedException var13) {
         this.asyncExecutor.shutdownNow();

         try {
            this.asyncExecutor.awaitTermination(timeout, timeUnit);
            if (this.asyncExecutor.isTerminated()) {
               LOGGER.debug("All asynchronous threads have terminated");
            }
         } catch (InterruptedException var11) {
            LOGGER.warn("RollingFileManager stopped but some asynchronous services may not have completed.");
         }

         Thread.currentThread().interrupt();
      }

      LOGGER.debug("RollingFileManager shutdown completed with status {}", status);
      return status;
   }

   public synchronized void rollover(final Date prevFileTime, final Date prevRollTime) {
      LOGGER.debug("Rollover PrevFileTime: {}, PrevRollTime: {}", prevFileTime.getTime(), prevRollTime.getTime());
      this.getPatternProcessor().setPrevFileTime(prevFileTime.getTime());
      this.getPatternProcessor().setCurrentFileTime(prevRollTime.getTime());
      this.rollover();
   }

   public synchronized void rollover() {
      if (this.hasOutputStream() || this.isCreateOnDemand() || this.isDirectWrite()) {
         String currentFileName = this.fileName;
         if (this.rolloverListeners.size() > 0) {
            for (RolloverListener listener : this.rolloverListeners) {
               try {
                  listener.rolloverTriggered(currentFileName);
               } catch (Exception var12) {
                  LOGGER.warn("Rollover Listener {} failed with {}: {}", listener.getClass().getSimpleName(), var12.getClass().getName(), var12.getMessage());
               }
            }
         }

         boolean interrupted = Thread.interrupted();

         try {
            if (interrupted) {
               LOGGER.warn("RollingFileManager cleared thread interrupted state, continue to rollover");
            }

            if (this.rollover(this.rolloverStrategy)) {
               try {
                  this.size = 0L;
                  this.initialTime = System.currentTimeMillis();
                  this.createFileAfterRollover();
               } catch (IOException var11) {
                  this.logError("Failed to create file after rollover", var11);
               }
            }
         } finally {
            if (interrupted) {
               Thread.currentThread().interrupt();
            }
         }

         if (this.rolloverListeners.size() > 0) {
            for (RolloverListener listener : this.rolloverListeners) {
               try {
                  listener.rolloverComplete(currentFileName);
               } catch (Exception var10) {
                  LOGGER.warn("Rollover Listener {} failed with {}: {}", listener.getClass().getSimpleName(), var10.getClass().getName(), var10.getMessage());
               }
            }
         }
      }
   }

   protected void createFileAfterRollover() throws IOException {
      this.setOutputStream(this.createOutputStream());
   }

   public PatternProcessor getPatternProcessor() {
      return this.patternProcessor;
   }

   public void setTriggeringPolicy(final TriggeringPolicy triggeringPolicy) {
      triggeringPolicy.initialize(this);
      TriggeringPolicy policy = this.triggeringPolicy;
      int count = 0;
      boolean policyUpdated = false;

      while (!(policyUpdated = triggeringPolicyUpdater.compareAndSet(this, this.triggeringPolicy, triggeringPolicy)) && ++count < 3) {
      }

      if (policyUpdated) {
         if (triggeringPolicy instanceof LifeCycle) {
            ((LifeCycle)triggeringPolicy).start();
         }

         if (policy instanceof LifeCycle) {
            ((LifeCycle)policy).stop();
         }
      } else if (triggeringPolicy instanceof LifeCycle) {
         ((LifeCycle)triggeringPolicy).stop();
      }
   }

   public void setRolloverStrategy(final RolloverStrategy rolloverStrategy) {
      rolloverStrategyUpdater.compareAndSet(this, this.rolloverStrategy, rolloverStrategy);
   }

   public void setPatternProcessor(final PatternProcessor patternProcessor) {
      patternProcessorUpdater.compareAndSet(this, this.patternProcessor, patternProcessor);
   }

   public <T extends TriggeringPolicy> T getTriggeringPolicy() {
      return (T)this.triggeringPolicy;
   }

   Semaphore getSemaphore() {
      return this.semaphore;
   }

   public RolloverStrategy getRolloverStrategy() {
      return this.rolloverStrategy;
   }

   private boolean rollover(final RolloverStrategy strategy) {
      boolean outputStreamClosed = false;

      try {
         this.semaphore.acquire();
      } catch (InterruptedException var12) {
         this.logError("Thread interrupted while attempting to check rollover", var12);
         return outputStreamClosed;
      }

      boolean asyncActionStarted = true;

      boolean syncActionSuccess;
      try {
         RolloverDescription descriptor = strategy.rollover(this);
         if (descriptor != null) {
            this.writeFooter();
            this.closeOutputStream();
            outputStreamClosed = true;
            syncActionSuccess = true;
            if (descriptor.getSynchronous() != null) {
               LOGGER.debug("RollingFileManager executing synchronous {}", descriptor.getSynchronous());

               try {
                  syncActionSuccess = descriptor.getSynchronous().execute();
               } catch (Exception var11) {
                  syncActionSuccess = false;
                  this.logError("Caught error in synchronous task", var11);
               }
            }

            if (syncActionSuccess && descriptor.getAsynchronous() != null) {
               LOGGER.debug("RollingFileManager executing async {}", descriptor.getAsynchronous());
               this.asyncExecutor.execute(new RollingFileManager.AsyncAction(descriptor.getAsynchronous(), this));
               asyncActionStarted = false;
            }
         }

         syncActionSuccess = outputStreamClosed;
      } finally {
         if (asyncActionStarted) {
            this.semaphore.release();
         }
      }

      return syncActionSuccess;
   }

   @Override
   public void updateData(final Object data) {
      RollingFileManager.FactoryData factoryData = (RollingFileManager.FactoryData)data;
      this.setRolloverStrategy(factoryData.getRolloverStrategy());
      this.setPatternProcessor(new PatternProcessor(factoryData.getPattern(), this.getPatternProcessor()));
      this.setTriggeringPolicy(factoryData.getTriggeringPolicy());
   }

   static long initialFileTime(final File file) {
      Path path = file.toPath();
      if (Files.exists(path)) {
         try {
            BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
            FileTime fileTime = attrs.creationTime();
            if (fileTime.compareTo(EPOCH) > 0) {
               LOGGER.debug("Returning file creation time for {}", file.getAbsolutePath());
               return alignMillisToSecond(fileTime.toMillis());
            }

            LOGGER.info("Unable to obtain file creation time for {}", file.getAbsolutePath());
         } catch (Exception var4) {
            LOGGER.info("Unable to calculate file creation time for {}: {}", file.getAbsolutePath(), var4.getMessage());
         }
      }

      return alignMillisToSecond(file.lastModified());
   }

   static long alignMillisToSecond(long millis) {
      return Math.round((double)millis / 1000.0) * 1000L;
   }

   private static class AsyncAction extends AbstractAction {
      private final Action action;
      private final RollingFileManager manager;

      public AsyncAction(final Action act, final RollingFileManager manager) {
         this.action = act;
         this.manager = manager;
      }

      @Override
      public boolean execute() throws IOException {
         boolean var1;
         try {
            var1 = this.action.execute();
         } finally {
            this.manager.semaphore.release();
         }

         return var1;
      }

      @Override
      public void close() {
         this.action.close();
      }

      @Override
      public boolean isComplete() {
         return this.action.isComplete();
      }

      @Override
      public String toString() {
         StringBuilder builder = new StringBuilder();
         builder.append(super.toString());
         builder.append("[action=");
         builder.append(this.action);
         builder.append(", manager=");
         builder.append(this.manager);
         builder.append(", isComplete()=");
         builder.append(this.isComplete());
         builder.append(", isInterrupted()=");
         builder.append(this.isInterrupted());
         builder.append("]");
         return builder.toString();
      }
   }

   private static class EmptyQueue extends ArrayBlockingQueue<Runnable> {
      private static final long serialVersionUID = 1L;

      EmptyQueue() {
         super(1);
      }

      @Override
      public int remainingCapacity() {
         return 0;
      }

      public boolean add(final Runnable runnable) {
         throw new IllegalStateException("Queue is full");
      }

      public void put(final Runnable runnable) throws InterruptedException {
         throw new InterruptedException("Unable to insert into queue");
      }

      public boolean offer(final Runnable runnable, final long timeout, final TimeUnit timeUnit) throws InterruptedException {
         Thread.sleep(timeUnit.toMillis(timeout));
         return false;
      }

      @Override
      public boolean addAll(final Collection<? extends Runnable> collection) {
         if (collection.size() > 0) {
            throw new IllegalArgumentException("Too many items in collection");
         } else {
            return false;
         }
      }
   }

   static class FactoryData extends ConfigurationFactoryData {
      private final String pattern;
      private final TriggeringPolicy policy;
      private final RolloverStrategy strategy;

      public FactoryData(final String pattern, final TriggeringPolicy policy, final RolloverStrategy strategy, final Configuration configuration) {
         super(configuration);
         this.pattern = pattern;
         this.policy = policy;
         this.strategy = strategy;
      }

      public TriggeringPolicy getTriggeringPolicy() {
         return this.policy;
      }

      public RolloverStrategy getRolloverStrategy() {
         return this.strategy;
      }

      public String getPattern() {
         return this.pattern;
      }
   }
}
