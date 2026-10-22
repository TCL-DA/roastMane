package org.apache.logging.log4j.core.appender.rolling;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.file.Paths;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.AppenderLoggingException;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.internal.annotation.SuppressFBWarnings;
import org.apache.logging.log4j.core.util.FileUtils;
import org.apache.logging.log4j.core.util.NullOutputStream;

public class RollingRandomAccessFileManager extends RollingFileManager {
   public static final int DEFAULT_BUFFER_SIZE = 262144;
   private RandomAccessFile randomAccessFile;

   @Deprecated
   public RollingRandomAccessFileManager(
      final LoggerContext loggerContext,
      final RandomAccessFile raf,
      final String fileName,
      final String pattern,
      final OutputStream os,
      final boolean append,
      final boolean immediateFlush,
      final int bufferSize,
      final long size,
      final long time,
      final TriggeringPolicy policy,
      final RolloverStrategy strategy,
      final String advertiseURI,
      final Layout<? extends Serializable> layout,
      final boolean writeHeader
   ) {
      this(
         loggerContext,
         raf,
         fileName,
         pattern,
         os,
         append,
         immediateFlush,
         bufferSize,
         size,
         time,
         policy,
         strategy,
         advertiseURI,
         layout,
         null,
         null,
         null,
         writeHeader
      );
   }

   public RollingRandomAccessFileManager(
      final LoggerContext loggerContext,
      final RandomAccessFile raf,
      final String fileName,
      final String pattern,
      final OutputStream os,
      final boolean append,
      final boolean immediateFlush,
      final int bufferSize,
      final long size,
      final long initialTime,
      final TriggeringPolicy policy,
      final RolloverStrategy strategy,
      final String advertiseURI,
      final Layout<? extends Serializable> layout,
      final String filePermissions,
      final String fileOwner,
      final String fileGroup,
      final boolean writeHeader
   ) {
      super(
         loggerContext,
         fileName,
         pattern,
         os,
         append,
         false,
         size,
         initialTime,
         policy,
         strategy,
         advertiseURI,
         layout,
         filePermissions,
         fileOwner,
         fileGroup,
         writeHeader,
         ByteBuffer.wrap(new byte[bufferSize])
      );
      this.randomAccessFile = raf;
      this.writeHeader();
   }

   private void writeHeader() {
      if (this.layout != null) {
         byte[] header = this.layout.getHeader();
         if (header != null) {
            try {
               if (this.randomAccessFile != null && this.randomAccessFile.length() == 0L) {
                  this.randomAccessFile.write(header, 0, header.length);
               }
            } catch (IOException var3) {
               this.logError("Unable to write header", var3);
            }
         }
      }
   }

   public static RollingRandomAccessFileManager getRollingRandomAccessFileManager(
      final String fileName,
      final String filePattern,
      final boolean append,
      final boolean immediateFlush,
      final int bufferSize,
      final TriggeringPolicy policy,
      final RolloverStrategy strategy,
      final String advertiseURI,
      final Layout<? extends Serializable> layout,
      final String filePermissions,
      final String fileOwner,
      final String fileGroup,
      final Configuration configuration
   ) {
      if (strategy instanceof DirectWriteRolloverStrategy && fileName != null) {
         LOGGER.error("The fileName attribute must not be specified with the DirectWriteRolloverStrategy");
         return null;
      } else {
         String actualName = fileName == null ? filePattern : fileName;
         return narrow(
            RollingRandomAccessFileManager.class,
            getManager(
               actualName,
               (name, data) -> {
                  File file = null;
                  long size = 0L;
                  long time = System.currentTimeMillis();
                  RandomAccessFile raf = null;
                  boolean fileExistedBefore = false;
                  if (fileName != null) {
                     file = new File(name);
                     fileExistedBefore = file.exists();
                     if (!append) {
                        file.delete();
                     }

                     size = append ? file.length() : 0L;
                     if (file.exists()) {
                        time = file.lastModified();
                     }

                     try {
                        FileUtils.makeParentDirs(file);
                        raf = new RandomAccessFile(name, "rw");
                        if (append) {
                           long length = raf.length();
                           LOGGER.trace("RandomAccessFile {} seek to {}", name, length);
                           raf.seek(length);
                        } else {
                           LOGGER.trace("RandomAccessFile {} set length to 0", name);
                           raf.setLength(0L);
                        }
                     } catch (IOException var21) {
                        LOGGER.error("Cannot access RandomAccessFile " + var21, var21);
                        if (raf != null) {
                           try {
                              raf.close();
                           } catch (IOException var20) {
                              LOGGER.error("Cannot close RandomAccessFile {}", name, var20);
                           }
                        }

                        return null;
                     }
                  }

                  boolean writeHeader = !append || file == null || !fileExistedBefore;
                  RollingRandomAccessFileManager rrm = new RollingRandomAccessFileManager(
                     data.getLoggerContext(),
                     raf,
                     name,
                     data.getPattern(),
                     NullOutputStream.getInstance(),
                     append,
                     immediateFlush,
                     bufferSize,
                     size,
                     time,
                     data.getTriggeringPolicy(),
                     data.getRolloverStrategy(),
                     advertiseURI,
                     layout,
                     filePermissions,
                     fileOwner,
                     fileGroup,
                     writeHeader
                  );
                  if (rrm.isAttributeViewEnabled()) {
                     rrm.defineAttributeView(file.toPath());
                  }

                  return rrm;
               },
               new RollingFileManager.FactoryData(filePattern, policy, strategy, configuration)
            )
         );
      }
   }

