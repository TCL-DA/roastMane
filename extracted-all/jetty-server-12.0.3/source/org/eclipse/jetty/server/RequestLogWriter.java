package org.eclipse.jetty.server;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.TimeZone;
import org.eclipse.jetty.util.RolloverFileOutputStream;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import org.eclipse.jetty.util.annotation.ManagedObject;
import org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.eclipse.jetty.util.thread.AutoLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ManagedObject("Request Log writer which writes to file")
public class RequestLogWriter extends AbstractLifeCycle implements RequestLog.Writer {
   private static final Logger LOG = LoggerFactory.getLogger(RequestLogWriter.class);
   private final AutoLock _lock = new AutoLock();
   private String _filename;
   private boolean _append;
   private int _retainDays;
   private boolean _closeOut;
   private String _timeZone = "GMT";
   private String _filenameDateFormat = null;
   private transient OutputStream _out;
   private transient OutputStream _fileOut;
   private transient Writer _writer;

   public RequestLogWriter() {
      this(null);
   }

   public RequestLogWriter(String filename) {
      this.setAppend(true);
      this.setRetainDays(31);
      if (filename != null) {
         this.setFilename(filename);
      }
   }

   public void setFilename(String filename) {
      if (filename != null) {
         filename = filename.trim();
         if (filename.length() == 0) {
            filename = null;
         }
      }

      this._filename = filename;
   }

   @ManagedAttribute("filename")
   public String getFileName() {
      return this._filename;
   }

   @ManagedAttribute("dated filename")
   public String getDatedFilename() {
      return this._fileOut instanceof RolloverFileOutputStream ? ((RolloverFileOutputStream)this._fileOut).getDatedFilename() : null;
   }

   public void setRetainDays(int retainDays) {
      this._retainDays = retainDays;
   }

   @ManagedAttribute("number of days to keep a log file")
   public int getRetainDays() {
      return this._retainDays;
   }

   public void setAppend(boolean append) {
      this._append = append;
   }

   @ManagedAttribute("if request log file will be appended after restart")
   public boolean isAppend() {
      return this._append;
   }

   public void setFilenameDateFormat(String logFileDateFormat) {
      this._filenameDateFormat = logFileDateFormat;
   }

   @ManagedAttribute("log file name date format")
   public String getFilenameDateFormat() {
      return this._filenameDateFormat;
   }

   @Override
   public void write(String requestEntry) throws IOException {
      AutoLock l = this._lock.lock();

      label40: {
         try {
            if (this._writer == null) {
               break label40;
            }

            this._writer.write(requestEntry);
            this._writer.write(System.lineSeparator());
            this._writer.flush();
         } catch (Throwable var6) {
            if (l != null) {
               try {
                  l.close();
               } catch (Throwable var5) {
                  var6.addSuppressed(var5);
               }
            }

            throw var6;
         }

         if (l != null) {
            l.close();
         }

         return;
      }

      if (l != null) {
         l.close();
      }
   }

   protected void doStart() throws Exception {
      AutoLock l = this._lock.lock();

      try {
         if (this._filename != null) {
            this._fileOut = new RolloverFileOutputStream(
               this._filename, this._append, this._retainDays, TimeZone.getTimeZone(this.getTimeZone()), this._filenameDateFormat, null
            );
            this._closeOut = true;
            LOG.info("Opened {}", this.getDatedFilename());
         } else {
            this._fileOut = System.err;
         }

         this._out = this._fileOut;
         this._writer = new OutputStreamWriter(this._out);
         super.doStart();
      } catch (Throwable var5) {
         if (l != null) {
            try {
               l.close();
            } catch (Throwable var4) {
               var5.addSuppressed(var4);
            }
         }

         throw var5;
      }

      if (l != null) {
         l.close();
      }
   }

   public void setTimeZone(String timeZone) {
      this._timeZone = timeZone;
   }

   @ManagedAttribute("timezone of the log")
   public String getTimeZone() {
      return this._timeZone;
   }

   protected void doStop() throws Exception {
      AutoLock ignored = this._lock.lock();

      try {
         super.doStop();

         try {
            if (this._writer != null) {
               this._writer.flush();
            }
         } catch (IOException var6) {
            LOG.trace("IGNORED", var6);
         }

         if (this._out != null && this._closeOut) {
            try {
               this._out.close();
            } catch (IOException var5) {
               LOG.trace("IGNORED", var5);
            }
         }

         this._out = null;
         this._fileOut = null;
         this._closeOut = false;
         this._writer = null;
      } catch (Throwable var7) {
         if (ignored != null) {
            try {
               ignored.close();
            } catch (Throwable var4) {
               var7.addSuppressed(var4);
            }
         }

         throw var7;
      }

      if (ignored != null) {
         ignored.close();
      }
   }
}
