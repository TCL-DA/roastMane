package org.apache.logging.log4j.status;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.logging.log4j.Level;

public class StatusConsoleListener implements StatusListener {
   private final Lock lock = new ReentrantLock();
   private final Level initialLevel;
   private final PrintStream initialStream;
   private volatile Level level;
   private volatile PrintStream stream;

   public StatusConsoleListener(final Level level) {
      this(level, System.out);
   }

   public StatusConsoleListener(final Level level, final PrintStream stream) {
      this.initialLevel = this.level = Objects.requireNonNull(level, "level");
      this.initialStream = this.stream = Objects.requireNonNull(stream, "stream");
   }

   public void setLevel(final Level level) {
      Objects.requireNonNull(level, "level");
      if (!this.level.equals(level)) {
         this.lock.lock();

         try {
            this.level = level;
         } finally {
            this.lock.unlock();
         }
      }
   }

   public void setStream(final PrintStream stream) {
      Objects.requireNonNull(stream, "stream");
      if (this.stream != stream) {
         OutputStream oldStream = null;
         this.lock.lock();

         try {
            if (this.stream != stream) {
               oldStream = this.stream;
               this.stream = stream;
            }
         } finally {
            this.lock.unlock();
         }

         if (oldStream != null) {
            closeNonSystemStream(oldStream);
         }
      }
   }

   @Override
   public Level getStatusLevel() {
      return this.level;
   }

   @Override
   public void log(final StatusData data) {
      Objects.requireNonNull(data, "data");
      String formattedStatus = data.getFormattedStatus();
      this.stream.println(formattedStatus);
   }

   @Deprecated
   public void setFilters(final String... filters) {
   }

   @Override
   public void close() {
      this.lock.lock();

      OutputStream oldStream;
      try {
         oldStream = this.stream;
         this.stream = this.initialStream;
         this.level = this.initialLevel;
      } finally {
         this.lock.unlock();
      }

      closeNonSystemStream(oldStream);
   }

   private static void closeNonSystemStream(final OutputStream stream) {
      if (stream != System.out && stream != System.err) {
         try {
            stream.close();
         } catch (IOException var2) {
            var2.printStackTrace(System.err);
         }
      }
   }
}
