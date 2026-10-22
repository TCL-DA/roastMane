package org.eclipse.jetty.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.eclipse.jetty.util.thread.AutoLock;

public class RolloverFileOutputStream extends OutputStream {
   static final String YYYY_MM_DD = "yyyy_mm_dd";
   static final String ROLLOVER_FILE_DATE_FORMAT = "yyyy_MM_dd";
   static final String ROLLOVER_FILE_BACKUP_FORMAT = "HHmmssSSS";
   static final int ROLLOVER_FILE_RETAIN_DAYS = 31;
   private static final ScheduledExecutorService __scheduler = Executors.newSingleThreadScheduledExecutor(job -> {
      Thread thread = new Thread(job, RolloverFileOutputStream.class.getName());
      thread.setDaemon(true);
      return thread;
   });
   private final AutoLock _lock = new AutoLock();
   private OutputStream _out;
   private ScheduledFuture<?> _rollTask;
   private final SimpleDateFormat _fileBackupFormat;
   private final SimpleDateFormat _fileDateFormat;
   private String _filename;
   private File _file;
   private final boolean _append;
   private final int _retainDays;

   public RolloverFileOutputStream(String filename) throws IOException {
      this(filename, true, 31);
   }

   public RolloverFileOutputStream(String filename, boolean append) throws IOException {
      this(filename, append, 31);
   }

   public RolloverFileOutputStream(String filename, boolean append, int retainDays) throws IOException {
      this(filename, append, retainDays, TimeZone.getDefault());
   }

   public RolloverFileOutputStream(String filename, boolean append, int retainDays, TimeZone zone) throws IOException {
      this(filename, append, retainDays, zone, null, null, ZonedDateTime.now(zone.toZoneId()));
   }

   public RolloverFileOutputStream(String filename, boolean append, int retainDays, TimeZone zone, String dateFormat, String backupFormat) throws IOException {
      this(filename, append, retainDays, zone, dateFormat, backupFormat, ZonedDateTime.now(zone.toZoneId()));
   }

   RolloverFileOutputStream(String filename, boolean append, int retainDays, TimeZone zone, String dateFormat, String backupFormat, ZonedDateTime now) throws IOException {
      if (dateFormat == null) {
         dateFormat = "yyyy_MM_dd";
      }

      this._fileDateFormat = new SimpleDateFormat(dateFormat);
      if (backupFormat == null) {
         backupFormat = "HHmmssSSS";
      }

      this._fileBackupFormat = new SimpleDateFormat(backupFormat);
      this._fileBackupFormat.setTimeZone(zone);
      this._fileDateFormat.setTimeZone(zone);
      if (filename != null) {
         filename = filename.trim();
         if (filename.length() == 0) {
            filename = null;
         }
      }

      if (filename == null) {
         throw new IllegalArgumentException("Invalid filename");
      } else {
         this._filename = filename;
         this._append = append;
         this._retainDays = retainDays;
         this.setFile(now);
         this.scheduleNextRollover(now);
      }
   }

   public static ZonedDateTime toMidnight(ZonedDateTime now) {
      return now.toLocalDate().atStartOfDay(now.getZone()).plus(1L, ChronoUnit.DAYS);
   }

   private void scheduleNextRollover(ZonedDateTime now) {
      ZonedDateTime midnight = toMidnight(now);
      long delay = midnight.toInstant().toEpochMilli() - now.toInstant().toEpochMilli();
      this._rollTask = __scheduler.schedule(this::rollOver, delay, TimeUnit.MILLISECONDS);
   }

   public String getFilename() {
      return this._filename;
   }

   public String getDatedFilename() {
      return this._file == null ? null : this._file.toString();
   }

   public int getRetainDays() {
      return this._retainDays;
   }

