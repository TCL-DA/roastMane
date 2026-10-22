package org.jvnet.mimepull;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

final class WeakDataFile extends WeakReference<DataFile> {
   private static final Logger LOGGER = Logger.getLogger(WeakDataFile.class.getName());
   private static int TIMEOUT = 10;
   private static ReferenceQueue<DataFile> refQueue = new ReferenceQueue<>();
   private static Queue<WeakDataFile> refList = new ConcurrentLinkedQueue<>();
   private File file;
   private final RandomAccessFile raf;
   private static boolean hasCleanUpExecutor = false;

   WeakDataFile(DataFile df, File file) {
      super(df, refQueue);
      refList.add(this);
      this.file = file;

      try {
         this.raf = new RandomAccessFile(file, "rw");
      } catch (IOException var4) {
         throw new MIMEParsingException(var4);
      }

      if (!hasCleanUpExecutor) {
         drainRefQueueBounded();
      }
   }

   synchronized void read(long pointer, byte[] buf, int offset, int length) {
      try {
         this.raf.seek(pointer);
         this.raf.readFully(buf, offset, length);
      } catch (IOException var7) {
         throw new MIMEParsingException(var7);
      }
   }

   synchronized long writeTo(long pointer, byte[] data, int offset, int length) {
      try {
         this.raf.seek(pointer);
         this.raf.write(data, offset, length);
         return this.raf.getFilePointer();
      } catch (IOException var7) {
         throw new MIMEParsingException(var7);
      }
   }

   void close() {
      if (LOGGER.isLoggable(Level.FINE)) {
         LOGGER.log(Level.FINE, "Deleting file = {0}", this.file.getName());
      }

      refList.remove(this);

      try {
         this.raf.close();
         boolean deleted = this.file.delete();
         if (!deleted && LOGGER.isLoggable(Level.INFO)) {
            LOGGER.log(Level.INFO, "File {0} was not deleted", this.file.getAbsolutePath());
         }
      } catch (IOException var2) {
         throw new MIMEParsingException(var2);
      }
   }

   void renameTo(File f) {
      if (LOGGER.isLoggable(Level.FINE)) {
         LOGGER.log(Level.FINE, "Moving file={0} to={1}", new Object[]{this.file, f});
      }

      refList.remove(this);

      try {
         this.raf.close();
         Path target = Files.move(this.file.toPath(), f.toPath(), StandardCopyOption.REPLACE_EXISTING);
         boolean renamed = f.toPath().equals(target);
         if (!renamed && LOGGER.isLoggable(Level.INFO)) {
            throw new MIMEParsingException("File " + this.file.getAbsolutePath() + " was not moved to " + f.getAbsolutePath());
         } else {
            this.file = target.toFile();
         }
      } catch (IOException var4) {
         throw new MIMEParsingException(var4);
      }
   }

   static void drainRefQueueBounded() {
      WeakDataFile weak;
      while ((weak = (WeakDataFile)refQueue.poll()) != null) {
         if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.log(Level.FINE, "Cleaning file = {0} from reference queue.", weak.file);
         }

         weak.close();
      }
   }

   static {
      int delay = 10;

      try {
         delay = Integer.getInteger("org.jvnet.mimepull.delay", 10);
      } catch (SecurityException var3) {
         if (LOGGER.isLoggable(Level.CONFIG)) {
            LOGGER.log(Level.CONFIG, "Cannot read ''{0}'' property, using defaults.", new Object[]{"org.jvnet.mimepull.delay"});
         }
      }

      CleanUpExecutorFactory executorFactory = CleanUpExecutorFactory.newInstance();
      if (executorFactory != null) {
         if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.log(Level.FINE, "Initializing clean up executor for MIMEPULL: {0}", executorFactory.getClass().getName());
         }

         ScheduledExecutorService scheduler = executorFactory.getScheduledExecutorService();
         scheduler.scheduleWithFixedDelay(new WeakDataFile.CleanupRunnable(), (long)delay, (long)delay, TimeUnit.SECONDS);
         hasCleanUpExecutor = true;
      }
   }

   private static class CleanupRunnable implements Runnable {
      private CleanupRunnable() {
      }

      @Override
      public void run() {
         try {
            if (WeakDataFile.LOGGER.isLoggable(Level.FINE)) {
               WeakDataFile.LOGGER.log(Level.FINE, "Running cleanup task");
            }

            for (WeakDataFile weak = (WeakDataFile)WeakDataFile.refQueue.remove((long)WeakDataFile.TIMEOUT);
               weak != null;
               weak = (WeakDataFile)WeakDataFile.refQueue.remove((long)WeakDataFile.TIMEOUT)
            ) {
               if (WeakDataFile.LOGGER.isLoggable(Level.FINE)) {
                  WeakDataFile.LOGGER.log(Level.FINE, "Cleaning file = {0} from reference queue.", weak.file);
               }

               weak.close();
            }
         } catch (InterruptedException var2) {
         }
      }
   }
}