   @Deprecated
   public Boolean isEndOfBatch() {
      return Boolean.FALSE;
   }

   @Deprecated
   public void setEndOfBatch(final boolean endOfBatch) {
   }

   @Override
   protected synchronized void write(final byte[] bytes, final int offset, final int length, final boolean immediateFlush) {
      super.write(bytes, offset, length, immediateFlush);
   }

   @Override
   protected synchronized void writeToDestination(final byte[] bytes, final int offset, final int length) {
      try {
         if (this.randomAccessFile == null) {
            this.createFileAfterRollover();
         }

         this.randomAccessFile.write(bytes, offset, length);
         this.size += (long)length;
      } catch (IOException var6) {
         String msg = "Error writing to RandomAccessFile " + this.getName();
         throw new AppenderLoggingException(msg, var6);
      }
   }

   @SuppressFBWarnings(
      value = {"PATH_TRAVERSAL_IN"},
      justification = "The name of the accessed files is based on a configuration value."
   )
   @Override
   protected void createFileAfterRollover() throws IOException {
      String fileName = this.getFileName();
      File file = new File(fileName);
      FileUtils.makeParentDirs(file);
      this.createFileAfterRollover(fileName);
   }

   @SuppressFBWarnings(
      value = {"PATH_TRAVERSAL_IN"},
      justification = "The name of the accessed files is based on a configuration value."
   )
   private void createFileAfterRollover(final String fileName) throws IOException {
      this.randomAccessFile = new RandomAccessFile(fileName, "rw");
      if (this.isAttributeViewEnabled()) {
         this.defineAttributeView(Paths.get(fileName));
      }

      if (this.isAppend()) {
         this.randomAccessFile.seek(this.randomAccessFile.length());
      }

      this.writeHeader();
   }

   @Override
   public synchronized void flush() {
      this.flushBuffer(this.byteBuffer);
   }

   @Override
   public synchronized boolean closeOutputStream() {
      this.flush();
      if (this.randomAccessFile != null) {
         try {
            this.randomAccessFile.close();
            return true;
         } catch (IOException var2) {
            this.logError("Unable to close RandomAccessFile", var2);
            return false;
         }
      } else {
         return true;
      }
   }

   @Override
   public int getBufferSize() {
      return this.byteBuffer.capacity();
   }

   @Override
   public void updateData(final Object data) {
      RollingFileManager.FactoryData factoryData = (RollingFileManager.FactoryData)data;
      this.setRolloverStrategy(factoryData.getRolloverStrategy());
      this.setPatternProcessor(new PatternProcessor(factoryData.getPattern(), this.getPatternProcessor()));
      this.setTriggeringPolicy(factoryData.getTriggeringPolicy());
   }
}