   void setFile(ZonedDateTime now) throws IOException {
      File oldFile = null;
      File newFile = null;
      File backupFile = null;

      try (AutoLock l = this._lock.lock()) {
         File file = new File(this._filename);
         this._filename = file.getCanonicalPath();
         file = new File(this._filename);
         File dir = file.getParentFile();
         if (!dir.exists()) {
            throw new IOException("Log directory does not exist. Path=" + dir);
         }

         if (!dir.isDirectory()) {
            throw new IOException("Path for Log directory is not a directory. Path=" + dir);
         }

         if (!dir.canWrite()) {
            throw new IOException("Cannot write log directory " + dir);
         }

         String filename = file.getName();
         int datePattern = filename.toLowerCase(Locale.ENGLISH).indexOf("yyyy_mm_dd");
         if (datePattern >= 0) {
            file = new File(
               dir,
               filename.substring(0, datePattern)
                  + this._fileDateFormat.format(new Date(now.toInstant().toEpochMilli()))
                  + filename.substring(datePattern + "yyyy_mm_dd".length())
            );
         }

         if (file.exists() && !file.canWrite()) {
            throw new IOException("Cannot write log file " + file);
         }

         if (this._out == null || datePattern >= 0) {
            oldFile = this._file;
            this._file = file;
            newFile = this._file;
            OutputStream oldOut = this._out;
            if (oldOut != null) {
               oldOut.close();
            }

            if (!this._append && file.exists()) {
               backupFile = new File(file.toString() + "." + this._fileBackupFormat.format(new Date(now.toInstant().toEpochMilli())));
               this.renameFile(file, backupFile);
            }

            this._out = new FileOutputStream(file.toString(), this._append);
         }
      }

      if (newFile != null) {
         this.rollover(oldFile, backupFile, newFile);
      }
   }

   private void renameFile(File src, File dest) throws IOException {
      if (!src.renameTo(dest)) {
         try {
            Files.move(src.toPath(), dest.toPath());
         } catch (IOException var4) {
            Files.copy(src.toPath(), dest.toPath());
            Files.deleteIfExists(src.toPath());
         }
      }
   }

   protected void rollover(File oldFile, File backupFile, File newFile) {
   }

   void removeOldFiles(ZonedDateTime now) {
      if (this._retainDays > 0) {
         long expired = now.minus((long)this._retainDays, ChronoUnit.DAYS).toInstant().toEpochMilli();
         File file = new File(this._filename);
         File dir = new File(file.getParent());
         String fn = file.getName();
         int s = fn.toLowerCase(Locale.ENGLISH).indexOf("yyyy_mm_dd");
         if (s < 0) {
            return;
         }

         String prefix = fn.substring(0, s);
         String suffix = fn.substring(s + "yyyy_mm_dd".length());
         String[] logList = dir.list();

         for (int i = 0; i < logList.length; i++) {
            fn = logList[i];
            if (fn.startsWith(prefix) && fn.indexOf(suffix, prefix.length()) >= 0) {
               File f = new File(dir, fn);
               if (f.lastModified() < expired) {
                  f.delete();
               }
            }
         }
      }
   }

   @Override
   public void write(int b) throws IOException {
      try (AutoLock l = this._lock.lock()) {
         this._out.write(b);
      }
   }

   @Override
   public void write(byte[] buf) throws IOException {
      try (AutoLock l = this._lock.lock()) {
         this._out.write(buf);
      }
   }

   @Override
   public void write(byte[] buf, int off, int len) throws IOException {
      try (AutoLock l = this._lock.lock()) {
         this._out.write(buf, off, len);
      }
   }

   @Override
   public void flush() throws IOException {
      try (AutoLock l = this._lock.lock()) {
         this._out.flush();
      }
   }

   @Override
   public void close() throws IOException {
      try (AutoLock l = this._lock.lock()) {
         try {
            this._out.close();
         } finally {
            this._out = null;
            this._file = null;
         }
      }

      ScheduledFuture<?> rollTask = this._rollTask;
      if (rollTask != null) {
         rollTask.cancel(false);
      }
   }

   private void rollOver() {
      try {
         ZonedDateTime now = ZonedDateTime.now(this._fileDateFormat.getTimeZone().toZoneId());
         this.setFile(now);
         this.removeOldFiles(now);
         this.scheduleNextRollover(now);
      } catch (Throwable var2) {
         var2.printStackTrace(System.err);
      }
   }
}
